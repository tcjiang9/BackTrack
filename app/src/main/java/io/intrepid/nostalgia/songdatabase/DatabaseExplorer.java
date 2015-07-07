package io.intrepid.nostalgia.songdatabase;

import java.io.IOException;
import java.util.Random;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.YearFragment;

public class DatabaseExplorer extends AppCompatActivity {

    DatabaseHelper dbhelper;
    @InjectView(R.id.song_artist)
    TextView artist;
    @InjectView(R.id.song_name)
    TextView songName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_explorer);
        ButterKnife.inject(this);
        DatabaseHelper myDbHelper = new DatabaseHelper(this);
        Intent receive = getIntent();
        String year = receive.getStringExtra(YearFragment.KEY);

        try {
            myDbHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            Cursor c = myDbHelper.getData(year);
            Random random = new Random();
            int index = random.nextInt(c.getCount() + 1);
            getRandomSongFromDb(c, index);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

    }

    private void getRandomSongFromDb(Cursor c, int index) {
        c.moveToPosition(index);
        artist.setText(c.getString(0));
        songName.setText(c.getString(1));
        c.close();
    }

}