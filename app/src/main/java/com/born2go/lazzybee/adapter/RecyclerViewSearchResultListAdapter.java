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
 * Created by Hue on 7/7/2015.
 */
public class RecyclerViewSearchResultListAdapter extends RecyclerView.Adapter<RecyclerViewSearchResultListAdapter.RecyclerViewSearchResultListAdapterViewHolder> {
    private List<Card> vocabularies;

    public RecyclerViewSearchResultListAdapter(List<Card> vocabularies) {
        this.vocabularies = vocabularies;
    }

    @Override
    public RecyclerViewSearchResultListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_vocabulary_review_list, parent, false); //Inflating the layout
        RecyclerViewSearchResultListAdapterViewHolder recyclerViewReviewTodayListAdapterViewHolder = new RecyclerViewSearchResultListAdapterViewHolder(view);
        return recyclerViewReviewTodayListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewSearchResultListAdapterViewHolder holder, int position) {
        //get Card by position
        Card card = vocabularies.get(position);
        //
        View view = holder.view;
        TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
        //Set data
        String cardId = String.valueOf(card.getId());
        lbQuestion.setTag(cardId);
        TextView lbAnswer = (TextView) view.findViewById(R.id.lbAnswer);

        lbQuestion.setText(card.getQuestion());
        lbAnswer.setText(card.getAnswers());

    }

    @Override
    public int getItemCount() {
        return vocabularies.size();
    }

    public class RecyclerViewSearchResultListAdapterViewHolder extends RecyclerView.ViewHolder {
        private View view;

        public RecyclerViewSearchResultListAdapterViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}

