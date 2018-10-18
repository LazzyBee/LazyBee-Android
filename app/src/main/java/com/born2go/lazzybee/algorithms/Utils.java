/****************************************************************************************
 * Copyright (c) 2009 Daniel Svärd <daniel.svard@gmail.com>                             *
 * Copyright (c) 2009 Edu Zamora <edu.zasu@gmail.com>                                   *
 * Copyright (c) 2011 Norbert Nagold <norbert.nagold@gmail.com>                         *
 * Copyright (c) 2012 Kostas Spyropoulos <inigo.aldana@gmail.com>                       *
 *                                                                                      *
 * This program is free software; you can redistribute it and/or modify it under        *
 * the terms of the GNU General Public License as published by the Free Software        *
 * Foundation; either version 3 of the License, or (at your option) any later           *
 * version.                                                                             *
 *                                                                                      *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY      *
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A      *
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.             *
 *                                                                                      *
 * You should have received a copy of the GNU General Public License along with         *
 * this program.  If not, see <http://www.gnu.org/licenses/>.                           *
 ****************************************************************************************/

package com.born2go.lazzybee.algorithms;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * TODO comments
 */
public class Utils {
    enum SqlCommandType {SQL_INS, SQL_UPD, SQL_DEL}

    ;

    // Used to format doubles with English's decimal separator system
    public static final Locale ENGLISH_LOCALE = new Locale("en_US");

    public static final int CHUNK_SIZE = 32768;

    private static final int DAYS_BEFORE_1970 = 719163;

    private static NumberFormat mCurrentNumberFormat;
    private static NumberFormat mCurrentPercentageFormat;

    private static TreeSet<Long> sIdTree;
    private static long sIdTime;

    private static final int TIME_SECONDS = 0;
    private static final int TIME_MINUTES = 1;
    private static final int TIME_HOURS = 2;
    private static final int TIME_DAYS = 3;
    private static final int TIME_MONTHS = 4;
    private static final int TIME_YEARS = 5;

    public static final int TIME_FORMAT_DEFAULT = 0;
    public static final int TIME_FORMAT_IN = 1;
    public static final int TIME_FORMAT_BEFORE = 2;

    /* Prevent class from being instantiated */
    private Utils() {
    }

    // Regex pattern used in removing tags from text before diff
    private static final Pattern stylePattern = Pattern.compile("(?s)<style.*?>.*?</style>");
    private static final Pattern scriptPattern = Pattern.compile("(?s)<script.*?>.*?</script>");
    private static final Pattern tagPattern = Pattern.compile("<.*?>");
    private static final Pattern imgPattern = Pattern.compile("<img src=[\\\"']?([^\\\"'>]+)[\\\"']? ?/?>");
    private static final Pattern htmlEntitiesPattern = Pattern.compile("&#?\\w+;");

    private static final String ALL_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String BASE91_EXTRA_CHARS = "!#$%&()*+,-./:;<=>?@[]^_`{|}~";

    public static final int FILE_COPY_BUFFER_SIZE = 2048;

    /**
     * The time in integer seconds. Pass scale=1000 to get milliseconds.
     */
    public static double now() {
        return (System.currentTimeMillis() / 1000.0);
    }


    /**
     * The time in integer seconds. Pass scale=1000 to get milliseconds.
     */
    public static long intNow() {
        return intNow(1);
    }

    public static long intNow(int scale) {
        return (long) (now() * scale);
    }

    public static long durationInMillisec(long baseTime) {
        return intNow(1000) - baseTime;
    }

    private static double convertSecondsTo(int seconds, int type) {
        switch (type) {
            case TIME_SECONDS:
                return seconds;
            case TIME_MINUTES:
                return seconds / 60.0;
            case TIME_HOURS:
                return seconds / 3600.0;
            case TIME_DAYS:
                return seconds / 86400.0;
            case TIME_MONTHS:
                return seconds / 2592000.0;
            case TIME_YEARS:
                return seconds / 31536000.0;
            default:
                return 0;
        }
    }

