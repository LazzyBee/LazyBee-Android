package com.born2go.lazzybee.db.impl;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Hue on 7/22/2015.
 */
public class LearnApiImplementsTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void test_updateCardQueueAndCardDue() throws Exception {
//        long time = new Date().getTime();
//        System.out.println("Time long value is :" + time);
//        System.out.println("Time int value (before) is :" + ((int) time) / 1000);
//        System.out.println("Time int value (after) is :" + (int) (time / 1000));
//        System.out.println("Time int value (after) + 600 is :" + (int) (time / 1000 + 600));
//
//        Card card = new Card();
//
//        CardSched cardSched = new CardSched();
//        for (String ivl : cardSched.nextIvlStrLst(card))
//            System.out.println("Ivl:" + ivl);

    }

    @Test
    public void test_getListPackageFormString() throws Exception {
        LazzyBeeShare.getListPackageFormString(",common,it,basic,");

    }

    @Test
    public void test_getAnswerHTML() throws Exception {
        Card card = new Card();
        card.setQuestion("card");
        card.setAnswers("{\"q\":\"smash\"" +
                ", \"pronoun\":\"/smæʃ/\"" +
                ", \"packages\":{\"" +
                "common\":{\"" +
                "meaning\":\"<p>v. đập n&aacute;t</p>\", \"explain\":\"<p>To <strong>smash</strong> something is to break it into many small pieces</p>\", \"example\":\"<p>Jacob <strong>smashed</strong> the window with a rock</p>\"}}}");
        card.setLast_ivl(200);
        card.setDue(38210391803910l);
        card.setFactor(2000);
        card.setLevel(5);
        card.setRev_count(200);

        System.out.println("Card html:" + LazzyBeeShare.getAnswerHTML(card, "menaing", "explain", "example"));
    }
}