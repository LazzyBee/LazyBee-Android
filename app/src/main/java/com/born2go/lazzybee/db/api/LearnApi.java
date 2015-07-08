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
     *
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

    List<Card> _getRandomCard(int number);
}
