package com.born2go.lazzybee.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Course;

import java.util.List;

/**
 * Created by Hue on 7/2/2015.
 */
public class RecyclerViewListCourseAdapter extends RecyclerView.Adapter<RecyclerViewListCourseAdapter.RecyclerViewListCourseAdapterViewHoler> {
    private static final String TAG = "ListCourseAdapter";
    final List<Course> objectList;

    public RecyclerViewListCourseAdapter(List<Course> objectList) {
        this.objectList = objectList;
    }

    @NonNull
    @Override
    public RecyclerViewListCourseAdapterViewHoler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_coures, parent, false); //Inflating the layout
        //init viewholder
        return new RecyclerViewListCourseAdapterViewHoler(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewListCourseAdapterViewHoler holder, int position) {
        //get view form holder
        View view = holder.view;
        //init lbNameCourse
        TextView lbNameCourse = view.findViewById(R.id.lbNameCourse);
        //get course form list by position
        Course course = objectList.get(position);
        //set data
        lbNameCourse.setText(course.getName());
        //setBackgroundColor
        if (position % 2 == 0) {
            view.setBackgroundColor(Color.parseColor("#ff009688"));
        }


    }

    @Override
    public int getItemCount() {
        return objectList.size();
    }

    public class RecyclerViewListCourseAdapterViewHoler extends RecyclerView.ViewHolder {
        private final View view;

        public RecyclerViewListCourseAdapterViewHoler(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}
