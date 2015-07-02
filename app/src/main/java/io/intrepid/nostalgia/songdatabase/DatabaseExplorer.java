package io.intrepid.nostalgia.songdatabase;

import java.io.IOException;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        setContentView(R.layout.database_explorer);

        DatabaseHelper myDbHelper = new DatabaseHelper(this);

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

//             myDbHelper.openDataBase();
            Cursor c = myDbHelper.getData();
            String[] from = c.getColumnNames();
            for (String s : from) {
                Log.e("colum names",""+s);
            }

        }catch(SQLException sqle){

            throw sqle;

        }

    }
}