    private static String convertSecondsToStr(int seconds, int type) {
        switch (type) {
            case TIME_SECONDS:
                return seconds + "s";
            case TIME_MINUTES:
                return fmtDouble(seconds / 60.0) + " p";
            case TIME_HOURS:
                return fmtDouble(seconds / 3600.0) + " h";
            case TIME_DAYS:
                return fmtDouble(seconds / 86400.0, 2) + " ngày";
            case TIME_MONTHS:
                return fmtDouble(seconds / 2592000.0, 2) + " tháng";
            case TIME_YEARS:
                return fmtDouble(seconds / 31536000.0, 2) + " năm";
            default:
                return "Error";
        }
    }
    /**
     * Locale
     * ***********************************************************************************************
     */

    /**
     * @return double with percentage sign
     */
    public static String fmtPercentage(Double value) {
        return fmtPercentage(value, 0);
    }

    public static String fmtPercentage(Double value, int point) {
        // only retrieve the percentage format the first time
        if (mCurrentPercentageFormat == null) {
            mCurrentPercentageFormat = NumberFormat.getPercentInstance(Locale.getDefault());
        }
        mCurrentNumberFormat.setMaximumFractionDigits(point);
        return mCurrentPercentageFormat.format(value);
    }


    /**
     * @return a string with decimal separator according to current locale
     */
    public static String fmtDouble(Double value) {
        return fmtDouble(value, 1);
    }

    public static String fmtDouble(Double value, int point) {
        // only retrieve the number format the first time
        if (mCurrentNumberFormat == null) {
            mCurrentNumberFormat = NumberFormat.getInstance(Locale.getDefault());
        }
        mCurrentNumberFormat.setMaximumFractionDigits(point);
        return mCurrentNumberFormat.format(value);
    }

    /**
     * Return a string representing a time span (eg '2 days').
     */
    public static String fmtTimeSpan(int time, int unit) {
        return fmtTimeSpan(time, false, false, unit);
    }

    public static String fmtTimeSpan(int time) {
        return fmtTimeSpan(time, false, false, 99);
    }

    public static String fmtTimeSpan(int time, boolean _short) {
        return fmtTimeSpan(time, _short, false, 99);
    }

    public static String fmtTimeSpan(int time, boolean _short, boolean boldNumber, int unit) {
        int type;
        if (Math.abs(time) < 60 || unit < 1) {
            type = TIME_SECONDS;
        } else if (Math.abs(time) < 3600 || unit < 2) {
            type = TIME_MINUTES;
        } else if (Math.abs(time) < 60 * 60 * 24 || unit < 3) {
            type = TIME_HOURS;
        } else if (Math.abs(time) < 60 * 60 * 24 * 29.5 || unit < 4) {
            type = TIME_DAYS;
        } else if (Math.abs(time) < 60 * 60 * 24 * 30 * 11.95 || unit < 5) {
            type = TIME_MONTHS;
        } else {
            type = TIME_YEARS;
        }
        double ftime = convertSecondsTo(time, type);


        String timeString = convertSecondsToStr(time, type);

/*
        if (boldNumber && time == 1) {
            timeString = timeString.replace("1", "<b>1</b>");
        }
*/
        return timeString;
    }

    /**
     * HTML
     * ***********************************************************************************************
     */

    /**
     * Strips a text from <style>...</style>, <script>...</script> and <_any_tag_> HTML tags.
     *
     * @param s The HTML text to be cleaned.
     * @return The text without the aforementioned tags.
     */
    public static String stripHTML(String s) {
        Matcher htmlMatcher = stylePattern.matcher(s);
        s = htmlMatcher.replaceAll("");
        htmlMatcher = scriptPattern.matcher(s);
        s = htmlMatcher.replaceAll("");
        htmlMatcher = tagPattern.matcher(s);
        s = htmlMatcher.replaceAll("");
        return entsToTxt(s);
    }


    /**
     * Strip HTML but keep media filenames
     */
    public static String stripHTMLMedia(String s) {
        Matcher imgMatcher = imgPattern.matcher(s);
        return stripHTML(imgMatcher.replaceAll(" $1 "));
    }

