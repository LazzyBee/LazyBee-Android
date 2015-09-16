package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
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
 * Created by Hue on 8/21/2015.
 */

public class RecyclerViewSettingListAdapter extends RecyclerView.Adapter<RecyclerViewSettingListAdapter.RecyclerViewSettingListAdapterViewHolder> {
    private static final String TAG = "SettingListAdapter";
    Context context;
    List<String> settings;
    LearnApiImplements learnApiImplements;
    DatabaseUpgrade databaseUpgrade;
    int TYPE_LINE = -1;
    int TYPE_TITLE = 0;
    int TYPE_SETTING_NAME = 1;
    int TYPE_SETTING_SWITCH = 2;
    int TYPE_SETTING_NAME_WITH_DESCRIPTION = 3;
    private static final int TYPE_SETTING_SPEECH_RATE_SLIDE = 4;
    private static final int TYPE_SETTING_ABOUT = 5;

    private RecyclerView mRecyclerViewSettings;

    public RecyclerViewSettingListAdapter(Context context, List<String> settings, RecyclerView mRecyclerViewSettings) {
        this.context = context;
        this.settings = settings;
        this.learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        this.databaseUpgrade = LazzyBeeSingleton.databaseUpgrade;
        this.mRecyclerViewSettings = mRecyclerViewSettings;
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
        } else if (viewType == TYPE_SETTING_NAME_WITH_DESCRIPTION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings_today_limit_review, parent, false); //Inflating the layout
        } else if (viewType == TYPE_SETTING_SPEECH_RATE_SLIDE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings_speech_rate_slide, parent, false); //Inflating the layout
        } else if (viewType == TYPE_SETTING_ABOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings_about, parent, false); //Inflating the layout
        }
        RecyclerViewSettingListAdapterViewHolder recyclerViewSettingListAdapterViewHolder = new RecyclerViewSettingListAdapterViewHolder(view, viewType);
        return recyclerViewSettingListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewSettingListAdapterViewHolder holder, int position) {

        View view = holder.view;
        RelativeLayout mCardView = (RelativeLayout) view.findViewById(R.id.mCardView);
        TextView lbSettingName = (TextView) view.findViewById(R.id.lbSettingName);
        String setting = settings.get(position);
        final Switch mSwitch = (Switch) view.findViewById(R.id.mSwitch);
        TextView lbLimit = (TextView) view.findViewById(R.id.lbLimit);

        if (holder.viewType == TYPE_TITLE) {
            lbSettingName.setText(settings.get(position));
            mSwitch.setVisibility(View.GONE);
            lbLimit.setVisibility(View.GONE);
            lbSettingName.setTextSize(15f);
            lbSettingName.setTextColor(context.getResources().getColor(R.color.teal_200));
        } else if (holder.viewType == TYPE_SETTING_NAME) {//TODO:TYPE_SETTING_NAME

            lbSettingName.setText(settings.get(position));
            mSwitch.setVisibility(View.GONE);
            lbLimit.setVisibility(View.GONE);
            if (setting.equals(context.getString(R.string.setting_today_new_card_limit))) {
                String limit = learnApiImplements._getValueFromSystemByKey(setting);
                getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT, limit);

            } else if (setting.equals(context.getString(R.string.setting_total_learn_per_day))) {
                String limit = learnApiImplements._getValueFromSystemByKey(setting);
                getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT, limit);

            } else if (setting.equals(context.getString(R.string.setting_check_update))) {
                lbLimit.setVisibility(View.GONE);
                _checkUpdate(mCardView);
            } else if (setting.equals(context.getString(R.string.setting_language))) {
                lbLimit.setVisibility(View.GONE);
                changeLanguage(mCardView);
            } else if (setting.equals(context.getString(R.string.setting_about))) {

            } else if (setting.equals(context.getString(R.string.setting_speech_rate))) {

                lbLimit.setVisibility(View.VISIBLE);
                _showDialogChangeSpeechRate(mCardView, lbLimit);

            } else if (setting.equals(context.getString(R.string.setting_reset_cache))) {
                lbLimit.setVisibility(View.GONE);
                _resetCache(mCardView);
            } else if (setting.equals(context.getString(R.string.setting_export_database))) {
                lbLimit.setVisibility(View.GONE);
                _exportDatabases(mCardView);
            } else if (setting.equals(context.getString(R.string.setting_update_db_form_query))) {

                _showDialogExecuteQueue(mCardView);
            }


        } else if (holder.viewType == TYPE_SETTING_SWITCH) {
            lbLimit.setVisibility(View.GONE);
            lbSettingName.setText(settings.get(position));
            if (setting.equals(context.getString(R.string.setting_auto_check_update))) {
                getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.KEY_SETTING_AUTO_CHECK_UPDATE);
            } else if (setting.equals(context.getString(R.string.setting_debug_info))) {
                getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.KEY_SETTING_DEBUG_INFOR);
            } else if (setting.equals(context.getString(R.string.setting_notification))) {
                getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.KEY_SETTING_NOTIFICTION);
            }
        } else if (holder.viewType == TYPE_SETTING_NAME_WITH_DESCRIPTION) {
            String limit = learnApiImplements._getValueFromSystemByKey(setting);
            lbSettingName.setText(setting);
            TextView lbDescription = (TextView) view.findViewById(R.id.lbDescription);
            getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT, limit);
            lbDescription.setText("");
        } else if (holder.viewType == TYPE_SETTING_ABOUT) {
            TextView lbAppVersion = (TextView) view.findViewById(R.id.lbAppVersion);
            TextView lbDbVersion = (TextView) view.findViewById(R.id.lbDbVersion);
            String versionName = "1";
            try {
                versionName = context.getPackageManager()
                        .getPackageInfo(context.getPackageName(), 0).versionName;
                lbAppVersion.setText("AppVersion:" + versionName);
                lbDbVersion.setText("DBVersion:3");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                lbAppVersion.setText("AppVersion:" + versionName);
            }


        }


    }

    private void _showDialogExecuteQueue(RelativeLayout mCardView) {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Define dialogExecuteEuery
                LayoutInflater li = LayoutInflater.from(context);
                View dialogExecuteEuery = li.inflate(R.layout.dialog_execute_query, null);

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

                builder.setView(dialogExecuteEuery);

                //Define txtQuery
                final EditText txtQuery = (EditText) dialogExecuteEuery.findViewById(R.id.txtQuery);

                // Add the buttons
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(R.string.action_query, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String query = txtQuery.getText().toString();
                        if (query != null || query.length() > 1) {
                            int result = learnApiImplements.executeQuery(query);
                            if (result == 1) {
                                Toast.makeText(context, "Execute Ok", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Execute Error", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            //Toast.makeText(context, "Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Get the AlertDialog from create()
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }

    private void _exportDatabases(RelativeLayout mCardView) {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

                // Chain together various setter methods to set the dialog characteristics
                builder.setTitle(R.string.dialog_title_export_database);

                // Add the buttons
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        learnApiImplements._exportDateBaseFile();
                        Toast.makeText(context, R.string.dialog_title_export_database, Toast.LENGTH_SHORT).show();
                    }
                });
                // Get the AlertDialog from create()
                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }

    private void _resetCache(RelativeLayout mCardView) {
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

                // Chain together various setter methods to set the dialog characteristics
                builder.setTitle(R.string.dialog_title_clear_cache_and_restart_app);

                // Add the buttons
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Clean cache
                        learnApiImplements.cleanCache();
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
        });
    }

    private void _showDialogChangeSpeechRate(RelativeLayout mCardView, final TextView lbLimit) {
        final CharSequence[] items = context.getResources().getStringArray(R.array.speech_rate);
        String sp = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        int index;
        if (sp.equals(context.getString(R.string.speech_rate_very_slow_value))) {
            index = 0;
        } else if (sp.equals(context.getString(R.string.speech_rate_slow_value))) {
            index = 1;
        } else if (sp.equals(context.getString(R.string.speech_rate_normal_value))) {
            index = 2;
        } else if (sp.equals(context.getString(R.string.speech_rate_fast_value))) {
            index = 3;
        } else if (sp.equals(context.getString(R.string.speech_rate_very_fast_value))) {
            index = 4;
        } else {
            index = 2;
        }

        lbLimit.setText(items[index]);
        final int finalIndex = index;
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
                builder.setTitle(context.getString(R.string.dialog_title_change_speech_rate));


                builder.setSingleChoiceItems(items, finalIndex, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        float speechRate = 1.0f;
                        if (items[item].equals(context.getString(R.string.speech_rate_very_slow))) {
                            speechRate = 0.7f;
                        } else if (items[item].equals(context.getString(R.string.speech_rate_slow))) {
                            speechRate = 0.9f;
                        } else if (items[item].equals(context.getString(R.string.speech_rate_normal))) {
                            speechRate = 1.0f;
                        } else if (items[item].equals(context.getString(R.string.speech_rate_fast))) {
                            speechRate = 1.1f;
                        } else if (items[item].equals(context.getString(R.string.speech_rate_very_fast))) {
                            speechRate = 1.3f;
                        }
                        learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_SPEECH_RATE, String.valueOf(speechRate));
                        dialog.cancel();
                        _reloadRecylerView();
                    }
                });
                // Get the AlertDialog from create()
                AlertDialog dialog = builder.create();

                dialog.show();


            }
        });
    }

    private void _reloadRecylerView() {
        mRecyclerViewSettings.setAdapter(this);
    }

    private void getSettingLimitOrUpdate(View mCardView, TextView lbLimit, final String key, String limit) {
        // lbLimit.setVisibility(View.VISIBLE);
        int value = 0;
        if (limit == null) {
            if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_MAX_NEW_LEARN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT)) {
                value = LazzyBeeShare.MAX_REVIEW_LEARN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
            }

        } else {
            value = Integer.valueOf(limit);
        }
        lbLimit.setText(context.getString(R.string.setting_limit_card_number, value));
        final int finalValue = value;
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDialogConfirmSetLimitCard(key, finalValue);
            }
        });

    }

    private void _showDialogConfirmSetLimitCard(final String key, final int value) {
        // Instantiate an AlertDialog.Builder with its constructor
        String title = LazzyBeeShare.EMPTY;
        String message = LazzyBeeShare.EMPTY;
        View viewDialog = View.inflate(context, R.layout.dialog_limit_card, null);
        TextView lbSettingLimitName = (TextView) viewDialog.findViewById(R.id.lbSettingLimitName);
        final EditText txtLimit = (EditText) viewDialog.findViewById(R.id.txtLimit);

        if (key == LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_new_card_limit_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_review_card_limit_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT) {
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

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String limit = txtLimit.getText().toString();
                learnApiImplements._insertOrUpdateToSystemTable(key, limit);
            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void changeLanguage(View mCardView) {
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


    private void getSettingAndUpdateWithSwitch(View mCardView, final String key) {
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
        else if (setting.equals(context.getString(R.string.setting_today_new_card_limit))
                || setting.equals(context.getString(R.string.setting_total_learn_per_day))
                || setting.equals(context.getString(R.string.setting_language))
                || setting.equals(context.getString(R.string.setting_check_update))
                || setting.equals(context.getString(R.string.setting_reset_cache))
                || setting.equals(context.getString(R.string.setting_all_right))
                || setting.equals(context.getString(R.string.setting_export_database))
                || setting.equals(context.getString(R.string.setting_update_db_form_query))
                || setting.equals(context.getString(R.string.setting_speech_rate)))
            return TYPE_SETTING_NAME;

        else if (setting.equals(context.getString(R.string.setting_notification))
                || setting.equals(context.getString(R.string.setting_auto_check_update))
                || setting.equals(context.getString(R.string.setting_debug_info)))
            return TYPE_SETTING_SWITCH;

        else if (setting.equals(context.getString(R.string.setting_today_review_card_limit)))

            return TYPE_SETTING_NAME_WITH_DESCRIPTION;
        else if (setting.equals(context.getString(R.string.setting_speech_rate_slider)))
            return TYPE_SETTING_SPEECH_RATE_SLIDE;
        else if (setting.equals(context.getString(R.string.setting_about)))

            return TYPE_SETTING_ABOUT;
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

    private void _checkUpdate(View mCardView) {
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
                Log.i(TAG, _clientVesion + ":" + update_local_version);

                if (_clientVesion == 0) {
                    if (update_local_version == -1) {
                        Log.i(TAG, "_checkUpdate():update_local_version == -1");
                        _showComfirmUpdateDatabase(LazzyBeeShare.DOWNLOAD_UPDATE);
                    } else {
                        Log.i(TAG, "_checkUpdate():update_local_version != -1");
                        _showComfirmUpdateDatabase(LazzyBeeShare.DOWNLOAD_UPDATE);
                    }
                } else {
                    if (update_local_version > _clientVesion) {
                        Log.i(TAG, "_checkUpdate():update_local_version > _clientVesion");
                        _showComfirmUpdateDatabase(LazzyBeeShare.DOWNLOAD_UPDATE);
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
            //learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.DB_VERSION, String.valueOf(LazzyBeeShare.VERSION_SERVER));
            //databaseUpgrade.close();
            Toast.makeText(context, R.string.update_database_sucsessfuly, Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Log.e(TAG, "Update DB Error:" + e.getMessage());
            e.printStackTrace();
        }


    }

    class DownloadFileUpdateDatabaseTask extends AsyncTask<String, Void, Void> {
        Context context;
        ProgressDialog progressDialog;

        public DownloadFileUpdateDatabaseTask(Context context) {
            this.context = context;
            progressDialog = new ProgressDialog(context);

        }

        protected void onPreExecute() {
            this.progressDialog.setMessage("Loading...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
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
            } catch (MalformedURLException mue) {
                Log.e("SYNC getUpdate", "malformed url error", mue);
            } catch (IOException ioe) {
                Log.e("SYNC getUpdate", "io error", ioe);
            } catch (SecurityException se) {
                Log.e("SYNC getUpdate", "security error", se);
            }
            _updateDB(LazzyBeeShare.DOWNLOAD_UPDATE);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        }
    }
}
