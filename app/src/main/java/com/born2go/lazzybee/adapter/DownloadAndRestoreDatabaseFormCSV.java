package com.born2go.lazzybee.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.DataServiceApi;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.DownloadTarget;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.ZipManager;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Hue on 12/17/2015.
 */
@SuppressLint("StaticFieldLeak")
public class DownloadAndRestoreDatabaseFormCSV extends AsyncTask<Void, Void, Integer> {
    private static final String TAG = DownloadAndRestoreDatabaseFormCSV.class.getSimpleName();
    final Context context;
    private final String code;
    private final String localPath;
    private final ProgressDialog dialog;
    final ZipManager zipManager;
    private final boolean debug;
    private final String backupFileName = "backup.zip";
    private String dotZip = ".zip";
    final String backupCVSFileName = "backup.csv";
    final File exportDir;

    public DownloadAndRestoreDatabaseFormCSV(Context context, boolean debug, String localPath, String code) {
        this.context = context;
        this.debug = debug;
        this.code = code;
        this.localPath = localPath;
        dialog = new ProgressDialog(context);
        zipManager = new ZipManager();
        exportDir = Environment.getExternalStorageDirectory();
        if (!exportDir.exists()) {
            boolean wasSuccessful = exportDir.mkdir();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }
        }
    }

    protected void onPreExecute() {
//        set up dialog
        this.dialog.setMessage(context.getString(R.string.msg_restore_data));
        this.dialog.show();
        this.dialog.setCancelable(false);
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int results = -2;
        if (LazzyBeeShare.checkConn(context)) {
            String pathFileRestore = debug ? localPath : _downloadFileRestoreDb();
            Log.d(TAG, "path:" + pathFileRestore);
            if (pathFileRestore == null) {
                results = _restoreDatabase("/sdcard/" + backupFileName);
            } else {
                results = -1;
            }
        }
        return results;
    }

    private String _downloadFileRestoreDb() {
//        String pathFileRestore = null;
//        try {
//            //get URL Download  by API
//            DataServiceApi.GetDownloadUrl getDownloadUrl = LazzyBeeSingleton.connectGdatabase.getDataServiceApi().getDownloadUrl(code);
//            DownloadTarget downloadTarget = getDownloadUrl.execute();
//            String downloadTargetUrl = downloadTarget.getUrl();
//            if (downloadTargetUrl != null) {
//                Log.d(TAG, "download file restore url:" + downloadTargetUrl);
//                URL u = new URL(downloadTargetUrl);
//                File file = new File(exportDir.getAbsolutePath() + "/" + backupFileName);
//                InputStream is = u.openStream();
//
//                DataInputStream dis = new DataInputStream(is);
//
//                byte[] buffer = new byte[1024];
//                int length;
//
//                FileOutputStream fos = new FileOutputStream(file);
//                while ((length = dis.read(buffer)) > 0) {
//                    fos.write(buffer, 0, length);
//                }
//                fos.close();
//                pathFileRestore = file.getPath();
//                Log.d(TAG, "Download file restore complete.Path:" + pathFileRestore);
//            } else {
//                Log.d(TAG, "Download file restore fails");
//            }
//        } catch (MalformedURLException mue) {
//            Log.e("SYNC getUpdate", "malformed url error", mue);
//            LazzyBeeSingleton.getCrashlytics().logException(mue);
//        } catch (IOException ioe) {
//            Log.e("SYNC getUpdate", "io error", ioe);
//            LazzyBeeSingleton.getCrashlytics().logException(ioe);
//        } catch (SecurityException se) {
//            Log.e("SYNC getUpdate", "security error", se);
//            LazzyBeeSingleton.getCrashlytics().logException(se);
//        } catch (Exception e) {
//            Log.e(TAG, "exception error", e);
//            LazzyBeeSingleton.getCrashlytics().logException(e);
//        }
//        return pathFileRestore;


        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            DataServiceApi.GetDownloadUrl getDownloadUrl = LazzyBeeSingleton.connectGdatabase.getDataServiceApi().getDownloadUrl(code);
            DownloadTarget downloadTarget = getDownloadUrl.execute();
            String downloadTargetUrl = downloadTarget.getUrl();
            Log.d(TAG, "url:" + downloadTargetUrl);
            URL url = new URL(downloadTargetUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

//            connection.setInstanceFollowRedirects(true);  //you still need to handle redirect manully.
//            HttpURLConnection.setFollowRedirects(true);

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
//            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
////                return "Server returned HTTP " + connection.getResponseCode()
////                        + " " + connection.getResponseMessage();
//            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream("/sdcard/" + backupFileName);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                // if (fileLength > 0) // only if total length is known
                // publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;

    }


    private int _restoreDatabase(String path) {
        int restore = 0;
        File zipfile = new File(path);
        String fileImport = exportDir.getPath() + "/";
        boolean unzip = zipManager.unzip(path, fileImport);
        if (unzip) {
            String wordFileName = "word.csv";
            String wordFilePath = exportDir.getPath() + "/" + wordFileName;
            String backupCVSFilePath = exportDir.getPath() + "/" + backupCVSFileName;
            String streakFileName = "streak.csv";
            String streakFilePath = exportDir.getPath() + "/" + streakFileName;
            // File backUpFileCsv = new File(backupCVSFilePath);
            File wordFileCsv = new File(wordFilePath);
            if (wordFileCsv.exists()) {
                restore = _restoreWordTableFormCSV(wordFilePath) + _restoreStreakTableFormCSV(streakFilePath);
            } else {
                Log.d(TAG, "File in path : " + backupCVSFilePath + " exists");
                wordFilePath = backupCVSFilePath;
                restore = _restoreWordTableFormCSV(wordFilePath);
            }
        } else {
            Log.d(TAG, "unzip file Fails");
        }
        Log.d(TAG, "Delete zip File:" + (zipfile.delete() ? " Ok" : " Fails"));

        return restore;

    }

    @SuppressWarnings("ConstantConditions")
    private int _restoreWordTableFormCSV(String wordFilePath) {
        int restore = 0;
        try {
            File fileCsv = new File(wordFilePath);
            if (fileCsv.exists()) {
                Log.d(TAG, "file Csv code:" + wordFilePath);
                FileReader fReader = new FileReader(wordFilePath);
                BufferedReader reader = new BufferedReader(fReader);
                String cvsSplitBy = ",\n";
                try {
                    StringBuilder all = new StringBuilder(LazzyBeeShare.EMPTY);
                    char[] buffer = new char[1024];
                    while (reader.read(buffer) > 0) {
                        String sBuffer = new String(buffer);
                        all.append(sBuffer);
                    }

                    //Split line
                    String[] allLine = all.toString().split(cvsSplitBy);
                    int length = allLine.length;
                    Log.d(TAG, "length:" + length);
                    int totalResults = 0;
                    for (int i = 0; i < length; i++) {
                        String[] sLine = allLine[i].split(",");
                        int slength = sLine.length;
                        // word.gid, word.queue, word.due,word.revCount, word.lastInterval, word.eFactor, userNote
                        long gId = 0L;
                        int factor = 0;
                        int last_ivl = 0;
                        int queue = 0;
                        int rev_count = 0;
                        int due = 0;
                        int level = -1;
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
                        if (slength > 7) {
                            if (sLine[(7)].length() > 0)
                                try {
                                    level = Integer.valueOf(sLine[(7)]);
                                } catch (Exception e) {
                                    Log.d(TAG, "Error7:" + e.getMessage());
                                }
                        }
                        int result = LazzyBeeSingleton.learnApiImplements._updateCardFormCSV(gId, queue, due, rev_count, last_ivl, factor, user_note);
                        Log.d(TAG, "gId:" + gId + ",-Update :" + ((result == 1) ? " OK" : " Fails"));
                        if (result != 1) {
                            if (level == 8 || level == -1) {
                                Voca voca = LazzyBeeSingleton.connectGdatabase._getGdatabase_byID(gId);//get voca by gID
                                if (voca != null) {
                                    Card _card = new Card();
                                    _card.setQueue(queue);
                                    _card.setDue(due);
                                    _card.setRev_count(rev_count);
                                    _card.setLast_ivl(last_ivl);

                                    _card.setFactor(factor);
                                    _card.setUser_note(user_note);

                                    Card card = defineCardbyVoca(_card, voca);
                                    Log.d(TAG, "-Insert card value:" + card.toString());
                                    LazzyBeeSingleton.learnApiImplements._insertOrUpdateCardbyGId(card);
                                    totalResults += 1;
                                }
                            }
                        } else {
                            totalResults += result;
                        }
                        restore = 1;

                    }
                    Log.d(TAG, "-Total row update ok:" + totalResults);
                } catch (Exception e) {
                    Log.d(TAG, "Error:" + e.getMessage());
                    e.printStackTrace();
                }
                Log.d(TAG, "Delete file Csv:" + (fileCsv.delete() ? " Ok" : " Fails"));

            } else {
                Log.d(TAG, "File in path : " + wordFilePath + " is not exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restore;
    }

    private int _restoreStreakTableFormCSV(String streakFilePath) {
        int restore = 0;
        try {
            Log.d(TAG, "Streak file path:" + streakFilePath);
            File fileCsv = new File(streakFilePath);
            if (fileCsv.exists()) {
                FileReader fReader = new FileReader(streakFilePath);
                CSVReader reader = new CSVReader(fReader);
                try {
                    String[] nextLine;
                    LazzyBeeSingleton.learnApiImplements.deteteStreak();
                    while ((nextLine = reader.readNext()) != null) {
                        int day = Integer.parseInt(nextLine[0]);
                        int results = LazzyBeeSingleton.learnApiImplements.restoreStreakDay(day);
                        Log.d(TAG, "Restore streak day =" + day + ((results > 0) ? " : Ok" : " : Fails"));
                    }
                    restore = 1;
                } catch (Exception e) {
                    Log.d(TAG, "Error:" + e.getMessage());
                    //noinspection AccessStaticViaInstance
                    LazzyBeeSingleton.getCrashlytics().logException(e);
                    e.printStackTrace();
                }
                Log.d(TAG, "Delete streak file Csv:" + (fileCsv.delete() ? " Ok" : " Fails"));

            } else {
                Log.d(TAG, "File in path : " + streakFilePath + " is not exists");
            }
        } catch (Exception e) {
            e.printStackTrace();
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(e);
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
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogLearnMore);
        builder.setTitle(R.string.setting_restore_database);
        String message;
        if (results >= 1) {
            message = context.getString(R.string.restore_database_sucessful);
        } else if (results == 0) {
            message = context.getString(R.string.restore_database_fails);
        } else if (results == -1) {
            message = context.getString(R.string.restore_database_wrong_code);
        } else if (results == -2) {
            message = context.getString(R.string.failed_to_connect_to_server_can_not_restore_database);
        } else {
            message = context.getString(R.string.restore_database_fails);
        }
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss());
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Card defineCardbyVoca(Card _card, Voca voca) {
        try {
            Card card = new Card();

            card.setgId(voca.getGid());
            card.setQuestion(voca.getQ());
            card.setAnswers(voca.getA());
            card.setPackage(voca.getPackages());
            card.setLevel(voca.getLevel());


            card.setLast_ivl(_card.getLast_ivl());
            card.setFactor(_card.getFactor());
            card.setRev_count(_card.getRev_count());
            card.setDue(_card.getDue());
            card.setQueue(_card.getQueue());

            card.setL_en(voca.getLEn());
            card.setL_vn(voca.getLVn());


            return card;
        } catch (Exception e) {
            Log.e(TAG, "Error getVoca:" + e.getMessage());
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(e);
            e.printStackTrace();
            return null;
        }
    }
}
