package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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
            File file = new File(sdCard_dir.getAbsolutePath() + "/" + LazzyBeeShare.DOWNLOAD + "/" + LazzyBeeShare.DB_UPDATE_NAME);
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
        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        }
        if (results == 1)
            _updateDB(LazzyBeeShare.DOWNLOAD_UPDATE);
        return results;
    }

    private void _updateDB(int downloadUpdate) {
        try {
            //Add 2 colum l_en and l_vn
            int add_colum_L_EN = learnApiImplements.executeQuery("ALTER TABLE " + LearnApiImplements.TABLE_VOCABULARY + " ADD COLUMN " + LearnApiImplements.KEY_L_EN + " TEXT;");
            int add_colum_L_VN = learnApiImplements.executeQuery("ALTER TABLE " + LearnApiImplements.TABLE_VOCABULARY + " ADD COLUMN " + LearnApiImplements.KEY_L_VN + " TEXT;");
            Log.i(TAG, " ADD COLUMN " + LearnApiImplements.KEY_L_EN + "?" + ((add_colum_L_EN == 1) ? "TRUE" : "FALSE"));
            Log.i(TAG, " ADD COLUMN " + LearnApiImplements.KEY_L_VN + "?" + ((add_colum_L_VN == 1) ? "TRUE" : "FALSE"));

            //Copy db to my app
            databaseUpgrade.copyDataBase(downloadUpdate);

            List<Card> cards = databaseUpgrade._getAllCard();
            for (Card card : cards) {
                learnApiImplements._insertOrUpdateCard(card);
            }
            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(version));
            databaseUpgrade.close();
        } catch (Exception e) {
            Log.e(TAG, "Update DB Error:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Integer aVoid) {
        super.onPostExecute(aVoid);
        if (this.progressDialog.isShowing()) {
            this.progressDialog.dismiss();
        }
        downloadFileDatabaseResponse.processFinish(aVoid);
    }


}

