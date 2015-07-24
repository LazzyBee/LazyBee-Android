package com.born2go.lazzybee.algorithms;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.born2go.lazzybee.algorithms.CardSched;
import com.born2go.lazzybee.db.Card;

import java.lang.System;

/**
 * Created by nobody on 7/21/2015.
 */
public class CardSchedTest extends CardSched{
    CardSched scheduler;
    @Before
    public void setUp() throws Exception {
        scheduler = new CardSched();
    }

    @Test
    public void test__daysLate(){
        Card card = new Card();

    }
    @Test
    public void testScheduler(){
        Card card = new Card();
        //card.se

    }
    @Test
    public void test_nextIntervalStr(){
        Card card = new Card();
            card.setLast_ivl(3);
            System.out.println("Next interval AGAIN: " + scheduler._nextIvlStr(card, Card.EASE_AGAIN));
            System.out.println("Next interval HARD: " + scheduler._nextIvlStr(card, Card.EASE_HARD));
            System.out.println("Next interval GOOD: " + scheduler._nextIvlStr(card, Card.EASE_GOOD));
            System.out.println("Next interval EASY: " + scheduler._nextIvlStr(card, Card.EASE_EASY));
    }

    @Test
    public void test_nextIntervalByDays(){
        Card card = new Card();
        card.setLast_ivl(3);
        assertEquals(card.getLast_ivl(), 3);
        System.out.println("Next interval HARD: " + scheduler._nextIntervalByDays(card, Card.EASE_HARD));
        System.out.println("Next interval GOOD: " + scheduler._nextIntervalByDays(card, Card.EASE_GOOD));
        System.out.println("Next interval EASY: " + scheduler._nextIntervalByDays(card, Card.EASE_EASY));
    }
}