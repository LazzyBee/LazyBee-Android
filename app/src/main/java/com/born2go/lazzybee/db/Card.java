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


    public Card() {
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

    @Override
    public String toString() {
        return "Card{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answers='" + answers + '\'' +
                ", categories='" + categories + '\'' +
                ", subcat='" + subcat + '\'' +
                ", status=" + status +
                '}';
    }
}
