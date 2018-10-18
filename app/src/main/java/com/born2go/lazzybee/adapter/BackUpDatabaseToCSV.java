package com.born2go.lazzybee.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.UploadTarget;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.ZipManager;
import com.born2go.lazzybee.view.dialog.DialogMyCodeRestoreDB;
import com.opencsv.CSVWriter;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by Hue on 12/16/2015.
 */
@SuppressLint("StaticFieldLeak")
public class BackUpDatabaseToCSV extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "BackUpDatabaseToCSV";
    private final String backup_key;
    Activity activity;
    private String device_id;
    private ProgressDialog dialog;
    ZipManager zipManager;

    private String wordFileName = "word.csv";
    private String streakFileName = "streak.csv";
    private String backupFileName = "backup.zip";
    File exportDir;
    private String dotZip;

    private Cursor curCSV;

    public BackUpDatabaseToCSV(Activity activity, Context context, String device_id, int type) {
        this.device_id = device_id;
        backup_key = device_id.substring(device_id.length() - 6, device_id.length());
        dialog = new ProgressDialog(context);
        zipManager = new ZipManager();
        int type1 = type;
        Log.d(TAG, "Type export:" + ((type == 0) ? " Full" : " Mini"));
        exportDir = new File(Environment.getExternalStorageDirectory(), LazzyBeeShare.EMPTY);
        if (!exportDir.exists()) {
            boolean wasSuccessful = exportDir.mkdir();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }
        }

        SQLiteDatabase db = LazzyBeeSingleton.dataBaseHelper.getReadableDatabase();
        // word.gid, word.queue, word.due,word.revCount, word.lastInterval, word.eFactor, userNote
        String queryExportWordTableToCsvFull = "Select " +
                "vocabulary.gid," +
                "vocabulary.queue," +
                "vocabulary.due," +
                "vocabulary.rev_count," +
                "vocabulary.last_ivl," +
                "vocabulary.e_factor," +
                "vocabulary.user_note, " +
                "vocabulary.level " +
                "from vocabulary where vocabulary.gid not null";
        String queryExportWordTableToCsv = "Select " +
                "vocabulary.gid," +
                "vocabulary.queue," +
                "vocabulary.due," +
                "vocabulary.rev_count," +
                "vocabulary.last_ivl," +
                "vocabulary.e_factor," +
                "vocabulary.user_note, " +
                "vocabulary.level " +
                "from vocabulary where vocabulary.queue = -1 OR vocabulary.queue = -2 OR vocabulary.queue > 0 AND vocabulary.gid not null";
        curCSV = db.rawQuery((type == 0) ? queryExportWordTableToCsvFull : queryExportWordTableToCsv, null);
        dotZip = "_" + curCSV.getCount() + ".zip";

    }

    protected void onPreExecute() {
        //set up dialog
        this.dialog.setMessage("Loading...");
        this.dialog.setCancelable(false);
        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (LazzyBeeShare.checkConn(activity)) {
            if (_exportToCSV()) {
                Log.d(TAG, "Export db to csv:" + activity.getString(R.string.successfully));
                if (_zipFileBackUp()) {
                    Log.d(TAG, "Zip backup file:" + activity.getString(R.string.successfully));
                    boolean resultsBackupFile = postFile(exportDir.getPath() + "/" + backup_key + dotZip);
                    _deleteFile();
                    if (resultsBackupFile) {
                        Log.d(TAG, "Post backup file:" + activity.getString(R.string.successfully));
                        return true;
                    } else {
                        Log.d(TAG, "Post backup file:Fails");
                        return false;
                    }
                } else {
                    Log.d(TAG, "Zip backup file:Fails");
                    return false;
                }
            } else {
                Log.d(TAG, "Export db to csv: Fails");
                return false;
            }
        } else return false;
    }

    private void _deleteFile() {
        File zipFile = new File(exportDir, backup_key + dotZip);
        File wordFile = new File(exportDir, wordFileName);
        File streakFile = new File(exportDir, streakFileName);
        Log.d(TAG, "Delete " + backup_key + dotZip + " file:" + (zipFile.delete() ? " Ok" : " Fails"));
        Log.d(TAG, "Delete word.csv:" + (wordFile.delete() ? " Ok" : " Fails"));
        Log.d(TAG, "Delete streak.csv:" + (streakFile.delete() ? " Ok" : " Fails"));
    }

    private Boolean _exportToCSV() {
        if (_exportWordTableToCSV()) {
            return _exportStreakTableToCSV();
        } else {
            return false;
        }
    }

    private Boolean _exportWordTableToCSV() {
        boolean results = false;
        //File file = new File(exportDir, ((type == 0) ? "Full_" : "")+(LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".csv");
        File file = new File(exportDir, wordFileName);
        try {
            boolean wasSuccessful = file.createNewFile();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',', '\0', ',', ",\n");
            if (curCSV.getCount() > 0) {
                if (curCSV.moveToFirst()) {
                    if (curCSV.getCount() > 0)
                        do {
                            String user_note = curCSV.getString(6);
                            if (user_note != null) {
                                if (user_note.contains(",")) {
                                    user_note = user_note.replace(",", "*#*");
                                }
                            } else {
                                user_note = LazzyBeeShare.EMPTY;
                            }
                            // word.gid, word.queue, word.due,word.revCount, word.lastInterval, word.eFactor, userNote,level
                            String arrStr[] = {
                                    curCSV.getString(0),
                                    curCSV.getString(1),
                                    curCSV.getString(2),
                                    curCSV.getString(3),
                                    curCSV.getString(4),
                                    curCSV.getString(5),
                                    String.valueOf(user_note),
                                    curCSV.getString(7)};
                            csvWrite.writeNext(arrStr, true);
                        } while (curCSV.moveToNext());
                }
                csvWrite.close();
                curCSV.close();
//                String[] files = new String[1];
//                files[0] = file.getPath();
//                String fileZipPath = exportDir.getPath() + "/backup.zip";
//                //zipManager.zip(files, exportDir.getPath() + "/" + ((type == 0) ? "Full_" : "") + (LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".zip");
//                zipManager.zip(files, fileZipPath);
//
//                //save file backup to server
//                boolean resultsBackupFile = postFile(exportDir.getPath() + "/backup.zip");
//                //Delete file
//                File zipFile = new File(fileZipPath);
//                Log.d(TAG, "Delete file Csv:" + (file.delete() ? " Ok" : " Fails"));
//                Log.d(TAG, "Delete zip File:" + (zipFile.delete() ? " Ok" : " Fails"));

            } else {
                Log.d(TAG, "No query");
            }
            results = true;
        } catch (Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(sqlEx);
        }
        return results;
    }

    boolean _zipFileBackUp() {
        File wordFile = new File(exportDir.getPath() + "/" + wordFileName);
        File streakFile = new File(exportDir.getPath() + "/" + streakFileName);
        String[] files = new String[2];
        files[0] = wordFile.getPath();
        files[1] = streakFile.getPath();
        String fileZipPath = exportDir.getPath() + "/" + backup_key + dotZip;
        return zipManager.zip(files, fileZipPath);
    }

    private Boolean _exportStreakTableToCSV() {
        boolean results = false;
        File file = new File(exportDir, streakFileName);
        try {
            boolean wasSuccessful = file.createNewFile();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',', '\0', ',', ",\n");
            SQLiteDatabase db = LazzyBeeSingleton.dataBaseHelper.getReadableDatabase();
            String queryExportStreakTableToCsv = "select day from streak";
            Cursor curCSV = db.rawQuery(queryExportStreakTableToCsv, null);
            if (curCSV.getCount() > 0) {
                if (curCSV.moveToFirst()) {
                    if (curCSV.getCount() > 0)
                        do {
                            String arrStr[] = {curCSV.getString(0),};
                            csvWrite.writeNext(arrStr, true);
                        } while (curCSV.moveToNext());
                }
                csvWrite.close();
                curCSV.close();
                results = true;
            } else {
                Log.d(TAG, "No query");
            }
            results = true;
        } catch (Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(sqlEx);
        }
        return results;
    }

    @Override
    protected void onPostExecute(Boolean results) {
        super.onPostExecute(results);
        //Dismis dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        if (results) {
            //show backUp Key
            DialogMyCodeRestoreDB dialogMyCodeRestoreDB = new DialogMyCodeRestoreDB(backup_key);
            dialogMyCodeRestoreDB.show(activity.getFragmentManager(), DialogMyCodeRestoreDB.TAG);
        } else {
            _showDialogFailsBackupDatabase();

        }

    }

    private void _showDialogFailsBackupDatabase() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity, R.style.DialogLearnMore);
        builder.setTitle(R.string.try_again);
        builder.setMessage(R.string.failed_to_connect_to_server_can_not_back_up_database);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean postFile(String fileName) {
        boolean results = false;
        String resCode = null;
        try {
            //get upload URl
            DataServiceApi.GetUploadUrl getUploadUrl = LazzyBeeSingleton.connectGdatabase.getDataServiceApi().getUploadUrl();
            UploadTarget uploadTarget = getUploadUrl.execute();
            String upLoadServerUri = uploadTarget.getUrl();
            if (upLoadServerUri != null) {
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(upLoadServerUri);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addPart("device_id", new StringBody(device_id, ContentType.TEXT_PLAIN));
                builder.addPart("file", new FileBody(new File(fileName)));
                post.setEntity(builder.build());

                //execute Post
                HttpResponse response = client.execute(post);
                int statusCode = response.getStatusLine().getStatusCode();
                Log.d(TAG, "Status code:" + statusCode);
                if (statusCode == 200) {
                    results = true;
                    Log.d(TAG, "Post file backup to Server Ok");
                } else {
                    Log.d(TAG, "Post file backup to Server Fails");
                }
                client.getConnectionManager().shutdown();
            }
        } catch (Exception e) {
            Log.d(TAG, "Post file backup to Server Fails");
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(e);
            e.printStackTrace();
        }
        return results;
    }
}


