package com.born2go.lazzybee.db;

import android.text.Html;

import com.born2go.lazzybee.shared.LazzyBeeShare;

import org.json.JSONObject;

/**
 * Created by Hue on 7/1/2015.
 */
public class Card {

    int id;
    long gId;/*"global unique id */

    String question;
    String answers;

    int status;
    int queue;

    String categories;
    String subcat;
    String _package;

    int level;      /*classify by popular rating */
    int rev_count;  /*Number of review count*/
    int factor;     /*Easy factor. Change every time user learns*/

    int last_ivl;/*Last interval by second*/
    long due;/*Time future,second*/

    String user_note;/*Anything user can note about this word*/

    String l_vn;
    String l_en;

    String pronoun;
    String meaning;
    String explain;
    String example;

    boolean custom_list;


    /*Static variables for queue value in database*/
    public static final int QUEUE_NEW_CRAM0 = 0;
    public static final int QUEUE_LNR1 = 1;
    public static final int QUEUE_REV2 = 2;
    public static int QUEUE_DAY_LRN3 = 3;// Don't know what to do
    public static int QUEUE_SUSPENDED_1 = -1;//Ignore - Bo qua
    public static int QUEUE_DONE_2 = -2;    //Learnt - Da biet

    public final static int EASE_AGAIN = 0;
    public final static int EASE_HARD = 1;
    public final static int EASE_GOOD = 2;
    public final static int EASE_EASY = 3;


    public Card() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getSubcat() {
        return subcat;
    }

    public void setSubcat(String subcat) {
        this.subcat = subcat;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getgId() {
        return gId;
    }

    public void setgId(long gId) {
        this.gId = gId;
    }


    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    public long getDue() {
        return due;
    }

    public void setDue(long due) {
        this.due = due;
    }

    public String getPackage() {
        return _package;
    }

    public void setPackage(String _package) {
        this._package = _package;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRev_count() {
        return rev_count;
    }

    public void setRev_count(int rev_count) {
        this.rev_count = rev_count;
    }

    public String getUser_note() {
        return user_note;
    }

    public void setUser_note(String user_note) {
        this.user_note = user_note;
    }

    public int getLast_ivl() {
        return last_ivl;
    }

    public void setLast_ivl(int last_ivl) {
        this.last_ivl = last_ivl;
    }

    public boolean isCustom_list() {
        return custom_list;
    }

    public void setCustom_list(boolean custom_list) {
        this.custom_list = custom_list;
    }

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", gId=" + gId +
                ", question='" + question + '\'' +
                ", queue=" + queue +
                ", _package='" + _package + '\'' +
                ", level=" + level +
                ", rev_count=" + rev_count +
                ", factor=" + factor +
                ", last_ivl=" + last_ivl +
                ", due=" + due +
                '}';
    }

    /**
     * Get Card by id & update increase one revew_user
     */
    public void increaseRevCount() {
        rev_count++;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public String getL_vn() {
        return l_vn;
    }

    public void setL_vn(String l_vn) {
        this.l_vn = l_vn;
    }

    public String getL_en() {
        return l_en;
    }

    public void setL_en(String l_en) {
        this.l_en = l_en;
    }


    public String getPronoun() {
        try {
            JSONObject answerObj = new JSONObject(answers);
            pronoun = answerObj.getString("pronoun");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pronoun;
    }

    public String getMeaning(String subject) {
        try {
            JSONObject answerObj = new JSONObject(answers);
            JSONObject packagesObj = answerObj.getJSONObject("packages");
            // System.out.print("\npackagesObj.length():" + packagesObj.length());
            if (packagesObj.length() > 0) {
                if (packagesObj.isNull(subject)) {
                    subject = "common";
                }
                JSONObject commonObj = packagesObj.getJSONObject(subject);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    meaning = Html.fromHtml(commonObj.getString("meaning"), Html.FROM_HTML_MODE_LEGACY).toString();
                } else {
                    meaning =  Html.fromHtml(commonObj.getString("meaning")).toString();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return meaning;
    }
    public String getMeaningWithHtml(String subject) {
        try {
            JSONObject answerObj = new JSONObject(answers);
            JSONObject packagesObj = answerObj.getJSONObject("packages");
            // System.out.print("\npackagesObj.length():" + packagesObj.length());
            if (packagesObj.length() > 0) {
                if (packagesObj.isNull(subject)) {
                    subject = "common";
                }
                JSONObject commonObj = packagesObj.getJSONObject(subject);
               return commonObj.getString("meaning");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return LazzyBeeShare.EMPTY;
        }
        return LazzyBeeShare.EMPTY;
    }

    public String getExplain(String subject, int type) {
        try {
            JSONObject answerObj = new JSONObject(answers);
            JSONObject packagesObj = answerObj.getJSONObject("packages");
            // System.out.print("\npackagesObj.length():" + packagesObj.length());
            if (packagesObj.length() > 0) {
                if (packagesObj.isNull(subject)) {
                    subject = "common";
                }
                JSONObject commonObj = packagesObj.getJSONObject(subject);
                if (type == LazzyBeeShare.TO_SPEECH_1)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        explain = Html.fromHtml(commonObj.getString("explain"), Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        explain = Html.fromHtml(commonObj.getString("explain")).toString();
                    }
                else
                    explain = commonObj.getString("explain");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return explain;
    }

    public String getExample(String subject, int type) {
        try {
            JSONObject answerObj = new JSONObject(answers);
            JSONObject packagesObj = answerObj.getJSONObject("packages");
            // System.out.print("\npackagesObj.length():" + packagesObj.length());
            if (packagesObj.length() > 0) {
                if (packagesObj.isNull(subject)) {
                    subject = "common";
                }
                JSONObject commonObj = packagesObj.getJSONObject(subject);
                if (type == LazzyBeeShare.TO_SPEECH_1)
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        example = Html.fromHtml(commonObj.getString("example"), Html.FROM_HTML_MODE_LEGACY).toString();
                    } else {
                        example = Html.fromHtml(commonObj.getString("example")).toString();
                    }
                else
                    example = commonObj.getString("example");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return example;
    }
}
