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
    public final static int EASE_AGAIN = 1;
    public final static int EASE_HARD = 2;
    public final static int EASE_GOOD = 3;
    public final static int EASE_EASY = 4;

    final static int SECONDS_PERDAY = 86400;

    /*
    Return string of next time to review corresponded to ease level
     */
    public String nextIvlStr(Card card, int ease) {
        return nextIvlStr(card, ease, false);
    }


    public String nextIvlStr(Card card, int ease, boolean _short) {
        int ivl = nextIvl(card, ease);
        if (ivl == 0) {
            return "(k?t th�c)";
        }
        String s = Utils.fmtTimeSpan(ivl, _short);
//        try {
//            if (ivl < mCol.getConf().getInt("collapseTime")) {
//                s = "<" + s;
//            }
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
        return s;
    }


    /**
     * Return the next interval for CARD, in seconds.
     */
    public int nextIvl(Card card, int ease) {
        try {
            if (card.getQueue() == QUEUE_NEW_CRAM0 || card.getQueue() == QUEUE_LNR1 || card.getQueue() == QUEUE_DAY_LRN3) {
                return _nextLrnIvl(card, ease);
            }
            else if (ease == 1) { /*QUEUE_REV2 & ease = 1*/
                // lapsed
                JSONObject conf = _lapseConf(card);
                if (conf.getJSONArray("delays").length() > 0) {
                    return (int) (conf.getJSONArray("delays").getDouble(0) * 60.0);
                }
                return _nextLapseIvl(card, conf) * SECONDS_PERDAY;
            } else { /*QUEUE_REV2 & ease => 1*/
                // review
                return _nextRevIvl(card, ease) * SECONDS_PERDAY;
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private int _nextLrnIvl(Card card, int ease) {
        // this isn't easily extracted from the learn code
        if (card.getQueue() == 0) {
            card.setLeft(_startingLeft(card));
        }
        JSONObject conf = _lrnConf(card);
        try {
            if (ease == 1) {
                // fail
                return _delayForGrade(conf, conf.getJSONArray("delays").length());
            } else if (ease == 3) {
                // early removal
                if (!_resched(card)) {
                    return 0;
                }
                return _graduatingIvl(card, conf, true, false) * SECONDS_PERDAY;
            } else {
                int left = card.getLeft() % 1000 - 1;
                if (left <= 0) {
                    // graduate
                    if (!_resched(card)) {
                        return 0;
                    }
                    return _graduatingIvl(card, conf, false, false) * SECONDS_PERDAY;
                } else {
                    return _delayForGrade(conf, left);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Interval management ******************************************************
     * *****************************************
     */

    /**
     * Ideal next interval for CARD, given EASE.
     */
    private int _nextRevIvl(Card card, int ease) {
        try {
            long delay = _daysLate(card);
            int interval = 0;
            double margin = 1.0;
            double margin_ease4 = 1.0;
            double fct = card.getFactor() / 1000.0;
            int ivl2 = _constrainedIvl((int)((card.getIvl() + delay/4) * 1.2), margin, card.getIvl());
            int ivl3 = _constrainedIvl((int)((card.getIvl() + delay/2) * fct), margin, ivl2);
            int ivl4 = _constrainedIvl((int)((card.getIvl() + delay) * fct * margin_ease4), margin, ivl3);
            if (ease == 2) {
                interval = ivl2;
            } else if (ease == 3) {
                interval = ivl3;
            } else if (ease == 4) {
                interval = ivl4;
            }
            // interval capped?
            return Math.min(interval, conf.getInt("maxIvl"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    /** Integer interval after interval factor and prev+1 constraints applied */
    private int _constrainedIvl(int ivl, double margin, double prev) {
        return (int) Math.max(ivl * margin, prev + 1);
    }

    /**
     * Number of days later than scheduled.
     */
    private long _daysLate(Card card) {
        //long due = card.getODid() != 0 ? card.getODue() : card.getDue();
        long due = 0;
        long mToday = 0;

        return Math.max(0, mToday - due);
    }
}
