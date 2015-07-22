package com.born2go.lazzybee.db;

/**
 * Created by Hue on 7/1/2015.
 */
public class Card {

    int id;
    String question;
    String answers;
    String categories;
    String subcat;
    int status;
    int gId;
    int queue;
    long due;
    String _package;
    int level;

    int rev_count;
    String user_note;
    int last_ivl;


    /*Static variables for queue value in database*/
    public static int QUEUE_NEW_CRAM0 = 0;
    public static int QUEUE_LNR1 = 1;
    public static int QUEUE_REV2 = 2;
    public static int QUEUE_DAY_LRN3 = 3;// Don't know what to do
    public static int QUEUE_SUSPENDED_1 = -1;
    public static int QUEUE_DONE_2 = -2;

    public final static int EASE_AGAIN = 0;
    public final static int EASE_HARD = 1;
    public final static int EASE_GOOD = 2;
    public final static int EASE_EASY = 3;


    public Card(int id, String question, String answers, int status) {
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.status = status;
    }

    public Card(int id, String question, String answers, String categories, String subcat, int status, int gId) {
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.categories = categories;
        this.subcat = subcat;
        this.status = status;
        this.gId = gId;
    }

    public Card(int id, String question, String answers, String categories, String subcat, int status) {
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.categories = categories;
        this.subcat = subcat;
        this.status = status;

    }

    public Card(int id, String question, String answers, String categories, String subcat, int status, int queue, long due, String _package, int level) {
        this.id = id;
        this.question = question;
        this.answers = answers;
        this.categories = categories;
        this.subcat = subcat;
        this.status = status;
        this.queue = queue;
        this.due = due;
        this._package = _package;
        this.level = level;
    }

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

    public int getgId() {
        return gId;
    }

    public void setgId(int gId) {
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

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answers='" + answers + '\'' +
                ", status=" + status +
                '}';
    }

    /**
     * Get last interver of card
     *
     */
    public int getLastInterval() {
        return 0;
    }

    /**
     * Get Card by id & update increase one revew_user
     */
    public void increaseRev_user() {

    }


}
