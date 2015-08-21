package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

/**
 * Created by Hue on 7/6/2015.
 */
public class RecyclerViewReviewTodayListAdapter extends RecyclerView.Adapter<RecyclerViewReviewTodayListAdapter.RecyclerViewReviewTodayListAdapterViewHolder> {
    private List<Card> vocabularies;
    private Context context;

    public RecyclerViewReviewTodayListAdapter(Context context, List<Card> vocabularies) {
        this.context = context;
        this.vocabularies = vocabularies;
    }

    @Override
    public RecyclerViewReviewTodayListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_list_result, parent, false); //Inflating the layout
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
        TextView lbMeaning = (TextView) view.findViewById(R.id.lbAnswer);
        TextView level = (TextView) view.findViewById(R.id.level);
        TextView learned = (TextView) view.findViewById(R.id.learned);
        TextView lbPronoun = (TextView) view.findViewById(R.id.lbPronoun);

        String meaning = LazzyBeeShare._getValueFromKey(card.getAnswers(), LazzyBeeShare.CARD_MEANING);
        String pronoun = LazzyBeeShare._getValueFromKey(card.getAnswers(), LazzyBeeShare.CARD_PRONOUN);

        lbQuestion.setText(card.getQuestion());
        lbMeaning.setText(meaning);
        lbPronoun.setText(pronoun);
        level.setText(String.valueOf(card.getLevel()));

        if (card.getQueue() >= Card.QUEUE_LNR1) {
            learned.setText(context.getResources().getString(R.string.learned));
        } else if (card.getQueue() == Card.QUEUE_DONE_2) {
            learned.setText(context.getResources().getString(R.string.done_card));
        } else if (card.getQueue() == Card.QUEUE_NEW_CRAM0) {
            learned.setText(context.getResources().getString(R.string.new_card));
        }
        learned.setVisibility(View.GONE);

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
