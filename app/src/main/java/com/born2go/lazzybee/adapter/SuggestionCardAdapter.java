package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
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


/**
 * Created by NguyenHue on 10/5/2016.
 */

public class SuggestionCardAdapter extends CursorAdapter {
    private static final String TAG = SuggestionCardAdapter.class.getSimpleName();
    private final String mySubject;

    public SuggestionCardAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.mySubject=LazzyBeeShare.getMySubject();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.row_card_list_result, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //
        TextView lbQuestion = (TextView) view.findViewById(R.id.lbQuestion);
        TextView lbMeaning = (TextView) view.findViewById(R.id.lbAnswer);
        TextView level = (TextView) view.findViewById(R.id.level);
        TextView learned = (TextView) view.findViewById(R.id.learned);
        TextView lbPronoun = (TextView) view.findViewById(R.id.lbPronoun);
        LinearLayout mDetailsCard = (LinearLayout) view.findViewById(R.id.mDetailsCard);

        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);
        //Define action card
        TextView lbIgnore = (TextView) view.findViewById(R.id.lbIgnore);
        TextView lbLearned = (TextView) view.findViewById(R.id.lbLearned);
        TextView lbAdd = (TextView) view.findViewById(R.id.lbAdd);
        lbIgnore.setVisibility(View.GONE);
        lbLearned.setVisibility(View.GONE);
        try {
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
            swipeLayout.addDrag(SwipeLayout.DragEdge.Left, null);
            swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.bottom_wrapper));
            //get Card by position
            final Card card = _defineCardbyCursor(cursor);

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
                int insertSuggesstionResults = LazzyBeeSingleton.learnApiImplements._insertSuggesstion(cardId);
                Intent intent = new Intent(context, CardDetailsActivity.class);
                intent.putExtra(LazzyBeeShare.CARDID, cardId);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
            });

            //Handel action add card to learn
            lbAdd.setOnClickListener(v -> {
                try {
                    LazzyBeeSingleton.learnApiImplements._addCardIdToQueueList(card);
                    Toast.makeText(context, context.getString(R.string.message_action_add_card_to_learn_complete, card.getQuestion()), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    LazzyBeeShare.showErrorOccurred(context, "1_onBindViewHolder", e);
                }

            });
        } catch (Exception e) {
            LazzyBeeShare.showErrorOccurred(context, "onBindViewHolder", e);
        }
    }

    private Card _defineCardbyCursor(Cursor cursor) {
        Card card = new Card();
        try {
            //get data from sqlite
            card.setId(cursor.getInt(0));
            card.setQuestion(cursor.getString(1));
            card.setAnswers(cursor.getString(2));
            card.setLevel(cursor.getInt(3));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "Define card");

        }
        return card;
    }
}
