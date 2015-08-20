package com.born2go.lazzybee.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    private static final String TAG = "SettingActivity";
    CardView mCardViewLanguage;
    LearnApiImplements dataBaseHelper;

    DatabaseUpgrade databaseUpgrade;
    Switch mSwitchNotification;
    Switch mSwitchAutoCheckUpdate;
    Switch mSwitchDebugInformation;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        this.context = this;
        initSqlIte();
        initSettingView();
        initSetting();


    }

    private void initSettingView() {
        mCardViewLanguage = (CardView) findViewById(R.id.mCardViewLanguage);
        mSwitchNotification = (Switch) findViewById(R.id.mSwitchNotification);
        mSwitchAutoCheckUpdate = (Switch) findViewById(R.id.mSwitchAutoCheckUpdate);
        mSwitchDebugInformation = (Switch) findViewById(R.id.mSwitchDebugInformation);
    }

    private void initSqlIte() {
        dataBaseHelper = new LearnApiImplements(context);
        databaseUpgrade = new DatabaseUpgrade(context);
    }

    private void initSetting() {
        getAutoUpdateSetting();
        getDebugInforSetting();
        getNotificationSetting();
    }

    private void getNotificationSetting() {
        String value = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.NOTIFICTION_SETTING);
        if (value == null)
            mSwitchNotification.setChecked(false);
        else if (value.equals(LazzyBeeShare.ON))
            mSwitchNotification.setChecked(true);
        else if (value.equals(LazzyBeeShare.OFF))
            mSwitchNotification.setChecked(false);
        else
            mSwitchNotification.setChecked(false);
        mSwitchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value;
                if (isChecked) {
                    value = LazzyBeeShare.ON;
                } else {
                    value = LazzyBeeShare.OFF;
                }
                dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.NOTIFICTION_SETTING, value);
            }
        });
    }

    private void getDebugInforSetting() {
        String value = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.DEBUG_INFOR_SETTING);
        if (value == null)
            mSwitchDebugInformation.setChecked(false);
        else if (value.equals(LazzyBeeShare.ON))
            mSwitchDebugInformation.setChecked(true);
        else if (value.equals(LazzyBeeShare.OFF))
            mSwitchDebugInformation.setChecked(false);
        else
            mSwitchDebugInformation.setChecked(false);

        mSwitchDebugInformation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value;
                if (isChecked) {
                    value = LazzyBeeShare.ON;
                } else {
                    value = LazzyBeeShare.OFF;
                }
                dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.DEBUG_INFOR_SETTING, value);
            }
        });
    }

    private void getAutoUpdateSetting() {
        String value = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.AUTO_CHECK_UPDATE_SETTING);
        if (value == null)
            mSwitchAutoCheckUpdate.setChecked(false);
        else if (value.equals(LazzyBeeShare.ON))
            mSwitchAutoCheckUpdate.setChecked(true);
        else if (value.equals(LazzyBeeShare.OFF))
            mSwitchAutoCheckUpdate.setChecked(false);
        else
            mSwitchAutoCheckUpdate.setChecked(false);

        mSwitchAutoCheckUpdate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value;
                if (isChecked) {
                    value = LazzyBeeShare.ON;
                } else {
                    value = LazzyBeeShare.OFF;
                }
                dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.AUTO_CHECK_UPDATE_SETTING, value);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onCardViewLanguageClick(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogLearnMore));
        builder.setTitle(getString(R.string.change_language));
        final CharSequence[] items = {getString(R.string.lang_english), getString(R.string.lang_viet)};
        int index = 0;
        String lang = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.KEY_LANGUAGE);

        if (lang != null) {
            if (lang.equals(LazzyBeeShare.LANG_VI)) {
                index = 1;
                Log.i(TAG, "lang:" + lang + ",index:" + index);
            }

        } else {
            Log.i(TAG, "lang null index:" + index);
        }


        builder.setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                if (items[item] == getString(R.string.lang_english)) {
                    Log.i(TAG, getString(R.string.lang_english) + " click");
                    dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_LANGUAGE, LazzyBeeShare.LANG_EN);
                    _showDialogConfirmRestartApp();
                } else if (items[item] == getString(R.string.lang_viet)) {
                    Log.i(TAG, getString(R.string.lang_viet) + " click");
                    dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_LANGUAGE, LazzyBeeShare.LANG_VI);
                    _showDialogConfirmRestartApp();
                }
                dialog.cancel();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void onCardViewNotificationClick(View view) {
        String value;
        if (!mSwitchNotification.isChecked()) {
            mSwitchNotification.setChecked(true);
            value = LazzyBeeShare.ON;
        } else {
            mSwitchNotification.setChecked(false);
            value = LazzyBeeShare.OFF;
        }
        dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.NOTIFICTION_SETTING, value);
    }

    public void onCardViewAutoCheckUpdateClick(View view) {
        String value;
        if (!mSwitchAutoCheckUpdate.isChecked()) {
            mSwitchAutoCheckUpdate.setChecked(true);
            value = LazzyBeeShare.ON;
        } else {
            mSwitchAutoCheckUpdate.setChecked(false);
            value = LazzyBeeShare.OFF;
        }
        dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.AUTO_CHECK_UPDATE_SETTING, value);
    }

    public void onCardViewDebugInformationClick(View view) {
        String value;
        if (!mSwitchDebugInformation.isChecked()) {
            mSwitchDebugInformation.setChecked(true);
            value = LazzyBeeShare.ON;
        } else {
            mSwitchDebugInformation.setChecked(false);
            value = LazzyBeeShare.OFF;
        }
        dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.DEBUG_INFOR_SETTING, value);
    }

    public void onCardViewCheckUpdateClick(View view) {
        _checkUpdate();
    }

    public void onCardViewTodayNewCardLimitClick(View view) {
        String limit = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.SETTING_TODAY_NEW_CARD_LIMIT);
        int value;
        if (limit == null) {
            value = LazzyBeeShare.MAX_LEARN_MORE_PER_DAY;
        } else {
            value = Integer.valueOf(limit);
        }
        _showDialogConfirmSetLimitCard(value, LazzyBeeShare.SETTING_TODAY_NEW_CARD_LIMIT);

    }


    public void onCardViewTodayReviewCardLimitClick(View view) {
        String limit = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.SETTING_TODAY_REVIEW_CARD_LIMIT);
        int value;
        if (limit == null) {
            value = LazzyBeeShare.MAX_REVIEW_LEARN_PER_DAY;
        } else {
            value = Integer.valueOf(limit);
        }
        _showDialogConfirmSetLimitCard(value, LazzyBeeShare.SETTING_TODAY_REVIEW_CARD_LIMIT);
    }

    public void onCardViewTotalCardLearnPreDayClick(View view) {
        String limit = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.SETTING_TOTAL_CARD_LEARN_PRE_DAY);
        int value;
        if (limit == null) {
            value = LazzyBeeShare.TOTAL_LEAN_PER_DAY;
        } else {
            value = Integer.valueOf(limit);
        }
        _showDialogConfirmSetLimitCard(value, LazzyBeeShare.SETTING_TOTAL_CARD_LEARN_PRE_DAY);
    }

    private void _showDialogConfirmSetLimitCard(int value, final String todayNewCardLimit) {
        // Instantiate an AlertDialog.Builder with its constructor
        String title = LazzyBeeShare.EMPTY;
        String message = LazzyBeeShare.EMPTY;
        View viewDialog = View.inflate(context, R.layout.dialog_limit_card, null);
        TextView lbSettingLimitName = (TextView) viewDialog.findViewById(R.id.lbSettingLimitName);
        final EditText txtLimit = (EditText) viewDialog.findViewById(R.id.txtLimit);

        if (todayNewCardLimit == LazzyBeeShare.SETTING_TODAY_NEW_CARD_LIMIT) {
            message = getString(R.string.dialog_message_setting_today_new_card_limit_by);
        } else if (todayNewCardLimit == LazzyBeeShare.SETTING_TODAY_REVIEW_CARD_LIMIT) {
            message = getString(R.string.dialog_message_setting_today_review_card_limit_by);
        } else if (todayNewCardLimit == LazzyBeeShare.SETTING_TOTAL_CARD_LEARN_PRE_DAY) {
            message = getString(R.string.dialog_message_setting_total_card_learn_pre_day_by);
        }

        lbSettingLimitName.setText(message);
        txtLimit.setText(String.valueOf(value));

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        // builder.setMessage(R.string.dialog_message_setting_today_new_card_limit_by);
        builder.setView(viewDialog);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        final String finalMessage = message;
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String limit = txtLimit.getText().toString();
                Log.i(TAG, finalMessage +limit);
                dataBaseHelper._insertOrUpdateToSystemTable(todayNewCardLimit,limit);
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private int initViewDialog(int value, String todayNewCardLimit) {
        return 0;
    }

    private void _showDialogConfirmRestartApp() {

        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message_restart_app)
                .setTitle(R.string.dialog_title_restart_app);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //restart app
                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }


    private void _checkUpdate() {
        //Check vesion form server
        String db_v = dataBaseHelper._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
        int update_local_version = databaseUpgrade._getVersionDB();
        int _clientVesion;
        if (db_v == null) {
            _clientVesion = 0;
        } else {
            _clientVesion = Integer.valueOf(db_v);
        }

        if (_clientVesion == 0) {
            if (update_local_version == -1) {
                Log.i(TAG, "_checkUpdate():update_local_version == -1");
                _showComfirmUpdateDatabase(LazzyBeeShare.NO_DOWNLOAD_UPDATE);
            }
        } else {
            if (update_local_version > _clientVesion) {
                Log.i(TAG, "_checkUpdate():update_local_version > _clientVesion");
                _showComfirmUpdateDatabase(LazzyBeeShare.NO_DOWNLOAD_UPDATE);
            } else if (LazzyBeeShare.VERSION_SERVER > _clientVesion) {
                Log.i(TAG, "_checkUpdate():LazzyBeeShare.VERSION_SERVER > _clientVesion");
                _showComfirmUpdateDatabase(LazzyBeeShare.DOWNLOAD_UPDATE);
            } else {
                Log.i(TAG, "_checkUpdate():" + R.string.updated);
                Toast.makeText(context, R.string.updated, Toast.LENGTH_SHORT).show();
            }

        }


    }

    private void _showComfirmUpdateDatabase(final int type) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_update)
                .setTitle(R.string.dialog_title_update);

        // Add the buttons
        builder.setPositiveButton(R.string.btn_update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked Update button
                //1.Download file from server
                //2.Open database
                //3.Upgade to my database
                //4.Remove file update
                if (type == LazzyBeeShare.DOWNLOAD_UPDATE) {
                    _downloadFile();
                } else {
                    _updateDB(type);
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void _updateDB(int type) {
        try {

            databaseUpgrade.copyDataBase(type);
            List<Card> cards = databaseUpgrade._getAllCard();
            for (Card card : cards) {
                dataBaseHelper._insertOrUpdateCard(card);
            }
            dataBaseHelper._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(databaseUpgrade._getVersionDB()));
            databaseUpgrade.close();
        } catch (Exception e) {
            Log.e(TAG, "Update DB Error:" + e.getMessage());
            e.printStackTrace();
        }


    }

    private void _downloadFile() {


        DownloadFileUpdateDatabaseTask downloadFileUpdateDatabaseTask = new DownloadFileUpdateDatabaseTask(context);
        downloadFileUpdateDatabaseTask.execute(LazzyBeeShare.URL_DATABASE_UPDATE);
    }

    class DownloadFileUpdateDatabaseTask extends AsyncTask<String, Void, Void> {
        Context context;

        public DownloadFileUpdateDatabaseTask(Context context) {
            this.context = context;

        }

        @Override
        protected Void doInBackground(String... params) {
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
                _updateDB(LazzyBeeShare.DOWNLOAD_UPDATE);
            } catch (MalformedURLException mue) {
                Log.e("SYNC getUpdate", "malformed url error", mue);
            } catch (IOException ioe) {
                Log.e("SYNC getUpdate", "io error", ioe);
            } catch (SecurityException se) {
                Log.e("SYNC getUpdate", "security error", se);
            }
            return null;
        }
    }


}
