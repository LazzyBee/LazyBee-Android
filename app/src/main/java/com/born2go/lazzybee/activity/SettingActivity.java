package com.born2go.lazzybee.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.shared.LazzyBeeShare;

public class SettingActivity extends ActionBarActivity {

    private static final String TAG = "SettingActivity";
    CardView mCardViewLanguage;
    LearnApiImplements learnApiImplements;
    Switch mSwitchNotification;
    Switch mSwitchAutoCheckUpdate;
    Switch mSwitchDebugInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        learnApiImplements = new LearnApiImplements(this);
        mCardViewLanguage = (CardView) findViewById(R.id.mCardViewLanguage);
        mSwitchNotification = (Switch) findViewById(R.id.mSwitchNotification);
        mSwitchAutoCheckUpdate = (Switch) findViewById(R.id.mSwitchAutoCheckUpdate);
        mSwitchDebugInformation = (Switch) findViewById(R.id.mSwitchDebugInformation);
        initSetting();


    }

    private void initSetting() {
        getAutoUpdateSetting();
        getDebugInforSetting();
        getNotificationSetting();
    }

    private void getNotificationSetting() {
        String value = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.NOTIFICTION_SETTING);
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
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.NOTIFICTION_SETTING, value);
            }
        });
    }

    private void getDebugInforSetting() {
        String value = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.DEBUG_INFOR_SETTING);
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
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DEBUG_INFOR_SETTING, value);
            }
        });
    }

    private void getAutoUpdateSetting() {
        String value = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.AUTO_CHECK_UPDATE_SETTING);
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
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.AUTO_CHECK_UPDATE_SETTING, value);
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
        String lang = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_LANGUAGE);

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
                    learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_LANGUAGE, LazzyBeeShare.LANG_EN);
                    _showDialogConfirmRestartApp();
                } else if (items[item] == getString(R.string.lang_viet)) {
                    Log.i(TAG, getString(R.string.lang_viet) + " click");
                    learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_LANGUAGE, LazzyBeeShare.LANG_VI);
                    _showDialogConfirmRestartApp();
                }
                dialog.cancel();
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
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


}
