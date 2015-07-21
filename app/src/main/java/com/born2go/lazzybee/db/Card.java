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
    long queue;


    /*Static variables for queue value in database*/
    public static int QUEUE_NEW_CRAM0 = 0;
    public static int QUEUE_LNR1 = 1;
    public static int QUEUE_REV2 = 2;
    public static int QUEUE_DAY_LRN3 = 3;// Don't know what to do
    public static int QUEUE_SUSPENDED_1 = -1;
    public static int QUEUE_DONE_2 = -2;


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


    public long getQueue() {
        return queue;
    }

    public void setQueue(long queue) {
        this.queue = queue;
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


}
