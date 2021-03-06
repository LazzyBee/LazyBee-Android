package com.born2go.lazzybee.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.api.LearnApi;
import com.born2go.lazzybee.gdatabase.server.dataServiceApi.model.GroupVoca;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Hue on 7/8/2015.
 */
public class LearnApiImplements implements LearnApi {
    //Column name in database
    private static final String KEY_ID = "id";
    private static final String KEY_QUESTION = "question";
    private static final String KEY_ANSWERS = "answers";
    private static final String KEY_CATEGORIES = "categories";
    private static final String KEY_SUBCAT = "subcat";
    private static final String KEY_STATUS = "status";
    private static final String KEY_G_ID = "gid";
    private static final String KEY_RELATED = "related";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_PACKAGES = "package";

    private static final String KEY_TAGS = "tags";
    //Table name
    public static final String TABLE_VOCABULARY = "vocabulary";
    private static final String TABLE_SYSTEM = "system";
    private static final String TAG = "LearnApiImplements";
    private static final int STATUS_CARD_LEARN_TODAY = 1;
    private static final String QUEUE_LIST = "queue_List";
    private static final String KEY_SYSTEM = "key";
    private static final String KEY_SYSTEM_VALUE = "value";
    private static final int STATUS_NO_LEARN = -1;
    private static final String KEY_QUEUE = "queue";
    private static final String KEY_DUE = "due";
    private static final String KEY_REV_COUNT = "rev_count";
    private static final String KEY_LAT_IVL = "last_ivl";
    private static final String KEY_FACTOR = "e_factor";
    private static final String KEY_COUNT_JSON = "count";
    private static final String KEY_CARD_JSON = "card";
    public static final String KEY_L_VN = "l_vn";
    public static final String KEY_L_EN = "l_en";
    public static final String TABLE_STREAK = "streak";
    public static final String CREATE_TABLE_STREAK = "CREATE TABLE " + TABLE_STREAK + " ( day INTEGER NOT NULL, PRIMARY KEY (day) );";
    private static final String KEY_USER_NOTE = "user_note";

    public static final String TABLE_SUGGESTION = "suggestion";
    public static final java.lang.String CREATE_TABLE_SUGGESTION = "CREATE TABLE suggestion (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, suggestion TEXT UNIQUE);";
    private static final String KEY_CUSTOM_LIST = "custom_list";


    private static final int TYPE_SUGGESTION_QUESTION_CARD__SEARCH = 0;
    public static int TYPE_SUGGESTION_QUESTION_CARD__RECENT = 1;

    private final int TYPE_CARD_DETAILS_0 = 0;
    private final int TYPE_CARD_LIST_1 = 1;

    private final Context context;
    private final DataBaseHelper dataBaseHelper;

    private Random randomGenerator;


    public LearnApiImplements(Context context) {
        this.context = context;
        //init dataBaseHelper
        dataBaseHelper = new DataBaseHelper(context);
    }

    private static final int CARD_INDEX_ID = 0;
    private static final int CARD_INDEX_QUESTION = 1;
    private static final int CARD_INDEX_ANSWER = 2;
    private static final int CARD_INDEX_QUEUE = 3;
    private static final int CARD_INDEX_LEVEL = 4;

    private static final int CARD_INDEX_PACKAGE = 5;
    private static final int CARD_INDEX_CATRGORIES = 6;
    private static final int CARD_INDEX_SUBCAT = 7;
    private static final int CARD_INDEX_STATUS = 8;

    private static final int CARD_INDEX_DUE = 9;
    private static final int CARD_INDEX_REV_COUNT = 10;
    private static final int CARD_INDEX_USER_NOTE = 11;

    private static final int CARD_INDEX_LAST_IVL = 12;
    private static final int CARD_INDEX_E_FACTOR = 13;

    private static final int CARD_INDEX_GID = 14;

    private static final int CARD_INDEX_L_EN = 15;
    private static final int CARD_INDEX_L_VN = 16;
    private static final int CARD_INDEX_CUSTOM_LIST_17 = 17;

    public static int CARD_INDEX_TAGS = 17;
    public static int CARD_INDEX_RELATED = 18;

    private final String selectFull = "vocabulary.id,vocabulary.question,vocabulary.answers," +
            "vocabulary.queue,vocabulary.level," +
            "vocabulary.package,vocabulary.category," +
            "vocabulary.subcats,vocabulary.status,vocabulary.due,vocabulary.rev_count," +
            "vocabulary.user_note,vocabulary.last_ivl,vocabulary.e_factor,vocabulary.gid," +
            "vocabulary.l_en,vocabulary.l_vn,vocabulary.custom_list";
    private final String selectList = "vocabulary.id,vocabulary.question,vocabulary.answers," +
            "vocabulary.queue,vocabulary.level";
    private static final String selectSuggestionList = "vocabulary.id,vocabulary.question,vocabulary.answers";


    /**
     * Get card by ID form sqlite
     *
     * @param cardId
     */
    @Override
    public Card _getCardByID(String cardId) {
        Card card = new Card();
        try {
            String selectbyIDQuery = "SELECT " + selectFull + " FROM " + TABLE_VOCABULARY + " WHERE " + KEY_ID + " = " + cardId;
            SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();

            //query for cursor
            Cursor cursor = db.rawQuery(selectbyIDQuery, null);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0)
                    do {
                        card = _defineCardbyCursor(cursor, TYPE_CARD_DETAILS_0);
                    } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return card;
    }

    private Card _defineCardbyCursor(Cursor cursor, int type) {
        Card card = new Card();
        //get data from sqlite
        card.setId(cursor.getInt(CARD_INDEX_ID));
        card.setQuestion(cursor.getString(CARD_INDEX_QUESTION));
        card.setAnswers(cursor.getString(CARD_INDEX_ANSWER));
        if (type == TYPE_CARD_DETAILS_0) {
            card.setQueue(cursor.getInt(CARD_INDEX_QUEUE));
            card.setLevel(cursor.getInt(CARD_INDEX_LEVEL));
            card.setPackage(cursor.getString(CARD_INDEX_PACKAGE));
            card.setCategories(cursor.getString(CARD_INDEX_CATRGORIES));
            card.setSubcat(cursor.getString(CARD_INDEX_SUBCAT));


            if (cursor.getString(CARD_INDEX_STATUS) != null) {
                card.setStatus(cursor.getInt(CARD_INDEX_STATUS));
            } else {
                card.setStatus(0);
            }

            card.setDue(cursor.getLong(CARD_INDEX_DUE));

            card.setRev_count(cursor.getInt(CARD_INDEX_REV_COUNT));
            card.setUser_note(cursor.getString(CARD_INDEX_USER_NOTE));
            card.setLast_ivl(cursor.getInt(CARD_INDEX_LAST_IVL));
            card.setFactor(cursor.getInt(CARD_INDEX_E_FACTOR));

            if (cursor.getString(CARD_INDEX_GID) != null) {
                card.setgId(cursor.getLong(CARD_INDEX_GID));
            } else {
                card.setgId(0);
            }
            try {
                if (cursor.getString(CARD_INDEX_L_EN) != null)
                    card.setL_en(cursor.getString(CARD_INDEX_L_EN));

                if (cursor.getString(CARD_INDEX_L_VN) != null)
                    card.setL_vn(cursor.getString(CARD_INDEX_L_VN));

                if (cursor.getString(CARD_INDEX_L_VN) != null)
                    card.setCustom_list(cursor.getInt(CARD_INDEX_CUSTOM_LIST_17) > 0);
            } catch (Exception e) {
                Log.e(TAG, "GetCardbyID Eror:" + e.getMessage());
                card.setL_vn(LazzyBeeShare.EMPTY);
                card.setL_en(LazzyBeeShare.EMPTY);

            }
        } else if (type == TYPE_CARD_LIST_1) {
            card.setQueue(cursor.getInt(CARD_INDEX_QUEUE));
            card.setLevel(cursor.getInt(CARD_INDEX_LEVEL));
        }
        return card;
    }

    /**
     * Get list card from today
     */
    @Override
    public List<Card> _getListCardForToday() {
        return _getListCard();
    }