    private String minimizeHTML(String s) {
        // TODO
        return s;
    }

    /**
     * Takes a string and replaces all the HTML symbols in it with their unescaped representation.
     * This should only affect substrings of the form &something; and not tags.
     * Internet rumour says that Html.fromHtml() doesn't cover all cases, but it doesn't get less
     * vague than that.
     *
     * @param html The HTML escaped text
     * @return The text with its HTML entities unescaped.
     */
    private static String entsToTxt(String html) {
        // entitydefs defines nbsp as \xa0 instead of a standard space, so we
        // replace it first
        html = html.replace("&nbsp;", " ");
        Matcher htmlEntities = htmlEntitiesPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (htmlEntities.find()) {
            htmlEntities.appendReplacement(sb, Html.fromHtml(htmlEntities.group()).toString());
        }
        htmlEntities.appendTail(sb);
        return sb.toString();
    }

    /**
     * IDs
     * ***********************************************************************************************
     */

    public static String hexifyID(long id) {
        return Long.toHexString(id);
    }


    public static long dehexifyID(String id) {
        return Long.valueOf(id, 16);
    }


    /**
     * Given a list of integers, return a string '(int1,int2,...)'.
     */
    public static String ids2str(int[] ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (ids != null) {
            String s = Arrays.toString(ids);
            sb.append(s.substring(1, s.length() - 1));
        }
        sb.append(")");
        return sb.toString();
    }


