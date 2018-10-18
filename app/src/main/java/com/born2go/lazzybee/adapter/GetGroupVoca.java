package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.born2go.lazzybee.db.api.ConnectGdatabase;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.GroupVoca;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

/**
 * Created by Hue on 8/6/2016.
 */
public class GetGroupVoca extends AsyncTask<Long, Void, GroupVoca> {
    private static final String TAG = "GetGroupVoca";
    private final ProgressDialog dialog;
    private final LearnApiImplements learnApiImplements;
    private final ConnectGdatabase connectGdatabase;

    public interface IGetGroupVoca {
        void processFinish(GroupVoca groupVoca);
    }

    public IGetGroupVoca iGetGroupVoca;

    public GetGroupVoca(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        connectGdatabase = LazzyBeeSingleton.connectGdatabase;
    }

    protected void onPreExecute() {
        //set up dialog
        this.dialog.setTitle("Please waiting ...");
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected GroupVoca doInBackground(Long... params) {
        Long groupId = params[0];
        GroupVoca groupVoca = connectGdatabase._getGroupVoca(groupId);
        if (groupVoca != null) {
            Log.d(TAG, "List voca:" + groupVoca.getListVoca());
//            String[] questions = groupVoca.getListVoca().split("\\n");
//            for (String q : questions) {
//                int cardId = learnApiImplements._getCardIDByQuestion(q);
//                if (cardId <= 0) {
//                    Voca voca=connectGdatabase._getGdatabase_byQ(q);
//                    if (voca!=null){
//                        //Save new card
//                        Card card = new Card();
//
//                        card.setgId(voca.getGid());
//                        card.setQuestion(voca.getQ());
//                        card.setAnswers(voca.getA());
//                        card.setPackage(voca.getPackages());
//                        card.setLevel(Integer.valueOf(voca.getLevel()));
//                        card.setL_en(voca.getLEn());
//                        card.setL_vn(voca.getLVn());
//                        learnApiImplements._insertOrUpdateCard(card);
//                    }
//                }
//            }
            return groupVoca;
        } else return null;
    }

    @Override
    protected void onPostExecute(GroupVoca groupVoca) {
        super.onPostExecute(groupVoca);
        //Dismis dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        iGetGroupVoca.processFinish(groupVoca);
    }

}
