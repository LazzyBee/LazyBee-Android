package com.born2go.lazzybee.adapter;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.UploadTarget;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.ZipManager;
import com.google.api.client.util.IOUtils;
import com.opencsv.CSVWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
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
    private Context context;
    private ProgressDialog dialog;
    ZipManager zipManager;
    private int type;
    private String queryExportToCsv = "Select " +
            "vocabulary.gid," +
            "vocabulary.e_factor," +
            "vocabulary.last_ivl," +
            "vocabulary.queue," +
            "vocabulary.rev_count," +
            "vocabulary.due " +
            "from vocabulary where vocabulary.queue = -1 OR vocabulary.queue = -2 OR vocabulary.queue > 0";

    private String queryExportToCsvFull = "Select " +
            "vocabulary.gid," +
            "vocabulary.e_factor," +
            "vocabulary.last_ivl," +
            "vocabulary.queue," +
            "vocabulary.rev_count," +
            "vocabulary.due " +
            "from vocabulary";


    public BackUpDatabaseToCSV(Context context, int type) {
        this.context = context;
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
        return _exportDBToCSV();
    }

    private boolean _exportDBToCSV() {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        boolean export = false;
        //File file = new File(exportDir, ((type == 0) ? "Full_" : "")+(LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".csv");
        File file = new File(exportDir, "backup.csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = LazzyBeeSingleton.dataBaseHelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery((type == 0) ? queryExportToCsvFull : queryExportToCsv, null);
            if (curCSV.getCount() > 0) {
                String[] columNames = curCSV.getColumnNames();
                Log.d(TAG, "columNames length:" + columNames.length);
                csvWrite.writeNext(columNames);
                if (curCSV.moveToFirst()) {
                    if (curCSV.getCount() > 0)
                        do {
                            String arrStr[] = {
                                    curCSV.getString(0),
                                    curCSV.getString(1),
                                    curCSV.getString(2),
                                    curCSV.getString(3),
                                    curCSV.getString(4),
                                    curCSV.getString(5)};
                            csvWrite.writeNext(arrStr);
                        } while (curCSV.moveToNext());
                }
                csvWrite.close();
                curCSV.close();
                String[] files = new String[1];
                files[0] = file.getPath();
                String fileZipPath = exportDir.getPath() + "/backup.zip";
                //zipManager.zip(files, exportDir.getPath() + "/" + ((type == 0) ? "Full_" : "") + (LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".zip");
                zipManager.zip(files, fileZipPath);
                postFile(exportDir.getPath() + "/backup.zip");
                File zipFile = new File(fileZipPath);
                Log.d(TAG, "Delete file Csv:" + (file.delete() ? " Ok" : " Fails"));
                Log.d(TAG, "Delete zip File:" + (zipFile.delete() ? " Ok" : " Fails"));
                export = true;
            } else {
                Log.d(TAG, "No query");
            }
        } catch (Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }
        return export;
    }

    @Override
    protected void onPostExecute(Boolean export) {
        super.onPostExecute(export);
        //Dismis dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        Toast.makeText(context, "Export to CSV" + (export ? " Ok" : " Fails"), Toast.LENGTH_SHORT).show();

    }

    private void postFile(String fileName) {
        try {
            DataServiceApi.GetUploadUrl getUploadUrl = LazzyBeeSingleton.connectGdatabase.getDataServiceApi().getUploadUrl();
            UploadTarget uploadTarget = getUploadUrl.execute();

            String upLoadServerUri = uploadTarget.getUrl();
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(upLoadServerUri);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("file", new FileBody(new File(fileName)));
            post.setEntity(builder.build());
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            entity.consumeContent();
            client.getConnectionManager().shutdown();
            Log.d(TAG, "Post file backup to Server Ok");
        } catch (Exception e) {
            Log.d(TAG, "Post file backup to Server Fails");
        }
    }
}


