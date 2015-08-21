package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Hue on 8/21/2015.
 */

public class RecyclerViewSettingListAdapter extends RecyclerView.Adapter<RecyclerViewSettingListAdapter.RecyclerViewSettingListAdapterViewHolder> {
    private static final String TAG = "SettingListAdapter";
    Context context;
    List<String> settings;
    LearnApiImplements learnApiImplements;
    DatabaseUpgrade databaseUpgrade;
    int TYPE_TITLE = 0;
    int TYPE_SETTING_NAME = 1;
    int TYPE_SETTING_SWITCH = 2;
    int TYPE_LINE = -1;

    public RecyclerViewSettingListAdapter(Context context, List<String> settings) {
        this.context = context;
        this.settings = settings;
        this.learnApiImplements = new LearnApiImplements(context);
        this.databaseUpgrade = new DatabaseUpgrade(context);
    }

    @Override
    public RecyclerViewSettingListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_TITLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false); //Inflating the layout
        } else if (viewType == TYPE_SETTING_NAME) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false); //Inflating the layout
        } else if (viewType == TYPE_SETTING_SWITCH) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false); //Inflating the layout
        } else if (viewType == TYPE_LINE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_setting_lines, parent, false); //Inflating the layout
        }
        RecyclerViewSettingListAdapterViewHolder recyclerViewSettingListAdapterViewHolder = new RecyclerViewSettingListAdapterViewHolder(view, viewType);
        return recyclerViewSettingListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewSettingListAdapterViewHolder holder, int position) {

        View view = holder.view;
        CardView mCardView = (CardView) view.findViewById(R.id.mCardView);
        TextView lbSettingName = (TextView) view.findViewById(R.id.lbSettingName);
        String setting = settings.get(position);
        final Switch mSwitch = (Switch) view.findViewById(R.id.mSwitch);
        if (holder.viewType == TYPE_TITLE) {
            lbSettingName.setText(settings.get(position));
            mSwitch.setVisibility(View.GONE);
            lbSettingName.setTextSize(15f);
            lbSettingName.setTextColor(context.getResources().getColor(R.color.teal_200));
        } else if (holder.viewType == TYPE_SETTING_NAME) {
            lbSettingName.setText(settings.get(position));
            mSwitch.setVisibility(View.GONE);
            if (setting.equals(context.getString(R.string.setting_today_new_card_limit))) {
                getSettingLimitOrUpdate(mCardView, LazzyBeeShare.SETTING_TODAY_NEW_CARD_LIMIT);

            } else if (setting.equals(context.getString(R.string.setting_today_review_card_limit))) {
                getSettingLimitOrUpdate(mCardView, LazzyBeeShare.SETTING_TODAY_REVIEW_CARD_LIMIT);

            } else if (setting.equals(context.getString(R.string.setting_total_learn_per_day))) {
                getSettingLimitOrUpdate(mCardView, LazzyBeeShare.SETTING_TOTAL_CARD_LEARN_PRE_DAY);

            } else if (setting.equals(context.getString(R.string.setting_check_update))) {
                _checkUpdate(mCardView);
            } else if (setting.equals(context.getString(R.string.setting_language))) {
                changeLanguage(mCardView);
            }
        } else if (holder.viewType == TYPE_SETTING_SWITCH) {
            lbSettingName.setText(settings.get(position));
            if (setting.equals(context.getString(R.string.setting_auto_check_update))) {
                getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.AUTO_CHECK_UPDATE_SETTING);
            } else if (setting.equals(context.getString(R.string.setting_debug_info))) {
                getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.DEBUG_INFOR_SETTING);
            } else if (setting.equals(context.getString(R.string.setting_notification))) {
                getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.NOTIFICTION_SETTING);
            }
        }
    }

    private void getSettingLimitOrUpdate(CardView mCardView, final String key) {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String limit = learnApiImplements._getValueFromSystemByKey(key);
                int value;
                if (limit == null) {
                    value = LazzyBeeShare.MAX_LEARN_MORE_PER_DAY;
                } else {
                    value = Integer.valueOf(limit);
                }
                _showDialogConfirmSetLimitCard(value, LazzyBeeShare.SETTING_TODAY_NEW_CARD_LIMIT);
            }
        });

    }

    private void _showDialogConfirmSetLimitCard(int value, final String todayNewCardLimit) {
        // Instantiate an AlertDialog.Builder with its constructor
        String title = LazzyBeeShare.EMPTY;
        String message = LazzyBeeShare.EMPTY;
        View viewDialog = View.inflate(context, R.layout.dialog_limit_card, null);
        TextView lbSettingLimitName = (TextView) viewDialog.findViewById(R.id.lbSettingLimitName);
        final EditText txtLimit = (EditText) viewDialog.findViewById(R.id.txtLimit);

        if (todayNewCardLimit == LazzyBeeShare.SETTING_TODAY_NEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_new_card_limit_by);
        } else if (todayNewCardLimit == LazzyBeeShare.SETTING_TODAY_REVIEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_review_card_limit_by);
        } else if (todayNewCardLimit == LazzyBeeShare.SETTING_TOTAL_CARD_LEARN_PRE_DAY) {
            message = context.getString(R.string.dialog_message_setting_total_card_learn_pre_day_by);
        }

        lbSettingLimitName.setText(message);
        txtLimit.setText(String.valueOf(value));

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

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
                //Log.i(TAG, finalMessage + limit);
                learnApiImplements._insertOrUpdateToSystemTable(todayNewCardLimit, limit);
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void changeLanguage(CardView mCardView) {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
                builder.setTitle(context.getString(R.string.change_language));
                final CharSequence[] items = {context.getString(R.string.lang_english), context.getString(R.string.lang_viet)};
                int index = 0;
                String lang = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_LANGUAGE);

                if (lang != null) {
                    if (lang.equals(LazzyBeeShare.LANG_VI)) {
                        index = 1;
                        //Log.i(TAG, "lang:" + lang + ",index:" + index);
                    }

                } else {
                    //Log.i(TAG, "lang null index:" + index);
                }


                builder.setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        if (items[item] == context.getString(R.string.lang_english)) {
                            //Log.i(TAG, getString(R.string.lang_english) + " click");
                            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_LANGUAGE, LazzyBeeShare.LANG_EN);
                            _showDialogConfirmRestartApp();
                        } else if (items[item] == context.getString(R.string.lang_viet)) {
                            //Log.i(TAG, getString(R.string.lang_viet) + " click");
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
        });

    }

    private void _showDialogConfirmRestartApp() {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

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
                Intent i = context.getPackageManager()
                        .getLaunchIntentForPackage(context.getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }


    private void getSettingAndUpdateWithSwitch(CardView mCardView, final String key) {
        final Switch mSwitch = (Switch) mCardView.findViewById(R.id.mSwitch);
        String value = learnApiImplements._getValueFromSystemByKey(key);
        if (value == null)
            mSwitch.setChecked(false);
        else if (value.equals(LazzyBeeShare.ON))
            mSwitch.setChecked(true);
        else if (value.equals(LazzyBeeShare.OFF))
            mSwitch.setChecked(false);
        else {
            mSwitch.setChecked(false);
        }
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String value;
                if (isChecked) {
                    value = LazzyBeeShare.ON;
                } else {
                    value = LazzyBeeShare.OFF;
                }
                learnApiImplements._insertOrUpdateToSystemTable(key, value);
            }
        });
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, R.string.setting_auto_check_update, Toast.LENGTH_SHORT).show();
                String value;
                if (!mSwitch.isChecked()) {
                    mSwitch.setChecked(true);
                    value = LazzyBeeShare.ON;
                } else {
                    mSwitch.setChecked(false);
                    value = LazzyBeeShare.OFF;
                }
                learnApiImplements._insertOrUpdateToSystemTable(key, value);
            }
        });
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }

    @Override
    public int getItemViewType(int position) {
        String setting = settings.get(position);
        if (setting.equals(context.getString(R.string.setting_learn_title))
                || setting.equals(context.getString(R.string.setting_update_title)))
            return TYPE_TITLE;
        else if (setting.equals(context.getString(R.string.setting_today_review_card_limit))
                || setting.equals(context.getString(R.string.setting_today_new_card_limit))
                || setting.equals(context.getString(R.string.setting_total_learn_per_day))
                || setting.equals(context.getString(R.string.setting_language))
                || setting.equals(context.getString(R.string.setting_check_update)))
            return TYPE_SETTING_NAME;
        else if (setting.equals(context.getString(R.string.setting_notification))
                || setting.equals(context.getString(R.string.setting_auto_check_update))
                || setting.equals(context.getString(R.string.setting_debug_info)))
            return TYPE_SETTING_SWITCH;
        else
            return -1;
    }

    public class RecyclerViewSettingListAdapterViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private int viewType;

        public RecyclerViewSettingListAdapterViewHolder(View itemView, int viewType) {
            super(itemView);
            this.view = itemView;
            this.viewType = viewType;
        }
    }

    private void _checkUpdate(CardView mCardView) {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check vesion form server
                String db_v = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
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
        });
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

    private void _downloadFile() {
        DownloadFileUpdateDatabaseTask downloadFileUpdateDatabaseTask = new DownloadFileUpdateDatabaseTask(context);
        downloadFileUpdateDatabaseTask.execute(LazzyBeeShare.URL_DATABASE_UPDATE);
    }

    private void _updateDB(int type) {
        try {

            databaseUpgrade.copyDataBase(type);
            List<Card> cards = databaseUpgrade._getAllCard();
            for (Card card : cards) {
                learnApiImplements._insertOrUpdateCard(card);
            }
            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(databaseUpgrade._getVersionDB()));
            databaseUpgrade.close();
        } catch (Exception e) {
            Log.e(TAG, "Update DB Error:" + e.getMessage());
            e.printStackTrace();
        }


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
