package com.born2go.lazzybee.shared;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.Course;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hue on 6/29/2015.
 */
public class LazzyBeeShare {

    public static final int COURSE_ID_TEST = 100;
    public static List<String> initWord = Arrays.asList("hot", "you", "but", "now");
    public static String mime = "text/html";
    public static String encoding = "utf-8";
    public static String ASSETS = "file:///android_asset/";

    /**
     * Init data demo List Course
     */
    public static List<Course> initListCourse() {
        List<Course> courses=new ArrayList<Course>();
        courses.add(new Course("Spain"));
        courses.add(new Course("Italia"));
        courses.add(new Course("Korea"));
        courses.add(new Course("Japan"));
        courses.add(new Course("USA"));
        courses.add(new Course("Lao"));
        courses.add(new Course("Thailand"));
        return courses;
    }

    /**
     * init HTML answer
     */
    public static String getAnswerHTML(Card card) {
        String html =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<style>\n" +
                        " figure {" +
                        "   text-align: center;" +
                        "   margin: auto;" +
                        "}" +
                        "figure.image img {" +
                        "   width: 100% !important;" +
                        "   height: auto !important;" +
                        "}" +
                        "figcaption {" +
                        "   font-size: 10px;" +
                        "}" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>" + card.getQuestion() + "<a onclick='question.playQuestion();'><img src='ic_play_black.png'/></a></h1>" +
                        "<h3>" + card.getAnswers() + "<a onclick='answers.playAnswers();'><img src='ic_play_black.png'/></a></h3>"
                        + "</body>\n" +
                        "</html>";
        return html;
    }

}