    /**
     * Get Review List Today
     * <p>List vocabulary complete in today</p>
     */
    @Override
    public List<Card> _getReviewListCard() {
        return _getListCardLearned();
    }

    /**
     * Seach vocabulary
     *
     * @param query
     */
    @Override
    public List<Card> _searchCard(String query) {
        //select like query
//            String likeQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " WHERE " + KEY_QUESTION + " like '%" + query + "%'"
//                + " ORDER BY (CASE WHEN " +
//                " question = '" + query + "' THEN 1 WHEN " +
//                " question LIKE '" + query + "%' THEN 2 ELSE 3 END) ";
//        if (query.equals(LazzyBeeShare.GOTO_DICTIONARY)) {
//            return _getDictionary();
//        } else {
        String likeQuery = "SELECT  " + selectList + " FROM " + TABLE_VOCABULARY + " WHERE "
                + KEY_QUESTION + " like '" + query + "%' OR "
                + KEY_QUESTION + " like '% " + query + "%'"
                + " ORDER BY " + KEY_QUESTION;
        List<Card> datas = new ArrayList<>();
        //Seach card
        if (query != null)
            datas = _getListCardQueryString(likeQuery, TYPE_CARD_LIST_1);
        return datas;
    }

    public Card _getCardByQuestion(String query) {
        Card card = null;
        String selectbyQuestionQuery = "Select " + selectFull + " from " + TABLE_VOCABULARY + " where vocabulary.question ='" + query + "'";

        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(selectbyQuestionQuery, null);
        if (cursor.getCount() > 0)
            if (cursor.moveToFirst()) {
                do {
                    card = _defineCardbyCursor(cursor, TYPE_CARD_DETAILS_0);
                } while (cursor.moveToNext());
            }
        return card;
    }

    private List<Card> _getDictionary() {
        String query = "SELECT " + selectList + " FROM " + TABLE_VOCABULARY + " order by question";
        return _getListCardQueryString(query, TYPE_CARD_LIST_1);
    }

    /**
     * Get Random list card from today
     * Check List Card
     * Get random card->update List card and update statu card
     *
     * @param number
     */
    @Override
    public List<Card> _getRandomCard(int number, boolean learnmore) {
        List<Card> datas = new ArrayList<>();
        //Check today list card
        int checkListToday = _checkListTodayExit();
        if (checkListToday > -1 && !learnmore) {

            //get data from sqlite
            String value = _getValueFromSystemByKey(QUEUE_LIST);
            Log.i(TAG, QUEUE_LIST + ":" + value);
            List<Card> allCardLearn = _getListCardFromStringArray(value);
            //
            if (number > allCardLearn.size())
                number = allCardLearn.size();

            for (int i = 0; i < number; i++) {
                datas.add(allCardLearn.get(i));
            }
        } else {
            //limit learn more =5 row
            if (learnmore)
                number = _getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);
            String value = _getValueFromSystemByKey(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST);
            List<Card> cards = _getListCardFromStringArray(value);
            Log.d(TAG, "incoming List Size:" + cards.size());
            int countNewCard = cards.size();
            if (countNewCard == 0) {//over 100 card
                countNewCard = _initIncomingCardIdList();//define new incoming list
                value = _getValueFromSystemByKey(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST);
                cards = _getListCardFromStringArray(value);
            }
            List<Card> cloneCard = new ArrayList<>(cards);
            if (countNewCard > 0) {
                if (number > LazzyBeeShare.MAX_NEW_PRE_DAY) {//newCard > size MAX_NEW_PRE_DAY
                    number = LazzyBeeShare.MAX_NEW_PRE_DAY;
                } else if (number > countNewCard) {
                    number = countNewCard;
                }
                for (int i = 0; i < number; i++) {
                    Card card = cards.get(i);//define card
                    datas.add(card);
                    cloneCard.remove(card);
                }
                Log.d(TAG, "_getRandomCard: -cards toArray after remove cards size=" + cloneCard.size());
                //remove
                _insertOrUpdatePreFetchNewCardList(cloneCard);
                _insertOrUpdateToSystemTable(QUEUE_LIST, _listCardTodayToArrayListCardId(datas, null));

            }
        }


