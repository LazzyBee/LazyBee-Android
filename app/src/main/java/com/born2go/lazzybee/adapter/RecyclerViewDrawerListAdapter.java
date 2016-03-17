package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

/**
 * Created by Hue on 7/1/2015.
 */
public class RecyclerViewDrawerListAdapter extends RecyclerView.Adapter<RecyclerViewDrawerListAdapter.RecyclerViewDrawerListAdapterViewHolder> {

    private static final String TAG = "DrawerListAdapter";

    public static final int TYPE_COURSE = 0;
    public static final int TYPE_ADD_COURCE = 1;
    public static final int TYPE_USER = 2;
    private static final int TYPE_TITLE_COURSE = 3;
    private static final int TYPE_SETTING = 4;
    private static final int TYPE_ABOUT = 5;
    private static final int TYPE_LINES = 6;
    private static final int TYPE_HELP = 7;

    List<String> objectList;
    Context context;
    LearnApiImplements learnApiImplements;

    public RecyclerViewDrawerListAdapter(Context context, List<String> objectList) {
        this.objectList = objectList;
        this.context = context;
        learnApiImplements = LazzyBeeSingleton.learnApiImplements;
    }

    @Override
    public RecyclerViewDrawerListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == TYPE_COURSE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coures, parent, false); //Inflating the layout
        } else if (viewType == TYPE_ADD_COURCE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_course, parent, false);
        } else if (viewType == TYPE_USER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_profile, parent, false);
        } else if (viewType == TYPE_TITLE_COURSE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer, parent, false);
        } else if (viewType == TYPE_SETTING) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer_setting, parent, false);
        } else if (viewType == TYPE_ABOUT) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer_icon, parent, false);
        } else if (viewType == TYPE_LINES) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lines, parent, false);
        } else if (viewType == TYPE_HELP) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer_icon, parent, false);
        }
        RecyclerViewDrawerListAdapterViewHolder recyclerViewDrawerListAdapterViewHolder = new RecyclerViewDrawerListAdapterViewHolder(v, viewType);
        return recyclerViewDrawerListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewDrawerListAdapterViewHolder holder, int position) {
        try {
            View view = holder.view;
            if (holder.viewType == TYPE_COURSE) {
                ImageView mImg = (ImageView) view.findViewById(R.id.mImg);

                TextView lbNameCourse = (TextView) view.findViewById(R.id.lbNameCourse);
                TextView lbCount = (TextView) view.findViewById(R.id.lbCountMyWord);
                lbNameCourse.setText(String.valueOf(objectList.get(position)));
                lbCount.setVisibility(View.GONE);
                if (objectList.get(position).equals("English Word")) {
                    lbCount.setVisibility(View.VISIBLE);
                    lbNameCourse.setTag(LazzyBeeShare.COURSE_ID_TEST);
                    int allCount = learnApiImplements._getCountAllListCard();

                    lbNameCourse.setText(String.valueOf(objectList.get(position)));
                    lbNameCourse.setTag(LazzyBeeShare.COURSE_ID_TEST);

                    lbCount.setText(context.getString(R.string.setting_limit_card_number, allCount));
                } else if (objectList.get(position).equals(context.getString(R.string.drawer_dictionary))) {
                    mImg.setImageDrawable(LazzyBeeShare.getDraweble(context, R.drawable.ic_dictionary));
                    lbCount.setVisibility(View.GONE);
                } else if (objectList.get(position).equals(context.getString(R.string.drawer_subject))) {
                    mImg.setImageDrawable(LazzyBeeShare.getDraweble(context, R.drawable.ic_list));
                    String my_subject = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MY_SUBJECT);
                    if (my_subject != null) {
                        lbCount.setVisibility(View.VISIBLE);
                        if (my_subject.equals(context.getString(R.string.subject_it_value)))
                            lbCount.setText(context.getString(R.string.subject_it));
                        else if (my_subject.equals(context.getString(R.string.subject_economy_value)))
                            lbCount.setText(context.getString(R.string.subject_economy));
                        else if (my_subject.equals(context.getString(R.string.subject_science_value)))
                            lbCount.setText(context.getString(R.string.subject_science));
                        else if (my_subject.equals(context.getString(R.string.subject_medical_value)))
                            lbCount.setText(context.getString(R.string.subject_medical));
                        else if (my_subject.equals(context.getString(R.string.subject_ielts_value)))
                            lbCount.setText(context.getString(R.string.subject_ielts));
                        else if (my_subject.equals(context.getString(R.string.subject_600_toeic_value)))
                            lbCount.setText(context.getString(R.string.subject_600toeic));
                        else
                            lbCount.setText(LazzyBeeShare.EMPTY);
                    }
                } else if (objectList.get(position).equals(context.getString(R.string.setting_about_message))) {
                    lbNameCourse.setTextColor(context.getResources().getColor(R.color.grey_300));
                    lbNameCourse.setTextSize(15f);
                } else if (objectList.get(position).equals(context.getString(R.string.drawer_statistical))) {
                    mImg.setImageDrawable(LazzyBeeShare.getDraweble(context, R.drawable.ic_graph));
                } else if (objectList.get(position).equals(context.getString(R.string.drawer_home))) {
                    mImg.setImageDrawable(LazzyBeeShare.getDraweble(context, R.drawable.ic_home));
                }
            } else if (holder.viewType == TYPE_ADD_COURCE) {

            } else if (holder.viewType == TYPE_USER) {

            } else if (holder.viewType == TYPE_TITLE_COURSE) {
                TextView lbDrawerName = (TextView) view.findViewById(R.id.lbDrawerName);
                lbDrawerName.setText(context.getString(R.string.my_course));
                lbDrawerName.setTextSize(15f);
                lbDrawerName.setTextColor(context.getResources().getColor(R.color.grey_300));
            } else if (holder.viewType == TYPE_SETTING) {
                ImageView mNoti = (ImageView) view.findViewById(R.id.mNoti);
                if (learnApiImplements._checkUpdateDataBase()) {
                    mNoti.setVisibility(View.VISIBLE);
                } else {
                    mNoti.setVisibility(View.GONE);
                }
            } else if (holder.viewType == TYPE_ABOUT) {
                ImageView mImg = (ImageView) view.findViewById(R.id.mImg);
                mImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_about));
                TextView lbDrawerName = (TextView) view.findViewById(R.id.mTextView);
                lbDrawerName.setText(context.getString(R.string.setting_about));
            } else if (holder.viewType == TYPE_HELP) {
                ImageView mImg = (ImageView) view.findViewById(R.id.mImg);
                mImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_help));
                TextView lbDrawerName = (TextView) view.findViewById(R.id.mTextView);
                lbDrawerName.setText(context.getString(R.string.drawer_help));
            }

        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onBindViewHolder", e);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (objectList.get(position).equals("English Word")
                || objectList.get(position).equals(context.getString(R.string.drawer_home))
                || objectList.get(position).equals(context.getString(R.string.drawer_dictionary))
                || objectList.get(position).equals(context.getString(R.string.drawer_statistical))
                || objectList.get(position).equals(context.getString(R.string.drawer_subject)))

            return TYPE_COURSE;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_title_course)))
            return TYPE_TITLE_COURSE;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_user)))
            return TYPE_USER;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_setting)))
            return TYPE_SETTING;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_about)))
            return TYPE_ABOUT;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_help)))
            return TYPE_HELP;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_line)))
            return TYPE_LINES;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_add_course)))
            return TYPE_ADD_COURCE;
        else {
            return -1;
        }

    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class RecyclerViewDrawerListAdapterViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private int viewType;

        public RecyclerViewDrawerListAdapterViewHolder(View itemView, int viewType) {
            super(itemView);
            this.view = itemView;
            this.viewType = viewType;

        }
    }
}
