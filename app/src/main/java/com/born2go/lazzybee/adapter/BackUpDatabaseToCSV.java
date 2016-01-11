package com.born2go.lazzybee.adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.UploadTarget;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.ZipManager;
import com.born2go.lazzybee.view.dialog.DialogMyCodeRestoreDB;
import com.google.api.client.util.IOUtils;
import com.opencsv.CSVWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.Header;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by Hue on 12/16/2015.
 */
public class BackUpDatabaseToCSV extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "BackUpDatabaseToCSV";
    private Activity activity;
    private Context context;
    private String device_id;
    private ProgressDialog dialog;
    ZipManager zipManager;
    // word.gid, word.queue, word.due,word.revCount, word.lastInterval, word.eFactor, userNote
    private String queryExportToCsvFull = "Select " +
            "vocabulary.gid," +
            "vocabulary.queue," +
            "vocabulary.due," +
            "vocabulary.rev_count," +
            "vocabulary.last_ivl," +
            "vocabulary.e_factor," +
            "vocabulary.user_note " +
            "from vocabulary where vocabulary.gid not null";
    private int type;
    private String queryExportToCsv = "Select " +
            "vocabulary.gid," +
            "vocabulary.queue," +
            "vocabulary.due," +
            "vocabulary.rev_count," +
            "vocabulary.last_ivl," +
            "vocabulary.e_factor," +
            "vocabulary.user_note " +
            "from vocabulary where vocabulary.queue = -1 OR vocabulary.queue = -2 OR vocabulary.queue > 0 AND vocabulary.gid not null";


    public BackUpDatabaseToCSV(Activity activity, Context context, String device_id, int type) {
        this.activity = activity;
        this.context = context;
        this.device_id = device_id;
        dialog = new ProgressDialog(context);
        zipManager = new ZipManager();
        this.type = type;
        Log.d(TAG, "Type export:" + ((type == 0) ? " Full" : " Mini"));

    }

    protected void onPreExecute() {
        //set up dialog
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (LazzyBeeShare.checkConn(context)) {
            return _exportDBToCSV();
        } else return false;
    }

    private Boolean _exportDBToCSV() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        String code = null;
        boolean results = false;
        //File file = new File(exportDir, ((type == 0) ? "Full_" : "")+(LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".csv");
        File file = new File(exportDir, "backup.csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file), ',', '\0', ',', ",\n");

            SQLiteDatabase db = LazzyBeeSingleton.dataBaseHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery((type == 0) ? queryExportToCsvFull : queryExportToCsv, null);
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
                            // word.gid, word.queue, word.due,word.revCount, word.lastInterval, word.eFactor, userNote
                            String arrStr[] = {
                                    curCSV.getString(0),
                                    curCSV.getString(1),
                                    curCSV.getString(2),
                                    curCSV.getString(3),
                                    curCSV.getString(4),
                                    curCSV.getString(5),
                                    String.valueOf(user_note)};
                            csvWrite.writeNext(arrStr, true);
                        } while (curCSV.moveToNext());
                }
                csvWrite.close();
                curCSV.close();
                String[] files = new String[1];
                files[0] = file.getPath();
                String fileZipPath = exportDir.getPath() + "/backup.zip";
                //zipManager.zip(files, exportDir.getPath() + "/" + ((type == 0) ? "Full_" : "") + (LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".zip");
                zipManager.zip(files, fileZipPath);

                //save file backup to server
                boolean resultsBackupFile = postFile(exportDir.getPath() + "/backup.zip");
                //Delete file
                File zipFile = new File(fileZipPath);
                Log.d(TAG, "Delete file Csv:" + (file.delete() ? " Ok" : " Fails"));
                Log.d(TAG, "Delete zip File:" + (zipFile.delete() ? " Ok" : " Fails"));
                results = resultsBackupFile;
            } else {
                Log.d(TAG, "No query");
            }
        } catch (Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
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
            String backup_key = device_id.substring(device_id.length() - 6, device_id.length());
            //show backUp Key
            DialogMyCodeRestoreDB dialogMyCodeRestoreDB = new DialogMyCodeRestoreDB(backup_key);
            dialogMyCodeRestoreDB.show(activity.getFragmentManager(), DialogMyCodeRestoreDB.TAG);
        } else {
            _showDialogFailsBackupDatabase();

        }

    }

    private void _showDialogFailsBackupDatabase() {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
        builder.setTitle(R.string.try_again);
        builder.setMessage(R.string.failed_to_connect_to_server);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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
            e.printStackTrace();
        }
        return results;
    }
}