    /**
     * Given a list of integers, return a string '(int1,int2,...)'.
     */
    public static String ids2str(long[] ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (ids != null) {
            String s = Arrays.toString(ids);
            sb.append(s.substring(1, s.length() - 1));
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Given a list of integers, return a string '(int1,int2,...)'.
     */
    public static String ids2str(Long[] ids) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (ids != null) {
            String s = Arrays.toString(ids);
            sb.append(s.substring(1, s.length() - 1));
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Given a list of integers, return a string '(int1,int2,...)'.
     */
    public static <T> String ids2str(List<T> ids) {
        StringBuilder sb = new StringBuilder(512);
        sb.append("(");
        boolean isNotFirst = false;
        for (T id : ids) {
            if (isNotFirst) {
                sb.append(", ");
            } else {
                isNotFirst = true;
            }
            sb.append(id);
        }
        sb.append(")");
        return sb.toString();
    }


    /**
     * Given a list of integers, return a string '(int1,int2,...)'.
     */
    public static String ids2str(JSONArray ids) {
        StringBuilder str = new StringBuilder(512);
        str.append("(");
        if (ids != null) {
            int len = ids.length();
            for (int i = 0; i < len; i++) {
                try {
                    if (i == (len - 1)) {
                        str.append(ids.get(i));
                    } else {
                        str.append(ids.get(i)).append(",");
                    }
                } catch (JSONException e) {
                    //Timber.e(e, "ids2str :: JSONException");
                }
            }
        }
        str.append(")");
        return str.toString();
    }


    /**
     * LIBANKI: not in libanki
     */
    public static long[] arrayList2array(List<Long> list) {
        long[] ar = new long[list.size()];
        int i = 0;
        for (long l : list) {
            ar[i++] = l;
        }
        return ar;
    }

    /**
     * Return the first safe ID to use.
     */
//    public static long maxID(AnkiDb db) {
//        long now = intNow(1000);
//        now = Math.max(now, db.queryLongScalar("SELECT MAX(id) FROM cards"));
//        now = Math.max(now, db.queryLongScalar("SELECT MAX(id) FROM notes"));
//        return now + 1;
//    }


    // used in ankiweb
    public static String base62(int num, String extra) {
        String table = ALL_CHARACTERS + extra;
        int len = table.length();
        String buf = "";
        int mod;
        while (num != 0) {
            mod = num % len;
            buf = buf + table.substring(mod, mod + 1);
            num = num / len;
        }
        return buf;
    }

    // all printable characters minus quotes, backslash and separators
    public static String base91(int num) {
        return base62(num, BASE91_EXTRA_CHARS);
    }


    /**
     * return a base91-encoded 64bit random number
     */
    public static String guid64() {
        return base91((new Random()).nextInt((int) (Math.pow(2, 61) - 1)));
    }

    // increment a guid by one, for note type conflicts
    public static String incGuid(String guid) {
        return new StringBuffer(_incGuid(new StringBuffer(guid).reverse().toString())).reverse().toString();
    }

    private static String _incGuid(String guid) {
        String table = ALL_CHARACTERS + BASE91_EXTRA_CHARS;
        int idx = table.indexOf(guid.substring(0, 1));
        if (idx + 1 == table.length()) {
            // overflow
            guid = table.substring(0, 1) + _incGuid(guid.substring(1, guid.length()));
        } else {
            guid = table.substring(idx + 1) + guid.substring(1, guid.length());
        }
        return guid;
    }

//    public static JSONArray listToJSONArray(List<Object> list) {
//        JSONArray jsonArray = new JSONArray();
//
//        for (Object o : list) {
//            jsonArray.put(o);
//        }
//
//        return jsonArray;
//    }
//
//
//    public static List<String> jsonArrayToListString(JSONArray jsonArray) throws JSONException {
//        ArrayList<String> list = new ArrayList<String>();
//
//        int len = jsonArray.length();
//        for (int i = 0; i < len; i++) {
//            list.add(jsonArray.getString(i));
//        }
//
//        return list;
//    }

    public static long[] jsonArrayToLongArray(JSONArray jsonArray) throws JSONException {
        long[] ar = new long[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++) {
            ar[i] = jsonArray.getLong(i);
        }
        return ar;
    }

    /**
     * Fields
     * ***********************************************************************************************
     */

    public static String joinFields(String[] list) {
        StringBuilder result = new StringBuilder(128);
        for (int i = 0; i < list.length - 1; i++) {
            result.append(list[i]).append("\u001f");
        }
        if (list.length > 0) {
            result.append(list[list.length - 1]);
        }
        return result.toString();
    }

    public static String[] splitFields(String fields) {
        // do not drop empty fields
        fields = fields.replaceAll("\\x1f\\x1f", "\u001f\u001e\u001f");
        fields = fields.replaceAll("\\x1f$", "\u001f\u001e");
        String[] split = fields.split("\\x1f");
        for (int i = 0; i < split.length; i++) {
            if (split[i].matches("\\x1e")) {
                split[i] = "";
            }
        }
        return split;
    }

    /**
     * Replace HTML line break tags with new lines.
     */
    public static String replaceLineBreak(String text) {
        return text.replaceAll("<br(\\s*\\/*)>", "\n");
    }

    /**
     *  Tempo files
     * ***********************************************************************************************
     */

    // tmpdir
    // tmpfile
    // namedtmp

    /**
     * Converts an InputStream to a String.
     *
     * @param is InputStream to convert
     * @return String version of the InputStream
     */
    public static String convertStreamToString(InputStream is) {
        String contentOfMyInputStream = "";
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is), 4096);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            contentOfMyInputStream = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return contentOfMyInputStream;
    }

    public static boolean unzipFiles(ZipFile zipFile, String targetDirectory, String[] zipEntries, HashMap<String, String> zipEntryToFilenameMap) {
        byte[] buf = new byte[FILE_COPY_BUFFER_SIZE];
        File dir = new File(targetDirectory);
        if (!dir.exists() && !dir.mkdirs()) {
            //Timber.e("Utils.unzipFiles: Could not create target directory: " + targetDirectory);
            return false;
        }
        if (zipEntryToFilenameMap == null) {
            zipEntryToFilenameMap = new HashMap<>();
        }
        BufferedInputStream zis = null;
        BufferedOutputStream bos = null;
        try {
            for (String requestedEntry : zipEntries) {
                ZipEntry ze = zipFile.getEntry(requestedEntry);
                if (ze != null) {
                    String name = ze.getName();
                    if (zipEntryToFilenameMap.containsKey(name)) {
                        name = zipEntryToFilenameMap.get(name);
                    }
                    File destFile = new File(dir, name);
                    File parentDir = destFile.getParentFile();
                    if (!parentDir.exists() && !parentDir.mkdirs()) {
                        return false;
                    }
                    if (!ze.isDirectory()) {
                        //Timber.i("uncompress %s", name);
                        zis = new BufferedInputStream(zipFile.getInputStream(ze));
                        bos = new BufferedOutputStream(new FileOutputStream(destFile), FILE_COPY_BUFFER_SIZE);
                        int n;
                        while ((n = zis.read(buf, 0, FILE_COPY_BUFFER_SIZE)) != -1) {
                            bos.write(buf, 0, n);
                        }
                        bos.flush();
                        bos.close();
                        zis.close();
                    }
                }
            }
        } catch (IOException e) {
            //Timber.e(e, "Utils.unzipFiles: Error while unzipping archive.");
            return false;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                //Timber.e(e, "Utils.unzipFiles: Error while closing output stream.");
            }
            try {
                if (zis != null) {
                    zis.close();
                }
            } catch (IOException e) {
                //Timber.e(e, "Utils.unzipFiles: Error while closing zip input stream.");
            }
        }
        return true;
    }

