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
    
    String userID;

    double voca = 0;    /*User's voca base on wordLevels*/

    public WordEstimate(){
        //Init array to MAX_LEVEL +1 due to the level start from 1
        wordTotal = new int[MAX_LEVEL + 1];
        wordTestTotal = new int[MAX_LEVEL + 1];
        wordTestOk = new int[MAX_LEVEL + 1];
        userID = "";
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
     *  Parse last voca's result from database
     *  {
        "userid": "abcxyz",
        "tested":["0","1","2","3","4","5","6","7"],
        "testok":["0","1","2","3","4","5","6","7"]
        }
     */
    public void parseSaveResult(String json){

    }

    public String packSaveResult(){
        String jsonStr = "";
        try {
            JSONObject json = new JSONObject();
            json.put("userid", userID);
            JSONArray arrTested = new JSONArray();
            JSONArray arrTestOk = new JSONArray();
            for (int i = 0; i <= MAX_LEVEL; i++){
                arrTested.put(i,wordTestTotal[i]);
                arrTestOk.put(i,wordTestOk[i]);
            }
            json.put("tested",arrTested);
            json.put("testok",arrTestOk);

            jsonStr = json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonStr;
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

    /**
     * We get number of words correspond to each level
     * Base on the voca point of user, we will return suitable
     * words of levels for them
     * @param voca estimate vocabulary of user
     * @return int[MAX_LEVEL + 1]. Count from 1 (skip index 0).
     * If voca = 0, we provide sample data set
     */
    public int[] getNumberWordEachLevel(double voca){
        int arr[];
        if (voca == 0)/*return sample*/
            return _sampleNumberWordEachLevel();
        else{
            arr = _getEstimateLevel(voca);
            int level   = arr[0];
            int rate    = arr[1];
            /*Follow 80/20 rule:
            - 80% will be around level (level[0]) [level-1],[level],[level+1] varies by rate (level[1])
            - 20% will fix 10 for level 1, remain 10 random for uper levels*/
            int number[] = new int[MAX_LEVEL + 1];
            number[0] = 0;//Never mind
            number[1] = 10;//Fix number

            //Now we divide 80
            int a = (10 - rate)*2;
            int b = rate * 2;
            if (level > 1)
                number[level-1] += a + 5;
            else
                number[1] += a + 5;

            number[level] += 50;

            if (level < MAX_LEVEL)
                number[level+1] += b + 5;
            else
                number[MAX_LEVEL] += b + 5;

            //The last 10
            number[level] += 5;
            if (level + 2 < MAX_LEVEL)
                number[level+2] += 5;
            else
                number[MAX_LEVEL] += 5;
            //return value
            return number;
        }
    }

    protected int[] _sampleNumberWordEachLevel(){
        int number[] = new int[MAX_LEVEL + 1];
        number[0] = 0;
        number[1] = 10;
        number[2] = 15;
        number[3] = 40;
        number[4] = 25;
        number[5] = 10;
        number[6] = 0;
        number[7] = 0;
        return number;
    }

    static double   PERCEN_THRESOLD = 0.75;
    static int      WORDS_PER_LEVEL = 600;
    /**
     * We estimate real level base on vocabulary size, with one digit after point
     * Number before decimal point will be level, the after will be level's percentage completed
     *
     * @param voca estimated vocabulary of user
     * @return estimate level like 2.3
     */
    protected int[] _getEstimateLevel(double voca){
        int ret[] = new int[2];
        /* This looks quite complicated, but it's not
         * We assume that each level has 600 words, so if user has voca:
         * - voca < 450 words (75% level 1)     => user is at level 1
         * - 450 <= voca < 900 (75% level 1+2)   => user is at level 2
         * ... and so on
         */
        double a = voca/(WORDS_PER_LEVEL * PERCEN_THRESOLD) + 1; //Level starts from 1, not 0

        //Mulply by 10 so we can get one digit after decimal point
        int tmp = (int) Math.round(a*10);
        ret[0] = tmp/10;
        ret[1] = tmp%10;
        if (ret[0] > MAX_LEVEL) {
            ret[0] = MAX_LEVEL;
            ret[1] = 9;//Maximum number is 7.9
        }
        return ret;
    }
}
