package com.born2go.lazzybee.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.api.LearnApi;
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

    Context context;
    DataBaseHelper dataBaseHelper;

    private Random randomGenerator;


    public LearnApiImplements(Context context) {
        this.context = context;
        //init dataBaseHelper
        dataBaseHelper = new DataBaseHelper(context);
    }

    static int CARD_INDEX_ID = 0;
    public static int CARD_INDEX_QUESTION = 1;
    public static int CARD_INDEX_ANSWER = 2;
    public static int CARD_INDEX_QUEUE = 3;
    public static int CARD_INDEX_LEVEL = 4;

    public static int CARD_INDEX_PACKAGE = 5;
    public static int CARD_INDEX_CATRGORIES = 6;
    public static int CARD_INDEX_SUBCAT = 7;
    public static int CARD_INDEX_STATUS = 8;

    public static int CARD_INDEX_DUE = 9;
    public static int CARD_INDEX_REV_COUNT = 10;
    public static int CARD_INDEX_USER_NOTE = 11;

    public static int CARD_INDEX_LAST_IVL = 12;
    public static int CARD_INDEX_E_FACTOR = 13;

    public static int CARD_INDEX_GID = 14;

    public static final int CARD_INDEX_L_EN = 15;
    public static final int CARD_INDEX_L_VN = 16;

    public static int CARD_INDEX_TAGS = 17;
    public static int CARD_INDEX_RELATED = 18;

    private String selectFull = "vocabulary.id,vocabulary.question,vocabulary.answers," +
            "vocabulary.queue,vocabulary.level," +
            "vocabulary.package,vocabulary.category," +
            "vocabulary.subcats,vocabulary.status,vocabulary.due,vocabulary.rev_count," +
            "vocabulary.user_note,vocabulary.last_ivl,vocabulary.e_factor,vocabulary.gid," +
            "vocabulary.l_en,vocabulary.l_vn";
    private String selectList = "vocabulary.id,vocabulary.question,vocabulary.answers," +
            "vocabulary.queue,vocabulary.level";
    private String selectSucgetioList = "vocabulary.id,vocabulary.question,vocabulary.answers";


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
                        card = _defineCardbyCursor(cursor, 0);
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
        if (type < 2) {
            card.setQueue(cursor.getInt(CARD_INDEX_QUEUE));
            card.setLevel(cursor.getInt(CARD_INDEX_LEVEL));
            if (type == 0) {
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

                } catch (Exception e) {
                    Log.e(TAG, "GetCardbyID Eror:" + e.getMessage());
                    card.setL_vn(LazzyBeeShare.EMPTY);
                    card.setL_en(LazzyBeeShare.EMPTY);

                }
            }
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
        if (query != null || query.length() > 0)
            datas = _getListCardQueryString(likeQuery, 1);
        return datas;
    }

    private List<Card> _getDictionary() {
        String query = "SELECT " + selectList + " FROM " + TABLE_VOCABULARY + " order by question";
        List<Card> cardList = _getListCardQueryString(query, 1);
        return cardList;
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
        List<Card> datas = new ArrayList<Card>();
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
            if (learnmore == true)
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
            List<Card> cloneCard = new ArrayList<Card>();
            cloneCard.addAll(cards);
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
        List<String> cardIds = new ArrayList<String>();
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
            List<String> listCardId = new ArrayList<String>();
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


        List<Card> cardList = new ArrayList<Card>();//init Card List

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
        List<String> listCardId = new ArrayList<String>();
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
        List<Card> datas = _getListCardQueryString(selectQuery, 0);
        return datas;
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
                        String value = cursor.getString(0);
                        queue_List_value = value;
                    } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return queue_List_value;
    }


    List<Card> _getListCardFromStringArray(String value) {
        List<Card> cardList = new ArrayList<Card>();
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
        List<String> cardListId = new ArrayList<String>();
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
        List cardListByStatus = _getListCardQueryString(select_list_card_by_status, 1);
        return cardListByStatus;
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
        String select_list_card_by_queue = "";

        if (queue == Card.QUEUE_LNR1) {
            //Query select_list_card_by_queue
            select_list_card_by_queue = "SELECT " + selectFull + " FROM " + TABLE_VOCABULARY + " where queue = " + queue + " order by due LIMIT " + limit;

            cardListByQueue = _getListCardQueryString(select_list_card_by_queue, 0);

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
        List<Card> datas = new ArrayList<Card>();
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
        List<Card> cardList = _getListCardQueryString(query, 0);
        return cardList;
    }

    public List<Card> _getListCardLearned() {
        String query = "SELECT " + selectList + " FROM " + TABLE_VOCABULARY + " where queue >= 1";
        List<Card> cardList = _getListCardQueryString(query, 1);
        return cardList;
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
        if (subject == null) {
            return _initIncomingCardIdListbyLevel(myLevel);
        } else {
            return _initIncomingCardIdListbyLevelandSubject(myLevel, subject);
        }

    }

    public int _initIncomingCardIdListbyLevelandSubject(int myLevel, String subject) {
        List<String> cardIds = new ArrayList<String>();
        int final_limit = 100;
        int limit = 100;
        String subCommon = "common";

        String select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                " where queue = " + Card.QUEUE_NEW_CRAM0 + " AND level >= " +
                myLevel + " AND package like '%," + subject + ",%' LIMIT " + limit;
        cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));

        int count = cardIds.size();
        if (count < limit) {
            limit = limit - cardIds.size();
            //set default
            select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                    " where queue = " + Card.QUEUE_NEW_CRAM0 + " AND level = " +
                    myLevel + " AND package like '%," + subCommon + ",%' NOT LIKE '%," + subject
                    + ",%' LIMIT " + limit;
            cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));
        }

        while (cardIds.size() < limit) {
            limit = limit - cardIds.size();
            myLevel++;
            select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                    " where queue = " + Card.QUEUE_NEW_CRAM0 + " AND level = " +
                    myLevel + " AND package like '%," + subject + ",%'  LIMIT " + limit;

            Log.i(TAG, "initIncomingListwithSubject: Level " + myLevel +
                    ", target = " + limit);
            cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));
            if (cardIds.size() < final_limit) {
                limit = final_limit - cardIds.size();
                //set default
                select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                        " where queue = " + Card.QUEUE_NEW_CRAM0 + " AND level = " +
                        myLevel + " AND package like '%," + subCommon + ",%' NOT LIKE '%," + subject
                        + ",%' LIMIT " + limit;
                cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));
            }
        }
        if (cardIds.size() < 0) {
            return -1;
        } else {
            saveIncomingCardIdList(cardIds);
            return cardIds.size();
        }
    }

    public int _initIncomingCardIdListbyLevel(int myLevel) {
        String subject = _getValueFromSystemByKey(LazzyBeeShare.KEY_SETTING_MY_SUBJECT);
        if (subject == null) {
            List<String> cardIds = new ArrayList<String>();
            Log.i(TAG, "my_level:" + myLevel);
            if (myLevel == 0)
                myLevel = 2;
            int limit = 100;
            String select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                    " where queue = " + Card.QUEUE_NEW_CRAM0 + " AND level = " + myLevel + "" + " LIMIT " + limit;
            cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));

            while (cardIds.size() < limit) {
                limit = limit - cardIds.size();
                myLevel++;
                select_list_card_by_queue = "SELECT id FROM " + TABLE_VOCABULARY +
                        " where queue = " + Card.QUEUE_NEW_CRAM0 + " AND level = " + myLevel + " " + " LIMIT " + limit;
                Log.i(TAG, "_initIncomingCardIdListbyLevel: Level " + myLevel +
                        ", target = " + limit);
                cardIds.addAll(_getCardIDListQueryString(select_list_card_by_queue));
            }
            int count = cardIds.size();
            Log.i(TAG, "_initIncomingCardIdListbyLevel: Card size=" + count);
            if (count < 0) {
                return -1;
            } else {
                saveIncomingCardIdList(cardIds);
                return count;
            }
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


    /*
    * @param key
    * */
    public int _getCustomStudySetting(String key) {
        String value = _getValueFromSystemByKey(key);
        if (value == null) {
            if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT)) {
                //Learn more
                return LazzyBeeShare.DEFAULT_MAX_LEARN_MORE_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TODAY_NEW_CARD_LIMIT)) {
                //New
                return LazzyBeeShare.DEFAULT_MAX_NEW_LEARN_PER_DAY;
            } else if (key.equals(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT)) {
                //Total
                return LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
            } else {
                return 0;
            }
        } else
            return Integer.valueOf(value);
    }


    private List<String> _getCardIDListQueryString(String query) {
        List<String> cardIds = new ArrayList<String>();
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
        return cardIds;
    }

    public void _addCardIdToQueueList(Card card) {
        if (_checkListTodayExit() < 0) {
            _getRandomCard(_getCustomStudySetting(LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT), false);
        }
        String queue_list = _getValueFromSystemByKey(QUEUE_LIST);
        List<String> cardIDs = _getListCardIdFromStringArray(queue_list);
        //Add cardId
        cardIDs.add(String.valueOf(card.getId()));
        //Update queue list
        _insertOrUpdateToSystemTable(QUEUE_LIST, _listCardTodayToArrayListCardId(null, cardIDs));

        //Update Queue Card from DB
        card.setQueue(Card.QUEUE_NEW_CRAM0);

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
        if (defineListCard100.contains(card.getId())) {
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

        int update_result = db.update(TABLE_VOCABULARY, values, KEY_QUESTION + " = ?",
                new String[]{card.getQuestion()});
        Log.i(TAG, "_insertOrUpdateCard:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);
        if (update_result == 0) {
            values.put(KEY_QUESTION, card.getQuestion());
            db.insert(TABLE_VOCABULARY, null, values);
        }

    }


    public List<Card> _getListCarDue(int limit) {
        String select_list_card_by_queue = "SELECT " + selectFull + " FROM " + TABLE_VOCABULARY +
                " where queue = " + Card.QUEUE_REV2 + " AND due <= " + (LazzyBeeShare.getEndOfDayInSecond()) + " order by due " + " LIMIT " + limit;
        return _getListCardQueryString(select_list_card_by_queue, 0);
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
        String selectbyQuestionQuery = "Select id from " + TABLE_VOCABULARY + " where vocabulary.question ='" + question + "'";
        Log.i(TAG, "selectbyQuestionQuery=" + selectbyQuestionQuery);
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();

        //query for cursor
        Cursor cursor = db.rawQuery(selectbyQuestionQuery, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                do {
                    id = cursor.getInt(0);
                } while (cursor.moveToNext());
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
        Log.i(TAG, "_queryCount -Query:" + query + "\t" + "Result count:" + count);
        return count;
    }

    public int getSettingIntergerValuebyKey(String keySettingMyLevel) {
        String settingMyLevel = _getValueFromSystemByKey(keySettingMyLevel);
        if (settingMyLevel == null) {
            if (keySettingMyLevel == LazzyBeeShare.KEY_SETTING_MY_LEVEL) {
                return LazzyBeeShare.DEFAULT_MY_LEVEL;
            } else if (keySettingMyLevel == LazzyBeeShare.KEY_SETTING_HOUR_NOTIFICATION) {
                return LazzyBeeShare.DEFAULT_HOUR_NOTIFICATION;
            } else if (keySettingMyLevel == LazzyBeeShare.KEY_SETTING_MINUTE_NOTIFICATION) {
                return LazzyBeeShare.DEFAULT_MINUTE_NOTIFICATION;
            } else if (keySettingMyLevel == LazzyBeeShare.KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT) {
                return LazzyBeeShare.DEFAULT_TOTAL_LEAN_PER_DAY;
            } else if (keySettingMyLevel == LazzyBeeShare.KEY_SETTING_TIME_SHOW_ANSWER) {
                return LazzyBeeShare.DEFAULT_TIME_SHOW_ANSWER;
            } else
                return 0;

        } else {
            return Integer.valueOf(settingMyLevel);
        }
    }

    public List<Card> _getIncomingListCard() {
        List<Card> cards = new ArrayList<Card>();
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
        List<Integer> dayCompleteStudys = new ArrayList<Integer>();
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

        return dayCompleteStudys;
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
        List<Integer> streak_days = new ArrayList<Integer>();
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
                return (int) -1;
            }
        } else {
            return (int) -1;
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
        String likeQuery = "SELECT  " + selectSucgetioList + " FROM " + TABLE_VOCABULARY + " WHERE "
                + KEY_QUESTION + " like '" + query + "%' OR "
                + KEY_QUESTION + " like '% " + query + "%'"
                + " ORDER BY " + KEY_QUESTION + " LIMIT 50";

        List<Card> datas = new ArrayList<Card>();
        //Seach card
        if (query != null || query.length() > 1)
            datas = _getListCardQueryString(likeQuery, 2);
        return datas;
    }


    public List<Integer> _getListCountCardbyLevel() {
        //get max level
        int maxlevel = _queryCount("SELECT max(level) FROM vocabulary");
        List<Integer> data = new ArrayList<Integer>();
        for (int i = 1; i <= maxlevel; i++) {
            String count_card_learner_by_level = "select count(id) from vocabulary where  (vocabulary.queue = -2 or vocabulary.queue >= 1) and level =" + i;
            data.add(_queryCount(count_card_learner_by_level));
        }
        return data;
    }

    public boolean checkTableExist(String tableName) {
        String checkTableExit = "SELECT count(name) FROM sqlite_master WHERE type ='table' AND name='" + tableName + "';";
        if (_queryCount(checkTableExit) == 0) {
            return false;
        } else {
            return true;
        }
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

            update_result = db.update(TABLE_VOCABULARY, values, KEY_G_ID + " = ?",
                    new String[]{String.valueOf(card.getgId())});
            Log.i(TAG, "_updateCard:" + (update_result == 1 ? "OK" : "False") + "_" + update_result);
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
        } catch (Exception e) {
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
        List<String> cardIdRecents = new ArrayList<String>();
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
        List<Card> datas = new ArrayList<Card>();
        if (cardIdRecents.size() > 0) {
            for (String cardId : cardIdRecents) {
                datas.add(_getCardByID(cardId));
            }
        }
        return datas;
    }

}