    /**
     * Compress data.
     *
     * @param bytesToCompress is the byte array to compress.
     * @return a compressed byte array.
     * @throws IOException
     */
    public static byte[] compress(byte[] bytesToCompress, int comp) throws IOException {
        // Compressor with highest level of compression.
        Deflater compressor = new Deflater(comp, true);
        // Give the compressor the data to compress.
        compressor.setInput(bytesToCompress);
        compressor.finish();

        // Create an expandable byte array to hold the compressed data.
        // It is not necessary that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(bytesToCompress.length);

        // Compress the data
        byte[] buf = new byte[65536];
        while (!compressor.finished()) {
            bos.write(buf, 0, compressor.deflate(buf));
        }

        bos.close();

        // Get the compressed data
        return bos.toByteArray();
    }

    /**
     * Utility method to write to a file.
     * Throws the exception, so we can report it in syncing log
     *
     * @throws IOException
     */
    public static void writeToFile(InputStream source, String destination) throws IOException {
        //Timber.d("Creating new file... = %s", destination);
        File file = new File(destination);

        if (!file.createNewFile()) {
            System.out.print("create file fails");
        }


        long startTimeMillis = System.currentTimeMillis();
        OutputStream output = new BufferedOutputStream(new FileOutputStream(destination));

        // Transfer bytes, from source to destination.
        byte[] buf = new byte[CHUNK_SIZE];
        long sizeBytes = 0;
        int len;

        while ((len = source.read(buf)) >= 0) {
            output.write(buf, 0, len);
            sizeBytes += len;
        }
        long endTimeMillis = System.currentTimeMillis();

        //Timber.d("Finished writeToFile!");
        long durationSeconds = (endTimeMillis - startTimeMillis) / 1000;
        long sizeKb = sizeBytes / 1024;
        //Timber.d("Utils.writeToFile: Size: %d Kb, Duration: %d s, Speed: %d Kb/s", sizeKb, durationSeconds, speedKbSec);
        output.close();
    }

    // Print methods
    public static void printJSONObject(JSONObject jsonObject) {
        printJSONObject(jsonObject, "-", null);
    }

    public static void printJSONObject(JSONObject jsonObject, boolean writeToFile) {
        BufferedWriter buff;
        try {
            buff = writeToFile ?
                    new BufferedWriter(new FileWriter("/sdcard/payloadAndroid.txt"), 8192) : null;
            try {
                printJSONObject(jsonObject, "-", buff);
            } finally {
                if (buff != null) {
                    buff.close();
                }
            }
        } catch (IOException ioe) {
            //Timber.e(ioe, "printJSONObject.IOException");
        }
    }

