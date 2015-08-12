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

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.shared.LazzyBeeShare;

public class SettingActivity extends ActionBarActivity {

    private static final String TAG = "SettingActivity";
    CardView mCardViewLanguage;
    LearnApiImplements learnApiImplements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        learnApiImplements = new LearnApiImplements(this);
        mCardViewLanguage = (CardView) findViewById(R.id.mCardViewLanguage);


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
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                String action = LazzyBeeShare.EMPTY;
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
