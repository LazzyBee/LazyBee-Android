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
    List<Card> _getRandomCard(int number);

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
    int _updateQueueCard(String cardId,int queue);


}
