package io.intrepid.nostalgia.songdatabase;

import java.io.IOException;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import io.intrepid.nostalgia.R;

public class DatabaseExplorer extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     */
    DatabaseHelper dbhelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_year);

        String[] from = new String[]{"_id", "columnName1", "columnName2"};
        int[] to = new int[]{R.id.song_name, R.id.song_artist};

        dbhelper = new DatabaseHelper(this);
        try {
            dbhelper.createDataBase();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Cursor c = dbhelper.getData();

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getApplicationContext(), R.layout.database_explorer, c, from, to);

        ListView list = (ListView) findViewById(R.id.items);

        list.setAdapter(adapter);
    }
}