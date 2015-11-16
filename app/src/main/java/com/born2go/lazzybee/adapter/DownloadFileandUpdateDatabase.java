package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Hue on 9/22/2015.
 */
public class DownloadFileandUpdateDatabase extends AsyncTask<String, Void, Integer> {

    private static final String TAG = "DownloadUpdateDatabase";

    public interface DownloadFileDatabaseResponse {
        void processFinish(int code);
    }

    Context context;
    ProgressDialog progressDialog;
    public DownloadFileDatabaseResponse downloadFileDatabaseResponse;
    LearnApiImplements learnApiImplements;
    DatabaseUpgrade databaseUpgrade;
    int version;

    public DownloadFileandUpdateDatabase(Context context, int version) {
        this.context = context;
        progressDialog = new ProgressDialog(context);
        this.learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        this.databaseUpgrade = LazzyBeeSingleton.databaseUpgrade;
        this.version = version;
    }

    protected void onPreExecute() {
        this.progressDialog.setMessage("Loading...");
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    @Override
    protected Integer doInBackground(String... params) {
        int results = 0;
        try {

            URL u = new URL(params[0]);

            File sdCard_dir = Environment.getExternalStorageDirectory();
            File file = new File(sdCard_dir.getAbsolutePath() + "/" + LazzyBeeShare.DB_UPDATE_NAME);
            Log.i(TAG, "db path:" + file.getAbsolutePath());
            //dlDir.mkdirs();
            InputStream is = u.openStream();

            DataInputStream dis = new DataInputStream(is);

            byte[] buffer = new byte[1024];
            int length;

            FileOutputStream fos = new FileOutputStream(file);
            while ((length = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            Log.e("Download file update:", "Complete");
            results = 1;
            _updateDB(LazzyBeeShare.DOWNLOAD_UPDATE);

        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
        return results;
    }

    private void _updateDB(int downloadUpdate) {
        try {
            //Copy db to my app
            databaseUpgrade.copyDataBase(downloadUpdate);

            List<Card> cards = databaseUpgrade._getAllCard();
            for (Card card : cards) {
                learnApiImplements._insertOrUpdateCard(card);
            }
            Log.i(TAG, "_insertOrUpdateCard:" + cards.size());
            //Update version
            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(version));
            databaseUpgrade.close();

            //Delete database update.db
            Log.i(TAG, "Delete database?" + context.deleteDatabase(DatabaseUpgrade.DB_NAME));

        } catch (Exception e) {
            // LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    @Override
    protected void onPostExecute(Integer aVoid) {
        super.onPostExecute(aVoid);
        try {
            if (aVoid == 1) {
                downloadFileDatabaseResponse.processFinish(aVoid);
            } else {
                Log.i(TAG, "dowload DB False:" + aVoid);
                Toast.makeText(context, R.string.message_download_database_fail, Toast.LENGTH_SHORT).show();
            }
            if (this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        } catch (Exception e) {
            String messageError = context.getString(R.string.an_error_occurred)
                    + "\t" + context.getClass().getName() + ":" + e.getMessage();
            Toast.makeText(context, messageError, Toast.LENGTH_SHORT).show();
            Log.e(TAG, messageError);
        }

    }


}

