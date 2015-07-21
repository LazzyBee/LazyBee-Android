package com.born2go.lazzybee.algorithms;

import com.born2go.lazzybee.activity.MainActivity;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.Utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nobody on 7/14/2015.
 */
public class CardSched {

    /*Static variables for queue value in database*/
    final static int QUEUE_NEW_CRAM0 = 0;
    final static int QUEUE_LNR1 = 1;
    final static int QUEUE_REV2 = 2;
    final static int QUEUE_DAY_LRN3 = 3;// Don't know what to do
    final static int QUEUE_SUSPENDED_1 = -1;
    final static int QUEUE_DONE_2 = -2;

    final static int REVLOG_LRN = 0;
    final static int REVLOG_REV = 1;
    final static int REVLOG_RELRN = 2;
    final static int REVLOG_CRAM = 3;
    /**
     * Return the next interval for CARD as a string.
     */
    public final static int EASE_AGAIN = 0;
    public final static int EASE_HARD = 1;
    public final static int EASE_GOOD = 2;
    public final static int EASE_EASY = 3;

    final static int SECONDS_PERDAY = 86400;

    /*
    Return string of next time to review corresponded to ease level
     */
    public String[] nextIvlStrLst(Card card) {
        String ret[] = new String[4];
        for (int i = 1; i < 4; i++){
            ret[i] = _nextIvlStr(card, i);
        }
        return ret;
    }
    /*
    Return string of next time to review corresponded to ease level
     */
    public String _nextIvlStr(Card card, int ease) {
        String str;
        int ivl = nextIvl(card, ease);
        double day = ivl / SECONDS_PERDAY;
        if (day < 1)
            str =  "<10min";
        else {
            str = Math.round(day) + "day";
        }
        return str;
    }


    /**
     * Return the next interval for CARD, in seconds.
     */
    public int nextIvl(Card card, int ease) {
        switch (ease){
            case EASE_AGAIN:
                return 600;
            case EASE_EASY:
                return SECONDS_PERDAY * 8;
            case EASE_GOOD:
                return SECONDS_PERDAY * 4;
            case EASE_HARD:
                return SECONDS_PERDAY;
            default:
                return SECONDS_PERDAY;
        }
    }

    /*
    * Whenever a Card is answered, call this function on Card:
    * - Scheduler will update
     */
    public void answerCard(Card card, int ease){

    }
}
