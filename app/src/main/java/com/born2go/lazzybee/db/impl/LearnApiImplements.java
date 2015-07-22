package com.born2go.lazzybee.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.api.LearnApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private static final String KEY_TAGS = "tags";
    //Table name
    private static final String TABLE_VOCABULARY = "vocabulary";
    private static final String TABLE_SYSTEM = "system";
    private static final String TAG = "LearnApiImplements";
    private static final int STATUS_CARD_LEARN_TODAY = 1;
    private static final String QUEUE_LIST = "queue_List";
    private static final String KEY_SYSTEM = "key";
    private static final String KEY_SYSTEM_VALUE = "value";
    private static final int STATUS_NO_LEARN = -1;
    private static final String KEY_QUEUE = "queue";

    String inputPattern = "EEE MMM d HH:mm:ss zzz yyyy";

    String outputPattern = "dd-MM-yyyy";

    SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

    Context context;
    DataBaseHelper dataBaseHelper;

    public LearnApiImplements(Context context) {
        this.context = context;
        //init dataBaseHelper
        dataBaseHelper = new DataBaseHelper(context);
    }

    /**
     * Get card by ID form sqlite
     *
     * @param cardId
     */
    @Override
    public Card _getCardByID(String cardId) {
        Card card = new Card();

        String selectbyIDQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " WHERE " + KEY_ID + " = " + cardId;
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();

        //query for cursor
        Cursor cursor = db.rawQuery(selectbyIDQuery, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                do {
                    //get data from sqlite
                    int id = cursor.getInt(0);

                    String question = cursor.getString(1);

                    String answers = cursor.getString(2);

                    String categories = cursor.getString(3);

                    String subcat = cursor.getString(4);

                    int status = 1;

                    int queue = cursor.getInt(9);

                    String _package = cursor.getString(10);

                    int level = cursor.getInt(11);

                    long due = cursor.getLong(12);

                    card = new Card(id, question, answers, categories, subcat, status, queue, due, _package, level);

                } while (cursor.moveToNext());
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
        return _getListCard();
    }

    /**
     * Seach vocabulary
     *
     * @param query
     */
    @Override
    public List<Card> _searchCard(String query) {


        //select like query
        String likeQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " WHERE " + KEY_QUESTION + " like '%" + query + "%'";

        //Todo:Seach card
        List<Card> datas = _getListCardQueryString(likeQuery);

        return datas;
    }

    /**
     * Get Random list card from today
     * Check List Card
     * Get random card->update List card and update statu card
     *
     * @param number
     */
    @Override
    public List<Card> _getRandomCard(int number) {
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        List<Card> datas = new ArrayList<Card>();

        //Check today list card
        boolean checkListToday = _checkListTodayExit(number);
        if (checkListToday) {
//
            //TODO: get data from sqlite
            String value = _getValueFromSystemByKey(QUEUE_LIST);
            try {
                //TODO:Pass string value to object
                JSONObject jsonObject = new JSONObject(value);

                //TODO:get List card array
                JSONArray jsonArray = jsonObject.getJSONArray("card");

                for (int i = 0; i < jsonArray.length(); i++) {
                    //TODO:g
                    String cardId = jsonArray.getString(i);

                    //TODO:get Card by id
                    Card card = _getCardByID(cardId);

                    //TODO:add card
                    datas.add(card);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            //select random limit 5 row
            String selectRandomAndLimitQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " ORDER BY RANDOM() LIMIT " + number;

//            SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
            //query for cursor
            Cursor cursor = db.rawQuery(selectRandomAndLimitQuery, null);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0)
                    do {
                        //get data from sqlite
                        int id = cursor.getInt(0);
                        String question = cursor.getString(1);
                        String answers = cursor.getString(2);
                        String categories = cursor.getString(3);
                        String subcat = cursor.getString(4);
                        Card card = new Card(id, question, answers, categories, subcat, 1);
                        datas.add(card);
                        _updateStatusCard("" + id, STATUS_CARD_LEARN_TODAY);

                    } while (cursor.moveToNext());
            }
            //TODO: ADD QUEUE LIST TO SYSTEM
            _insertOrUpdateToSystemTable(QUEUE_LIST, _listCardTodayToArrayListCardId(datas));
        }


        return datas;
    }

    private String _listCardTodayToArrayListCardId(List<Card> datas) {
        //TODO: init ListCardID
        List<String> listCardId = new ArrayList<String>();
        for (Card card : datas) {
            listCardId.add("" + card.getId());
        }

        //TODO new date create List Today
        Date nowdate = new Date();
        long now_date_long = nowdate.getTime();//get Time by @param nowdate

        //TODO: Init Obj josn
        JSONObject valueJoson = new JSONObject();
        try {
            JSONArray cardIDArray = new JSONArray(listCardId);//TODO:init cardID Array

            //todo: put properties of @param valueJoson
            valueJoson.put("date", now_date_long);
            valueJoson.put("card", cardIDArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonValuestr = "";//Todo: init string json value

        jsonValuestr = valueJoson.toString();
        Log.i(TAG, "jsonValuestr:" + jsonValuestr);
        return jsonValuestr;
    }

    private boolean _checkListTodayExit(int number) {

        //TODO:get value queue List
        String value = _getValueFromSystemByKey(QUEUE_LIST);
        if (value == null) {
            //TODO: NO List Queue
            return false;
        } else {
            //TODO Yes,Compareto Date
            try {
                //Pass string value to object
                JSONObject valueObj = new JSONObject(value);

                //TODO:get date create list today
                long _long_date = valueObj.getLong("date");//get Long date
                JSONArray cardListIDArray = valueObj.getJSONArray("card");//get List card ID
                Log.i(TAG, "-Long date:" + _long_date);

                Date _date = new Date(_long_date);
                //new date
                Date _now_date = new Date();

                Log.i(TAG, "_date:" + _date);
                Log.i(TAG, "_now_date:" + _now_date);

                //TODO:Compare Date
                try {
                    //TODO: parse date
//                    Date date_create_list_card_today_parse = inputFormat.parse(_date.toString());
//                    Date date_now = inputFormat.parse(_now_date.toString());

                    //TODO: format date to string
                    String str_date_create_list_card_today_parse = outputFormat.format(_date);
                    String str_date_now = outputFormat.format(_now_date);

                    //TODO: compareTo date learn vs now date
                    if (str_date_create_list_card_today_parse.compareTo(str_date_now) == 0) {
                        //TODO: Equal then return true
                        Log.i(TAG, "date_create_list_card_today_parse is equal to date_now");

                        return true;
                    } else {
                        //TODO: Not equal then return false

                        //TODO: Check yesterday  learn
                        if (cardListIDArray != null) {
                            int lenght_list_card_id_json_array = cardListIDArray.length();//Get lenght list card id json aray
                            if (lenght_list_card_id_json_array == number) {
                                //TODO: NO Learn,Update status =NO_LEARN
                                int lengh = cardListIDArray.length();//get lenght

                                for (int i = 0; i < lengh; i++) {
                                    try {

                                        String cardId = cardListIDArray.getString(i);//get CardId by index

                                        _updateStatusCard(cardId, STATUS_NO_LEARN);//Update status by cardid


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }

                        }

                        Log.i(TAG, "date_create_list_card_today_parse is not equal to date_now");

                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
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

        //TODO: Update staus card by id
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
            valueObj.put("card", jsonArray);
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
        List<Card> datas = new ArrayList<Card>();
        //select query
        String selectQuery = "SELECT  * FROM " + TABLE_VOCABULARY;
        //select limit 5 row
        String selectLimitQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " LIMIT 5 ";

        //TODO query select List Card by status=learned
        String selectListCardByStatus = "SELECT  * FROM " + TABLE_VOCABULARY + " where status = 1 ";

        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                do {
                    //get data from sqlite
                    int id = cursor.getInt(0);
                    String question = cursor.getString(1);
                    String answers = cursor.getString(2);
                    String categories = cursor.getString(3);
                    String subcat = cursor.getString(4);
                    Card card = new Card(id, question, answers, categories, subcat, 1);
                    datas.add(card);

                } while (cursor.moveToNext());
        }
        return datas;
    }

    /**
     * Update Status Card and Time Again Lean
     */
    public int updateStatusAndTimeAgainVocabulary(Card card) {
        SQLiteDatabase db = this.dataBaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, card.getStatus());
        return db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?",
                new String[]{String.valueOf(card.getId())});

    }


    @Override
    public int _updateStatusCard(String cardId, int status) {
        //TODO: Update staus card by id
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
        Log.i(TAG, "value:" + value);


        //TODO check value by key
        String valuebyKey = _getValueFromSystemByKey(key);
        if (_getValueFromSystemByKey(key) == null) {
            Log.i(TAG, "Insert list card");
            //Todo: No,Then insert
            ContentValues values = new ContentValues();

            //TODO put value
            values.put(KEY_SYSTEM, key);
            values.put(KEY_SYSTEM_VALUE, value);

            //TODO insert to system table
            SQLiteDatabase db_insert = this.dataBaseHelper.getWritableDatabase();
            long long_insert_results = db_insert.insert(TABLE_SYSTEM, null, values);
            Log.i(TAG, "Insert Results:" + long_insert_results);
            db_insert.close();
            return (int) long_insert_results;
        } else {
            Log.i(TAG, "Update list card today:" + valuebyKey);
            //Todo: Yes,update for key
            ContentValues values = new ContentValues();
            //TODO put value
            values.put(KEY_SYSTEM_VALUE, value);
            //TODO update to system table
            SQLiteDatabase db_update = this.dataBaseHelper.getWritableDatabase();
            try {
                int update_results = db_update.update(TABLE_SYSTEM, values, KEY_SYSTEM + " = ?",
                        new String[]{String.valueOf(key)});
                Log.i(TAG, "update_results:" + update_results);
                String valuebyKeyafterUpdate = _getValueFromSystemByKey(key);
                Log.i(TAG, "valuebyKeyafterUpdate:" + valuebyKeyafterUpdate);
                return update_results;
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
            //Todo query for cursor
            Cursor cursor = db.rawQuery(selectValueByKey, null);
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0)
                    do {
                        //TODO:get data from sqlite
                        String value = cursor.getString(0);
                        queue_List_value = value;
                    } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return queue_List_value;
    }

    @Override
    public List<Card> _getListCardByStatus(int status) {

        //Query select_list_card_by_status
        String select_list_card_by_status = "SELECT  * FROM " + TABLE_VOCABULARY + " where status = " + status;

        //Get card list by status
        List cardListByStatus = _getListCardQueryString(select_list_card_by_status);
        return cardListByStatus;
    }

    /**
     * Get List Card by queue
     *
     * @param queue
     */
    @Override
    public List<Card> _getListCardByQueue(int queue) {
//
//        if (queue <= 600l) {

        //Query select_list_card_by_queue
        String select_list_card_by_queue = "SELECT  * FROM " + TABLE_VOCABULARY + " where queue = " + queue;

        //Get card list by status
        List<Card> cardListByQueue = _getListCardQueryString(select_list_card_by_queue);

        return cardListByQueue;
//        } else {
//            //select query
//            String selectQuery = "SELECT  * FROM " + TABLE_VOCABULARY + " where queue > 600";
//
//            //
//            Date now_date = new Date(queue);
//
//            //Get card list by status
//            List<Card> cardListAll = _getListCardQueryString(selectQuery);
//
//
//            List<Card> cardListDueToday = new ArrayList<Card>();
//            //TODO: Check queue date
//            for (Card card : cardListAll) {
//                //TODO:CompateTo date_due vs now date
//                Date card_due_date = new Date(card.getQueue());
//                if (_compreaToDate(card_due_date, now_date)) {
//                    cardListDueToday.add(card);
//                }
//
//            }
//            return cardListDueToday;
//        }


    }

    /**
     * Get last interver of card
     *
     * @param cardId return iterver second
     */
    @Override
    public int _getLastInterval(String cardId) {
        return 0;
    }

    /**
     * Get Card by id & update increase  one revew_user
     * param cardId
     *
     * @param cardId
     */
    @Override
    public void _increaseRev_user(String cardId) {

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
        return 0;
    }

    private boolean _compreaToDate(Date card_due_date, Date now_date) {
        try {
            Date date_compateTo = inputFormat.parse(card_due_date.toString());
            Date date_now = inputFormat.parse(now_date.toString());

            //TODO: format date to string
            String str_date_create_list_card_today_parse = outputFormat.format(date_compateTo);
            String str_date_now = outputFormat.format(date_now);

            //TODO: compareTo date learn vs now date
            if (str_date_create_list_card_today_parse.compareTo(str_date_now) == 0) {
                //TODO: Equal then return true
                Log.i(TAG, "date_compateTo is equal to date_now");

                return true;
            } else {
                //TODO: Not equal then return false
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     *
     * */
    private List<Card> _getListCardQueryString(String query) {
        List<Card> datas = new ArrayList<Card>();
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0)
                do {
                    //get data from sqlite
                    int id = cursor.getInt(0);
                    String question = cursor.getString(1);
                    String answers = cursor.getString(2);
                    String categories = cursor.getString(3);
                    String subcat = cursor.getString(4);
                    int status = 1;
                    int queue = cursor.getInt(9);
                    String _package = cursor.getString(10);
                    int level = cursor.getInt(11);
                    long due = cursor.getLong(12);

                    Card card = new Card(id, question, answers, categories, subcat, status, queue, due, _package, level);
                    datas.add(card);

                } while (cursor.moveToNext());
        }
        return datas;
    }
}
