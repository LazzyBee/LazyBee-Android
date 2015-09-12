package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca;

/**
 * Created by Hue on 9/12/2015.
 */
public class UpdateContenCardFormServer extends AsyncTask<Card, Void, Card> {

    private static final String TAG = "UpdateContenCard";
    private ProgressDialog dialog;
    private LearnApiImplements learnApiImplements;
    public AsyncResponse delegate=null;

    public UpdateContenCardFormServer(Context context) {
        dialog = new ProgressDialog(context);
        learnApiImplements = new LearnApiImplements(context);
    }

    protected void onPreExecute() {
        //set up dialog
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected Card doInBackground(Card... params) {
        //Call Api Update card
        Log.i(TAG, "Q:" + params[0]);
        ConnectGdatabase connectGdatabase = new ConnectGdatabase();
        try {
            //Get voca in Server
            Voca voca = connectGdatabase._getGdatabase_byQ(params[0].getQuestion());
            Card card = new Card();
            card.setId(params[0].getId());
            card.setQuestion(voca.getQ());
            card.setLevel(Integer.valueOf(voca.getLevel()));
            card.setAnswers(voca.getA());
            card.setPackage(voca.getPackages());
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
        //Update Card form DB
        learnApiImplements._updateCardFormServer(card);

        delegate.processFinish(card);
    }
    public interface AsyncResponse {
        void processFinish(Card card);
    }
}
