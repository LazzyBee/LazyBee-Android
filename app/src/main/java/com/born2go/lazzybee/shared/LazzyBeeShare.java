package com.born2go.lazzybee.shared;

import android.text.Html;
import android.util.Log;
import android.webkit.JavascriptInterface;

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
    public static final String EMPTY = "";
    public static final int TOTTAL_LEAN_PER_DAY = 20;
    public static final String CARDID = "cardId";
    private static final boolean DEBUG = true;
    public static List<String> initWord = Arrays.asList("hot", "you", "but", "now");
    public static String mime = "text/html";
    public static String encoding = "utf-8";
    public static String ASSETS = "file:///android_asset/";
    public static final int MAX_NEW_LEARN_PER_DAY = 10;
    public static final int MAX_LEARN_MORE_PER_DAY = 5;


    public static final String PRE_FETCH_NEWCARD_LIST = "pre_fetch_newcard_list";

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
    public static String getAnswerHTML(Card card, String _meaning, String _explain, String _example) {
        String html = null;
        try {
            JSONObject answerObj = new JSONObject(card.getAnswers());
            String pronoun = answerObj.getString("pronoun");
            JSONObject packagesObj = answerObj.getJSONObject("packages");

            String meaning = EMPTY;
            String explain = EMPTY;
            String example = EMPTY;


            try {
                JSONObject commonObj = packagesObj.getJSONObject("common");
                if (commonObj != null) {

                    meaning = Html.fromHtml(commonObj.getString("meaning")).toString();
                    explain = Html.fromHtml(commonObj.getString("explain")).toString();
                    example = Html.fromHtml(commonObj.getString("example")).toString();
//                    meaning = commonObj.getString("meaning");
//                    explain = commonObj.getString("explain");
//                    example = commonObj.getString("example");
//                    Log.i(TAG, "meaning" + meaning);
//                    Log.i(TAG, "explain" + explain);
//                    Log.i(TAG, "example" + example);
                }
            } catch (JSONException e) {
//                e.printStackTrace();
                System.out.print("Error 1:" + e.getMessage());
            }


            String explainTagA = EMPTY;
            String exampleTagA = EMPTY;


//            if (!meaning.isEmpty())
//                meaning = meaning + "<a onclick='meaning.playQuestion();'><img src='ic_speaker_red.png'/></a>";
//            else
//                meaning = "";

            if (!explain.isEmpty())
                explainTagA = "<a onclick='explain.speechExplain();'><img src='ic_speaker_red.png'/></a>";

            if (!example.isEmpty())
                exampleTagA = "<a onclick='example.speechExample();'><img src='ic_speaker_red.png'/></a>";

            String imageURL = EMPTY;
            html = "<html>\n" +
                    "<head>\n" +
                    "<meta content=\"width=device-width, initial-scale=1.0, user-scalable=yes\"\n" +
                    "name=\"viewport\">\n" +
                    "</head>\n" +
                    "<body >\n" +
                    "   <div style='width:100%'>\n" +
                    "       <div style='float:left;width:90%;text-align: center;'>\n" +
                    "           <strong style='font-size:25pt;'>" + card.getQuestion() + "</strong>\n" +
                    "       </div>\n" +
                    "       <div style='float:left;width:10%'>\n" +
                    "           <a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/></a>\n" +
                    "       </div>\n" +
                    "       <div style='width:90%'>\n" +
                    "           <center><font>" + pronoun + "</font></center>\n" +
                    "           <p style='text-align: center'><em style='color:blue;'>" + meaning + "</em></p>\n" +
                    "       </div>\n" +
                    "           <p style=\"text-align: center;\">" + imageURL + "</p>\n" +
                    "       <div style=\"width:100%\">\n" +
                    "           <div style=\"float:left;width:90%\">" +
                    "               <p><strong>" + _explain + "</strong></br>" + explain + "</p>\n" +
                    "               <p><strong>" + _example + "</strong></br>" + example + "</p>\n" +
                    "           </div>\n" +
                    "           <div style=\"float:right;;width:10%\">\n " +
                    "               <p><strong></strong></br>" + explainTagA + "</p>\n" +
                    "               <p><strong></strong></br>" + exampleTagA + "</p>\n" +
                    "           </div>\n" +
                    "       </div>\n" +
                    "   </div>\n";

            // Log.v(TAG, "html:" + html);
            String debug = "</body></html>\n";
            if (DEBUG) {
                debug = "       </div>\n" +
                        "           <div id='debug'>\n " +
                        "              Debug infor:</br>\n" +
                        "              -------------------------------------</br>\n" +
                        "              Level:" + card.getLevel() + "</br>\n" +
                        "              lat_ivl:" + card.getLast_ivl() + "</br>\n" +
                        "              Factor:" + card.getFactor() + "</br>\n" +
                        "              Rev_count:" + card.getRev_count() + "</br>\n" +
                        "              Due:" + card.getDue() + "-" + new Date(card.getDue()).toString() + "</br>\n" +
                        "              -------------------------------------</br>\n" +
                        "           </div>\n" +
                        "   </body>" +
                        "</html>\n";
            }
            html += debug;
        } catch (JSONException e) {
            // e.printStackTrace();
            System.out.print("Error 2:" + e.getMessage());
        }
        return html;
    }

    /**
     * init HTML question
     */
    public static String _getQuestionDisplay(String s) {
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
                        "a {" +
                        " margin-top:5px;" +
                        "}" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body onload='question.playQuestion()'>\n" +
                        "<div style='width:100%'>\n" +
                        "<div style='float:left;width:90%;text-align: center;'>\n" +
                        "<strong style='font-size:25pt;'>" + s + "</strong>\n" +
                        "</div>\n" +
                        "<div style='float:left;width:10%'>\n" +
                        "<a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/><p>\n" +
                        "</div>\n" +
                        "</div>\n"
                        + "</body>\n" +
                        "</html>";
        return html;
    }


    static SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    public static boolean compareDate(Date date1, Date date2) {
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static String _getValueFromKey(String answer, String key) {
        String value = EMPTY;

        try {
            JSONObject answerObj = new JSONObject(answer);
            JSONObject packagesObj = answerObj.getJSONObject("packages");//Get json package
            JSONObject commonObj = packagesObj.getJSONObject("common");//Get json by package name

            //get value by key
            value = Html.fromHtml(commonObj.getString(key)).toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return value;
    }

    /*
    * Conver package ,pacakage,...
    * to list String
    *
    * */
    public static List<String> getListPackageFormString(String aPackage) {
        List<String> packages = new ArrayList<String>();

        //split package
        String[] splitPackage = aPackage.split(",");

        for (int i = 1; i < splitPackage.length; i++) {
            String pack = splitPackage[i];
            System.out.println("-Package:" + pack);
            packages.add(pack);
        }

        return packages;
    }

    public static String getAnswerHTMLwithPackage(Card card, String packages) {
        String html = null;
        try {
            JSONObject answerObj = new JSONObject(card.getAnswers());
            String pronoun = answerObj.getString("pronoun");
            JSONObject packagesObj = answerObj.getJSONObject("packages");

            JSONObject commonObj = packagesObj.getJSONObject(packages);

            String meaning = Html.fromHtml(commonObj.getString("meaning")).toString();
            String explain = Html.fromHtml(commonObj.getString("explain")).toString();
            String example = Html.fromHtml(commonObj.getString("example")).toString();


            String explainTagA = EMPTY;
            String exampleTagA = EMPTY;

            Log.i(TAG, "meaning" + meaning);
            Log.i(TAG, "explain" + explain);
            Log.i(TAG, "example" + example);

//            if (!meaning.isEmpty())
//                meaning = meaning + "<a onclick='meaning.playQuestion();'><img src='ic_play_black.png'/></a>";
//            else
//                meaning = "";

            if (!explain.isEmpty())
                explainTagA = "<a onclick='explain.speechExplain();'><img src='ic_speaker_red.png'/></a>";

            if (!example.isEmpty())
                exampleTagA = "<a onclick='example.speechExample();'><img src='ic_speaker_red.png'/></a>";

            String imageURL = EMPTY;

            html = "<html>\n" +
                    "<head>\n" +
                    "<meta content=\"width=device-width, initial-scale=1.0, user-scalable=yes\"\n" +
                    "name=\"viewport\">\n" +
                    "</head>\n" +
                    "<div style='width:100%'>\n" +
                    "<div style='float:left;width:90%;text-align: center;'>\n" +
                    "<strong style='font-size:25pt;'>" + card.getQuestion() + "</strong>\n" +
                    "</div>\n" +
                    "<div style='float:left;width:10%'>\n" +
                    "<p><a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/></a><p>\n" +
                    "</div>\n" +
                    "</div>\n" +
                    "<div div style='width:90%'>\n" +
                    "<p style=\"text-align: center;\">" + pronoun + "</p>\n" +
                    "</div>\n" +
                    "<p style=\"text-align: center;\">" + imageURL + "</p>\n" +
                    "<div style=\"width:100%\">\n" +
                    "    <div style=\"float:left;width:90%\">" +
                    "<p><strong>Meaning:</strong></br><em style=\"color:blue\">" + meaning + "</em></p>\n" +
                    "<p><strong>Explain:</strong></br>" + explain + "</p>\n" +
                    "<p><strong>Example:</strong></br>" + example + "</p>\n" +
                    "</div>\n" +
                    "    <div style=\"float:right;;width:10%\">\n " +
                    "<p></br></p>\n" +
                    "<p><strong></strong></br>" + explainTagA + "</p>\n" +
                    "<p><strong></strong></br>" + exampleTagA + "</p>\n" +
                    "    </div>\n" +
                    "</div>\n" +
                    "</html>\n";
            Log.v(TAG, "html:" + html);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return html;
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
//        int value = MAX_NEW_LEARN_PER_DAY;
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
 /*
       /*
    *Java Scrip Object Question
    * */
    public static class JsObjectQuestion {
        @JavascriptInterface
        public String toString() {
            return "question";
        }
    }

    /*
   *Java Scrip Object explain
   * */
    public static class JsObjectExplain {
        @JavascriptInterface
        public String toString() {
            return "explain";
        }

    }

    /*
  *Java Scrip Object example
  * */
    public static class JsObjectExample {
        @JavascriptInterface
        public String toString() {
            return "example";
        }

    }
}
