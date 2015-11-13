package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.CardDetailsActivity;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

/**
 * Created by Hue on 7/6/2015.
 */
public class RecyclerViewReviewTodayListAdapter extends RecyclerView.Adapter<RecyclerViewReviewTodayListAdapter.RecyclerViewReviewTodayListAdapterViewHolder> {
    private static final String TAG = "ReviewAdapter";
    List<Card> vocabularies;
    private Context context;
    private LearnApiImplements learnApiImplements;
    private RecyclerView mRecyclerViewReviewTodayList;
    private TextView lbCountReviewCard;

    public RecyclerViewReviewTodayListAdapter(Context context, RecyclerView mRecyclerViewReviewTodayList, List<Card> vocabularies, TextView lbCountReviewCard) {
        this.context = context;
        this.vocabularies = vocabularies;
        this.learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        this.mRecyclerViewReviewTodayList = mRecyclerViewReviewTodayList;
        this.lbCountReviewCard = lbCountReviewCard;
    }

    @Override
    public RecyclerViewReviewTodayListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_list_result, parent, false); //Inflating the layout
        RecyclerViewReviewTodayListAdapterViewHolder recyclerViewReviewTodayListAdapterViewHolder = new RecyclerViewReviewTodayListAdapterViewHolder(view);
        return recyclerViewReviewTodayListAdapterViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewReviewTodayListAdapterViewHolder holder, final int position) {
        //Define view
        final View view = holder.view;
        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);
        TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
        TextView lbMeaning = (TextView) view.findViewById(R.id.lbAnswer);
        TextView level = (TextView) view.findViewById(R.id.level);
        final TextView learned = (TextView) view.findViewById(R.id.learned);
        TextView lbPronoun = (TextView) view.findViewById(R.id.lbPronoun);

        TextView lbIgnore = (TextView) view.findViewById(R.id.lbIgnore);
        TextView lbLearned = (TextView) view.findViewById(R.id.lbLearned);
        LinearLayout mDetailsCard = (LinearLayout) view.findViewById(R.id.mDetailsCard);
        try {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, null);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.bottom_wrapper));


            //get Card by position
            final Card card = vocabularies.get(position);

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
            lbLearned.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // Toast.makeText(context, "Learned Card:"+position, Toast.LENGTH_SHORT).show();
                        ignoreAndLearnedCard(mRecyclerViewReviewTodayList.getChildAdapterPosition(view), Card.QUEUE_DONE_2);
                    } catch (Exception e) {
                        LazzyBeeShare.showErrorOccurred(context, e);
                    }
                }
            });
            lbIgnore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        // Toast.makeText(context, "Ignore Card:"+position, Toast.LENGTH_SHORT).show();
                        ignoreAndLearnedCard(mRecyclerViewReviewTodayList.getChildAdapterPosition(view), Card.QUEUE_SUSPENDED_1);
                    } catch (Exception e) {
                        LazzyBeeShare.showErrorOccurred(context, e);
                    }
                }
            });
            mDetailsCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cardId = String.valueOf(card.getId());
                    Intent intent = new Intent(context, CardDetailsActivity.class);
                    intent.putExtra(LazzyBeeShare.CARDID, cardId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }

    }

    private void ignoreAndLearnedCard(int position, int queue) {
        try {
            Log.i(TAG, "position:" + position);
            Card card = vocabularies.get(position);
            card.setQueue(queue);

            //Update Card in server
            learnApiImplements._updateCard(card);

            //re display Icoming list
            vocabularies.remove(card);

            //remove card id in Incomming List
            learnApiImplements.saveIncomingCardIdList(learnApiImplements._converlistCardToListCardId(vocabularies));

            //set size
            lbCountReviewCard.setText(context.getString(R.string.message_total_card_incoming) + vocabularies.size());
            lbCountReviewCard.setTag(vocabularies.size());
            //reset adapter
            //mRecyclerViewReviewTodayList.setAdapter(this);
            mRecyclerViewReviewTodayList.getAdapter().notifyItemRemoved(position);
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, e);
        }
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
    public List<Card> getVocabularies() {
        return vocabularies;
    }

    public void setVocabularies(List<Card> vocabularies) {
        this.vocabularies = vocabularies;
    }

}
