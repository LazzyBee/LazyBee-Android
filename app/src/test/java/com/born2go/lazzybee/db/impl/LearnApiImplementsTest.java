package com.born2go.lazzybee.db.impl;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Hue on 7/22/2015.
 */
@SuppressWarnings({"EmptyMethod", "RedundantThrows"})
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

    }

    @Test
    public void test_getAnswerHTML() throws Exception {
        Card card = new Card();
        card.setQuestion("card");
        card.setAnswers("{\"q\":\"display\", " +
                "\"pronoun\":\"/dɪˈspleɪ/\", \"packages\":" +
                "{\"common\":{\"meaning\":\"<p>v. tr&igrave;nh chiếu</p>\"," +
                " \"explain\":\"<p>To <strong>display</strong> something is to show it, especially by putting it in a certain place</p>\", \"example\":\"<p>The museum <strong>displayed</strong> many wonderful paintings</p>\"}, \"it\":{\"meaning\":\"<p>n. m&agrave;n h&igrave;nh (m&aacute;y t&iacute;nh)</p>\", \"explain\":\"\", \"example\":\"\"}}}");
        card.setLast_ivl(200);
        card.setDue(38210391803910l);
        card.setFactor(2000);
        card.setLevel(5);
        card.setRev_count(200);

        //System.out.println("Card html:" + LazzyBeeShare.getAnswerHTML(card, "menaing", "explain", "example"));
    }

    @Test
    public void test_getAnswerHTMLwithPackage() throws Exception {
        Card card = new Card();
        card.setQuestion("debate");
//        card.setAnswers("{\"q\":\"display\", " +
//                "\"pronoun\":\"/dɪˈspleɪ/\", \"packages\":" +
//                "{\"common\":{\"meaning\":\"<p>v. tr&igrave;nh chiếu</p>\"," +
//                " \"explain\":\"<p>To <strong>display</strong> something is to show it, especially by putting it in a certain place</p>\", \"example\":\"<p>The museum <strong>displayed</strong> many wonderful paintings</p>\"}, \"it\":{\"meaning\":\"<p>n. m&agrave;n h&igrave;nh (m&aacute;y t&iacute;nh)</p>\", \"explain\":\"\", \"example\":\"\"}}}");
        card.setAnswers("{\"q\":\"debate\", \"pronoun\":\"/dɪˈbeɪt/\", \"packages\":{}}");

        card.setLast_ivl(200);
        card.setDue(38210391803910l);
        card.setFactor(2000);
        card.setLevel(5);
        card.setRev_count(200);
      // LazzyBeeShare.getAnswerHTMLwithPackage(card, "menaing","","",false);

        //System.out.println("Card html:" + );
    }


}