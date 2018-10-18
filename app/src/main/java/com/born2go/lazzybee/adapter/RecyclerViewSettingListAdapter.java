package com.born2go.lazzybee.adapter;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.born2go.lazzybee.BuildConfig;
import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.CustomStudySettingActivity;
import com.born2go.lazzybee.activity.SettingActivity;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DatabaseUpgrade;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.utils.CustomTimePickerDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

/**
 * Created by Hue on 8/21/2015.
 */

public class RecyclerViewSettingListAdapter extends
        RecyclerView.Adapter<RecyclerViewSettingListAdapter.RecyclerViewSettingListAdapterViewHolder>
        implements DownloadFileandUpdateDatabase.DownloadFileDatabaseResponse {
    private static final String TAG = "SettingListAdapter";
    Context context;
    List<String> settings;
    LearnApiImplements learnApiImplements;
    DatabaseUpgrade databaseUpgrade;
    int TYPE_LINE = -1;
    int TYPE_LINE_CHILD = -2;
    int TYPE_SUB_HEADER = 0;
    int TYPE_SETTING_NAME = 1;
    int TYPE_SETTING_SWITCH = 2;
    int TYPE_SETTING_NAME_WITH_DESCRIPTION = 3;
    private static final int TYPE_SETTING_SPEECH_RATE_SLIDE = 4;
    private static final int TYPE_SETTING_ABOUT = 5;
    private static final int TYPE_SETTING_NOTIFICATION = 6;
    SettingActivity activity;
    private String queryExportToCsv = "Select vocabulary.gid,vocabulary.e_factor,vocabulary.last_ivl,vocabulary.level,vocabulary.queue,vocabulary.rev_count " +
            "from vocabulary where vocabulary.queue = -1 OR vocabulary.queue = -2 OR vocabulary.queue > 0";
    String device_id;
    private RecyclerView mRecyclerViewSettings;
    private RecyclerViewSettingListAdapter thiz;

    @SuppressLint("HardwareIds")
    public RecyclerViewSettingListAdapter(SettingActivity activity, Context context, List<String> settings, RecyclerView mRecyclerViewSettings) {
        this.activity = activity;
        this.context = context;
        this.settings = settings;
        this.learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        this.databaseUpgrade = LazzyBeeSingleton.databaseUpgrade;
        this.mRecyclerViewSettings = mRecyclerViewSettings;
        device_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        this.thiz = this;

    }

    @NonNull
    @Override
    public RecyclerViewSettingListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_SUB_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings_sub_header, parent, false); //Inflating the layout
        } else if (viewType == TYPE_LINE_CHILD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_setting_line_child, parent, false); //Inflating the layout
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
        } else if (viewType == TYPE_SETTING_NOTIFICATION) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_setting_set_notification, parent, false); //Inflating the layout
        }
        RecyclerViewSettingListAdapterViewHolder recyclerViewSettingListAdapterViewHolder = new RecyclerViewSettingListAdapterViewHolder(view, viewType);
        return recyclerViewSettingListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewSettingListAdapterViewHolder holder, int position) {

        View view = holder.view;
        RelativeLayout mCardView = (RelativeLayout) view.findViewById(R.id.mCardView);
        TextView lbSettingName = (TextView) view.findViewById(R.id.lbSettingName);
        String setting = settings.get(position);
        final Switch mSwitch = (Switch) view.findViewById(R.id.mSwitch);
        TextView lbLimit = (TextView) view.findViewById(R.id.lbLimit);
        ImageView imageView = (ImageView) view.findViewById(R.id.imgGoto);

        // Log.i(TAG, "Setting Name:" + setting);


        try {
            if (holder.viewType == TYPE_SUB_HEADER) {
                lbSettingName.setText(settings.get(position));
//                mSwitch.setVisibility(View.GONE);
//                lbLimit.setVisibility(View.GONE);
//                imageView.setVisibility(View.GONE);

                lbSettingName.setTextSize(15f);
                lbSettingName.setTextColor(context.getResources().getColor(R.color.color_sub_header));
            } else if (holder.viewType == TYPE_SETTING_NAME) {
                //TODO:TYPE_SETTING_NAME_1

                lbSettingName.setText(settings.get(position));
                mSwitch.setVisibility(View.GONE);
                lbLimit.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);

                if (setting.equals(context.getString(R.string.setting_today_new_card_limit))) {
                    String limit = learnApiImplements._getValueFromSystemByKey(setting);
                    getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT, limit);

                } else if (setting.equals(context.getString(R.string.setting_total_learn_per_day))) {
                    String limit = learnApiImplements._getValueFromSystemByKey(setting);
                    getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT, limit);

                } else if (setting.equals(context.getString(R.string.setting_check_update))) {
                    //check Update
                    lbLimit.setVisibility(View.GONE);
                    if (learnApiImplements._checkUpdateDataBase()) {
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.ic_action_about_green);
                    } else {
                        imageView.setVisibility(View.GONE);
                    }
                    _onClickCheckUpdate(mCardView);
                } else if (setting.equals(context.getString(R.string.setting_language))) {
                    lbLimit.setVisibility(View.GONE);
                    changeLanguage(mCardView);
                }
