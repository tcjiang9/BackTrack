package io.intrepid.nostalgia.songdatabase;


    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.OutputStream;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;
    import android.util.Log;
    import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper {

        private static String DB_NAME = "tracks";
        private SQLiteDatabase db;
        private final Context context;
        private String DB_PATH;

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, 1);

            this.context = context;
            String path = context.getFilesDir().getPath();
            DB_PATH =path.substring(0, path.lastIndexOf("/")) + "/" + "databases/";
        }

        public void createDataBase() throws IOException {

            boolean dbExist = checkDataBase();
            if (dbExist) {
                Log.e("database check ","im in database");
            } else {
                this.getReadableDatabase();
                try {
                    copyDataBase();
                } catch (IOException e) {
                    throw new Error("Error copying database");
                }
            }
        }

        private boolean checkDataBase() {
            File dbFile = new File(DB_PATH + DB_NAME);
            return dbFile.exists();
        }

        private void copyDataBase() throws IOException {

            InputStream myInput = context.getAssets().open(DB_NAME);
            String outFileName = DB_PATH + DB_NAME;
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        }

        public Cursor getData() {
            String myPath = DB_PATH + DB_NAME;
            if(db)
            db = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);
             return db.rawQuery("SELECT *", null);
            // Note: Master is the one table in External db. Here we trying to access the records of table from external db.
        }

        @Override
        public void onCreate(SQLiteDatabase arg0) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
        }
    }