    private static void printJSONObject(JSONObject jsonObject, String indentation, BufferedWriter buff) {
        try {
            @SuppressWarnings("unchecked") Iterator<String> keys = (Iterator<String>) jsonObject.keys();
            TreeSet<String> orderedKeysSet = new TreeSet<>();
            while (keys.hasNext()) {
                orderedKeysSet.add(keys.next());
            }

            Iterator<String> orderedKeys = orderedKeysSet.iterator();
            while (orderedKeys.hasNext()) {
                String key = orderedKeys.next();

                try {
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONObject) {
                        if (buff != null) {
                            buff.write(indentation + " " + key + " : ");
                            buff.newLine();
                        }
                        //Timber.i("  " + indentation + key + " : ");
                        printJSONObject((JSONObject) value, indentation + "-", buff);
                    } else {
                        if (buff != null) {
                            buff.write(indentation + " " + key + " = " + jsonObject.get(key).toString());
                            buff.newLine();
                        }
                        //Timber.i("  " + indentation + key + " = " + jsonObject.get(key).toString());
                    }
                } catch (JSONException e) {
                    //Timber.e(e, "printJSONObject : JSONException");
                }
            }
        } catch (IOException e1) {
            //Timber.e(e1, "printJSONObject : IOException");
        }
    }

    /*
    public static void saveJSONObject(JSONObject jsonObject) throws IOException {
        Timber.i("saveJSONObject");
        BufferedWriter buff = new BufferedWriter(new FileWriter("/sdcard/jsonObjectAndroid.txt", true));
        buff.write(jsonObject.toString());
        buff.close();
    }
    */

    /**
     * Returns 1 if true, 0 if false
     *
     * @param b The boolean to convert to integer
     * @return 1 if b is true, 0 otherwise
     */
    public static int booleanToInt(boolean b) {
        return (b) ? 1 : 0;
    }

    /**
     * Returns the effective date of the present moment.
     * If the time is prior the cut-off time (9:00am by default as of 11/02/10) return yesterday,
     * otherwise today
     * Note that the Date class is java.sql.Date whose constructor sets hours, minutes etc to zero
     *
     * @param utcOffset The UTC offset in seconds we are going to use to determine today or yesterday.
     * @return The date (with time set to 00:00:00) that corresponds to today in Anki terms
     */
    public static Date genToday(double utcOffset) {
        // The result is not adjusted for timezone anymore, following libanki model
        // Timezone adjustment happens explicitly in Deck.updateCutoff(), but not in Deck.checkDailyStats()
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis(System.currentTimeMillis() - (long) utcOffset * 1000l);
        return Date.valueOf(df.format(cal.getTime()));
    }

    public static void printDate(String name, double date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.setTimeInMillis((long) date * 1000);
        //Timber.d("Value of %s: %s", name, cal.getTime().toGMTString());
    }

    public static String doubleToTime(double value) {
        int time = (int) Math.round(value);
        int seconds = time % 60;
        int minutes = (time - seconds) / 60;
        String formattedTime;
        if (seconds < 10) {
            formattedTime = Integer.toString(minutes) + ":0" + Integer.toString(seconds);
        } else {
            formattedTime = Integer.toString(minutes) + ":" + Integer.toString(seconds);
        }
        return formattedTime;
    }

    /**
     * Indicates whether the specified action can be used as an intent. This method queries the package manager for
     * installed packages that can respond to an intent with the specified action. If no suitable package is found, this
     * method returns false.
     *
     * @param context The application's environment.
     * @param action  The Intent action to check for availability.
     * @return True if an Intent with the specified action can be sent and responded to, false otherwise.
     */
    public static boolean isIntentAvailable(Context context, String action) {
        return isIntentAvailable(context, action, null);
    }


    public static boolean isIntentAvailable(Context context, String action, ComponentName componentName) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        intent.setComponent(componentName);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * @param mediaDir media directory path on SD card
     * @return path converted to file URL, properly UTF-8 URL encoded
     */
    public static String getBaseUrl(String mediaDir) {
        // Use android.net.Uri class to ensure whole path is properly encoded
        // File.toURL() does not work here, and URLEncoder class is not directly usable
        // with existing slashes
        if (mediaDir.length() != 0 && !mediaDir.equalsIgnoreCase("null")) {
            Uri mediaDirUri = Uri.fromFile(new File(mediaDir));

            return mediaDirUri.toString() + "/";
        }
        return "";
    }


    /**
     * Take an array of Long and return an array of long
     *
     * @param array The input with type Long[]
     * @return The output with type long[]
     */
    public static long[] toPrimitive(Long[] array) {
        if (array == null) {
            return null;
        }
        long[] results = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            results[i] = array[i];
        }
        return results;
    }

    public static long[] toPrimitive(Collection<Long> array) {
        if (array == null) {
            return null;
        }
        long[] results = new long[array.size()];
        int i = 0;
        for (Long item : array) {
            results[i++] = item;
        }
        return results;
    }


    public static void updateProgressBars(View view, int x, int y) {
        if (view == null) {
            return;
        }
        if (view.getParent() instanceof LinearLayout) {
            LinearLayout.LayoutParams lparam = new LinearLayout.LayoutParams(0, 0);
            lparam.height = y;
            lparam.width = x;
            view.setLayoutParams(lparam);
        } else if (view.getParent() instanceof FrameLayout) {
            FrameLayout.LayoutParams lparam = new FrameLayout.LayoutParams(0, 0);
            lparam.height = y;
            lparam.width = x;
            view.setLayoutParams(lparam);
        }
    }


    /**
     * Calculate the UTC offset
     */
    public static double utcOffset() {
        Calendar cal = Calendar.getInstance();
        // 4am
        return 4 * 60 * 60 - (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / 1000;
    }

    /**
     * Returns the filename without the extension.
     */
    public static String removeExtension(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition == -1) {
            return filename;
        }
        return filename.substring(0, dotPosition);
    }


    /**
     * Returns only the filename extension.
     */
    public static String getFileExtension(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition == -1) {
            return "";
        }
        return filename.substring(dotPosition);
    }

    /**
     * Removes any character that are not valid as deck names.
     */
    public static String removeInvalidDeckNameCharacters(String name) {
        if (name == null) {
            return null;
        }
        // The only characters that we cannot absolutely allow to appear in the filename are the ones reserved in some
        // file system. Currently these are \, /, and :, in order to cover Linux, OSX, and Windows.
        return name.replaceAll("[:/\\\\]", "");
    }

    /**
     * Joins the given string values using the delimiter between them.
     */
    public static String join(String delimiter, String... values) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (sb.length() != 0) {
                sb.append(delimiter);
            }
            sb.append(value);
        }
        return sb.toString();
    }

    /**
     * Simply copy a file to another location
     *
     * @param sourceFile The source file
     * @param destFile   The destination file, doesn't need to exist yet.
     * @throws IOException
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            if (destFile.createNewFile()){
                System.out.print("create file fails");
            }
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * Like org.json.JSONObject except that it doesn't escape forward slashes
     * The necessity for this method is due to python's 2.7 json.dumps() function that doesn't escape chracter '/'.
     * The org.json.JSONObject parser accepts both escaped and unescaped forward slashes, so we only need to worry for
     * our output, when we write to the database or syncing.
     *
     * @param json a json object to serialize
     * @return the json serialization of the object
     * @see JSONObject#toString()
     */
    public static String jsonToString(JSONObject json) {
        return json.toString().replaceAll("\\\\/", "/");
    }

    /**
     * Like org.json.JSONArray except that it doesn't escape forward slashes
     * The necessity for this method is due to python's 2.7 json.dumps() function that doesn't escape chracter '/'.
     * The org.json.JSONArray parser accepts both escaped and unescaped forward slashes, so we only need to worry for
     * our output, when we write to the database or syncing.
     *
     * @param json a json object to serialize
     * @return the json serialization of the object
     * @see JSONArray#toString()
     */
    public static String jsonToString(JSONArray json) {
        return json.toString().replaceAll("\\\\/", "/");
    }

    /**
     * @return A description of the device, including the model and android version. No commas are present in the
     * returned string.
     */
    public static String platDesc() {
        // AnkiWeb reads this string and uses , and : as delimiters, so we remove them.
        String model = android.os.Build.MODEL.replace(',', ' ').replace(':', ' ');
        return String.format(Locale.US, "android:%s:%s",
                android.os.Build.VERSION.RELEASE, model);
    }
}
