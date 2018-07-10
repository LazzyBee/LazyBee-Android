package com.born2go.lazzybee.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.GroupVoca;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.Voca;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hue on 8/15/2016.
 */
public class AddCustomListToIncomingList extends AsyncTask<GroupVoca, Void, Void> {
    private static final String TAG = AddCustomListToIncomingList.class.getSimpleName();
    private final ProgressDialog dialog;

    List<String> incomingList = new ArrayList<>();
    List<String> newIncomingList = new ArrayList<>();
    List<String> defaultIncomingLists = new ArrayList<>();

    LearnApiImplements learnApiImplements;
    private Context context;

    public interface IAddCustomListToIncomingList {
        void processFinish();
    }

    public IAddCustomListToIncomingList iAddCustomListToIncomingList;


    public AddCustomListToIncomingList(Context context) {
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        learnApiImplements = LazzyBeeSingleton.learnApiImplements;
    }

    protected void onPreExecute() {
        //set up dialog
        this.dialog.setTitle("Please waiting ...");
        this.dialog.setMessage("Loading...");
        this.dialog.show();
    }

    @Override
    protected Void doInBackground(GroupVoca... groupVocas) {
        //
        GroupVoca groupVoca = groupVocas[0];

        String listVoca = groupVoca.getListVoca();
        // split break
        String[] questions_n = listVoca.split("\\n");
        List<String> question = new ArrayList<>();

        for (String line : questions_n) {
            //split commma
            if (line.contains(",")) {
                String[] questions_comma = line.split(",");
                question.addAll(Arrays.asList(questions_comma));
            } else {
                question.add(line);
            }
        }

        for (String q : question) {
            int cardId = learnApiImplements._getCardIDByQuestion(q.trim().toLowerCase());
            if (cardId > 0) {
                newIncomingList.add(String.valueOf(cardId));
                int update = learnApiImplements.markCustomList(cardId);
                Log.d(TAG, "-Mark card Id : " + cardId + " into custom list,Update : " + update);

            } else {
                Voca voca = LazzyBeeSingleton.connectGdatabase._getGdatabase_byQ(q);
                if (voca != null) {
                    Card card = defineCardbyVoca(voca);
                    //Inset new card
                    long _cardId = learnApiImplements.insertCard(card);
                    if (_cardId > 0) {
                        newIncomingList.add(String.valueOf(_cardId));
                        int _update = learnApiImplements.markCustomList((int) _cardId);
                        Log.d(TAG, "-Mark card Id : " + _cardId + " into custom list,Update : " + _update);
                    }

                }
            }
        }

        learnApiImplements._initIncomingCardIdList();
//        if (newIncomingList.size() > 0) {
//            String list100Card = learnApiImplements._getValueFromSystemByKey(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST);
//            try {
//                JSONObject valueObj = new JSONObject(list100Card);
//                JSONArray listIdArray = valueObj.getJSONArray("card");
//                for (int i = 0; i < listIdArray.length(); i++) {
//                    String _cardId = listIdArray.getString(i);
//                    defaultIncomingLists.add(String.valueOf(_cardId));
//                }
//
//                List<String> clone_newIncomingList = new ArrayList<>(newIncomingList);
//                for (String cardId : clone_newIncomingList) {
//                    if (defaultIncomingLists.contains(cardId)) {
//                        newIncomingList.remove(cardId);
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            incomingList.addAll(newIncomingList);
//            incomingList.addAll(defaultIncomingLists);
//            Log.d(TAG, "-new incoming list:" + newIncomingList.toString());
//            Log.d(TAG, "-default incoming list:" + defaultIncomingLists.toString());
//            Log.d(TAG, "-incoming list:" + incomingList.toString());
//
//            learnApiImplements.saveIncomingCardIdList(incomingList);
//        } else {
//            Log.d(TAG, "-Empty new incoming list");
//        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Dismis dialog
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        iAddCustomListToIncomingList.processFinish();
    }

    private Card defineCardbyVoca(Voca voca) {
        try {
            Card card = new Card();

            card.setgId(voca.getGid());
            card.setQuestion(voca.getQ());
            card.setAnswers(voca.getA());
            card.setPackage(voca.getPackages());
            card.setLevel(Integer.valueOf(voca.getLevel()));
            card.setL_en(voca.getLEn());
            card.setL_vn(voca.getLVn());


            return card;
        } catch (Exception e) {
            Log.e(TAG, "Error getVoca:" + e.getMessage());
            e.printStackTrace();
            LazzyBeeSingleton.getCrashlytics().logException(e);
            return null;
        }
    }


}