//                else if (setting.equals(context.getString(R.string.setting_about))) {
//
//                }
                else if (setting.equals(context.getString(R.string.setting_reset_cache))) {
                    lbLimit.setVisibility(View.GONE);
                    _resetCache(mCardView);
                } else if (setting.equals(context.getString(R.string.setting_export_database))) {
                    lbLimit.setVisibility(View.GONE);
                    _exportDatabases(mCardView);

                } else if (setting.equals(context.getString(R.string.setting_back_up_database))) {
                    lbLimit.setVisibility(View.VISIBLE);
                    String backup_key = device_id.substring(device_id.length() - 6, device_id.length());
                    lbLimit.setText(backup_key);
                    _backupDatabase(mCardView);
                } else if (setting.equals(context.getString(R.string.setting_back_up_database_dev))) {
                    lbLimit.setVisibility(View.VISIBLE);
                    String backup_key = device_id.substring(device_id.length() - 6, device_id.length());
                    lbLimit.setText(backup_key);
                    _backupDatabases_Dev(mCardView);
                } else if (setting.equals(context.getString(R.string.setting_restore_database))) {
                    lbLimit.setVisibility(View.GONE);
                    _importDatabasesFormCVS(mCardView);
                } else if (setting.equals(context.getString(R.string.setting_update_db_form_query))) {
                    _showDialogExecuteQueue(mCardView);
                } else if (setting.equals(context.getString(R.string.setting_custom_study))) {
                    Log.i(TAG, "Here?");
                    imageView.setVisibility(View.VISIBLE);
                    _gotoCustomStudySetting(mCardView);
                }


            } else if (holder.viewType == TYPE_SETTING_SWITCH) {
                lbLimit.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);

                lbSettingName.setText(settings.get(position));
                if (setting.equals(context.getString(R.string.setting_auto_check_update))) {
                    getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.KEY_SETTING_AUTO_CHECK_UPDATE);
                } else if (setting.equals(context.getString(R.string.setting_debug_info))) {
                    getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.KEY_SETTING_DEBUG_INFOR);
                }
