package com.born2go.lazzybee.algorithms;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nobody on 7/9/2015.
 * Calculate the vocabulary's size of user, base on result tests
 * We save the new result after every test.
 * More test, more accurate result
 *
 */
public class WordEstimate {
    public static double PRESUME_RATE = 1.1; /* Presume that user's voca is bigger than calculated number*/
    public static int MAX_LEVEL = 7;

    /*Store counter in arrays: Number of words, Number of known*/
    int wordTotal[];
    int wordTestTotal[];
    int wordTestOk[];

    double voca = 0;    /*User's voca base on wordLevels*/

    public WordEstimate(){
        //Init array to MAX_LEVEL +1 due to the level start from 1
        wordTotal = new int[MAX_LEVEL + 1];
        wordTestTotal = new int[MAX_LEVEL + 1];
        wordTestOk = new int[MAX_LEVEL + 1];
    }

    /**
     * At the begining, we have to setup initial params base on results in the
     * past, plus system voca number
     *
     * @param level popular level of word, from 1 - 7. Smaller is more popular
     * @param total total number of vocabulary in <b>level<b/>.
     * @param tested total vocabulary user had tested in <b>level<b/>.
     * @param testok number of vocabulary user confirmed he/she knows
     *
     */
    public void setUpParams(int level, int total, int tested, int testok){
        if (level > 0 && level <= MAX_LEVEL) {
            wordTotal[level] = total;
            wordTestTotal[level] = tested;
            wordTestOk[level] = testok;
        }
    }

    /**
     * Calculate the user's vocabulary, update <b>voca</b> variable
     * @return vocabulary size of user
     */
    public double _estimateVoca(){
        voca = 0;
        for (int i = 1; i <= MAX_LEVEL; i++){
            if (wordTestTotal[i] > 0) {
                if (wordTestOk[i] > wordTestTotal[i])
                    voca += wordTotal[i];
                else
                    voca += wordTestOk[i] * wordTotal[i] / wordTestTotal[i];
            }
        }
        return voca;
    }

    /**
     *  Get last voca's result from database
     */
    public void _getLastResult(){
        //TODO: Query database for voca,
    }

    public static int[] jsonArrayToIntArray(JSONArray jsonArray) throws JSONException {
        int length;
        length = (jsonArray.length() > MAX_LEVEL )? jsonArray.length(): MAX_LEVEL + 1;
        int[] ar = new int[length];
        for (int i = 0; i < length; i++) {
            ar[i] = jsonArray.getInt(i);
        }
        return ar;
    }

    public static JSONArray intArrayToJSONArray(int[] ar) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (int i : ar) {
            jsonArray.put(i);
        }
        return jsonArray;
    }

    /**
     * Update voca in real-time after each word answered
     * @param level popular level of word, from 1 - 7. Smaller is more popular
     * @param answer true if user's known already, false otherwise
     */
    public double updateVoca(int level, boolean answer){
        return updateVoca(level, answer, false);
    }

    /**
     * Update voca in real-time after each word answered.
     * If redo is true, you have to set level & answer exactly as doing answer - NOT in reverted way
     *
     * @param level popular level of word, from 1 - 7. Smaller is more popular
     * @param answer true if user's known already, false otherwise
     * @param redo true if user want to revert the previous answer
     */
    public double updateVoca(int level, boolean answer, boolean redo){
        if (level > 0 && level <= MAX_LEVEL) {
            if (!redo) {
                wordTestTotal[level] ++;
                if (answer)
                    wordTestOk[level] ++;
            }
            else{
                wordTestTotal[level] --;
                if (answer)
                    wordTestOk[level] --;
            }
        }
        return _estimateVoca();
    }

    public double getVoca(){
        _estimateVoca();
        return voca;
    }

}
