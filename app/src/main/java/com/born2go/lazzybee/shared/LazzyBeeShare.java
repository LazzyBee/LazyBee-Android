package com.born2go.lazzybee.shared;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;

import com.born2go.lazzybee.R;
import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.Course;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;

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
    public static final int DEFAULT_TOTAL_LEAN_PER_DAY = 20;
    public static final String CARDID = "cardId";

    public static final String KEY_SETTING_TODAY_NEW_CARD_LIMIT = "today_new_card_limit";
    public static final String KEY_SETTING_TODAY_REVIEW_CARD_LIMIT = "today_review_card_limit";
    public static final String KEY_SETTING_TODAY_LEARN_MORE_PER_DAY_LIMIT = "max_learn_more_per_day";
    //Total card learn
    public static final String KEY_SETTING_TOTAL_CARD_LEARN_PRE_DAY_LIMIT = "total_card_learn_pre_day";

    public static final String KEY_SETTING_AUTO_CHECK_UPDATE = "auto_check_update";
    public static final String KEY_SETTING_DEBUG_INFOR = "debug_infor";
    public static final String KEY_SETTING_NOTIFICTION = "notification";
    public static final String KEY_SETTING_SPEECH_RATE = "speech_rate";
    public static final String KEY_SETTING_MY_LEVEL = "my_level";


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

    public static final int DRAWER_DICTIONARY_INDEX = 5;

    public static final int CODE_COMPLETE_STUDY_RESULTS_1000 = 1000;
    public static final int CODE_SEARCH_RESULT = 1001;
    public static final String NOTIFICATION_MESSAGE = "n_message";
    public static final String NOTIFICATION_INDEX = "index";
    public static final String INIT_NOTIFICATION = "init_notification";
    public static final String NOTIFICATION_WHEN = "when";
    public static final String KEY_SETTING_POSITION_MEANIG = "position_meaning";
    public static final String YES = "yes";
    public static final String NO = "no";


    private static boolean DEBUG = true;
    private static boolean POSITION_MEANING = true;
    public static final String CARD_MEANING = "meaning";
    public static final String CARD_PRONOUN = "pronoun";
    public static final String CARD_EXPLAIN = "explain";
    public static final String CARD_EXAMPLE = "example";
    public static final String KEY_LANGUAGE = "lang";

    public static final String LANG_EN = "en";
    public static final String LANG_VI = "vi";

    public static final String DB_VERSION = "db_v";
    public static final String GAE_DB_VERSION = "gae_db_version";
    public static final java.lang.String DB_UPDATE_NAME = "update.db";
    public static final int NO_DOWNLOAD_UPDATE = 0;
    public static final int DOWNLOAD_UPDATE = 1;
    public static final String ON = "on";
    public static final String OFF = "off";

    public static final String UP = "Up";
    public static final String DOWN = "Down";

    public static String mime = "text/html";
    public static String encoding = "utf-8";
    public static String ASSETS = "file:///android_asset/";

    public static final int DEFAULT_MAX_NEW_LEARN_PER_DAY = 10;
    public static final int MAX_REVIEW_LEARN_PER_DAY = 10;
    public static final int DEFAULT_MAX_LEARN_MORE_PER_DAY = 5;
    public static final int DEFAULT_MY_LEVEL = 0;

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
    public static final int CARD_INDEX_L_VN = 17;
    public static final int CARD_INDEX_L_EN = 18;

    public static final String PRE_FETCH_NEWCARD_LIST = "pre_fetch_newcard_list";


    public static int VERSION_SERVER = 4;

    //https://docs.google.com/uc?export=download&id=0B34E3-aHBkuFR0hIU3FCU0xuU28
    //https://docs.google.com/uc?export=download&id=0B34E3-aHBkuFd05remxQR0ctU0E

    //28/8/2015 database upgrade
    //https://docs.google.com/uc?export=download&id=0B34E3-aHBkuFSEJOREdDQ2VLQ28
    public static final String URL_DATABASE_UPDATE = "https://docs.google.com/uc?export=download&id=0B34E3-aHBkuFSEJOREdDQ2VLQ28";

    static SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
    public static String BASE_URL_DB = "base_url_db";
    public static String ADV_ENABLE ="adv_enable";

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
                        "</head>\n" +
                        "<body onload='question.playQuestion()'>\n" +
                        "<div style='width:100%'>\n" +
                        "<div style='float:left;width:90%;text-align: center;'>\n" +
                        "<strong style='font-size:" + context.getResources().getDimension(R.dimen.study_question_size) + "pt'>" + question + "</strong>\n" +
                        "</div>\n" +
                        "<div style='float:left;width:10%'>\n" +
                        "<a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/><p>\n" +
                        "</div>\n" +
                        "</div>\n" +
                        "</body>\n" +
                        "</html>";
        //Log.v(TAG, html);
        return html;
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
            Log.e(TAG, "_getValueFromKey\tError:" + e.getMessage());
            //e.printStackTrace();
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
            //System.out.println("-Package:" + pack);
            packages.add(pack);
        }

        return packages;
    }

    public static String getAnswerHTMLwithPackage(Context context, Card card, String packages, boolean onload) {
        getDebugSetting();
        getPositionMeaning();

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
            // System.out.print("\npackagesObj.length():" + packagesObj.length());
            if (packagesObj.length() > 0) {
                JSONObject commonObj = packagesObj.getJSONObject(packages);
                meaning = commonObj.getString("meaning");
                explain = commonObj.getString("explain");
                example = commonObj.getString("example");
            } else {
                _example = EMPTY;
                _explain = EMPTY;
                Log.e(TAG, "getAnswerHTMLwithPackage E:Passing JSON ERROR");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!explain.isEmpty()) {
            explainTagA = "<p style=''><a onclick='explain.speechExplain();'><img src='ic_speaker_red.png'/></a></p>";
        }
        if (!example.isEmpty()) {
            exampleTagA = "<p style=''><a onclick='example.speechExample();'><img src='ic_speaker_red.png'/></a></p>";
        }
        String meaningUP;
        String meaningDOWN;
        if (!POSITION_MEANING) {
            meaningUP = "<div style='float:left;width:90%;text-align: center;'>\n" +
                    "<font size='4' color='blue'><em>" + meaning + "</em></font>\n" +
                    "</div>";
            meaningDOWN = EMPTY;
        } else {
            meaningUP = EMPTY;
            meaningDOWN = "<div style='float:left;width:90%;text-align: center;'>\n" +
                    "<font size='4' color='blue'><em>" + meaning + "</em></font>\n" +
                    "</div>";
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
//                "           <font size='4' color='blue'><em>" + meaning + "</em></font>\n" +
                "       </div>\n" +
                "       <div style='float:left;width:10%'>\n" +
                "           <a onclick='question.playQuestion();'><img src='ic_speaker_red.png'/></a>\n" +
                "       </div>\n"
                + meaningUP +


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
                "       </div>\n"
                + meaningDOWN +
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

    private static void getPositionMeaning() {
        LearnApiImplements learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        String value = learnApiImplements._getValueFromSystemByKey(KEY_SETTING_POSITION_MEANIG);
        if (value == null)
            POSITION_MEANING = false;
        else if (value.equals(UP)) {
            POSITION_MEANING = false;
        } else if (value.equals(DOWN)) {
            POSITION_MEANING = true;
        }
    }

    private static void getDebugSetting() {
        LearnApiImplements learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        String value = learnApiImplements._getValueFromSystemByKey(KEY_SETTING_DEBUG_INFOR);
        if (value == null)
            DEBUG = false;
        else if (value.equals(ON)) {
            DEBUG = true;
        } else if (value.equals(OFF)) {
            DEBUG = false;
        }
    }

    public static String getTextColor(int color, String string) {
        String message = "<font color=" + color + ">" + string + "</font>";
        return message;
    }

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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setMenuIconFavoriteGreater21(MenuItem item, Context context) {
        if (item.getTitle().toString().equals(context.getString(R.string.action_not_favorite))) {
            item.setTitle(context.getString(R.string.action_favorite));
            item.setIcon(context.getDrawable(R.drawable.ic_action_important));
        } else {
            item.setTitle(context.getString(R.string.action_not_favorite));
            item.setIcon(context.getDrawable(R.drawable.ic_action_important));
        }
    }

    public static void setMenuIconFavoriteUnder20(MenuItem item, Context context) {
        if (item.getTitle().toString().equals(context.getString(R.string.action_not_favorite))) {
            item.setIcon(context.getResources().getDrawable(R.drawable.ic_action_important));
            item.setTitle(context.getString(R.string.action_favorite));
        } else {
            item.setTitle(context.getString(R.string.action_not_favorite));
            item.setIcon(context.getResources().getDrawable(R.drawable.ic_action_not_important));
        }

    }

    public static Drawable getDraweble(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDrawableGreater21(context, id);
        } else {
            return getDrawableUnder20(context, id);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable getDrawableGreater21(Context context, int id) {
        return context.getDrawable(id);
    }

    private static Drawable getDrawableUnder20(Context context, int id) {
        return context.getResources().getDrawable(id);
    }


}
