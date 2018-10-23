package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.activity.CardDetailsActivity;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;
import com.daimajia.swipe.SwipeLayout;

import java.util.List;

/**
 * Created by Hue on 7/7/2015.
 */
public class RecyclerViewSearchResultListAdapter extends RecyclerView.Adapter<RecyclerViewSearchResultListAdapter.RecyclerViewSearchResultListAdapterViewHolder> {
    private static final String TAG = "SearchAdapter";
    private final List<Card> vocabularies;
    private final Context context;
    private final String mySubject;

    public RecyclerViewSearchResultListAdapter(Context context, List<Card> vocabularies) {
        this.context = context;
        this.vocabularies = vocabularies;
        this.mySubject=LazzyBeeShare.getMySubject();
    }

    @NonNull
    @Override
    public RecyclerViewSearchResultListAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_card_list_result, parent, false); //Inflating the layout
        return new RecyclerViewSearchResultListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewSearchResultListAdapterViewHolder holder, final int position) {
        //
        final View view = holder.view;
        TextView lbQuestion = view.findViewById(R.id.lbQuestion);
        TextView lbMeaning = view.findViewById(R.id.lbAnswer);
        TextView level = view.findViewById(R.id.level);
        TextView learned = view.findViewById(R.id.learned);
        TextView lbPronoun = view.findViewById(R.id.lbPronoun);
        LinearLayout mDetailsCard = view.findViewById(R.id.mDetailsCard);

        final com.daimajia.swipe.SwipeLayout swipeLayout = view.findViewById(R.id.swipeLayout);
        //Define action card
        TextView lbIgnore = view.findViewById(R.id.lbIgnore);
        TextView lbLearned = view.findViewById(R.id.lbLearned);
        TextView lbAdd = view.findViewById(R.id.lbAdd);
        lbIgnore.setVisibility(View.GONE);
        lbLearned.setVisibility(View.GONE);
        try {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, null);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.bottom_wrapper));
            //get Card by position
            final Card card = vocabularies.get(position);

            String pronoun = card.getPronoun();//LazzyBeeShare._getValueFromKey(card.getAnswers(), LazzyBeeShare.CARD_PRONOUN);
            String meaning = card.getMeaning(mySubject);//LazzyBeeShare._getValueFromKey(card.getAnswers(), LazzyBeeShare.CARD_MEANING);

            lbQuestion.setText(card.getQuestion());
            lbQuestion.setTag(card);
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
            mDetailsCard.setOnClickListener(v -> {
                String cardId = String.valueOf(card.getId());
                Intent intent = new Intent(context, CardDetailsActivity.class);
                intent.putExtra(LazzyBeeShare.CARDID, cardId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            });

            //Handel action add card to learn
            lbAdd.setOnClickListener(v -> {
                try {
                    LazzyBeeSingleton.learnApiImplements._addCardIdToQueueList(card);
                    notifyItemChanged(position);
                    Toast.makeText(context, context.getString(R.string.message_action_add_card_to_learn_complete, card.getQuestion()), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    LazzyBeeShare.showErrorOccurred(context, "1_onBindViewHolder", e);
                }

            });
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onBindViewHolder", e);
        }
    }

    @Override
    public int getItemCount() {
        return vocabularies.size();
    }

    public class RecyclerViewSearchResultListAdapterViewHolder extends RecyclerView.ViewHolder {
        private final View view;

        public RecyclerViewSearchResultListAdapterViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}

