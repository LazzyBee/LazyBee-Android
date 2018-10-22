package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

/**
 * Created by Hue on 9/12/2015.
 */
public class GetCardFormServerByQuestion extends AsyncTask<Card, Void, Card> {

    private static final String TAG = "GetCardFormServer";
    private final ProgressDialog dialog;
    private final ConnectGdatabase connectGdatabase;
    public GetCardFormServerByQuestionResponse delegate = null;
    private final String msg_Loading;

    public GetCardFormServerByQuestion(Context context, Card card) {
        dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        LearnApiImplements learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        connectGdatabase = LazzyBeeSingleton.connectGdatabase;
        if (card != null)
            msg_Loading = context.getString(R.string.msg_find_card_question, card.getQuestion());
        else
            msg_Loading = context.getString(R.string.msg_upadte_card);
    }

    protected void onPreExecute() {
        //set up dialog
        this.dialog.setMessage(msg_Loading);
        this.dialog.show();
    }

    @Override
    protected Card doInBackground(Card... params) {
        //Call Api Update card
        Log.i(TAG, "Question:" + params[0].getQuestion());

        String q = params[0].getQuestion(); //Define q
        long gId = params[0].getgId(); //Define gId

        Log.d(TAG, "q:" + q + ",gId:" + gId);

        Voca voca;//Define voca

        if (gId > 0) {
            voca = connectGdatabase._getGdatabase_byID(gId);//get voca by gID
        } else {
            voca = connectGdatabase._getGdatabase_byQ(q);//get voca by question
        }
        if (voca != null) {
            return defineCardbyVoca(params[0], voca);
        } else {
            return null;
        }
    }

    private Card defineCardbyVoca(Card _card, Voca voca) {
        try {
            Log.d(TAG, "voca:\t Q:" + voca.getQ() +
                    "\n,level:" + voca.getLevel() +
                    "\n,package:" + voca.getPackages() +
                    "\nLVN=:" + voca.getLVn() +
                    "\n,LEN=:" + voca.getLEn());
            Card card = new Card();

            card.setgId(voca.getGid());
            card.setQuestion(voca.getQ());
            card.setAnswers(voca.getA());
            card.setPackage(voca.getPackages());
            card.setLevel(voca.getLevel());


            card.setId(_card.getId());
            card.setLast_ivl(_card.getLast_ivl());
            card.setFactor(_card.getFactor());
            card.setRev_count(_card.getRev_count());
            card.setDue(_card.getDue());
            card.setQueue(_card.getQueue());

            card.setL_en(voca.getLEn());
            card.setL_vn(voca.getLVn());


            return card;
        } catch (Exception e) {
            Log.e(TAG, "Error getVoca:" + e.getMessage());
            e.printStackTrace();
            //noinspection AccessStaticViaInstance
            LazzyBeeSingleton.getCrashlytics().logException(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Card card) {
        super.onPostExecute(card);
        //Dismis dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        delegate.processFinish(card);
    }

    public interface GetCardFormServerByQuestionResponse {
        void processFinish(Card card);
    }
}
