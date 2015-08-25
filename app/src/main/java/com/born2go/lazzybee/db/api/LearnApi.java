package com.born2go.lazzybee.db.api;

import com.born2go.lazzybee.db.Card;

import java.util.List;

/**
 * Created by Hue on 7/8/2015.
 */
public interface LearnApi {
    /**
     * Get card by ID form sqlite
     *
     * @param cardId
     */
    Card _getCardByID(String cardId);

    /**
     * Get list card from today
     */
    List<Card> _getListCardForToday();

    /**
     * Get Review List Card Today
     * <p>List card complete in today</p>
     */
    List<Card> _getReviewListCard();

    /**
     * Seach card in Database
     *
     * @param query
     */
    List<Card> _searchCard(String query);

    /**
     * Get Random list card from today
     *
     * @param number
     */
    List<Card> _getRandomCard(int number,boolean learnmore);

    /**
     * _export to SqlIte form ListCard
     *
     * @param cardList
     * @return 1 if _export complete else 2 to false
     */
    int _export(List<Card> cardList);


    /**
     * _updateListCardByStatus to SqlIte form ListCard
     *
     * @param cardList
     * @param status
     * @return 1 if update complete else -1 false
     */
    int _updateListCardByStatus(List<Card> cardList, int status);


    /**
     * _updateCompleteCard to SqlIte form System Table
     *
     * @param cardId
     * @return 1 if update complete else -1 false
     */
    int _updateCompleteCard(String cardId);

    /**
     * _updateQueueCard to SqlIte form System Table
     *
     * @param cardId
     * @param queue
     * @return 1 if update complete else -1 false
     */
    int _updateQueueCard(String cardId, long queue);

    /**
     * _insertListTodayCard to SqlIte form System Table
     *
     * @param cardList
     * @return 1 if update complete else -1 false
     */
    int _insertListTodayCard(List<Card> cardList);

    /**
     * update Status Card
     *
     * @param cardId
     * @param status
     * @return 1 if update complete else -1 false
     */
    int _updateStatusCard(String cardId, int status);

    /**
     * add to system config
     * Key and value JSON
     *
     * @param key
     * @param value json string
     * @return 1 if update complete else -1 false
     */
    int _insertOrUpdateToSystemTable(String key, String value);

    /**
     * get value Json by key from System Table
     *
     * @param key
     * @return JSON Value String
     */
    String _getValueFromSystemByKey(String key);


    /**
     * Get List Card by Status
     *
     * @param status
     */
    List<Card> _getListCardByStatus(int status);

    /**
     * Get List Card by queue
     *
     * @param queue
     * @param limit
     */
    List<Card> _getListCardByQueue(int queue,int limit);

    /**
     * Update queue and due card
     *
     * @param cardId
     * @param queue  queue
     * @param due    due time review card
     */
    int _updateCardQueueAndCardDue(String cardId, int queue, int due);

    /**
     * Update card
     *
     * @param card
     */
    int _updateCard(Card card);


    int _get100Card();


    void _insertOrUpdateCard(Card card);

}
