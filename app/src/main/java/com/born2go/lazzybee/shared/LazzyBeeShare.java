package com.born2go.lazzybee.shared;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.Course;
import com.born2go.lazzybee.db.impl.LearnApiImplements;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    public static final int TOTAL_LEAN_PER_DAY = 20;
    public static final String CARDID = "cardId";

    public static final String KEY_SETTING_TODAY_NEW_CARD_LIMIT = "today_new_card_limit";
    public static final String KEY_SETTING_TODAY_REVIEW_CARD_LIMIT = "today_review_card_limit";
    public static final String KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY = "total_card_learn_pre_day";
    public static final String KEY_SETTING_MAX_LEARN_MORE_PER_DAY = "max_learn_more_per_day";
    public static final String KEY_SETTING_AUTO_CHECK_UPDATE = "auto_check_update";
    public static final String KEY_SETTING_DEBUG_INFOR = "debug_infor";
    public static final String KEY_SETTING_NOTIFICTION = "notification";

    public static final String DRAWER_USER = "user";
    public static final String DRAWER_TITLE_COURSE = "title_course";
    public static final String DRAWER_SETTING = "settings";
    public static final String DRAWER_ABOUT = "about";

    public static final String DRAWER_LINES = "lines";
    public static final String DRAWER_ADD_COURSE = "add_course";
    public static final int DRAWER_ADD_COURSE_INDEX = 0;
    public static final int DRAWER_SETTINGS_INDEX = 1;
    public static final int DRAWER_ABOUT_INDEX = 2;

    public static final int DRAWER_USER_INDEX = 3;

    public static final int DRAWER_COURSE_INDEX = 4;
    private static boolean DEBUG = true;
    public static final String CARD_MEANING = "meaning";
    public static final String CARD_PRONOUN = "pronoun";
    public static final String CARD_EXPLAIN = "explain";
    public static final String CARD_EXAMPLE = "example";
    public static final String KEY_LANGUAGE = "lang";

    public static final String LANG_EN = "en";
    public static final String LANG_VI = "vi";

    public static final String DB_VERSION = "db_v";
    public static final java.lang.String DB_UPDATE_NAME = "update.db";
    public static final int NO_DOWNLOAD_UPDATE = 0;
    public static final int DOWNLOAD_UPDATE = 1;
    public static final String ON = "on";
    public static final String OFF = "off";

    public static String mime = "text/html";
    public static String encoding = "utf-8";
    public static String ASSETS = "file:///android_asset/";
    public static final int MAX_NEW_LEARN_PER_DAY = 10;
    public static final int MAX_REVIEW_LEARN_PER_DAY = 10;
    public static final int MAX_LEARN_MORE_PER_DAY = 5;

    public static final String DOWNLOAD = "Download";

    //
    public static int CARD_INDEX_ID = 0;
    public static int CARD_INDEX_QUESTION = 1;
    public static int CARD_INDEX_ANSWER = 2;
    public static int CARD_INDEX_CATRGORIES = 3;
    public static int CARD_INDEX_SUBCAT = 4;
    public static int CARD_INDEX_TAGS = 5;
    public static int CARD_INDEX_RELATED = 6;
    public static int CARD_INDEX_GID = 7;
    public static int CARD_INDEX_STATUS = 8;
    public static int CARD_INDEX_QUEUE = 9;
    public static int CARD_INDEX_PACKAGE = 10;
    public static int CARD_INDEX_LEVEL = 11;
    public static int CARD_INDEX_DUE = 12;
    public static int CARD_INDEX_REV_COUNT = 13;
    public static int CARD_INDEX_USER_NOTE = 14;
    public static int CARD_INDEX_LAST_IVL = 15;
    public static int CARD_INDEX_E_FACTOR = 16;

    public static final String PRE_FETCH_NEWCARD_LIST = "pre_fetch_newcard_list";


    public static int VERSION_SERVER = 1;

    //https://docs.google.com/uc?export=download&id=0B34E3-aHBkuFR0hIU3FCU0xuU28
    //https://docs.google.com/uc?export=download&id=0B34E3-aHBkuFd05remxQR0ctU0E
    public static final String URL_DATABASE_UPDATE = "https://docs.google.com/uc?export=download&id=0B34E3-aHBkuFd05remxQR0ctU0E";

    static SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

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
    public static String getAnswerHTML(Context context, Card card) {
//        String html = null;
//        try {
//            JSONObject answerObj = new JSONObject(card.getAnswers());
//            String pronoun = answerObj.getString("pronoun");
//            JSONObject packagesObj = answerObj.getJSONObject("packages");
//
//            String meaning = EMPTY;
//            String explain = EMPTY;
//            String example = EMPTY;
//
//
//            try {
//                JSONObject commonObj = packagesObj.getJSONObject("common");
//                if (commonObj != null) {
//
//                    meaning = Html.fromHtml(commonObj.getString("meaning")).toString();
//                    explain = Html.fromHtml(commonObj.getString("explain")).toString();
//                    example = Html.fromHtml(commonObj.getString("example")).toString();
////                    meaning = commonObj.getString("meaning");
////                    explain = commonObj.getString("explain");
////                    example = commonObj.getString("example");
////                    Log.i(TAG, "meaning" + meaning);
////                    Log.i(TAG, "explain" + explain);
////                    Log.i(TAG, "example" + example);
//                }
//            } catch (JSONException e) {
////                e.printStackTrace();
//                System.out.print("Error 1:" + e.getMessage());
//            }
//
//
//            String explainTagA = EMPTY;
//            String exampleTagA = EMPTY;
//
//
////            if (!meaning.isEmpty())
////                meaning = meaning + "<a onclick='meaning.playQuestion();'><img src='ic_speaker_red.png'/></a>";
////            else
////                meaning = "";
//
//            if (!explain.isEmpty())
//                explainTagA = "<a onclick='explain.speechExplain();'><img src='ic_speaker_red.png'/></a>";
//
//            if (!example.isEmpty())
//                exampleTagA = "<a onclick='example.speechExample();'><img src='ic_speaker_red.png'/></a>";
//
//            String imageURL = EMPTY;
//            html = "<html>\n" +
//                    "<head>\n" +
//                    "<meta content=\"width=device-width, initial-scale=1.0, user-scalable=yes\"\n" +
//                    "name=\"viewport\">\n" +
//                    "</head>\n" +
//                    "<body >\n" +
//                    "   <div style='width:100%'>\n" +
//                    "       <div style='float:left;width:90%;text-align: center;'>\n" +
//                    "           <strong style='font-size:25pt;'>" + card.getQuestion() + "</strong>\n" +
//                    "       </div>\n" +
//                    "       <div style='float:left;width:10%'>\n" +
//                    "           <a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/></a>\n" +
//                    "       </div>\n" +
//                    "       <div style='width:90%'>\n" +
//                    "           <center><font>" + pronoun + "</font></center>\n" +
//                    "           <p style='text-align: center'><em style='color:blue;'>" + meaning + "</em></p>\n" +
//                    "       </div>\n" +
//                    "           <p style=\"text-align: center;\">" + imageURL + "</p>\n" +
//                    "       <div style=\"width:100%\">\n" +
//                    "           <div style=\"float:left;width:90%\">" +
//                    "               <p><strong>" + _explain + "</strong></br>" + explain + "</p>\n" +
//                    "               <p><strong>" + _example + "</strong></br>" + example + "</p>\n" +
//                    "           </div>\n" +
//                    "           <div style=\"float:right;;width:10%\">\n " +
//                    "               <p><strong></strong></br>" + explainTagA + "</p>\n" +
//                    "               <p><strong></strong></br>" + exampleTagA + "</p>\n" +
//                    "           </div>\n" +
//                    "       </div>\n" +
//                    "   </div>\n";
//
//            // Log.v(TAG, "html:" + html);
//            String debug = "</body></html>\n";
//            if (DEBUG) {
//                debug = "       </div>\n" +
//                        "           <div id='debug'>\n " +
//                        "              Debug infor:</br>\n" +
//                        "              -------------------------------------</br>\n" +
//                        "              Level:" + card.getLevel() + "</br>\n" +
//                        "              lat_ivl:" + card.getLast_ivl() + "</br>\n" +
//                        "              Factor:" + card.getFactor() + "</br>\n" +
//                        "              Rev_count:" + card.getRev_count() + "</br>\n" +
//                        "              Due:" + card.getDue() + "-" + new Date(card.getDue()).toString() + "</br>\n" +
//                        "              -------------------------------------</br>\n" +
//                        "           </div>\n" +
//                        "   </body>" +
//                        "</html>\n";
//            }
//            html += debug;
//        } catch (JSONException e) {
//            // e.printStackTrace();
//            System.out.print("Error 2:" + e.getMessage());
//        }
//        return html;
        return getAnswerHTMLwithPackage(context, card, "common", false);
    }

    /**
     * init HTML question
     */
    public static String _getQuestionDisplay(Context context, String question) {
        String html =
                "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<meta content=\"width=device-width, initial-scale=1.0, user-scalable=yes\"\n" +
                        "name=\"viewport\">\n" +
                        /*"<style>\n" +
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
                        "</style>\n" +*/
                        "</head>\n" +
                        "<body onload='question.playQuestion()'>\n" +
                        "<div style='width:100%'>\n" +
                        "<div style='float:left;width:90%;text-align: center;'>\n" +
                        "<strong style='font-size:"+context.getResources().getDimension(R.dimen.study_question_size)+"pt'>" + question + "</strong>\n" +
                        "</div>\n" +
                        "<div style='float:left;width:10%'>\n" +
                        "<a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/><p>\n" +
                        "</div>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";
        Log.v(TAG, html);
        return html;
    }


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
            if (key.equals(CARD_PRONOUN))
                value = answerObj.getString(key).toString();
            else {
                value = Html.fromHtml(commonObj.getString(key)).toString();

            }


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

    public static String getAnswerHTMLwithPackage(Context context, Card card, String packages, boolean onload) {
        getDebugSetting(context);
        String html = null;
        String meaning = EMPTY;
        String explain = EMPTY;
        String example = EMPTY;

        String pronoun = EMPTY;
        String explainTagA = EMPTY;
        String exampleTagA = EMPTY;
        String imageURL = EMPTY;
        String debug = "</body>\n</html>\n";
        String _example = context.getResources().getString(R.string.example);
        Object _explain = context.getResources().getString(R.string.explain);

        //Log.i(TAG, "getAnswerHTMLwithPackage: Card Answer:" + card.getAnswers());
        // System.out.print("getAnswerHTMLwithPackage: Card Answer:" + card.getAnswers() + "\n");
        try {
            JSONObject answerObj = new JSONObject(card.getAnswers());
            pronoun = answerObj.getString("pronoun");
            JSONObject packagesObj = answerObj.getJSONObject("packages");
            System.out.print("\npackagesObj.length():" + packagesObj.length());
            if (packagesObj.length() > 0) {
                System.out.print("\n Ok");
                JSONObject commonObj = packagesObj.getJSONObject(packages);
                meaning = commonObj.getString("meaning");
                explain = commonObj.getString("explain");
                example = commonObj.getString("example");
            } else {
                _example = EMPTY;
                _explain = EMPTY;
                System.out.print("\n not Ok");
            }

        } catch (Exception e) {
            //System.out.print("Error 2:" + e.getMessage() + "\n");
            e.printStackTrace();
            //return e.getMessage();
        }

        if (!explain.isEmpty()) {
            explainTagA = "<p style=''><a onclick='explain.speechExplain();'><img src='ic_speaker_red.png'/></a></p>";
        }
        if (!example.isEmpty()) {
            exampleTagA = "<p style=''><a onclick='example.speechExample();'><img src='ic_speaker_red.png'/></a></p>";
        }
        html = "\n<html>\n" +
                "<head>\n" +
                "<meta content=\"width=device-width, initial-scale=1.0, user-scalable=yes\"\n" +
                "name=\"viewport\">\n" +
                "</head>\n" +
                "<body " + ((onload == true) ? "onload='question.playQuestion()'" : "") + ">\n" +
                "   <div style='width:100%'>\n" +

                "       <div style='float:left;width:90%;text-align: center;'>\n" +
                "           <strong style='font-size:" + context.getResources().getDimension(R.dimen.study_question_size) + "'>" + card.getQuestion() + "</strong><br>\n" +
                "           <font size='3'>" + pronoun + "</font><br>\n" +
                "           <font size='4' color='blue'><em>" + meaning + "</em></font>\n" +
                "       </div>\n" +

                "       <div style='float:left;width:10%'>\n" +
                "           <a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/></a>\n" +
                "       </div>\n" +

                "       <div style='width:90%'>\n" +
                "       </div>\n" +

                "           <p style=\"text-align: center;\">" + imageURL + "</p>\n" +

                "       <div style=\"float:left;width:100%\">\n" +
                "            <div style=\"float:left;width:100%\"><strong>" + _explain + "</strong></div>\n" +
                "           <div style=\"float:left;width:90%\">\n" +
                "               " + explain + "\n" +
                "           </div>\n" +
                "           <div style=\"float:right;width:10%;vertical-align: middle;\">\n " +
                "               " + explainTagA + "\n" +
                "           </div>\n" +
                "       </div>\n" +

                "       <div style=\"float:left;width:100%\">\n" +
                "            <div style=\"float:left;width:100%\"><strong>" + _example + "</strong></div>\n" +
                "           <div style=\"float:left;width:90%\">\n" +
                "               " + example + "\n" +
                "           </div>\n" +
                "           <div style=\"float:right;width:10%;vertical-align: middle;\">\n " +
                "               " + exampleTagA + "\n" +
                "           </div>\n" +
                "       </div>\n" +

                "   </div>\n";

        if (DEBUG) {
            debug = "           <div id='debug'>\n " +
                    "              Debug infor:</br>\n" +
                    "              -------------------------------------</br>\n" +
                    "              Level:" + card.getLevel() + "</br>\n" +
                    "              lat_ivl:" + card.getLast_ivl() + "</br>\n" +
                    "              Factor:" + card.getFactor() + "</br>\n" +
                    "              Rev_count:" + card.getRev_count() + "</br>\n" +
                    "              Queue:" + card.getQueue() + "</br>\n" +
                    "              Due:" + card.getDue() + "-" + new Date(card.getDue()).toString() + "</br>\n" +
                    "              -------------------------------------</br>\n" +
                    "           </div>\n" +
                    "   </body>\n" +
                    "</html>\n";
        }
        html += debug;
        Log.w(TAG, "_getAnswerHTMLwithPackage: HTML return=" + html);

        //System.out.print("\n_getAnswerHTMLwithPackage: HTML return=" + html);
        //  Log.i(TAG, "Error:" + e.getMessage());
        return html;

    }

    private static void getDebugSetting(Context context) {
        LearnApiImplements learnApiImplements = new LearnApiImplements(context);
        String value = learnApiImplements._getValueFromSystemByKey(KEY_SETTING_DEBUG_INFOR);
        if (value == null)
            DEBUG = false;
        else if (value.equals(ON)) {
            DEBUG = true;
        } else if (value.equals(OFF)) {
            DEBUG = false;
        }
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
