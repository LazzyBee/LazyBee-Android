package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Hue on 12/17/2015.
 */
public class ImportDatabaseFormCSV extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = "ImportDBFormCSV";
    private Context context;
    private String path;
    private ProgressDialog dialog;

    public ImportDatabaseFormCSV(Context context, String fpath) {
        this.context = context;
        this.path = fpath;
        dialog = new ProgressDialog(context);
    }

    protected void onPreExecute() {
//        set up dialog
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        boolean results = false;
        Log.d("", "path file select:" + path);
        try {
            File file = new File(path);
            if (file != null) {
                CSVReader reader = new CSVReader(new FileReader(file));
                // if the first line is the header
                String[] header = reader.readNext();
                // iterate over reader.readNext until it returns null
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
//                    Log.d(TAG, ("\t" + header[0] + ":" + "\t" + nextLine[0]) + "\n,"
//                            + ("\t" + header[1] + ":" + "\t" + nextLine[1]) + "\n,"
//                            + ("\t" + header[2] + ":" + "\t" + nextLine[2]) + "\n,"
//                            + ("\t" + header[3] + ":" + "\t" + nextLine[3]) + "\n,"
//                            + ("\t" + header[4] + ":" + "\t" + nextLine[4]) + "\n,"
//                            + ("\t" + header[5] + ":" + "\t" + nextLine[5]) + "\n,"
//                            + ("\t" + header[6] + ":" + "\t" + nextLine[6]));
                    if (nextLine[0] != null) {
                        if (nextLine[0].length() > 0) {
                            Card card = new Card();
                            card.setgId(Long.valueOf(nextLine[0]));
                            int factor = 0;
                            int last_ivl = 0;
                            int level = 0;
                            int queue = 0;
                            int rev_count = 0;
                            int due = 0;
                            if (nextLine[1] != null) {
                                factor = Integer.valueOf(nextLine[1]);
                            }
                            if (nextLine[2] != null) {
                                last_ivl = Integer.valueOf(nextLine[2]);
                            }
                            if (nextLine[3] != null) {
                                level = Integer.valueOf(nextLine[3]);
                            }
                            if (nextLine[4] != null) {
                                queue = Integer.valueOf(nextLine[4]);
                            }
                            if (nextLine[5] != null) {
                                rev_count = Integer.valueOf(nextLine[5]);
                            }
                            if (nextLine[6] != null) {
                                due = Integer.valueOf(nextLine[6]);
                            }
                            card.setFactor(factor);
                            card.setLast_ivl(last_ivl);
                            card.setLevel(level);
                            card.setRev_count(rev_count);
                            card.setQueue(queue);
                            card.setDue(due);
                            LazzyBeeSingleton.learnApiImplements._updateCardFormCSV(card);
                        }
                    }
                    results = true;
                }
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
