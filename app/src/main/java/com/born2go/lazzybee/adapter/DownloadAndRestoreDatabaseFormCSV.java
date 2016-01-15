package com.born2go.lazzybee.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.DownloadTarget;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.ZipManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Hue on 12/17/2015.
 */
public class DownloadAndRestoreDatabaseFormCSV extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = "DRDatabaseFormCSV";
    private Context context;
    private String code;
    private ProgressDialog dialog;
    ZipManager zipManager;

    public DownloadAndRestoreDatabaseFormCSV(Context context, String fpath) {
        this.context = context;
        this.code = fpath;
        dialog = new ProgressDialog(context);
        zipManager = new ZipManager();
    }

    protected void onPreExecute() {
//        set up dialog
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int results = 0;
        if (LazzyBeeShare.checkConn(context)) {
            String pathFileRestore = _downloadFileRestoreDb();
            if (pathFileRestore != null) {
                results = _restoreDatabase(pathFileRestore);
            } else {
                results = -1;
            }
        }
        return results;
    }

    private String _downloadFileRestoreDb() {
        String pathFileRestore = null;
        try {
            //get URL Download  by API
            DataServiceApi.GetDownloadUrl getDownloadUrl = LazzyBeeSingleton.connectGdatabase.getDataServiceApi().getDownloadUrl(code);
            DownloadTarget downloadTarget = getDownloadUrl.execute();
            String downloadTargetUrl = downloadTarget.getUrl();
            if (downloadTargetUrl != null) {
                Log.d(TAG, "download file restore url:" + downloadTargetUrl);
                URL u = new URL(downloadTargetUrl);
                File sdCard_dir = Environment.getExternalStorageDirectory();
                File file = new File(sdCard_dir.getAbsolutePath() + "/backup.zip");
                InputStream is = u.openStream();

                DataInputStream dis = new DataInputStream(is);

                byte[] buffer = new byte[1024];
                int length;

                FileOutputStream fos = new FileOutputStream(file);
                while ((length = dis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
                pathFileRestore = file.getPath();
                Log.d(TAG, "Download file restore complete.Path:" + pathFileRestore);
            } else {
                Log.d(TAG, "Download file restore fails");
            }
        } catch (MalformedURLException mue) {
            Log.e("SYNC getUpdate", "malformed url error", mue);
        } catch (IOException ioe) {
            Log.e("SYNC getUpdate", "io error", ioe);
        } catch (SecurityException se) {
            Log.e("SYNC getUpdate", "security error", se);
        } catch (Exception e) {
            Log.e(TAG, "exception error", e);
        }
        return pathFileRestore;
    }

    private int _restoreDatabase(String path) {
        int restore = 0;
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File zipfile = new File(path);
            String fileImport = exportDir.getPath() + "/";
            String fileCsvName = exportDir.getPath() + "/backup.csv";
            boolean unzip = zipManager.unzip(path, fileImport);
            if (unzip) {
                File fileCsv = new File(fileCsvName);
                if (fileCsv != null) {
                    Log.d(TAG, "file Csv code:" + fileCsvName);
                    FileReader fReader = new FileReader(fileCsv);
                    BufferedReader reader = new BufferedReader(fReader);
                    String cvsSplitBy = ",\n";
                    try {
                        String all = LazzyBeeShare.EMPTY;
                        char[] buffer = new char[1024];
                        while (reader.read(buffer) > 0) {
                            String sBuffer = new String(buffer);
                            all += sBuffer;
                        }

                        //Split line
                        String[] allLine = all.split(cvsSplitBy);
                        int length = allLine.length;
                        Log.d(TAG, "length:" + length);
                        int totalResults = 0;
                        for (int i = 0; i < length; i++) {
                            String[] sLine = allLine[i].split(",");
                            int slength = sLine.length;
                            // word.gid, word.queue, word.due,word.revCount, word.lastInterval, word.eFactor, userNote
                            long gId = 0l;
                            int factor = 0;
                            int last_ivl = 0;
                            int queue = 0;
                            int rev_count = 0;
                            int due = 0;
                            String user_note = LazzyBeeShare.EMPTY;
//                            // word.gid, word.queue, word.due,word.revCount, word.lastInterval, word.eFactor, userNote
                            if (sLine[0] != null) {
                                try {
                                    gId = Long.valueOf(sLine[(0)]);
                                } catch (Exception e) {
                                    Log.d(TAG, "gId not Long:" + sLine[(0)]);
                                }
                            }
                            if (slength > 1) {
                                if (sLine[(1)] != null) {
                                    if (sLine[(1)].length() > 0) {
                                        try {
                                            queue = Integer.valueOf(sLine[(1)]);
                                        } catch (Exception e) {
                                            Log.d(TAG, "Error1:" + e.getMessage());
                                        }
                                    }
                                }
                            }
                            if (slength > 2) {
                                if (sLine[2] != null) {
                                    if (sLine[(2)].length() > 0)
                                        try {
                                            due = Integer.valueOf(sLine[(2)]);
                                        } catch (Exception e) {
                                            Log.d(TAG, "Error2:" + e.getMessage());
                                        }
                                }
                            }
                            if (slength > 3) {
                                if (sLine[(3)] != null) {
                                    if (sLine[(3)].length() > 0)
                                        try {
                                            rev_count = Integer.valueOf(sLine[(3)]);
                                        } catch (Exception e) {
                                            Log.d(TAG, "Error3:" + e.getMessage());
                                        }
                                }
                            }
                            if (slength > 4) {
                                if (sLine[(4)] != null) {
                                    if (sLine[(4)].length() > 0)
                                        try {
                                            last_ivl = Integer.valueOf(sLine[(4)]);
                                        } catch (Exception e) {
                                            Log.d(TAG, "Error4:" + e.getMessage());
                                        }
                                }
                            }

                            if (slength > 5) {
                                if (sLine[(5)].length() > 0) {
                                    try {
                                        factor = Integer.valueOf(sLine[(5)]);
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error5:" + e.getMessage());
                                    }
                                }
                            }
                            if (slength > 6) {
                                if (sLine[(6)].length() > 0)
                                    try {
                                        user_note = String.valueOf(sLine[(6)]);
                                        if (user_note.contains("*#*")) {
                                            user_note = user_note.replace("*#*", ",");
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error6:" + e.getMessage());
                                    }
                            }
                            int result = LazzyBeeSingleton.learnApiImplements._updateCardFormCSV(gId, queue, due, rev_count, last_ivl, factor, user_note);
                            Log.d(TAG, "gId:" + gId
                                    + ",queue:" + queue
                                    + ",due:" + due
                                    + ",rev_count:" + rev_count
                                    + ",last_ivl:" + last_ivl
                                    + ",factor:" + factor
                                    + ",user_note:" + user_note
                                    + ",Update :" + ((result == 1) ? " OK" : " Fails"));
                            restore = 1;
                            totalResults += result;
                        }
                        Log.d(TAG, "_update Ok:" + totalResults);
                    } catch (Exception e) {
                        Log.d(TAG, "Error:" + e.getMessage());
                        e.printStackTrace();
                    }
                    Log.d(TAG, "Delete file Csv:" + (fileCsv.delete() ? " Ok" : " Fails"));

                } else {
                    Log.d(TAG, "file csv Null");
                }
            } else {
                Log.d(TAG, "unzip file Fails");
            }
            Log.d(TAG, "Delete zip File:" + (zipfile.delete() ? " Ok" : " Fails"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return restore;

    }

    @Override
    protected void onPostExecute(Integer results) {
        super.onPostExecute(results);
        //Dismis dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
        builder.setTitle(R.string.setting_restore_database);
        String message;
        if (results == 1) {
            message = context.getString(R.string.restore_database_sucessful);
        } else if (results == 0) {
            message = context.getString(R.string.restore_database_fails);
        } else if (results == -1) {
            message = context.getString(R.string.restore_database_wrong_code);
        } else {
            message = context.getString(R.string.restore_database_fails);
        }
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}
