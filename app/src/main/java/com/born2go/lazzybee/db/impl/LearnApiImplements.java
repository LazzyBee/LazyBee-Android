package com.born2go.lazzybee.db.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.api.LearnApi;

import java.util.ArrayList;
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
    private static final String TABLE = "vocabulary";


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

        String selectbyIDQuery = "SELECT  * FROM " + TABLE + " WHERE " + KEY_ID + " = " + cardId;
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
                    // Card card = new Card(id, question, answers, categories, subcat, 1);
                    card.setId(id);
                    card.setQuestion(question);
                    card.setAnswers(answers);
                    card.setCategories(categories);
                    card.setSubcat(subcat);
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
        List<Card> datas = new ArrayList<Card>();

        //select like query
        String likeQuery = "SELECT  * FROM " + TABLE + " WHERE " + KEY_QUESTION + " like '%" + query + "%'";
        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();

        //query for cursor
        Cursor cursor = db.rawQuery(likeQuery, null);
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
     * Get Random list card from today
     *
     * @param number
     */
    @Override
    public List<Card> _getRandomCard(int number) {
        return null;
    }

    /**
     * export to SqlIte form ListCard
     *
     * @param cardList
     * @return 1 if export complete else 2 to false
     */
    @Override
    public int export(List<Card> cardList) {
        return 0;
    }

    /**
     *
     * */
    private List<Card> _getListCard() {
        List<Card> datas = new ArrayList<Card>();
        //select query
        String selectQuery = "SELECT  * FROM " + TABLE;
        //select limit 5 row
        String selectLimitQuery = "SELECT  * FROM " + TABLE + " LIMIT 5 ";

        SQLiteDatabase db = this.dataBaseHelper.getReadableDatabase();
        //query for cursor
        Cursor cursor = db.rawQuery(selectLimitQuery, null);
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
        return db.update(TABLE, values, KEY_ID + " = ?",
                new String[]{String.valueOf(card.getId())});

    }
}
