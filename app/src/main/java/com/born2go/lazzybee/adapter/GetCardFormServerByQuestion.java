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

    private static final String TAG = "UpdateContenCard";
    private ProgressDialog dialog;
    private LearnApiImplements learnApiImplements;
    public AsyncResponse delegate = null;
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

        try {
            //Define q
            String q = params[0].getQuestion();

            //Get voca in Server
            Voca voca = connectGdatabase._getGdatabase_byQ(q.replaceAll("\\s+", "") /*Remove remove special characters*/);
            if (voca != null) {
                Log.i(TAG, "voca:\t Q:" + voca.getQ() + ",level:" + voca.getLevel() + ",package:" + voca.getPackages());
                Card card = new Card();
                card.setId(params[0].getId());

                card.setQuestion(params[0].getQuestion());
                card.setLevel(Integer.valueOf(params[0].getLevel()));
                card.setAnswers(voca.getA());
                card.setPackage(voca.getPackages());
                card.setLast_ivl(params[0].getLast_ivl());
                card.setFactor(params[0].getFactor());
                card.setRev_count(params[0].getRev_count());
                card.setDue(params[0].getDue());
                card.setQueue(params[0].getQueue());
                return card;

            } else {
                Log.i(TAG, "get voca null");
                return null;
            }

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
        if (card != null) {
            //Update Card form DB
            learnApiImplements._updateCardFormServer(card);
        }

        delegate.processFinish(card);
    }

    public interface AsyncResponse {
        void processFinish(Card card);
    }
}
