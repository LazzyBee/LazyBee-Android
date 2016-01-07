package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.ZipManager;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.CharBuffer;

/**
 * Created by Hue on 12/17/2015.
 */
public class ImportDatabaseFormCSV extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "ImportDBFormCSV";
    private Context context;
    private String path;
    private ProgressDialog dialog;
    ZipManager zipManager;

    public ImportDatabaseFormCSV(Context context, String fpath) {
        this.context = context;
        this.path = fpath;
        dialog = new ProgressDialog(context);
        zipManager = new ZipManager();
    }

    protected void onPreExecute() {
//        set up dialog
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean results = false;
        Log.d(TAG, "path file select:" + path);
        try {
            File exportDir = new File(Environment.getExternalStorageDirectory(), "");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(path);
            String fileImport = exportDir.getPath() + "/";
            String fileCsvName = exportDir.getPath() + "/" + (file.getName().split("zip")[0]) + "csv";
            boolean unzip = zipManager.unzip(path, fileImport);
            if (unzip) {
                File fileCsv = new File(fileCsvName);
                if (fileCsv != null) {
                    Log.d(TAG, "file Csv path:" + fileCsvName);
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

                            if (sLine[(1)] != null) {
                                if (sLine[(1)].length() > 0) {
                                    try {
                                        queue = Integer.valueOf(sLine[(1)]);
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error1:" + e.getMessage());
                                    }
                                }
                            }


                            if (sLine[2] != null) {
                                if (sLine[(2)].length() > 0)
                                    try {
                                        due = Integer.valueOf(sLine[(2)]);
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error2:" + e.getMessage());
                                    }
                            }

                            if (sLine[(3)] != null) {
                                if (sLine[(3)].length() > 0)
                                    try {
                                        rev_count = Integer.valueOf(sLine[(3)]);
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error3:" + e.getMessage());
                                    }
                            }
                            if (sLine[(4)] != null) {
                                if (sLine[(4)].length() > 0)
                                    try {
                                        last_ivl = Integer.valueOf(sLine[(4)]);
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error4:" + e.getMessage());
                                    }
                            }
                            if (slength > 6) {
                                if (sLine[(5)].length() > 0)
                                    try {
                                        factor = Integer.valueOf(sLine[(5)]);
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error5:" + e.getMessage());
                                    }

                                if (sLine[(6)].length() > 0)
                                    try {
                                        user_note = String.valueOf(sLine[(6)]);
                                        if (user_note.contains("*#*")) {
                                            user_note = user_note.replace("*#*", ",");
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, "Error6:" + e.getMessage());
                                    }
                            } else {
                                Log.d(TAG, "User note empty");
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
                            results = true;
                            totalResults += result;
                        }
                        Log.d(TAG, "_update Ok:" + totalResults);
                    } catch (Exception e) {
                        Log.d(TAG, "Error:" + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "file csv Null");
                }
            } else {
                Log.d(TAG, "unzip file Fails");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        Toast.makeText(context, "Import  to CSV " + (results ? "Ok" : "Fails"), Toast.LENGTH_SHORT).show();

    }
}
