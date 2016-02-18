package com.born2go.lazzybee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;

/**
 * Created by Hue on 2/18/2016.
 */
public class SuggestionCardHomeAdapter extends ArrayAdapter<String> {

    public SuggestionCardHomeAdapter(Context context, List<String> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String question = getItem(position);
        Card card = LazzyBeeSingleton.learnApiImplements._getCardByQuestion(question);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_card_result_suggesstion_home, parent, false);
        }
        String meaning = LazzyBeeShare._getValueFromKey(card.getAnswers(), LazzyBeeShare.CARD_MEANING);
        TextView lbQuestion = (TextView) convertView.findViewById(R.id.lbQuestion);
        TextView lbAnswer = (TextView) convertView.findViewById(R.id.lbAnswer);
        lbQuestion.setText(card.getQuestion());
        lbAnswer.setText(meaning);
        convertView.setId(card.getId());
        return convertView;
    }
}
