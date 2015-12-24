package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.ZipManager;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

/**
 * Created by Hue on 12/16/2015.
 */
public class ExportDatabaseToCSV extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "ExportDatabaseToCSV";
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


    public ExportDatabaseToCSV(Context context, int type) {
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
        File file = new File(exportDir, ((type == 0) ? "Full_" : "")+(LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".csv");
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
                zipManager.zip(files, exportDir.getPath() + "/" + ((type == 0) ? "Full_" : "")+(LazzyBeeShare.getStartOfDayInMillis() / 1000) + ".zip");
                Log.d(TAG, "Delete file Csv:" + (file.delete() ? " Ok" : " Fails"));
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

}
