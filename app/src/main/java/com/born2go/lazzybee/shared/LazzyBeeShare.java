package com.born2go.lazzybee.shared;

import android.text.Html;
import android.util.Log;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.Course;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Hue on 6/29/2015.
 */
public class LazzyBeeShare {

    public static final int COURSE_ID_TEST = 100;
    public static final String LEARN_MORE = "LEARN_MORE";
    private static final String TAG = "LazzyBeeShare";
    public static List<String> initWord = Arrays.asList("hot", "you", "but", "now");
    public static String mime = "text/html";
    public static String encoding = "utf-8";
    public static String ASSETS = "file:///android_asset/";
    public static final int MAX_LEARN_PER_DAY = 10;


    /**
     * Init data demo List Course
     */
    public static List<Course> initListCourse() {
        List<Course> courses = new ArrayList<Course>();
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
//        String html =
//                "<!DOCTYPE html>\n" +
//                        "<html>\n" +
//                        "<head>\n" +
//                        "<style>\n" +
//                        " figure {" +
//                        "   text-align: center;" +
//                        "   margin: auto;" +
//                        "}" +
//                        "figure.image img {" +
//                        "   width: 100% !important;" +
//                        "   height: auto !important;" +
//                        "}" +
//                        "figcaption {" +
//                        "   font-size: 10px;" +
//                        "}" +
//                        "</style>\n" +
//                        "</head>\n" +
//                        "<body>\n" +
//                        "<h1>" + card.getQuestion() + "<a onclick='question.playQuestion();'><img src='ic_play_black.png'/></a></h1>" +
//                        "<h3>" + card.getAnswers() + "<a onclick='answers.playAnswers();'><img src='ic_play_black.png'/></a></h3>"
//                        + "</body>\n" +
//                        "</html>";
        String html = null;
        try {
            JSONObject answerObj = new JSONObject(card.getAnswers());
            String pronoun = answerObj.getString("pronoun");
            JSONObject packagesObj = answerObj.getJSONObject("packages");

            JSONObject commonObj = packagesObj.getJSONObject("common");

            String meaning = Html.fromHtml(commonObj.getString("meaning")).toString();
            String explain = Html.fromHtml(commonObj.getString("explain")).toString();
            String example = Html.fromHtml(commonObj.getString("example")).toString();

            Log.i(TAG, "meaning" + meaning);
            Log.i(TAG, "explain" + explain);
            Log.i(TAG, "example" + example);

            if (!meaning.isEmpty())
                meaning = meaning + "<a onclick='meaning.playQuestion();'><img src='ic_play_black.png'/></a>";
            else
                meaning = "";

            if (!explain.isEmpty())
                explain = explain + "<a onclick='explain.playQuestion();'><img src='ic_play_black.png'/></a>";
            else
                explain = "";

            if (!example.isEmpty())
                example = example + "<a onclick='example.playQuestion();'><img src='ic_play_black.png'/></a>";
            else
                example = "";

            html = "<html>\n" +
                    "<head>\n" +
                    "<meta content=\"width=device-width, initial-scale=1.0, user-scalable=yes\"\n" +
                    "name=\"viewport\">\n" +
                    "</head><h1 style=\"text-align: center;\">" + card.getQuestion() + "<a onclick='question.playQuestion();'><img src='ic_play_black.png'/></a></h1>\n" +
                    "<p style=\"text-align: center;\">" + pronoun + "</p>\n" +
                    "<div style=\"width:100%\">\n" +
                    "    <div style=\"float:left;width:30%\"></div>\n" +
                    "    <div style=\"float:right;;width:70%\">\n " +
                    "<p>" + explain + "</p>\n" +
                    "<p>" + meaning + "</p>\n" +
                    "<p>" + example + "</p>\n" +
                    "    </div>\n" +
                    "</div></html>";
            Log.i(TAG, "html:" + html);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return html;
    }

    static SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    public static boolean compareDate(Date date1, Date date2) {
        return fmt.format(date1).equals(fmt.format(date2));
    }

//    public static String convertJsonObjMaxLearnPerDayToString(int maxlearn) {
//        String value = "";
//        Date date = new Date();
//
//        long long_date = date.getTime() / 1000;
//        JSONObject jsonObject = new JSONObject();
//
//        try {
//            jsonObject.put("date", long_date);
//            jsonObject.put("max", maxlearn);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        value = jsonObject.toString();
//        return value;
//    }
//
//    public static int getMaxLearnPerDay(String maxlearn) {
//        int value = MAX_LEARN_PER_DAY;
//
//        try {
//            JSONObject jsonObject = new JSONObject(maxlearn);
//            value = jsonObject.getInt("date");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//        return value;
//    }

}
