package com.born2go.lazzybee.algorithms;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import com.born2go.lazzybee.algorithms.CardSched;
import com.born2go.lazzybee.algorithms.Utils;
import com.born2go.lazzybee.db.Card;

import java.lang.System;


/**
 * Created by nobody on 7/21/2015.
 */
@SuppressWarnings("EmptyMethod")
public class CardSchedTest extends CardSched{
    CardSched scheduler;
    Card cardLast_ivl3days;
    Card cardLast_ivl23days;

    @SuppressWarnings("RedundantThrows")
    @Before
    public void setUp() throws Exception {
        scheduler = new CardSched();
        cardLast_ivl3days = new Card();
        cardLast_ivl3days.setLast_ivl(3);
        cardLast_ivl3days.setFactor(2500);

        cardLast_ivl23days = new Card();
        cardLast_ivl23days.setLast_ivl(23);
        cardLast_ivl23days.setFactor(2500);
    }

    @Test
    public void test__daysLate(){
        Card card_late_10days = new Card();

        card_late_10days.setDue(Utils.intNow() - 10 * CardSched.SECONDS_PERDAY);
        assertEquals(this._daysLate(card_late_10days), 0);

        card_late_10days.setQueue(Card.QUEUE_REV2);
        assertEquals(this._daysLate(card_late_10days), 10);
    }
    @Test
    public void testScheduler(){

    }
    @Test
    public void test_nextIntervalStr(){
//        System.out.println("===========Card 3 days: Next interval string=============");
//        System.out.println("Button AGAIN: " + scheduler._nextIvlStr(cardLast_ivl3days, Card.EASE_AGAIN));
//        System.out.println("Button HARD: " + scheduler._nextIvlStr(cardLast_ivl3days, Card.EASE_HARD));
//        System.out.println("Button GOOD: " + scheduler._nextIvlStr(cardLast_ivl3days, Card.EASE_GOOD));
//        System.out.println("Button EASY: " + scheduler._nextIvlStr(cardLast_ivl3days, Card.EASE_EASY));
//        System.out.println("===========Card 23 days: Next interval string=============");
//        System.out.println("Button AGAIN: " + scheduler._nextIvlStr(cardLast_ivl23days, Card.EASE_AGAIN));
//        System.out.println("Button HARD: " + scheduler._nextIvlStr(cardLast_ivl23days, Card.EASE_HARD));
//        System.out.println("Button GOOD: " + scheduler._nextIvlStr(cardLast_ivl23days, Card.EASE_GOOD));
//        System.out.println("Button EASY: " + scheduler._nextIvlStr(cardLast_ivl23days, Card.EASE_EASY));
    }

    @Test
    public void test_nextIntervalByDays(){
        assertEquals(cardLast_ivl3days.getLast_ivl(), 3);
        System.out.println("Next interval HARD: " + scheduler._nextIntervalByDays(cardLast_ivl3days, Card.EASE_HARD));
        System.out.println("Next interval GOOD: " + scheduler._nextIntervalByDays(cardLast_ivl3days, Card.EASE_GOOD));
        System.out.println("Next interval EASY: " + scheduler._nextIntervalByDays(cardLast_ivl3days, Card.EASE_EASY));
    }

    @Test
    public void test_answerCard(){
        Card card = new Card();
        System.out.println(debugCard("Card BEFORE AGAIN answer:\r\n", card));
        this.answerCard(card, Card.EASE_AGAIN);
        System.out.println(debugCard("Card AFTER AGAIN answer:\r\n", card));
        this.answerCard(card, Card.EASE_AGAIN);
        System.out.println(debugCard("Card AFTER 2 AGAIN answer:\r\n", card));
        this.answerCard(card, Card.EASE_HARD);
        System.out.println(debugCard("Card AFTER HARD answer:\r\n", card));

    }

    private String debugCard(String prefix, Card card){
        String debug = prefix;
        debug += "CardID: " + card.getId();
        debug += "\r\nQueue: " + card.getQueue();
        debug += "\r\nDue: " + card.getDue() + " ("+ Utils.doubleToTime(card.getDue())+")";
        debug += "\r\nRevCount: " + card.getRev_count();
        debug += "\r\nLast Ivl: " + card.getLast_ivl();
        debug += "\r\nEasy Factor: " + card.getFactor();
        return debug;
    }
}