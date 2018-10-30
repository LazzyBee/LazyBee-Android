package com.born2go.lazzybee.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Hue on 6/29/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String TABLE_VOCABULARY = "vocabulary";    //The Android's default system path of your application database.
    private static final String TAG = "DataBaseHelper";
    private static final String SDCARD = "sdcard";
    private static final String DOWNLOAD = "Download";
    private static final String DATA = "data";
    private static final String PACKAGE = "com.born2go.lazzybee";
    private static final String DATABASE = "databases";
    public static String DB_PATH;//= "/data/data/com.born2go.lazzybee/databases/";

    public static final String DB_NAME = "english.db";
    public static final String DB_UPDATE_NAME = "update.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;


    public static final String KEY_ID = "id";
    public static final String KEY_QUESTION = "question";
    public static final String KEY_ANSWERS = "answers";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_SUBCAT = "subcat";
    public static final String KEY_STATUS = "status";
    public static final String KEY_G_ID = "gid";
    public static final String KEY_RELATED = "related";
    public static final String KEY_TAGS = "tags";


    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    @SuppressLint("SdCardPath")
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/" + DB_NAME;//context.getDatabasePath(DB_NAME).getAbsolutePath();
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/" + DB_NAME;
        }
    }


    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void _createDataBase() {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            //do nothing - database already exist
            Log.i(TAG, "database already exist");
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
//            Log.e(TAG, "copyDataBase");
            try {
                copyDataBase(0);
            } catch (IOException e) {
                Log.e(TAG, "Error copying database:" + e.getMessage());
                throw new Error("Error copying database");

            }
        }

    }

    public boolean checkDataBase() {
        File dbFile = new File(DB_PATH);
        return dbFile.exists();
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    public void copyDataBase(int type) throws IOException {

        //Open your local db as the input stream
        //InputStream myInput = myContext.getAssets().open("/" + SDCARD + "/" + DOWNLOAD + "/" + DB_NAME);

//        Open your local db as the input stream
        InputStream myInput;
        if (type == 0) {
            myInput = myContext.getAssets().open(DB_NAME);
        } else {
            File sdCard_dir = Environment.getExternalStorageDirectory();
            File dlDir = new File(sdCard_dir.getAbsolutePath() + "/" + DOWNLOAD);
            boolean wasSuccessful = dlDir.mkdir();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }

            File source = new File(dlDir, DB_UPDATE_NAME);
            myInput = new FileInputStream(source);
        }

//        // Path to the just created empty db
        String outFileName = DB_PATH;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    public static boolean copyFile(File source, File dest) {
        try {
            // Declaration et ouverture des flux

            try (FileInputStream sourceFile = new FileInputStream(source)) {
                FileOutputStream destinationFile = null;

                try {
                    destinationFile = new FileOutputStream(dest);

                    // Lecture par segment de 0.5Mo
                    byte buffer[] = new byte[512 * 1024];
                    int nbLecture;

                    while ((nbLecture = sourceFile.read(buffer)) != -1) {
                        destinationFile.write(buffer, 0, nbLecture);
                    }
                } finally {
                    if (destinationFile != null)
                        destinationFile.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Erreur
        }

        return true; // Rsultat OK
    }

    public SQLiteDatabase openDataBase(String myPath) throws SQLException {
        //Open the database
        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @SuppressLint("SdCardPath")
    void _upgrageDatabase() {
        SQLiteDatabase checkInDowload = null;
        try {
            checkInDowload = SQLiteDatabase.openDatabase("/sdcard/Download/english.db", null, SQLiteDatabase.OPEN_READONLY);
            Log.e(TAG, "OK");
        } catch (SQLiteException e) {
            //database does't exist yet.
            Log.e(TAG, "database in Download does't exist yet:" + e.getMessage());
        }
        if (checkInDowload != null) {
            checkInDowload.close();
        }
    }

    public void _exportDatabase() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/com.born2go.lazzybee/databases/" + DB_NAME;


        //String backupDBPath = DB_NAME;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, "/" + DOWNLOAD + "/" + DB_NAME);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }
}
