package com.born2go.lazzybee.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.born2go.lazzybee.db.Course;

import java.util.List;

/**
 * Created by Hue on 7/1/2015.
 */
public class RecyclerViewDrawerListAdapter extends RecyclerView.Adapter<RecyclerViewDrawerListAdapter.RecyclerViewDrawerListAdapterViewHolder> {
    public static final int TYPE_COURSE = 0;
    public static final int TYPE_ADD_COURCE = 1;
    private static final String TAG = "DrawerListAdapter";
    List<Object> objectList;

    public RecyclerViewDrawerListAdapter(List<Object> objectList) {
        this.objectList = objectList;
    }

    @Override
    public RecyclerViewDrawerListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == TYPE_COURSE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coures, parent, false); //Inflating the layout
        } else if (viewType == TYPE_ADD_COURCE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_add_course, parent, false);

        }
        RecyclerViewDrawerListAdapterViewHolder recyclerViewDrawerListAdapterViewHolder = new RecyclerViewDrawerListAdapterViewHolder(v, viewType);
        return recyclerViewDrawerListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewDrawerListAdapterViewHolder holder, int position) {

        if (holder.viewType == TYPE_COURSE) {
            //
            View view=holder.view;
            Course course = (Course) objectList.get(position);
            TextView lbNameCourse = (TextView) view.findViewById(R.id.lbNameCourse);
            //TextView lbCountTotalVocabulary = (TextView) rowView.findViewById(R.id.lbCountTotalVocabulary);
            lbNameCourse.setText(course.getName());
            lbNameCourse.setTag(LazzyBeeShare.COURSE_ID_TEST);
            if (position % 2 == 0) {
                view.setBackgroundColor(R.color.material_deep_teal_500);
            } else {

            }
        } else if (holder.viewType == TYPE_ADD_COURCE) {

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (objectList.get(position) instanceof Course) {
            return TYPE_COURSE;
        } else {
            return TYPE_ADD_COURCE;
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
