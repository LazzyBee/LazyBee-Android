package com.born2go.lazzybee.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;

import java.util.List;

/**
 * Created by Hue on 7/6/2015.
 */
public class RecyclerViewReviewTodayListAdapter extends RecyclerView.Adapter<RecyclerViewReviewTodayListAdapter.RecyclerViewReviewTodayListAdapterViewHolder> {
    private List<Card> vocabularies;

    public RecyclerViewReviewTodayListAdapter(List<Card> vocabularies) {
        this.vocabularies = vocabularies;
    }

    @Override
    public RecyclerViewReviewTodayListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_vocabulary_review_list, parent, false); //Inflating the layout
        RecyclerViewReviewTodayListAdapterViewHolder recyclerViewReviewTodayListAdapterViewHolder = new RecyclerViewReviewTodayListAdapterViewHolder(view);
        return recyclerViewReviewTodayListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewReviewTodayListAdapterViewHolder holder, int position) {
        //get Card by position
        Card card = vocabularies.get(position);
        //
        View view = holder.view;
        TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
        TextView lbAnswer = (TextView) view.findViewById(R.id.lbAnswer);

        lbQuestion.setText(card.getQuestion());
        lbAnswer.setText(card.getAnswers());

    }

    @Override
    public int getItemCount() {
        return vocabularies.size();
    }

    public class RecyclerViewReviewTodayListAdapterViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public RecyclerViewReviewTodayListAdapterViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}