        return datas;
    }

    private void _insertOrUpdatePreFetchNewCardList(List<Card> cards) {
        try {

            int count = cards.size();
            List<String> cardIds = _converlistCardToListCardId(cards);
            JSONObject objNewCard = new JSONObject();
            JSONArray arrCard = new JSONArray(cardIds);

            objNewCard.put(KEY_COUNT_JSON, count);
            objNewCard.put(KEY_CARD_JSON, arrCard);

            Log.i(TAG, "_insertOrUpdatePreFetchNewCardList: Count:" + count);
//            Log.i(TAG, "_insertOrUpdatePreFetchNewCardList: cards:" + cards.toString());
//            Log.i(TAG, "_insertOrUpdatePreFetchNewCardList: arrCard:" + arrCard.toString());

            _insertOrUpdateToSystemTable(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST, objNewCard.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> _converlistCardToListCardId(List<Card> cards) {
        List<String> cardIds = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            String id = String.valueOf(cards.get(i).getId());
            cardIds.add(id);
        }
        return cardIds;
    }

    public String _listCardTodayToArrayListCardId(List<Card> datas, List<String> _listCardId) {
        String jsonValuestr = "";//Todo: init string json value
        JSONObject valueJoson = new JSONObject();

        Date nowdate = new Date();
        long dayInSecond = nowdate.getTime() / 1000;//get Time by @param nowdate

        //TODO: init ListCardID
        try {
            List<String> listCardId = new ArrayList<>();
            if (_listCardId != null && !_listCardId.isEmpty()) {
                listCardId = _listCardId;
            } else {
                if (datas != null) {
                    for (Card card : datas) {
                        listCardId.add("" + card.getId());
                    }
                }
            }

            JSONArray cardIDArray = new JSONArray(listCardId);//TODO:init cardID Array

            //todo: put properties of @param valueJoson
            valueJoson.put("date", dayInSecond);
            valueJoson.put(KEY_CARD_JSON, cardIDArray);

            jsonValuestr = valueJoson.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(TAG, "jsonValuestr:" + jsonValuestr);
        return jsonValuestr;
    }

    public int _checkListTodayExit() {
        //get value queue List
        Log.i(TAG, "|---------------------_checkListTodayExit------------------|\n");
        String value = _getValueFromSystemByKey(QUEUE_LIST);
        if (value == null) {
            //NO List Queue
            Log.i(TAG, "_checkListTodayExit First Initial:Return=-2\n");
            Log.i(TAG, "|------------------------------End-------------------------|");
            return -2;
        } else {
            //Yes,Compareto Date
            try {
                //Pass string value to object
                JSONObject valueObj = new JSONObject(value);

                //get date create list today
                long dayInMilis = (valueObj.getLong("date"));//get Long date
                JSONArray listIdArray = valueObj.getJSONArray(KEY_CARD_JSON);//get List card ID
                //Log.i(TAG, "_checkListTodayExit -Long date:" + dayInMilis);
                int dayInSecond = (int) (dayInMilis);
//
                int countListId = listIdArray.length();
                //Log.i(TAG, (dayInSecond > (getStartOfDayInMillis() / 1000) && dayInSecond < getEndOfDayInSecond()) ? "_checkListTodayExit: inday" : "_checkListTodayExit: outday");

                if (dayInSecond > (LazzyBeeShare.getStartOfDayInMillis() / 1000) && dayInSecond < LazzyBeeShare.getEndOfDayInSecond()) {
                    Log.i(TAG, "_checkListTodayExit:today_parse is equal to date_now\n");
                    Log.i(TAG, "|------------------------------End-------------------------|\n");
                    return countListId;
                } else {
                    Log.i(TAG, "_checkListTodayExit:today_parse is not equal to date_now\n");
                    Log.i(TAG, "|------------------------------End-------------------------|\n");
                    return -1;
                }

            } catch (JSONException e) {
                Log.i(TAG, "_checkListTodayExit:Error Return=" + -1 + "\n");
                e.printStackTrace();
                Log.i(TAG, "|------------------------------End-------------------------|\n");
                return -1;
            }
        }

    }

    /**
     * _get List Card By List CardId JsonArray
     *
     * @param cardListIDArray JsonArray String
     * @return list card
     */
    private List<Card> _getListCardByListCardIdJsonArray(JSONArray cardListIDArray) {
        //TODO:


        List<Card> cardList = new ArrayList<>();//init Card List

        int lengh = cardListIDArray.length();//get lenght

        for (int i = 0; i < lengh; i++) {
            try {

                String cardId = cardListIDArray.getString(i);//get CardId by index

                Card card = _getCardByID(cardId);//get card by @param cardId

                cardList.add(card);//Add card to list

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return cardList;
    }

    /**
     * _export to SqlIte form ListCard
     *
     * @param cardList
     * @return 1 if _export complete else 2 to false
     */
    @Override
    public int _export(List<Card> cardList) {
        return 0;
    }

    /**
     * _updateListCardByStatus to SqlIte form ListCard
     *
     * @param cardList
     * @param status
     * @return 1 if update complete else -1 false
     */
    @Override
    public int _updateListCardByStatus(List<Card> cardList, int status) {
        return 0;
    }

    /**
     * _updateCompleteCard to SqlIte form System Table
     *
     * @param cardId
     * @return 1 if update complete else -1 false
     */
    @Override
    public int _updateCompleteCard(String cardId) {
        return 0;
    }

    /**
     * _updateQueueCard to SqlIte form System Table
     *
     * @param cardId
     * @param queue
     * @return 1 if update complete else -1 false
     */
    @Override
    public int _updateQueueCard(String cardId, long queue) {

        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(KEY_QUEUE, queue);//put Status

        //
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cardId)});
        Log.i(TAG, "Update Queue Card Complete: Update Result Code:" + update_result);
        return update_result;
    }

    /**
     * _insertListTodayCard to SqlIte form System Table
     *
     * @param cardList
     * @return 1 if update complete else -1 false
     */
    @Override
    public int _insertListTodayCard(List<Card> cardList) {
        List<String> listCardId = new ArrayList<>();
        for (Card card : cardList) {
            listCardId.add("" + card.getId());
        }
        Date date = new Date();
        JSONObject valueObj = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray(listCardId);
            valueObj.put("date", date.getTime());
            valueObj.put(KEY_CARD_JSON, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Quere Obj:" + valueObj.toString());
        return 0;
    }


    /**
     *
     * */
    private List<Card> _getListCard() {
        //select query
        String selectQuery = "SELECT  * FROM " + TABLE_VOCABULARY;
        //select limit 5 row
        String selectLimitQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " LIMIT 5 ";

        //TODO query select List Card by status=learned
        String selectListCardByStatus = "SELECT  " + selectFull + " FROM " + TABLE_VOCABULARY + " where status = 1 ";
        return _getListCardQueryString(selectQuery, 0);
    }


    @Override
    public int _updateStatusCard(String cardId, int status) {
        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_STATUS, status);//put Status

        //
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cardId)});
        Log.i(TAG, "Update Status Card Complete: Update Result Code:" + update_result);
        return update_result;

    }

    /**
     * add to system config
     * Key and value JSON
     *
     * @param key   key of system
     * @param value format json string
     * @return 1 if update complete else -1 false
     */
    @Override
    public int _insertOrUpdateToSystemTable(String key, String value) {
        //TODO check value by key
        String valuebyKey = _getValueFromSystemByKey(key);
        if (_getValueFromSystemByKey(key) == null) {
            Log.i(TAG, "Insert Key:" + key + ",value:" + value);
            //Todo: No,Then insert
            ContentValues values = new ContentValues();

            //TODO put value
            values.put(KEY_SYSTEM, key);
            values.put(KEY_SYSTEM_VALUE, value);

            //TODO insert to system table
            SQLiteDatabase db_insert = this.dataBaseHelper.getWritableDatabase();
            long long_insert_results = db_insert.insert(TABLE_SYSTEM, null, values);
            Log.i(TAG, "_insertOrUpdateToSystemTable\tInsert:KEY=" + key + "-value=" + value + "-Insert: " + long_insert_results);
            db_insert.close();
            return (int) long_insert_results;
        } else {
            SQLiteDatabase db_update = this.dataBaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            //TODO put value
            values.put(KEY_SYSTEM_VALUE, value);
            //TODO update to system table
            try {
                int update_result = db_update.update(TABLE_SYSTEM, values, KEY_SYSTEM + " = ?",
                        new String[]{String.valueOf(key)});
                Log.i(TAG, "_insertOrUpdateToSystemTable\tUpdate:KEY=" + key + "-value=" + value + "-Update: " + (update_result == 1 ? "OK" : "False") + "_" + update_result);
//                String valueUpdate = _getValueFromSystemByKey(key);
//                Log.i(TAG, "Update Key:" + key + " ,value:" + valueUpdate);
                db_update.close();

                return update_result;
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }

        }

    }

    @Override
    public String _getValueFromSystemByKey(String key) {
        String queue_List_value = null;

        String selectValueByKey = "SELECT value FROM " + TABLE_SYSTEM + " where key = '" + key + "'";

        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        try {
            //query for cursor
            Cursor cursor = db.rawQuery(selectValueByKey, null);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0)
                    do {
                        //get data from sqlite
                        queue_List_value = cursor.getString(0);
                    } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return queue_List_value;
    }


    private List<Card> _getListCardFromStringArray(String value) {
        List<Card> cardList = new ArrayList<>();
        try {
            //Pass string value to object
            JSONObject valueObj = new JSONObject(value);
            JSONArray listIdArray = valueObj.getJSONArray(KEY_CARD_JSON);//get List card ID

            for (int i = 0; i < listIdArray.length(); i++) {
                String cardId = listIdArray.getString(i);

                //get Card by id
                Card card = _getCardByID(cardId);
                if (card.getQueue() == 0)
                    cardList.add(card);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardList;
    }

    public List<String> _getListCardIdFromStringArray(String value) {
        List<String> cardListId = new ArrayList<>();
        try {
            //Pass string value to object
            JSONObject queueObj = new JSONObject(value);
            JSONArray listIdArray = queueObj.getJSONArray(KEY_CARD_JSON);//get List card ID

            for (int i = 0; i < listIdArray.length(); i++) {

                String cardId = listIdArray.getString(i);
                cardListId.add(cardId);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cardListId;
    }

    @Override
    public List<Card> _getListCardByStatus(int status) {

        //Query select_list_card_by_status
        String select_list_card_by_status = "SELECT " + selectList + " FROM " + TABLE_VOCABULARY + " where status = " + status;

        //Get card list by status
        return _getListCardQueryString(select_list_card_by_status, TYPE_CARD_LIST_1);
    }

    /**
     * Get List Card by queue
     *
     * @param queue
     */
    @Override
    public List<Card> _getListCardByQueue(int queue, int limit) {
        List<Card> cardListByQueue = null;

        //get current time
        long long_curent_time = new Date().getTime();

        int curent_time = (int) (long_curent_time / 1000);
        Log.d(TAG, "Current Time:" + curent_time + ":" + new Date().getTime());
        Log.d(TAG, "StartOfDayInMillis:" + LazzyBeeShare.getStartOfDayInMillis() + ":" + LazzyBeeShare.getEndOfDayInSecond());
        String select_list_card_by_queue;

        if (queue == Card.QUEUE_LNR1) {
            //Query select_list_card_by_queue
            select_list_card_by_queue = "SELECT " + selectFull + " FROM " + TABLE_VOCABULARY + " where queue = " + queue + " order by due LIMIT " + limit;

            cardListByQueue = _getListCardQueryString(select_list_card_by_queue, TYPE_CARD_DETAILS_0);

        } else if (queue == Card.QUEUE_REV2) {
            cardListByQueue = _getListCarDue(limit);
        }

        return cardListByQueue;
    }


    /**
     * Update queue and due card
     *
     * @param cardId
     * @param queue  queue
     * @param due    due time review card
     */
    @Override
    public int _updateCardQueueAndCardDue(String cardId, int queue, int due) {
        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(KEY_QUEUE, queue);//put Status
        values.put(KEY_DUE, due);
        //
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cardId)});

        Log.i(TAG, "_updateCardQueueAndCardDue:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);

        return update_result;
    }

//    private boolean _compreaToDate(Date card_due_date, Date now_date) {
//        try {
//            Date date_compateTo = inputFormat.parse(card_due_date.toString());
//            Date date_now = inputFormat.parse(now_date.toString());
//
//            //TODO: format date to string
//            String str_date_create_list_card_today_parse = outputFormat.format(date_compateTo);
//            String str_date_now = outputFormat.format(date_now);
//
//            //TODO: compareTo date learn vs now date
//            if (str_date_create_list_card_today_parse.compareTo(str_date_now) == 0) {
//                //TODO: Equal then return true
//                Log.i(TAG, "date_compateTo is equal to date_now");
//
//                return true;
//            } else {
//                //TODO: Not equal then return false
//                return false;
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }


    /**
     *
     * */
    private List<Card> _getListCardQueryString(String query, int type) {
        List<Card> datas = new ArrayList<>();
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                do {
                    Card card = _defineCardbyCursor(cursor, type);
                    datas.add(card);

                } while (cursor.moveToNext());
            }
        }
        //Log.i(TAG, "-------------------------_getListCardQueryString-------------------------");
        Log.i(TAG, "_getListCardQueryString: \t Result card count=" + datas.size() + " \t,Query String: " + query + "\n");
        //Log.i(TAG, "---------------------------------END-------------------------------------\n");
        db.close();
        return datas;
    }

    /**
     * Update card
     *
     * @param card
     */
    @Override
    public int _updateCard(Card card) {

        // Log.i(TAG, "_updateCard: Card=" + card.toString());
        String cardId = String.valueOf(card.getId());

        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();


        values.put(KEY_QUEUE, card.getQueue());
        Log.i(TAG, "_updateCard: queue:" + card.getQueue());
        if (card.getDue() != 0)
            values.put(KEY_DUE, card.getDue());
        if (card.getLast_ivl() >= 0)
            values.put(KEY_LAT_IVL, card.getLast_ivl());
        if (card.getRev_count() != 0)
            values.put(KEY_REV_COUNT, card.getRev_count());
        if (card.getFactor() != 0)
            values.put(KEY_FACTOR, card.getFactor());
        if (card.getStatus() != 0)
            values.put(KEY_STATUS, card.getStatus());

        //
//        int update_result = db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?",
//                new String[]{cardId});
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_QUESTION + " = ?",
                new String[]{card.getQuestion()});
        Log.i(TAG, "_updateCard:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);

        //Update queue_list system table
        String queue_list = _getValueFromSystemByKey(QUEUE_LIST);
        if (queue_list != null) {
            //Get Card list id form system tabele
            List<String> cardListId = _getListCardIdFromStringArray(queue_list);

            //Check cardListId.contains(cardId)==true remeve carId
            if (cardListId.contains(cardId)) {
                cardListId.remove(cardId);

                //update queue list
                _insertOrUpdateToSystemTable(QUEUE_LIST, _listCardTodayToArrayListCardId(null, cardListId));
            }
        }
        db.close();
        return update_result;
    }

    public String _getStringDueToday() {
        String duetoday = LazzyBeeShare.EMPTY;

        int todayCount = _checkListTodayExit();
        int againCount = _getCountListCardByQueue(Card.QUEUE_LNR1, 0);
        int leared = _getCountListCardLearned();
        int noLearn = _getCountAllListCard() - leared;
        int total_learn_per_day = _getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT);
        int limitToday = _getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT);

        int dueCount = _getCountListCardByQueue(Card.QUEUE_REV2, total_learn_per_day);

        if (todayCount == -2) {
            Log.i(TAG, "_getStringDueToday: todayCount == -2");
//            dueCount = 0;
//            againCount = 0;
            if (limitToday > noLearn) {
                todayCount = noLearn;
            } else {
                todayCount = limitToday;
            }

        } else if (todayCount == 0) {
            Log.i(TAG, "_getStringDueToday: todayCount == 0");
        } else if (todayCount == -1) {
            Log.i(TAG, "_getStringDueToday: todayCount == -1");
            if (limitToday > noLearn) {
                todayCount = noLearn;
            } else {
                todayCount = limitToday;
            }
        }

        if (dueCount == 0) {
            Log.i(TAG, "_getStringDueToday:dueCount == 0");
        } else {
            if (dueCount < total_learn_per_day) {
                if (total_learn_per_day - dueCount < limitToday) {

                    Log.i(TAG, "_getStringDueToday total_learn_per_day - dueCount < limit_today");
                    todayCount = total_learn_per_day - dueCount;

                } else if (total_learn_per_day - dueCount > limitToday) {

                    Log.i(TAG, "_getStringDueToday  total_learn_per_day - dueCount > limit_today");
                }
            } else if (dueCount >= total_learn_per_day) {

                dueCount = total_learn_per_day;
                todayCount = 0;
            }
        }

        if (todayCount > 0 || againCount > 0 || dueCount > 0)
            duetoday = "<font color='" + context.getResources().getColor(R.color.card_new_color) + "'>" + todayCount + "</font>\n" +
                    " <font color='" + context.getResources().getColor(R.color.card_again_color) + "'>" + againCount + "</font>\n" +
                    " <font color='" + context.getResources().getColor(R.color.card_due_color) + "'>" + dueCount + "</font>";

        Log.i(TAG, "_getStringDueToday  Total learn:" + total_learn_per_day + ",Today:" + todayCount + ",Again:" + againCount + ",Due:" + dueCount);
        return duetoday;
    }

    public int _getCountListCardByQueue(int queue, int limit) {
        String query = "";
        if (queue == Card.QUEUE_LNR1) {
            query = "SELECT  COUNT(id) FROM " + TABLE_VOCABULARY + " where queue = " + queue + " order by due";
        } else if (queue == Card.QUEUE_REV2) {
            query = "SELECT  COUNT(id) FROM " + TABLE_VOCABULARY +
                    " where queue = " + Card.QUEUE_REV2 + " AND due <= " + (LazzyBeeShare.getEndOfDayInSecond()) + " order by due " + " LIMIT " + limit;
        }
        return _queryCount(query);
    }


    public List<Card> _getAllListCard() {
        String query = "SELECT " + selectFull + " FROM " + TABLE_VOCABULARY;
        return _getListCardQueryString(query, TYPE_CARD_DETAILS_0);
    }

    public List<Card> _getAllListCardforSearch() {
        String query = "SELECT " + selectList + " FROM " + TABLE_VOCABULARY;
        return _getListCardQueryString(query, TYPE_CARD_LIST_1);
    }

    public List<String> _getAllLQuestionCard() {
        String query = "SELECT vocabulary.question FROM " + TABLE_VOCABULARY;
        List<String> datas = new ArrayList<>();
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                do {
                    datas.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();
        return datas;
    }


    public List<String> _suggestionQuestionCard(String query, int type) {
        if (type == TYPE_SUGGESTION_QUESTION_CARD__SEARCH) {
            List<String> datas = new ArrayList<>();
            String likeQuery = "SELECT vocabulary.question FROM " + TABLE_VOCABULARY + " WHERE "
                    + KEY_QUESTION + " like '" + query + "%' OR "
                    + KEY_QUESTION + " like '% " + query + "%'"
                    + " ORDER BY " + KEY_QUESTION + " LIMIT 50";

            SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
            //query for cursor
            Cursor cursor = db.rawQuery(likeQuery, null);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0) {
                    do {
                        datas.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
            db.close();
            return datas;
        } else {
            return _recentSuggestionQuestionCard();
        }


    }

    private List<String> _recentSuggestionQuestionCard() {
        List<Card> cardList = _recentSuggestionCard();
        List<String> datas = new ArrayList<>();
        for (Card card : cardList) {
            datas.add(card.getQuestion());
        }
        return datas;
    }

    private List<Card> _getListCardLearned() {
        String query = "SELECT " + selectList + " FROM " + TABLE_VOCABULARY + " where queue >= 1";
        return _getListCardQueryString(query, TYPE_CARD_LIST_1);
    }


    @Override
    public int _get100Card() {
        String value = _getValueFromSystemByKey(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST);
        if (value != null) {
            try {
                //Define jsonObj by value
                JSONObject jsonObject = new JSONObject(value);
                int count = jsonObject.getInt(KEY_COUNT_JSON);
                //Check count < today new card limit ->init
                if (count < _getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT)) {
                    Log.i(TAG, "_get100Card:Init New 100 Card");
                    return _initIncomingCardIdList();
                } else {
                    Log.i(TAG, "_get100Card:" + count);
                    return count;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return _initIncomingCardIdList();
            }

        } else {
            return _initIncomingCardIdList();
        }
    }

    public int _initIncomingCardIdList() {
        String subject = _getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MY_SUBJECT);
        int myLevel = getSettingIntergerValuebyKey(LazzyBeeShare.KEY_SETTING_MY_LEVEL);
        if (subject == null || subject.trim().isEmpty() || subject.trim().length() < 0) {
            return _initIncomingCardIdListbyLevel(myLevel);
        } else {
            return _initIncomingCardIdListbyLevelandSubject(myLevel, subject);
        }

    }

    public int _initIncomingCardIdListbyLevelandSubject(int myLevel, String subject) {
        Log.i(TAG, "my_level:" + myLevel + ",subject:" + subject);

        int final_limit = 100;
        int limit = 100;
        String subCommon = "common";

        String select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                " where queue = " + Card.QUEUE_NEW_CRAM0
                + " AND level >= " + myLevel
                + " AND package like '%," + subject
                + ",%' LIMIT " + limit;
        List<String> cardIds = _getCardIDListQueryString(select_list_card_by_queue);

        int count = cardIds.size();

        if (count < limit) {
            limit = limit - cardIds.size();
            //set default
            select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                    " where queue = " + Card.QUEUE_NEW_CRAM0
                    + " AND level = " + myLevel
                    + " AND package like '%," + subCommon + ",%' NOT LIKE '%," + subject
                    + ",%' LIMIT " + limit;
            cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));
        }

        while (cardIds.size() < limit) {
            limit = limit - cardIds.size();
            myLevel++;
            select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                    " where queue = " + Card.QUEUE_NEW_CRAM0
                    + " AND level = " + myLevel
                    + " AND package like '%," + subject
                    + ",%'  LIMIT " + limit;

            Log.i(TAG, "initIncomingListwithSubject: Level " + myLevel +
                    ", target = " + limit);
            cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));
            if (cardIds.size() < final_limit) {
                limit = final_limit - cardIds.size();
                //set default
                select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                        " where queue = " + Card.QUEUE_NEW_CRAM0
                        + " AND level = " + myLevel
                        + " AND package like '%," + subCommon + ",%' NOT LIKE '%," + subject
                        + ",%' LIMIT " + limit;
                cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));
            }
        }

        if (cardIds.size() < 0) {
            return -1;
        } else {
            saveIncomingCardIdListwithCustomList(cardIds);
            return cardIds.size();
        }
    }

    private void saveIncomingCardIdListwithCustomList(List<String> cardIds) {
        List<String> incomingList = new ArrayList<>();

        //Clone default list
        List<String> clone_DefaultList = new ArrayList<>(cardIds);

        //Custom list
        List<String> customListId = getCustomListId();

        //remove duplicate
        int customSize = customListId.size();
        if (customSize > 0) {
            for (String cardId : customListId) {
                if (cardIds.contains(cardId)) {
                    clone_DefaultList.remove(cardId);
                }
            }
            incomingList.addAll(customListId);
            int sizeDefault = 100 - customSize;
            //Hoavq add check to clear crash:
            // https://console.firebase.google.com/u/0/project/lazeebee-977/crashlytics/app/android:com.born2go.lazzybee/issues/5c09328bf8b88c2963c7c2f1?time=last-seven-days&sessionId=5C0932A6037D00014F7B39CC388242C2_DNE_0_v2
            if (clone_DefaultList.size() > 0 && sizeDefault > 0)
            {
                if (sizeDefault > clone_DefaultList.size())
                    sizeDefault = clone_DefaultList.size();
                for (int i = 0; i < sizeDefault; i++) {
                    incomingList.add(clone_DefaultList.get(i));
                }
            }
        } else {
            incomingList.addAll(clone_DefaultList);
        }


        saveIncomingCardIdList(incomingList);
    }

    public int _initIncomingCardIdListbyLevel(int myLevel) {
        String subject = _getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MY_SUBJECT);
        if (subject == null || subject.trim().isEmpty() || subject.trim().length() < 0) {

            Log.i(TAG, "my_level:" + myLevel);
            if (myLevel == 0)
                myLevel = 2;
            else if (myLevel > 6) {
                myLevel = 6;
            }
            int limit = 100;
            String select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY
                    + " where queue = " + Card.QUEUE_NEW_CRAM0
                    + " AND level = " + myLevel
                    + " LIMIT " + limit;
            List<String> cardIds = _getCardIDListQueryString(select_list_card_by_queue);

            while (cardIds.size() < limit) {
                if (myLevel == 6)
                    break;

                limit = limit - cardIds.size();
                myLevel++;
                select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY
                        + " where queue = " + Card.QUEUE_NEW_CRAM0
                        + " AND level = " + myLevel
                        + " LIMIT " + limit;
                Log.i(TAG, "_initIncomingCardIdListbyLevel: Level " + myLevel +
                        ", target = " + limit);
                cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));

            }
            int count = cardIds.size();
            Log.i(TAG, "_initIncomingCardIdListbyLevel: Card size=" + count);
            saveIncomingCardIdListwithCustomList(cardIds);
            return count;
        } else {
            return _initIncomingCardIdListbyLevelandSubject(myLevel, subject);
        }

    }

    public void saveIncomingCardIdList(List<String> cardIds) {
        try {

            String key = LazzyBeeShare.PRE_FETCH_NEWCARD_LIST;
            JSONObject newcardlist = new JSONObject();
            JSONArray jsonArray = new JSONArray(cardIds);
            newcardlist.put(KEY_COUNT_JSON, cardIds.size());
            newcardlist.put(KEY_CARD_JSON, jsonArray);

            _insertOrUpdateToSystemTable(key, newcardlist.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<String> getCustomListId() {
        String select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                " where queue = " + Card.QUEUE_NEW_CRAM0 + " AND custom_list = " + 1 + " LIMIT " + 100;
        return _getCardIDListQueryString(select_list_card_by_queue);
    }


    /*
     * @param key
     * */
    public int _getCustomStudySetting(String key) {
        String value = _getValueFromSystemByKey(key);
        if (value == null) {
            switch (key) {
                case LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT:
                    //Learn more
                    return LazzyBeeShare.DEFAULT_MAX_LEARN_MORE_PER_DAY;
                case LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT:
                    //New
                    return LazzyBeeShare.DEFAULT_MAX_NEW_LEARN_PER_DAY;
                case LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT:
                    //Total
                    return LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
                default:
                    return 0;
            }
        } else
            return Integer.valueOf(value);
    }


    private List<String> _getCardIDListQueryString(String query) {
        List<String> cardIds = new ArrayList<>();
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                do {
                    String cardId = cursor.getString(0);

                    cardIds.add(cardId);

                } while (cursor.moveToNext());
        }
        Log.i(TAG, "Query String: " + query + " --Result card count:" + cardIds.size());
        cursor.close();
        return cardIds;
    }

    public void _addCardIdToQueueList(Card card) {
//        if (_checkListTodayExit() < 0) {
//            _getRandomCard(_getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT), false);
//        }
//        String queue_list = _getValueFromSystemByKey(QUEUE_LIST);
//        List<String> cardIDs = _getListCardIdFromStringArray(queue_list);
//        //Add cardId
//        cardIDs.add(String.valueOf(card.getId()));
//        //Update queue list
//        _insertOrUpdateToSystemTable(QUEUE_LIST, _listCardTodayToArrayListCardId(null, cardIDs));

        //Update Queue Card from DB
        card.setQueue(Card.QUEUE_LNR1);

        //String cardId = String.valueOf(card.getId());

        //Define SQLiteDatabase
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        //Define SQLiteDatabase
        ContentValues values = new ContentValues();

        //set value for card
        values.put(KEY_QUEUE, card.getQueue());

        if (card.getDue() != 0)
            values.put(KEY_DUE, card.getDue());
        if (card.getLast_ivl() != 0)
            values.put(KEY_LAT_IVL, card.getLast_ivl());
        if (card.getRev_count() != 0)
            values.put(KEY_REV_COUNT, card.getRev_count());
        if (card.getFactor() != 0)
            values.put(KEY_FACTOR, card.getFactor());

        //Update query
//        db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?",
//                new String[]{cardId});
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_QUESTION + " = ?",
                new String[]{card.getQuestion()});
        Log.i(TAG, "_addCardIdToQueueList:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);
        db.close();

        //Check card in comming list
        //get String Incoming list
        String list100Card = _getValueFromSystemByKey(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST);

        //define list
        List<String> defineListCard100 = _getListCardIdFromStringArray(list100Card);

        //Check contain
        if (defineListCard100.contains(String.valueOf(card.getId()))) {
            //Remove
            defineListCard100.remove(card.getId());
            //save list 100
            saveIncomingCardIdList(defineListCard100);
            Log.d(TAG, "Card is contain List 100 card");
        } else {
            Log.d(TAG, "Card NOT contain List 100 card");
        }

    }

    @Override
    public void _insertOrUpdateCard(Card card) {
        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ANSWERS, card.getAnswers());
        values.put(KEY_LEVEL, card.getLevel());
        values.put(KEY_PACKAGES, card.getPackage());

        values.put(KEY_L_EN, card.getL_en());
        values.put(KEY_L_VN, card.getL_vn());
        values.put(KEY_G_ID, card.getgId());


        int update_result = db.update(TABLE_VOCABULARY, values, KEY_QUESTION + " = ?",
                new String[]{card.getQuestion()});
        if (update_result == 0) {
            values.put(KEY_QUESTION, card.getQuestion());
            db.insert(TABLE_VOCABULARY, null, values);
            Log.i(TAG, "Insert New Card \t -result=" + update_result);
        } else {
            Log.i(TAG, "Update Card:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);
        }

        db.close();

    }

    public void _insertOrUpdateCardbyGId(Card card) {
        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_G_ID, card.getgId());
        values.put(KEY_QUESTION, card.getQuestion());
        values.put(KEY_ANSWERS, card.getAnswers());
        values.put(KEY_LEVEL, card.getLevel());
        values.put(KEY_PACKAGES, card.getPackage());

        values.put(KEY_L_EN, card.getL_en());
        values.put(KEY_L_VN, card.getL_vn());

        //        _card.setQueue(queue);
//        _card.setDue(due);
//        _card.setRev_count(rev_count);
//        _card.setLast_ivl(last_ivl);
//
//        _card.setFactor(factor);
//        _card.setUser_note(user_note);

        values.put(KEY_QUEUE, card.getQueue());
        values.put(KEY_DUE, card.getDue());
        values.put(KEY_REV_COUNT, card.getRev_count());
        values.put(KEY_FACTOR, card.getFactor());

        values.put(KEY_USER_NOTE, card.getUser_note());


        long insert = db.insert(TABLE_VOCABULARY, null, values);
        Log.i(TAG, "_insertOrUpdateCardbyGId() \t -insert result=" + insert);
//        int update_result = db.update(TABLE_VOCABULARY, values, KEY_G_ID + " = ?",
//                new String[]{String.valueOf(card.getgId())});
//        if (update_result == 0) {
//        } else {
//            Log.i(TAG, "_insertOrUpdateCardbyGId() \t -update : " + (update_result == 1 ? "OK " : "False ") + "_" + update_result);
//        }

        db.close();

    }


    private List<Card> _getListCarDue(int limit) {
        String select_list_card_by_queue = "SELECT " + selectFull + " FROM " + TABLE_VOCABULARY +
                " where queue = " + Card.QUEUE_REV2 + " AND due <= " + (LazzyBeeShare.getEndOfDayInSecond()) + " order by due " + " LIMIT " + limit;
        return _getListCardQueryString(select_list_card_by_queue, TYPE_CARD_DETAILS_0);
    }

    public void cleanCache() {
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        if (db.delete(TABLE_SYSTEM, " key = ? ", new String[]{QUEUE_LIST}) > 0) {
            Log.i(TAG, "resetCache key:" + QUEUE_LIST + ",result:OK");
        }
        if (db.delete(TABLE_SYSTEM, " key = ? ", new String[]{LazzyBeeShare.PRE_FETCH_NEWCARD_LIST}) > 0) {
            Log.i(TAG, "resetCache key:" + LazzyBeeShare.PRE_FETCH_NEWCARD_LIST + ",result:OK");
        }

    }

    @Override
    public void _exportDateBaseFile() {
        dataBaseHelper._exportDatabase();
    }

    public void _updateCardFormServer(Card card) {
        //Define cardId
        String cardId = String.valueOf(card.getId());

        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        //set value for Card update
        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, card.getQuestion());
        values.put(KEY_ANSWERS, card.getAnswers());
        values.put(KEY_LEVEL, card.getLevel());
        values.put(KEY_PACKAGES, card.getPackage());
        values.put(KEY_L_EN, card.getL_en());
        values.put(KEY_L_VN, card.getL_vn());

//        int update_result = db.update(TABLE_VOCABULARY, values, KEY_ID + " = ? ",
//                new String[]{cardId});
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_QUESTION + " = ? ",
                new String[]{card.getQuestion()});
        Log.i(TAG, "_updateCardFormServer:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);
    }

    public int _deleteCard(Card card) {
        //Define cardId
        String cardId = String.valueOf(card.getId());

        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        //set value for Card update
        ContentValues values = new ContentValues();
        values.put(KEY_QUEUE, Card.QUEUE_DONE_2);
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_ID + " = " + cardId,
                null);
        db.close();
        if (update_result == 0) {
            //Update queue_list system table
            String queue_list = _getValueFromSystemByKey(QUEUE_LIST);

            //Get Card list id form system tabele
            List<String> cardListId = _getListCardIdFromStringArray(queue_list);

            //Check cardListId.contains(cardId)==true remeve carId
            if (cardListId.contains(cardId)) {
                cardListId.remove(cardId);

                //update queue list
                _insertOrUpdateToSystemTable(QUEUE_LIST, _listCardTodayToArrayListCardId(null, cardListId));
            }
        }

        return update_result;
    }

    public int executeQuery(String query) {
        Log.d(TAG, "executeQuery:" + query);
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        try {
            db.execSQL(query);
            return 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }


    public boolean _checkUpdateDataBase() {
        boolean update = false;
        int _dbVesion = LazzyBeeShare.DEFAULT_VERSION_DB;
        int _gdbVesion = LazzyBeeShare.DEFAULT_VERSION_DB;

        //get version in DB
        String db_v = _getValueFromSystemByKey(LazzyBeeShare.DB_VERSION);
        String g_db_v = _getValueFromSystemByKey(LazzyBeeShare.GAE_DB_VERSION);

        Log.i(TAG, "db_v:" + db_v + "\t g_db_v:" + g_db_v);

        //Check client DB
//        if (db_v == null) {
//
//        } else {
//
//        }
        if (db_v != null) {
            _dbVesion = Integer.valueOf(db_v);
        }

        //Check global DB
//        if (g_db_v == null) {
//
//        } else {
//            _gdbVesion = Integer.valueOf(g_db_v);
//        }

        if (g_db_v != null) {
            _gdbVesion = Integer.valueOf(g_db_v);
        }

        //
        if (_gdbVesion > _dbVesion) {
            Log.i(TAG, "Show confirm Update");
            update = true;
            //Update gDB version
            _insertOrUpdateToSystemTable(LazzyBeeShare.GAE_DB_VERSION, String.valueOf(_gdbVesion));
        }
        Log.i(TAG, "dbVesion:" + _dbVesion + "\t gdbVesion:" + _gdbVesion);
        return update;
    }

    public int _getCountAllListCard() {
        //String selectbyIDQuery = "SELECT  * FROM  sqlite_sequence";
        String selectbyIDQuery = "SELECT COUNT(id) From vocabulary";
        return _queryCount(selectbyIDQuery);
    }

    public int _getCountListCardNoLearn() {
        String selectbyIDQuery = "SELECT COUNT(id) From vocabulary where queue = 0";
        return _queryCount(selectbyIDQuery);
    }

    public int _getCountListCardLearned() {
        String selectbyIDQuery = "SELECT COUNT(id) From vocabulary where queue > 0";
        return _queryCount(selectbyIDQuery);
    }

    public int _getCardIDByQuestion(String question) {
        int id = 0;
        try {
            String selectbyQuestionQuery = "Select id from " + TABLE_VOCABULARY + " where vocabulary.question ='" + question + "'";
            SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();

            //query for cursor
            Cursor cursor = db.rawQuery(selectbyQuestionQuery, null);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0) {
                    do {
                        id = cursor.getInt(0);
                    } while (cursor.moveToNext());
                }
            }
            Log.i(TAG, "-query=" + selectbyQuestionQuery + ",id=" + id);
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public int _queryCount(String query) {
        int count = 0;
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                do {
                    count = cursor.getInt(0);
                } while (cursor.moveToNext());
        }
        cursor.close();
        Log.i(TAG, "_queryCount -Query:" + query + "\t" + "Result count:" + count);
        return count;
    }

    public int getSettingIntergerValuebyKey(String keySettingMyLevel) {
        String settingMyLevel = _getValueFromSystemByKey(keySettingMyLevel);
        if (settingMyLevel == null) {
            switch (keySettingMyLevel) {
                case LazzyBeeShare.KEY_SETTING_MY_LEVEL:
                    return LazzyBeeShare.DEFAULT_MY_LEVEL;
                case LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION:
                    return LazzyBeeShare.DEFAULT_HOUR_NOTIFICATION;
                case LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION:
                    return LazzyBeeShare.DEFAULT_MINUTE_NOTIFICATION;
                case LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT:
                    return LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
                case LazzyBeeShare.KEY_SETTING_TIME_SHOW_ANSWER:
                    return LazzyBeeShare.DEFAULT_TIME_SHOW_ANSWER;
                default:
                    return 0;
            }

        } else {
            return Integer.valueOf(settingMyLevel);
        }
    }

    public List<Card> _getIncomingListCard() {
        List<Card> cards = new ArrayList<>();
        String value = _getValueFromSystemByKey(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST);
        try {
            JSONObject valueObj = new JSONObject(value);
            JSONArray listIdArray = valueObj.getJSONArray(KEY_CARD_JSON);
            cards.addAll(_getListCardByListCardIdJsonArray(listIdArray));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cards;
    }


    public List<Card> _searchCardOrGotoDictionary(String query, int type) {
        if (type == LazzyBeeShare.GOTO_SEARCH_CODE) {
            return _searchCard(query);
        } else {
            return _getDictionary();
        }
    }


    public List<Integer> _getListDayStudyComplete() {
        String query = "SELECT streak.day FROM streak where streak.day > " + ((LazzyBeeShare.getStartOfDayInMillis() / 1000) - (86400 * 6)) + " ORDER by streak.day DESC LIMIT 6";
        List<Integer> dayCompleteStudys = new ArrayList<>();
//        int startOfDay = (int) (LazzyBeeShare.getStartOfDayInMillis() / 1000);
//        dayCompleteStudys.add(startOfDay - (84600 * 12));
//        dayCompleteStudys.add(startOfDay - (84600 * 14));
//        dayCompleteStudys.add(startOfDay - (84600 * 3));
//        dayCompleteStudys.add(startOfDay - (84600 * 13));
//        dayCompleteStudys.add(startOfDay - (84600 * 15));
//        dayCompleteStudys.add(startOfDay - (84600 * 17));
//        dayCompleteStudys.add(startOfDay - 84600);

        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                do {
                    dayCompleteStudys.add(cursor.getInt(0));

                } while (cursor.moveToNext());
            }
        }
        Log.i(TAG, "_getListDayStudyComplete: \t Query String: " + query + "\n");
        db.close();
        cursor.close();
        return dayCompleteStudys;
    }

    public int getTotalDayStudy() {
        String countStreak = "SELECT Count(day) FROM " + TABLE_STREAK + " order by day desc";
        return _queryCount(countStreak);
    }

    public int _getCountStreak() {
        int startOfday = (int) (LazzyBeeShare.getStartOfDayInMillis() / 1000);
        String countStreak = "SELECT Count(day) FROM " + TABLE_STREAK + " where day = " + startOfday + " order by day desc";
        if (_queryCount(countStreak) == 0) {
            startOfday = (startOfday - LazzyBeeShare.SECONDS_PERDAY);
        }
        String selectbyIDQuery = "SELECT day FROM " + TABLE_STREAK + " where day <= " + startOfday + " order by day desc";
        Log.d(TAG, "query:" + selectbyIDQuery);
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        List<Integer> streak_days = new ArrayList<>();
        //query for cursor
        Cursor cursor = db.rawQuery(selectbyIDQuery, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                do {
                    int streak_day = cursor.getInt(0);
                    streak_days.add(streak_day);

                } while (cursor.moveToNext());
        }
        int count = 0;
        int streak_days_count = streak_days.size();
        if (streak_days_count > 0) {
            Log.d(TAG, "Streak day count:" + streak_days_count);
            for (int i = 0; i < streak_days_count; i++) {
                int streak_day = streak_days.get(i);
                int perDay = (LazzyBeeShare.SECONDS_PERDAY * (i));//get day
                Log.d(TAG, streak_day + "\t:\t" + (startOfday - perDay) + "\t,i:" + i);
                if (streak_day == (startOfday - perDay)) {
                    count++;
                } else {
                    break;
                }
            }
        }
        cursor.close();
        return count;
    }

    public int _insetStreak() {
        int day = (int) (LazzyBeeShare.getStartOfDayInMillis() / 1000);
        if (_queryCount("select count(day) from streak where day = " + day) == 0) {
            ContentValues values = new ContentValues();
            values.put("day", day);
            SQLiteDatabase db_insert = this.dataBaseHelper.getWritableDatabase();
            try {
                long long_insert_results = db_insert.insert(TABLE_STREAK, null, values);
                Log.i(TAG, "_insetStreak\tInsert:day=" + day);
                db_insert.close();
                return (int) long_insert_results;
            } catch (SQLiteException e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    private int _insetStreak(int day) {
        if (_queryCount("select count(day) from streak where day = " + day) == 0) {
            ContentValues values = new ContentValues();
            values.put("day", day);
            SQLiteDatabase db_insert = this.dataBaseHelper.getWritableDatabase();
            try {
                long long_insert_results = db_insert.insert(TABLE_STREAK, null, values);
                Log.i(TAG, "_insetStreak\tInsert:day=" + day);
                db_insert.close();
                return (int) long_insert_results;
            } catch (SQLiteException e) {
                return -1;
            }
        } else {
            return -1;
        }
    }

    public void _updateUserNoteCard(Card card) {
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (card.getUser_note() != null)

            values.put(KEY_USER_NOTE, card.getUser_note());
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_QUESTION + " = ?",
                new String[]{card.getQuestion()});
        Log.i(TAG, "_updateUserNoteCard:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);

        db.close();
    }

    public List<Card> _suggestionCard(String query) {
        String likeQuery = "SELECT  " + selectSuggestionList + " FROM " + TABLE_VOCABULARY + " WHERE "
                + KEY_QUESTION + " like '" + query + "%' OR "
                + KEY_QUESTION + " like '% " + query + "%'"
                + " ORDER BY " + KEY_QUESTION + " LIMIT 50";

        List<Card> datas = new ArrayList<>();
        //Seach card
        int TYPE_CARD_LIST_SUGGESSTION_2 = 2;

        if (query != null && query.length() > 1) {
            datas = _getListCardQueryString(likeQuery, TYPE_CARD_LIST_SUGGESSTION_2);
        }
        return datas;
    }

    public List<Integer> _getListCountCardbyLevel() {
        //get max level
        int maxlevel = _queryCount("SELECT max(level) FROM vocabulary");
        List<Integer> data = new ArrayList<>();
        for (int i = 1; i <= maxlevel; i++) {
            String count_card_learner_by_level = "select count(id) from vocabulary where  (vocabulary.queue = -2 or vocabulary.queue >= 1) and level =" + i;
            data.add(_queryCount(count_card_learner_by_level));
        }
        return data;
    }

    public boolean checkTableExist(String tableName) {
        String checkTableExit = "SELECT count(name) FROM sqlite_master WHERE type ='table' AND name='" + tableName + "';";
        return _queryCount(checkTableExit) != 0;
    }

    public int _updateCardFormCSV(Card card) {
        int update_result = -2;
        //Update staus card by id
        if (card.getgId() > 0) {
            SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_FACTOR, card.getFactor());
            values.put(KEY_LAT_IVL, card.getLast_ivl());
            values.put(KEY_QUEUE, card.getQueue());
            values.put(KEY_REV_COUNT, card.getRev_count());
            values.put(KEY_DUE, card.getDue());
            values.put(KEY_USER_NOTE, card.getUser_note());

            update_result = db.update(TABLE_VOCABULARY, values, KEY_G_ID + " = ?",
                    new String[]{String.valueOf(card.getgId())});
            db.close();
        }
        return update_result;
    }

    public int _insertSuggesstion(String cardID) {
        int update_result = -1;
        try {
            SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(TABLE_SUGGESTION, cardID);
            db.insert(TABLE_SUGGESTION, null, values);
            update_result = 1;
            db.close();
        } catch (Exception ignored) {
        }

        return update_result;
    }

    public List<Card> _recentSuggestionCard() {
        int countSug = _queryCount("select Count(suggestion.suggestion) from suggestion");
        Log.d(TAG, "count Suggestion:" + countSug);
        if (countSug > 50) {
            String delete40Row = "delete from suggestion where suggestion.id in (select suggestion.id from suggestion order by suggestion.id LIMIT 40);";
            Log.d(TAG, "DELETE 40 ROW" + (executeQuery(delete40Row) == 1 ? " OK" : " Fails"));
        }
        String getSug = "select suggestion.suggestion from suggestion ORDER  BY id DESC LIMIT 5";
        List<String> cardIdRecents = new ArrayList<>();
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(getSug, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                do {
                    String cardId = cursor.getString(0);
                    cardIdRecents.add(cardId);
                } while (cursor.moveToNext());
            }
        }
        List<Card> datas = new ArrayList<>();
        if (cardIdRecents.size() > 0) {
            for (String cardId : cardIdRecents) {
                datas.add(_getCardByID(cardId));
            }
        }
        cursor.close();
        return datas;
    }


    public int _updateCardFormCSV(long gId, int queue, int due, int rev_count, int last_ivl, int factor, String user_note) {
        int update_result = -2;
        try {
            //Update staus card by id
            if (gId > 0) {
                SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_QUEUE, queue);
                values.put(KEY_DUE, due);
                values.put(KEY_REV_COUNT, rev_count);
                values.put(KEY_LAT_IVL, last_ivl);
                values.put(KEY_FACTOR, factor);
                values.put(KEY_USER_NOTE, user_note);

                update_result = db.update(TABLE_VOCABULARY, values, KEY_G_ID + " = ?",
                        new String[]{String.valueOf(gId)});
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return update_result;
    }

    public int updateStreakDay(int day) {
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        int update_result;
        String countStreak = "select count(day) from streak where streak.day=" + day;
        if (_queryCount(countStreak) == 1) {
            Log.d(TAG, "Streak is existing!");
            update_result = 1;
        } else {
            Log.d(TAG, "Inset new streak day =" + day);
            ContentValues values = new ContentValues();
            values.put("day", day);
            long long_insert_results = db.insert(TABLE_STREAK, null, values);
            update_result = (int) long_insert_results;
        }
        db.close();
        return update_result;
    }

    public void deteteStreak() {
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        String deleteStreak = "streak";
        db.delete(deleteStreak, null, null);
    }

    public int restoreStreakDay(int day) {
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        int update_result;
        Log.d(TAG, "Inset new streak day =" + day);
        ContentValues values = new ContentValues();
        values.put("day", day);
        long long_insert_results = db.insert(TABLE_STREAK, null, values);
        update_result = (int) long_insert_results;

        db.close();
        return update_result;
    }

    public Card getReverseCard() {
        Card card = new Card();
        try {
            String selectbyIDQuery = "SELECT " + selectFull + " FROM vocabulary Where vocabulary.due>0 ORDER BY RANDOM() LIMIT 1";
            SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();

            //query for cursor
            Cursor cursor = db.rawQuery(selectbyIDQuery, null);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0)
                    do {
                        card = _defineCardbyCursor(cursor, TYPE_CARD_DETAILS_0);
                    } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return card;
    }

    public void addToIncomingList(GroupVoca groupVoca) {
        String[] questions = groupVoca.getListVoca().split("\\n");
        String list100Card = _getValueFromSystemByKey(LazzyBeeShare.PRE_FETCH_NEWCARD_LIST);
        List<String> incomingList = new ArrayList<>();
        List<String> newIncomingList = new ArrayList<>();
        List<String> defaultIncomingLists = new ArrayList<>();
        for (String q : questions) {
            int cardId = _getCardIDByQuestion(q);
            if (cardId > 0) {
                newIncomingList.add(String.valueOf(cardId));
                int update = markCustomList(cardId);
                Log.d(TAG, "-Mark card Id : " + cardId + " into custom list,Update : " + update);

            }
        }
        if (newIncomingList.size() > 0) {
            try {
                JSONObject valueObj = new JSONObject(list100Card);
                JSONArray listIdArray = valueObj.getJSONArray(KEY_CARD_JSON);
                for (int i = 0; i < listIdArray.length(); i++) {
                    String _cardId = listIdArray.getString(i);
                    defaultIncomingLists.add(String.valueOf(_cardId));
                }

                List<String> clone_newIncomingList = new ArrayList<>(newIncomingList);
                for (String cardId : clone_newIncomingList) {
                    if (defaultIncomingLists.contains(cardId)) {
                        newIncomingList.remove(cardId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            incomingList.addAll(newIncomingList);
            incomingList.addAll(defaultIncomingLists);
            Log.d(TAG, "-new incoming list:" + newIncomingList.toString());
            Log.d(TAG, "-default incoming list:" + defaultIncomingLists.toString());
            Log.d(TAG, "-incoming list:" + incomingList.toString());

            saveIncomingCardIdList(incomingList);
        } else {
            Log.d(TAG, "-Empty new incoming list");
        }

    }

    public int markCustomList(int cardId) {
        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOM_LIST, 1);
        values.put(KEY_QUEUE, 0);
        int update_result = db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cardId)});
        db.close();
        return update_result;
    }

    public void addColumCustomList() {
        int add = executeQuery("ALTER TABLE vocabulary ADD COLUMN custom_list INTEGER");
        Log.d(TAG, "Add column custom_list " + add);
    }

    public long insertCard(Card card) {
        //Update staus card by id
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ANSWERS, card.getAnswers());
        values.put(KEY_LEVEL, card.getLevel());
        values.put(KEY_PACKAGES, card.getPackage());

        values.put(KEY_L_EN, card.getL_en());
        values.put(KEY_L_VN, card.getL_vn());
        values.put(KEY_G_ID, card.getgId());


        long insert;
        values.put(KEY_QUESTION, card.getQuestion());
        insert = db.insert(TABLE_VOCABULARY, null, values);
        Log.i(TAG, "Insert New Card \t -result=" + insert);

        db.close();
        return insert;
    }

    public void fillStreak(int limit) {
        int startOfday = (int) (LazzyBeeShare.getStartOfDayInMillis() / 1000);
        for (int i = 0; i < limit; i++) {
            startOfday = startOfday - LazzyBeeShare.SECONDS_PERDAY;
            Log.d(TAG, "Test fill streak:" + startOfday);
            int results = _insetStreak(startOfday);
            if (results == 1) {
                break;
            }
        }

    }
}
