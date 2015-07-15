package com.born2go.lazzybee.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Hue on 6/29/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    private static DataBaseHelper globalDB;
    private static final String TABLE_VOCABULARY = "vocabulary";    //The Android's default system path of your application database.
    private static final String TAG = "DataBaseHelper";
    private static final String SDCARD = "sdcard";
    private static final String DOWNLOAD = "Download";
    private static final String DATA = "data";
    private static final String PACKAGE = "com.born2go.lazzybee";
    private static final String DATABASE = "databases";
    private static String DB_PATH = "/data/data/com.born2go.lazzybee/databases/";

    private static String DB_NAME = "english";

    private SQLiteDatabase myDataBase;

    private final Context myContext;


    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWERS = "answers";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_SUBCAT = "subcat";
    private static final String KEY_STATUS = "status";
    private static final String KEY_G_ID = "gid";
    private static final String KEY_RELATED = "related";
    private static final String KEY_TAGS = "tags";


    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    public DataBaseHelper getGlobalDB(){
        if (globalDB==null){//Init DB here
            //TODO: Work out
        }
        return globalDB;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     */
    public void _createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
            Log.e(TAG, "database already exist");
        } else {

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
//            Log.e(TAG, "copyDataBase");

            try {

                copyDataBase();

            } catch (IOException e) {
                Log.e(TAG, "Error copying database:" + e.getMessage());
                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;

        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
            Log.e(TAG, "database does't exist yet:" + e.getMessage());
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        //Open your local db as the input stream
        //InputStream myInput = myContext.getAssets().open("/" + SDCARD + "/" + DOWNLOAD + "/" + DB_NAME);
//        File sdCard_dir = Environment.getExternalStorageDirectory();
//        File dlDir = new File(sdCard_dir.getAbsolutePath() + "/" + DOWNLOAD);
//        dlDir.mkdirs();
//        File source = new File(dlDir, DB_NAME);
//        InputStream myInput = new FileInputStream(source);
//        Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

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


        //get Source file
//        File sdCard = Environment.getExternalStorageDirectory();
//        File dir_download = new File(sdCard.getAbsolutePath() + "/" + DOWNLOAD);
//        Log.e(TAG, "dir_download path:" + dir_download.getAbsolutePath());
//        dir_download.mkdirs();
//        File source = new File(dir_download, DB_NAME);
//
//        //get destination file
//        File root = Environment.getRootDirectory();
//        File dir_data = new File(root.getAbsolutePath() + "/" + DATA + "/" + DATA + "/" + PACKAGE + "/" + DATABASE);
//        Log.e(TAG, "Data path:" + dir_data.getAbsolutePath());
//        dir_data.mkdirs();
//        File destination = new File(dir_data, DB_NAME);
//
//        //Move file
//
//        if (moveFile(source, destination)) {
//            //Thanh cong
//
//            Log.e(TAG, "Move thanh cong");
//        } else {
//            //That bai
//
//            Log.e(TAG, "Move that bai");
//        }


    }

    /**
     * Dplace le fichier source dans le fichier rsultat
     */
    public static boolean moveFile(File source, File destination) {
        if (!destination.exists()) {
            // On essaye avec renameTo
            boolean result = source.renameTo(destination);
            if (!result) {
                // On essaye de copier
                try {
                    result = true;
                    result &= copyFile(source, destination);
                    if (result) result &= source.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return (result);
        } else {
            // Si le fichier destination existe, on annule ...
            return (false);
        }
    }

    public static boolean copyFile(File source, File dest) {
        try {
            // Declaration et ouverture des flux
            FileInputStream sourceFile = new FileInputStream(source);

            try {
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
                    destinationFile.close();
                }
            } finally {
                sourceFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Erreur
        }

        return true; // Rsultat OK
    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

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

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.
//    public List<Card> _getListCard() {
//        List<Card> datas = new ArrayList<Card>();
//        //select query
//        String selectQuery = "SELECT  * FROM " + TABLE_VOCABULARY;
//        //select limit 5 row
//        String selectLimitQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " LIMIT 5 ";
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        //query for cursor
//        Cursor cursor = db.rawQuery(selectLimitQuery, null);
//        if (cursor.moveToFirst()) {
//            if (cursor.getCount() > 0)
//                do {
//                    //get data from sqlite
//                    int id = cursor.getInt(0);
//                    String question = cursor.getString(1);
//                    String answers = cursor.getString(2);
//                    String categories = cursor.getString(3);
//                    String subcat = cursor.getString(4);
//                    Card card = new Card(id, question, answers, categories, subcat, 1);
//                    datas.add(card);
//                } while (cursor.moveToNext());
//        }
//        return datas;
//    }
//
//
//
//    /**
//     * Get Review List Today
//     * <p>List vocabulary complete in today</p>
//     */
//    public List<Card> getReviewListVocabulary() {
//        return _getListCard();
//    }
//
//    /**
//     * Seach vocabulary
//     */
//    public List<Card> _searchCard(String query) {
//        List<Card> datas = new ArrayList<Card>();
//
//        //select like query
//        String likeQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " WHERE " + KEY_QUESTION + " like '%" + query + "%'";
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        //query for cursor
//        Cursor cursor = db.rawQuery(likeQuery, null);
//        if (cursor.moveToFirst()) {
//            if (cursor.getCount() > 0)
//                do {
//                    //get data from sqlite
//                    int id = cursor.getInt(0);
//                    String question = cursor.getString(1);
//                    String answers = cursor.getString(2);
//                    String categories = cursor.getString(3);
//                    String subcat = cursor.getString(4);
//                    Card card = new Card(id, question, answers, categories, subcat, 1);
//                    datas.add(card);
//                } while (cursor.moveToNext());
//        }
//        return datas;
//    }
//
//    /**
//     * Get card by ID form sqlite
//     *
//     * @param cardId
//     */
//    public Card _getCardByID(String cardId) {
//        Card card = new Card();
//
//        String selectbyIDQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " WHERE " + KEY_ID + " = " + cardId;
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        //query for cursor
//        Cursor cursor = db.rawQuery(selectbyIDQuery, null);
//        if (cursor.moveToFirst()) {
//            if (cursor.getCount() > 0)
//                do {
//                    //get data from sqlite
//                    int id = cursor.getInt(0);
//                    String question = cursor.getString(1);
//                    String answers = cursor.getString(2);
//                    String categories = cursor.getString(3);
//                    String subcat = cursor.getString(4);
//                    // Card card = new Card(id, question, answers, categories, subcat, 1);
//                    card.setId(id);
//                    card.setQuestion(question);
//                    card.setAnswers(answers);
//                    card.setCategories(categories);
//                    card.setSubcat(subcat);
//                } while (cursor.moveToNext());
//        }
//        return card;
//    }
}
