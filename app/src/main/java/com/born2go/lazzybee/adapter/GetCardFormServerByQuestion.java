package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
    private ProgressDialog dialog;
    private LearnApiImplements learnApiImplements;
    public GetCardFormServerByQuestionResponse delegate = null;
    private ConnectGdatabase connectGdatabase;

    public GetCardFormServerByQuestion(Context context) {
        dialog = new ProgressDialog(context);
        learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        connectGdatabase = LazzyBeeSingleton.connectGdatabase;
    }

    protected void onPreExecute() {
        //set up dialog
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected Card doInBackground(Card... params) {
        //Call Api Update card
        Log.i(TAG, "Question:" + params[0].getQuestion());
        //Define q
        String q = params[0].getQuestion();

        Log.i(TAG, "q:" + q + ",gId:" + params[0].getgId());

        //Voca voca = connectGdatabase._getGdatabase_byQ(q.replaceAll("\\s+", "") /*Remove remove special characters*/);

        //Get voca in Server
        Voca voca = connectGdatabase._getGdatabase_byQ(q);
        if (voca != null) {
            Log.i(TAG, "get voca by question:" + q);
            return defineCardbyVoca(params[0], voca);
        } else if (params[0].getgId() > 0) {
            Log.i(TAG, "get voca by gID:" + params[0].getgId());
            //get voca by gID
            voca = connectGdatabase._getGdatabase_byID(params[0].getgId());
            if (voca != null) {
                return defineCardbyVoca(params[0], voca);
            } else {
                return null;
            }
        } else {
            Log.i(TAG, "get voca null");
            return null;
        }


    }

    private Card defineCardbyVoca(Card _card, Voca voca) {
        try {
            Log.i(TAG, "voca:\t Q:" + voca.getQ() + ",level:" + voca.getLevel() + ",package:" + voca.getPackages());
            Card card = new Card();

            card.setgId(voca.getGid());
            card.setQuestion(voca.getQ());
            card.setAnswers(voca.getA());
            card.setPackage(voca.getPackages());
            card.setLevel(Integer.valueOf(voca.getLevel()));


            card.setId(_card.getId());
            card.setLast_ivl(_card.getLast_ivl());
            card.setFactor(_card.getFactor());
            card.setRev_count(_card.getRev_count());
            card.setDue(_card.getDue());
            card.setQueue(_card.getQueue());

            return card;
        } catch (Exception e) {
            Log.e(TAG, "Error getVoca:" + e.getMessage());
            e.printStackTrace();
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
