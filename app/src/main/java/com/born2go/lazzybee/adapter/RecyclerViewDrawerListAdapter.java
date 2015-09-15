package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    List<String> objectList;
    Context context;
    LearnApiImplements learnApiImplements;

    public RecyclerViewDrawerListAdapter(Context context, List<String> objectList) {
        this.objectList = objectList;
        this.context = context;
        learnApiImplements= LazzyBeeSingleton.learnApiImplements;
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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer, parent, false);
        } else if (viewType == TYPE_ABOUT) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_drawer, parent, false);
        } else if (viewType == TYPE_LINES) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lines, parent, false);
        }
        RecyclerViewDrawerListAdapterViewHolder recyclerViewDrawerListAdapterViewHolder = new RecyclerViewDrawerListAdapterViewHolder(v, viewType);
        return recyclerViewDrawerListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewDrawerListAdapterViewHolder holder, int position) {

        View view = holder.view;
        if (holder.viewType == TYPE_COURSE) {
//            Course course = (Course) objectList.get(position);
            TextView lbNameCourse = (TextView) view.findViewById(R.id.lbNameCourse);
            TextView lbCount = (TextView) view.findViewById(R.id.lbCountMyWord);

            int allCount = learnApiImplements._getAllListCard().size();

            lbNameCourse.setText(String.valueOf( objectList.get(position)));
            lbNameCourse.setTag(LazzyBeeShare.COURSE_ID_TEST);

            lbCount.setText(context.getString(R.string.setting_limit_card_number,allCount));

        } else if (holder.viewType == TYPE_ADD_COURCE) {

        } else if (holder.viewType == TYPE_USER) {

        } else if (holder.viewType == TYPE_TITLE_COURSE) {
            TextView lbDrawerName = (TextView) view.findViewById(R.id.lbDrawerName);
            lbDrawerName.setText(context.getString(R.string.my_course));
            lbDrawerName.setTextSize(15f);
            lbDrawerName.setTextColor(context.getResources().getColor(R.color.grey_300));
        } else if (holder.viewType == TYPE_SETTING) {
            TextView lbDrawerName = (TextView) view.findViewById(R.id.lbDrawerName);
            lbDrawerName.setText(context.getString(R.string.action_settings));
        } else if (holder.viewType == TYPE_ABOUT) {
            TextView lbDrawerName = (TextView) view.findViewById(R.id.lbDrawerName);
            lbDrawerName.setText(context.getString(R.string.setting_about));
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (objectList.get(position).equals("English Word"))
            return TYPE_COURSE;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_title_course)))
            return TYPE_TITLE_COURSE;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_user)))
            return TYPE_USER;
        else if (objectList.get(position).equals(context.getString(R.string.drawer_setting)))
            return TYPE_SETTING;
        else if (objectList.get(position).equals(LazzyBeeShare.DRAWER_ABOUT))
            return TYPE_ABOUT;
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
