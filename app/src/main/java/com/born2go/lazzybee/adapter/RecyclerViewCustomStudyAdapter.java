package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

/**
 * Created by Hue on 8/24/2015.
 */
public class RecyclerViewCustomStudyAdapter extends
        RecyclerView.Adapter<RecyclerViewCustomStudyAdapter.RecyclerViewCustomStudyAdapterViewHolder> {
    private static final String TAG = "CustomStudyAdapter";
    Context context;
    List<String> customStudys;
    LearnApiImplements learnApiImplements;
    //    Dialog main;
    int TYPE_TITLE = 0;
    int TYPE_SETTING_NAME = 1;
    int TYPE_SETTING_SWITCH = 2;
    int TYPE_LINE = -1;
    int TYPE_SETTING_NAME_WITH_DESCRIPTION = 3;
    RecyclerView recyclerView;

    public RecyclerViewCustomStudyAdapter(Context context, List<String> customStudys, RecyclerView recyclerView) {
        this.context = context;
        this.customStudys = customStudys;
        this.learnApiImplements = LazzyBeeSingleton.learnApiImplements;
//        this.main = dialog;
        this.recyclerView = recyclerView;
    }

    private void _reloadRecylerView() {
        recyclerView.setAdapter(this);
    }


    @Override
    public RecyclerViewCustomStudyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_TITLE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false); //Inflating the layout
        } else if (viewType == TYPE_SETTING_NAME) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_settings, parent, false); //Inflating the layout
        } else if (viewType == TYPE_LINE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_setting_lines, parent, false); //Inflating the layout
        }
        RecyclerViewCustomStudyAdapterViewHolder recyclerViewCustomStudyAdapterViewHolder = new RecyclerViewCustomStudyAdapterViewHolder(view, viewType);
        return recyclerViewCustomStudyAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewCustomStudyAdapterViewHolder holder, int position) {
        View view = holder.view;
        RelativeLayout mCardView = (RelativeLayout) view.findViewById(R.id.mCardView);

        TextView lbSettingName = (TextView) view.findViewById(R.id.lbSettingName);
        String setting = customStudys.get(position);
        final Switch mSwitch = (Switch) view.findViewById(R.id.mSwitch);
        TextView lbLimit = (TextView) view.findViewById(R.id.lbLimit);
        try {
            if (holder.viewType == TYPE_TITLE) {
                lbSettingName.setText(customStudys.get(position));
                mSwitch.setVisibility(View.GONE);
                // mCardView.setRadius(0f);
                lbLimit.setVisibility(View.GONE);
                lbSettingName.setTextSize(15f);
                lbSettingName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            } else if (holder.viewType == TYPE_SETTING_NAME) {
                lbSettingName.setText(customStudys.get(position));
                // mCardView.setRadius(0f);
                mSwitch.setVisibility(View.GONE);
                if (setting.equals(context.getString(R.string.setting_today_new_card_limit))) {
                    String limit = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);
                    lbLimit.setTag(limit);
                    getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT, limit);
                } else if (setting.equals(context.getString(R.string.setting_total_learn_per_day))) {
                    String limit = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
                    lbLimit.setTag(limit);
                    getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT, limit);

                } else if (setting.equals(context.getString(R.string.setting_max_learn_more_per_day))) {
                    String limit = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT);
                    lbLimit.setTag(limit);
                    getSettingLimitOrUpdate(mCardView, lbLimit, LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT, limit);

                } else if (setting.equals(context.getString(R.string.setting_reset_to_default))) {
                    lbLimit.setVisibility(View.GONE);
                    mCardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            _showConfirmResetToDefauls();
                        }
                    });

                } else if (setting.equals(context.getString(R.string.setting_my_level))) {
                    getLevelandShowDialogChangeLevel(mCardView, lbLimit);
                } else if (setting.equals(context.getString(R.string.setting_position_meaning))) {
                    _setPositionMeaning(mCardView, lbLimit);
                }
            }
            //_reloadRecylerView();
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
    }

    private void getLevelandShowDialogChangeLevel(RelativeLayout mCardView, TextView lbLimit) {

        String strlevel = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MY_LEVEL);
        int level;
        if (strlevel == null) {
            level = LazzyBeeShare.DEFAULT_MY_LEVEL;
        } else {
            level = Integer.valueOf(strlevel);
        }
        lbLimit.setText(String.valueOf(level));

        //handel oncllick
        final int finalLevel = level;
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _showDialogSelectLevel(finalLevel);
            }
        });
    }

    private void _showDialogSelectLevel(int finalLevel) {

        final String[] strlevels = {"1", "2", "3", "4", "5", "6"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
        builder.setTitle(context.getString(R.string.dialog_title_change_my_level));

        builder.setSingleChoiceItems(strlevels, (finalLevel == 1) ? 0 : finalLevel - 1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_MY_LEVEL, strlevels[item]);
                learnApiImplements._initIncomingCardIdListbyLevel(Integer.valueOf(strlevels[item]));
                dialog.dismiss();
                _reloadRecylerView();

            }
        });
        // Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void _showConfirmResetToDefauls() {
        // Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        builder.setTitle(R.string.dialog_title_reset_custom_study).setMessage(R.string.dialog_message_reset_custom_study);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT
                //KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT
                //KEY_SETTING_TODAY_NEW_CARD_LIMIT
                // learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT, String.valueOf(LazzyBeeShare.DEFAULT_MAX_LEARN_MORE_PER_DAY));
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT, String.valueOf(LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY));
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT, String.valueOf(LazzyBeeShare.DEFAULT_MAX_NEW_LEARN_PER_DAY));
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_MY_LEVEL, String.valueOf(LazzyBeeShare.DEFAULT_MY_LEVEL));
//                studyInferface._finishCustomStudy();
                _reloadRecylerView();
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

    @Override
    public int getItemCount() {
        return customStudys.size();
    }

    @Override
    public int getItemViewType(int position) {
        String setting = customStudys.get(position);
        if (setting.equals(context.getString(R.string.custom_study)))
            return TYPE_TITLE;
        else if (setting.equals(context.getString(R.string.setting_today_new_card_limit))
                || setting.equals(context.getString(R.string.setting_total_learn_per_day))
                || setting.equals(context.getString(R.string.setting_reset_to_default))
                || setting.equals(context.getString(R.string.setting_my_level))
                || setting.equals(context.getString(R.string.setting_position_meaning))
                || setting.equals(context.getString(R.string.setting_max_learn_more_per_day)))
            return TYPE_SETTING_NAME;
        else
            return -1;
    }

    public class RecyclerViewCustomStudyAdapterViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private int viewType;

        public RecyclerViewCustomStudyAdapterViewHolder(View itemView, int viewType) {
            super(itemView);
            this.view = itemView;
            this.viewType = viewType;
        }
    }

    private void getSettingLimitOrUpdate(View mCardView, final TextView lbLimit, final String key, String limit) {
        // lbLimit.setVisibility(View.VISIBLE);
        int value = 0;
        if (limit == null) {
            if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_MAX_NEW_LEARN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT)) {
                value = LazzyBeeShare.MAX_REVIEW_LEARN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT)) {
                value = LazzyBeeShare.DEFAULT_MAX_LEARN_MORE_PER_DAY;
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
        final TextView lbEror = (TextView) viewDialog.findViewById(R.id.lbEror);
        final EditText txtLimit = (EditText) viewDialog.findViewById(R.id.txtLimit);

        if (key == LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_new_card_limit_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_today_review_card_limit_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_total_card_learn_pre_day_by);
        } else if (key == LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT) {
            message = context.getString(R.string.dialog_message_setting_max_learn_more_per_day_by);
        }

        lbSettingLimitName.setText(message);
        txtLimit.setText(String.valueOf(value));
        txtLimit.setFocusableInTouchMode(true);
        txtLimit.setFocusable(true);
        txtLimit.requestFocus();

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));

        // Chain together various setter methods to set the dialog characteristics
        // builder.setMessage(R.string.dialog_message_setting_today_new_card_limit_by);
        builder.setView(viewDialog);

        // Add the buttons
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
                //main.hide();
            }
        });
        builder.setPositiveButton(R.string.ok, null);

        // Get the AlertDialog from create()
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialog1) {

                Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String limit = txtLimit.getText().toString();
                        Log.e(TAG, limit);
                        if (key == LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT) {
                            int total = learnApiImplements._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
                            if (Integer.valueOf(limit) > total) {
                                String erorr_message = context.getString(R.string.custom_study_eror_limit) + " < " + context.getString(R.string.setting_total_learn_per_day) + "(" + context.getString(R.string.setting_limit_card_number, total) + ")";
                                Log.e(TAG, erorr_message);
                                lbEror.setText(erorr_message);
                            } else if (Integer.valueOf(limit) < total && Integer.valueOf(limit) > LazzyBeeShare.MAX_NEW_PRE_DAY) {
                                String erorr_message = context.getString(R.string.custom_study_eror_limit) + " < (" + context.getString(R.string.setting_limit_card_number, LazzyBeeShare.MAX_NEW_PRE_DAY) + ")";
                                Log.e(TAG, erorr_message);
                                lbEror.setText(erorr_message);
                            } else {
                                learnApiImplements._insertOrUpdateToSystemTable(key, limit);
                                //main.hide();
                                dialog.dismiss();
                                Log.e(TAG, "Update 1");
//                                studyInferface._finishCustomStudy();
                                _reloadRecylerView();
                            }
                        } else if (key == LazzyBeeShare.KEY_SETTING_TODAY_REVIEW_CARD_LIMIT) {

                        } else if (key == LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT) {
                            int total = learnApiImplements._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);
                            if (Integer.valueOf(limit) < total) {
                                String erorr_message = "Limit > " + context.getString(R.string.setting_today_new_card_limit) + "(" + context.getString(R.string.setting_limit_card_number, total) + ")";
                                Log.e(TAG, erorr_message);
                                lbEror.setText(erorr_message);
                            } else {
                                learnApiImplements._insertOrUpdateToSystemTable(key, limit);
                                //main.hide();
                                dialog.dismiss();
                                Log.e(TAG, "Update 2");
//                                studyInferface._finishCustomStudy();
                                _reloadRecylerView();
                            }
                        } else if (key == LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT) {
                            int total = learnApiImplements._getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);
                            if (Integer.valueOf(limit) > total) {
                                String erorr_message = "Limit < " + context.getString(R.string.setting_today_new_card_limit) + "(" + context.getString(R.string.setting_limit_card_number, total) + ")";
                                Log.e(TAG, erorr_message);
                                lbEror.setText(erorr_message);
                            } else {
                                learnApiImplements._insertOrUpdateToSystemTable(key, limit);
                                // main.hide();
                                dialog.dismiss();
                                Log.e(TAG, "Update 3");
//                                studyInferface._finishCustomStudy();
                                _reloadRecylerView();
                            }
                        }
                    }
                });
            }
        });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        dialog.show();
    }

    private void _setPositionMeaning(RelativeLayout mCardView, TextView lbLimit) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogLearnMore));
        builder.setTitle(context.getString(R.string.title_change_position_meaning));
        final CharSequence[] items = {context.getString(R.string.position_meaning_up), context.getString(R.string.position_meaning_down)};
        final CharSequence[] values = {LazzyBeeShare.UP, LazzyBeeShare.DOWN};
        int index = 0;
        String value = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_POSITION_MEANIG);

        if (value != null && value.equals(LazzyBeeShare.DOWN)) {
            index = 1;
            lbLimit.setText(context.getString(R.string.position_meaning_down));
        } else {
            lbLimit.setText(context.getString(R.string.position_meaning_up));
        }


        builder.setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                //Update position
                learnApiImplements._insertOrUpdateToSystemTable(LazzyBeeShare.KEY_SETTING_POSITION_MEANIG, values[item].toString());
                dialog.cancel();
                _reloadRecylerView();

            }
        });
        // Get the AlertDialog from create()
        final AlertDialog dialog = builder.create();
        mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

    }
}