//                else if (setting.equals(context.getString(R.string.setting_notification))) {
//                    getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.KEY_SETTING_NOTIFICTION);
//                }
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
                int _dbVesion = LazzyBeeShare.DEFAULT_VERSION_DB;

                try {
                    versionName = BuildConfig.VERSION_NAME;//context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                    String db_v = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
                    if (db_v != null) {
                        _dbVesion = Integer.valueOf(db_v);
                    }
                    lbAppVersion.setText(String.valueOf("AppVersion:" + BuildConfig.VERSION_NAME));
                    lbDbVersion.setText(String.valueOf("DBVersion:" + _dbVesion));
                } catch (Exception e) {
                    e.printStackTrace();
                    lbAppVersion.setText(String.valueOf("AppVersion:" + versionName));
                    lbDbVersion.setText(String.valueOf("DBVersion:" + _dbVesion));
                    //noinspection AccessStaticViaInstance
                    LazzyBeeSingleton.getCrashlytics().logException(e);
                }


            } else if (holder.viewType == TYPE_SETTING_NOTIFICATION) {
                LinearLayout mSetUpNotification = (LinearLayout) view.findViewById(R.id.mSetUpNotification);
                //getSettingAndUpdateWithSwitch(mCardView, LazzyBeeShare.KEY_SETTING_NOTIFICTION);
                getSettingNotificationAndUpdateWithSwitch(mCardView, mSetUpNotification);
            } else if (holder.viewType == TYPE_SETTING_SPEECH_RATE_SLIDE) {
                SeekBar mSlideSpeechRate = (SeekBar) view.findViewById(R.id.mSlideSpeechRate);
                _handlerChangeSpeechRate(mSlideSpeechRate);
            }
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onBindViewHolder", e);
        }

    }

    private void _backupDatabase(RelativeLayout mCardView) {
        mCardView.setOnClickListener(v -> {
            int mini = 1;
            BackUpDatabaseToCSV exportDatabaseToCSV = new BackUpDatabaseToCSV(activity, context, device_id, mini);
            exportDatabaseToCSV.execute();
        });
    }

    private void _importDatabasesFormCVS(RelativeLayout mCardView) {
        mCardView.setOnClickListener(v -> {
            if (SettingActivity.verifyStoragePermissions(activity))
                _restoreDatabase();
        });
    }

    private void _restoreDatabase() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

        builder.setTitle(R.string.setting_restore_database);
        View viewDialog = View.inflate(context, R.layout.dialog_set_my_backup_key, null);
        final EditText lbMybackupkey = (EditText) viewDialog.findViewById(R.id.lbMybackupkey);

        lbMybackupkey.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });
        builder.setView(viewDialog);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String getBackupKey = lbMybackupkey.getText().toString();
            lbMybackupkey.clearFocus();
            DownloadAndRestoreDatabaseFormCSV downloadAndRestoreDatabaseFormCSV = new DownloadAndRestoreDatabaseFormCSV(context, false, LazzyBeeShare.EMPTY, getBackupKey);
            downloadAndRestoreDatabaseFormCSV.execute();
            dialog.dismiss();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //intent.setType("*/*");      //all files
        intent.setType("text/xml");   //XML file only
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Import"), 159);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(ex);
        }
    }

    private void _backupDatabases_Dev(RelativeLayout mCardView) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);
        final CharSequence[] types = new CharSequence[2];
        types[0] = "Full-Card";
        types[1] = "Mini-Only Card Learned";
        final int[] type = new int[1];
        builder.setTitle(R.string.setting_back_up_database);
        builder.setSingleChoiceItems(types, 0, (dialog, item) -> type[0] = item);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            BackUpDatabaseToCSV exportDatabaseToCSV = new BackUpDatabaseToCSV(activity, context, device_id, type[0]);
            exportDatabaseToCSV.execute();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        mCardView.setOnClickListener(v -> {
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void _handlerChangeSpeechRate(SeekBar mSlideSpeechRate) {
        String sp = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        final int index;
        if (sp == null) {
            index = 8;
        } else if (sp.equals(context.getString(R.string.speech_rate_very_slow_value))) {
            index = 0;
        } else if (sp.equals(context.getString(R.string.speech_rate_slow_value))) {
            index = 4;
        } else if (sp.equals(context.getString(R.string.speech_rate_normal_value))) {
            index = 8;
        } else if (sp.equals(context.getString(R.string.speech_rate_fast_value))) {
            index = 12;
        } else if (sp.equals(context.getString(R.string.speech_rate_very_fast_value))) {
            index = 16;
        } else {
            index = 8;
        }
        mSlideSpeechRate.setMax(16);
        mSlideSpeechRate.setProgress(index);

//        float speechRate = 1.0f;
//        if (items[item].equals(context.getString(R.string.speech_rate_very_slow))) {
//            speechRate = 0.7f;
//        } else if (items[item].equals(context.getString(R.string.speech_rate_slow))) {
//            speechRate = 0.9f;
//        } else if (items[item].equals(context.getString(R.string.speech_rate_normal))) {
//            speechRate = 1.0f;
//        } else if (items[item].equals(context.getString(R.string.speech_rate_fast))) {
//            speechRate = 1.1f;
//        } else if (items[item].equals(context.getString(R.string.speech_rate_very_fast))) {
//            speechRate = 1.3f;
//        }
//        learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_SPEECH_RATE, String.valueOf(speechRate));

        mSlideSpeechRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;


            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                Log.i(TAG, "onChangeProgress process=" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStartTrackingTouch process=" + progress);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.i(TAG, "onStopTrackingTouch process=" + progress);
                float speechRate = 1.0f;
                if (progress <= 4) {
                    speechRate = 0.7f;
                } else if (progress > 0 && progress <= 4) {
                    speechRate = 0.9f;
                } else if (progress > 4 && progress <= 8) {
                    speechRate = 1.0f;
                } else if (progress > 8 && progress <= 12) {
                    speechRate = 1.1f;
                } else if (progress > 12 && progress <= 16) {
                    speechRate = 1.3f;
                }
                LazzyBeeShare._speakText(context.getString(R.string.test_speech_rate), speechRate);
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_SPEECH_RATE, String.valueOf(speechRate));


            }
        });
    }

    private void _gotoCustomStudySetting(RelativeLayout mCardView) {
        mCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomStudySettingActivity.class);
            context.startActivity(intent);

        });
    }

    private void getSettingNotificationAndUpdateWithSwitch(RelativeLayout mCardView, final LinearLayout mSetUpNotification) {
        final Switch mSwitch = (Switch) mCardView.findViewById(R.id.mSwitch);
        final TextView txtTimeNotification = (TextView) mSetUpNotification.findViewById(R.id.txtTimeNotification);
        String value = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_NOTIFICTION);

        String hour_str = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION);
        String minute_str = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION);
        if (value == null) {
            mSwitch.setChecked(true);
            mSetUpNotification.setVisibility(View.VISIBLE);
        } else if (value.equals(LazzyBeeShare.ON)) {
            mSetUpNotification.setVisibility(View.VISIBLE);
            mSwitch.setChecked(true);
        } else if (value.equals(LazzyBeeShare.OFF)) {
            mSetUpNotification.setVisibility(View.GONE);
            mSwitch.setChecked(false);
        } else {
            mSwitch.setChecked(false);
            mSetUpNotification.setVisibility(View.GONE);
        }
        String time;
        if (hour_str == null) {
            txtTimeNotification.setText(context.getString(R.string.setting_set_time_notification, LazzyBeeShare.DEFAULT_TIME_NOTIFICATION));
        } else {
            if (minute_str == null) {
                minute_str = "0";
            }
            int hour = Integer.valueOf(hour_str);
            int minute = Integer.valueOf(minute_str);
            if (hour < 10) {
                hour_str = "0" + hour;
            }
            if (minute < 10) {
                minute_str = "0" + minute;
            }
            time = hour_str + ":" + minute_str;
            txtTimeNotification.setText(context.getString(R.string.setting_set_time_notification, time));
        }


        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String value12;
            if (isChecked) {
                txtTimeNotification.setText(context.getString(R.string.setting_set_time_notification, LazzyBeeShare.DEFAULT_TIME_NOTIFICATION));
                value12 = LazzyBeeShare.ON;
                mSetUpNotification.setVisibility(View.VISIBLE);
                LazzyBeeShare._setUpNotification(context, LazzyBeeShare.DEFAULT_HOUR_NOTIFICATION, LazzyBeeShare.DEFAULT_MINUTE_NOTIFICATION);
            } else {
                value12 = LazzyBeeShare.OFF;
                mSetUpNotification.setVisibility(View.GONE);
                LazzyBeeShare._cancelNotification(context);
            }
            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_NOTIFICTION, value12);
        });

        View.OnClickListener mSOnclick = v -> {
            String value1;
            if (!mSwitch.isChecked()) {
                mSwitch.setChecked(true);
                value1 = LazzyBeeShare.ON;
                mSetUpNotification.setVisibility(View.VISIBLE);
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION, String.valueOf(LazzyBeeShare.DEFAULT_HOUR_NOTIFICATION));
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION, String.valueOf(LazzyBeeShare.DEFAULT_MINUTE_NOTIFICATION));
            } else {
                mSwitch.setChecked(false);
                value1 = LazzyBeeShare.OFF;
                mSetUpNotification.setVisibility(View.GONE);
            }
            learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_NOTIFICTION, value1);
        };
        mCardView.setOnClickListener(mSOnclick);

        mSetUpNotification.setOnClickListener(v -> {
            int hour = learnApiImplements.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION);
            int minute = learnApiImplements.getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION);
            Log.d(TAG, hour + ":" + minute);
            // Create a new instance of TimePickerDialog and return it
            CustomTimePickerDialog timePickerDialog = new CustomTimePickerDialog(context, (view, hourOfDay, minute1) -> {
                String hour_str1 = String.valueOf(hourOfDay);
                String minute_str1 = String.valueOf(minute1);

                if (hourOfDay < 10) {
                    hour_str1 = "0" + hourOfDay;
                }
                if (minute1 < 10) {
                    minute_str1 = "0" + minute1;
                }
                String time1 = String.valueOf(hour_str1 + ":" + minute_str1);
                txtTimeNotification.setText(context.getString(R.string.setting_set_time_notification, time1));
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION, String.valueOf(hourOfDay));
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION, String.valueOf(minute1));
                LazzyBeeShare._setUpNotification(context, hourOfDay, minute1);
            }, hour, minute, true);
            timePickerDialog.setTitle("Select Date");
            timePickerDialog.show();

        });
    }

    private void _showDialogExecuteQueue(RelativeLayout mCardView) {
        mCardView.setOnClickListener(v -> {

            //Define dialogExecuteEuery
            LayoutInflater li = LayoutInflater.from(context);
            View dialogExecuteEuery = li.inflate(R.layout.dialog_execute_query, null);

            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

            builder.setView(dialogExecuteEuery);

            //Define txtQuery
            final EditText txtQuery = (EditText) dialogExecuteEuery.findViewById(R.id.txtQuery);

            // Add the buttons
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // User cancelled the dialog
                dialog.cancel();
            });
            builder.setPositiveButton(R.string.action_query, (dialog, id) -> {
                String query = txtQuery.getText().toString();
                if (query != null || query.length() > 1) {
                    int result = learnApiImplements.executeQuery(query);
                    if (result == 1) {
                        Toast.makeText(context, "Execute Ok", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Execute Error", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            // Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();
        });
    }

    private void _exportDatabases(RelativeLayout mCardView) {
        mCardView.setOnClickListener(v -> {
            // Instantiate an AlertDialog.Builder with its constructor
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

            // Chain together various setter methods to set the dialog characteristics
            builder.setTitle(R.string.dialog_title_export_database);

            // Add the buttons
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // User cancelled the dialog
                dialog.cancel();
            });
            builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                learnApiImplements._exportDateBaseFile();
                Toast.makeText(context, R.string.dialog_title_export_database, Toast.LENGTH_SHORT).show();
            });
            // Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();
        });
    }

    private void _resetCache(RelativeLayout mCardView) {
        mCardView.setOnClickListener(v -> {
            // Instantiate an AlertDialog.Builder with its constructor
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

            // Chain together various setter methods to set the dialog characteristics
            builder.setTitle(R.string.dialog_title_clear_cache_and_restart_app);

            // Add the buttons
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // User cancelled the dialog
                dialog.cancel();
            });
            builder.setPositiveButton(R.string.ok, (dialog, id) -> {
                //Clean cache
                learnApiImplements.cleanCache();
                //restart app
                Intent i = context.getPackageManager()
                        .getLaunchIntentForPackage(context.getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            });
            // Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();
        });
    }

    private void _showDialogChangeSpeechRate(RelativeLayout mCardView, final TextView lbLimit) {
        final CharSequence[] items = context.getResources().getStringArray(R.array.speech_rate);
        String sp = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_SPEECH_RATE);
        int index;
        if (sp == null) {
            index = 2;
        } else if (sp.equals(context.getString(R.string.speech_rate_very_slow_value))) {
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
        mCardView.setOnClickListener(v -> {

            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);
            builder.setTitle(context.getString(R.string.dialog_title_change_speech_rate));


            builder.setSingleChoiceItems(items, finalIndex, (dialog, item) -> {
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
            });
            // Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();


        });
    }

    private void _reloadRecylerView() {
        mRecyclerViewSettings.setAdapter(this);
    }

    private void getSettingLimitOrUpdate(View mCardView, TextView lbLimit, final String key, String limit) {
        // lbLimit.setVisibility(View.VISIBLE);
        int value = 0;
        if (limit == null) {
            switch (key) {
                case LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT:
                    value = LazzyBeeShare.DEFAULT_MAX_NEW_LEARN_PER_DAY;
                    break;
                case LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT:
                    value = LazzyBeeShare.MAX_REVIEW_LEARN_PER_DAY;
                    break;
                case LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT:
                    value = LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
                    break;
            }

        } else {
            value = Integer.valueOf(limit);
        }
        lbLimit.setText(context.getString(R.string.setting_limit_card_number, value));
        final int finalValue = value;
        mCardView.setOnClickListener(v -> _showDialogConfirmSetLimitCard(key, finalValue));

    }

    private void _showDialogConfirmSetLimitCard(final String key, final int value) {
        // Instantiate an AlertDialog.Builder with its constructor
        String title = LazzyBeeShare.EMPTY;
        String message = LazzyBeeShare.EMPTY;
        View viewDialog = View.inflate(context, R.layout.dialog_limit_card, null);
        TextView lbSettingLimitName = (TextView) viewDialog.findViewById(R.id.lbSettingLimitName);
        final EditText txtLimit = (EditText) viewDialog.findViewById(R.id.txtLimit);

        if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT)) {
            message = context.getString(R.string.dialog_message_setting_today_new_card_limit_by);
        } else if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT)) {
            message = context.getString(R.string.dialog_message_setting_today_review_card_limit_by);
        } else if (key.equals(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT)) {
            message = context.getString(R.string.dialog_message_setting_total_card_learn_pre_day_by);
        }

        lbSettingLimitName.setText(message);
        txtLimit.setText(String.valueOf(value));

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

        // Chain together various setter methods to set the dialog characteristics
        // builder.setMessage(R.string.dialog_message_setting_today_new_card_limit_by);
        builder.setView(viewDialog);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
            dialog.cancel();
        });

        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            String limit = txtLimit.getText().toString();
            learnApiImplements._insertOrUpdateToSystemTable(key, limit);
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void changeLanguage(View mCardView) {
        mCardView.setOnClickListener(v -> {
            final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);
            builder.setTitle(context.getString(R.string.change_language));
            final CharSequence[] items = {context.getString(R.string.lang_english), context.getString(R.string.lang_viet)};
            int index;
            String lang = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_LANGUAGE);

            if (lang != null) {
                if (lang.equals(LazzyBeeShare.LANG_VI)) {
                    index = 1;
                    //Log.i(TAG, "lang:" + lang + ",index:" + index);
                } else {
                    index = 0;
                }
            } else {
                index = 1;
            }


            builder.setSingleChoiceItems(items, index, (dialog, item) -> {
                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                firebaseAnalytics.setUserProperty("Selected_language", String.valueOf(items[item]));
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
            });
            // Get the AlertDialog from create()
            AlertDialog dialog = builder.create();

            dialog.show();
        });

    }

    private void _showDialogConfirmRestartApp() {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_message_restart_app)
                .setTitle(R.string.dialog_title_restart_app);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
            dialog.cancel();
        });
        builder.setPositiveButton(R.string.ok, (dialog, id) -> {
            //restart app
            Intent i = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
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
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String value12;
            if (isChecked) {
                value12 = LazzyBeeShare.ON;
            } else {
                value12 = LazzyBeeShare.OFF;
            }
            learnApiImplements._insertOrUpdateToSystemTable(key, value12);
        });
        mCardView.setOnClickListener(v -> {
            //Toast.makeText(context, R.string.setting_auto_check_update, Toast.LENGTH_SHORT).show();
            String value1;
            if (!mSwitch.isChecked()) {
                mSwitch.setChecked(true);
                value1 = LazzyBeeShare.ON;
            } else {
                mSwitch.setChecked(false);
                value1 = LazzyBeeShare.OFF;
            }
            learnApiImplements._insertOrUpdateToSystemTable(key, value1);
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
                || setting.equals(context.getString(R.string.setting_update_title))
                || setting.equals(context.getString(R.string.setting_backup_and_restore_database_title))
                || setting.equals(context.getString(R.string.setting_sub_header_more))
                || setting.equals(context.getString(R.string.setting_sub_header_about))
                || setting.equals(context.getString(R.string.setting_speech_rate))
                ) {
            return TYPE_SUB_HEADER;
        } else if (setting.equals(context.getString(R.string.setting_today_new_card_limit))
                || setting.equals(context.getString(R.string.setting_total_learn_per_day))
                || setting.equals(context.getString(R.string.setting_language))
                || setting.equals(context.getString(R.string.setting_reset_cache))
                || setting.equals(context.getString(R.string.setting_all_right))
                || setting.equals(context.getString(R.string.setting_export_database))
                || setting.equals(context.getString(R.string.setting_back_up_database))
                || setting.equals(context.getString(R.string.setting_back_up_database_dev))
                || setting.equals(context.getString(R.string.setting_restore_database))
                || setting.equals(context.getString(R.string.setting_update_db_form_query))
                )
            return TYPE_SETTING_NAME;

        else if (setting.equals(context.getString(R.string.setting_auto_check_update))
                || setting.equals(context.getString(R.string.setting_debug_info))
            //|| setting.equals(context.getString(R.string.setting_notification))
                )
            return TYPE_SETTING_SWITCH;

        else if (setting.equals(context.getString(R.string.setting_today_review_card_limit)))

            return TYPE_SETTING_NAME_WITH_DESCRIPTION;
        else if (setting.equals(context.getString(R.string.setting_speech_rate_slider)))
            return TYPE_SETTING_SPEECH_RATE_SLIDE;
        else if (setting.equals(context.getString(R.string.setting_about)))

            return TYPE_SETTING_ABOUT;
        else if (setting.equals(context.getString(R.string.setting_notification)))

            return TYPE_SETTING_NOTIFICATION;
        else if (setting.equals(context.getString(R.string.setting_custom_study)))

            return TYPE_SETTING_NAME;
        else if (setting.equals(context.getString(R.string.setting_check_update)))

            return TYPE_SETTING_NAME;
        else if (setting.equals(context.getString(R.string.setting_lines_child)))

            return TYPE_LINE_CHILD;
        else
            return TYPE_LINE;
    }

    @Override
    public void processFinish(int code) {
        if (code == 1) {
            //Dowload and update Complete
            if (!learnApiImplements._checkUpdateDataBase()) {
                Toast.makeText(context, context.getString(R.string.mesage_update_database_successful), Toast.LENGTH_SHORT).show();
                _reloadRecylerView();
            } else {
                _downloadFile();
            }

        } else {
            Toast.makeText(context, context.getString(R.string.mesage_update_database_fails), Toast.LENGTH_SHORT).show();
        }

    }

    public void updateRequestPermissions() {
        _restoreDatabase();
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

    private void _onClickCheckUpdate(View mCardView) {
        mCardView.setOnClickListener(v -> {
            if (LazzyBeeShare.checkConn(context)) {
                _checkUpdate();
            } else {
                Toast.makeText(context, R.string.failed_to_connect_to_server, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void _checkUpdate() {
        if (learnApiImplements._checkUpdateDataBase()) {
            Log.i(TAG, "Co Update");
            //Toast.makeText(context, "Co Update", Toast.LENGTH_SHORT).show();
            _showComfirmUpdateDatabase();
        } else {
            Toast.makeText(context, R.string.message_updated, Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Khong co Update");
        }
    }

    private void _showComfirmUpdateDatabase() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogLearnMore);

        // Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.dialog_update)
                .setTitle(R.string.dialog_title_update);

        // Add the buttons
        builder.setPositiveButton(R.string.btn_update, (dialog, id) -> {
            // User clicked Update button
            //1.Download file from server
            //2.Open database
            //3.Upgade to my database
            //4.Remove file update
            _updateDB(LazzyBeeShare.DOWNLOAD_UPDATE);


        });
        builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
            // User cancelled the dialog
            dialog.cancel();
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();

    }

    private void _downloadFile() {
        try {
            LazzyBeeSingleton.getFirebaseRemoteConfig().fetch(LazzyBeeShare.CACHE_EXPIRATION).addOnCompleteListener(activity, task -> {
                String base_url = "http://222.255.29.25/lazzybee/";
                if (task.isSuccessful()) {
                    base_url = LazzyBeeSingleton.getFirebaseRemoteConfig().getString(LazzyBeeShare.BASE_URL_DB);
                }
                String db_v = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
                int version = LazzyBeeShare.DEFAULT_VERSION_DB;
                if (db_v != null) {
                    version = Integer.valueOf(db_v);
                }
                String dbUpdateName = (version + 1) + ".db";
                String download_url = base_url + dbUpdateName;
                Log.i(TAG, "download_url=" + download_url);

                if (!base_url.isEmpty() || base_url != null) {

                    DownloadFileandUpdateDatabase downloadFileandUpdateDatabase = new DownloadFileandUpdateDatabase(context, version + 1);

                    //downloadFileandUpdateDatabase.execute(LazzyBeeShare.URL_DATABASE_UPDATE);
                    downloadFileandUpdateDatabase.execute(download_url);
                    downloadFileandUpdateDatabase.downloadFileDatabaseResponse = thiz;
                } else {
                    Toast.makeText(context, R.string.message_download_database_fail, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "_downloadFile", e);
        }
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
        } catch (Exception e) {
            Log.e(TAG, "Update DB Error:" + e.getMessage());
            e.printStackTrace();
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(e);
        }


    }
}
