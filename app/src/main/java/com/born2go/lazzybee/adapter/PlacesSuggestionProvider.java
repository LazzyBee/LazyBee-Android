package com.born2go.lazzybee.adapter;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.born2go.lazzybee.db.Card;
import com.born2go.lazzybee.db.DataBaseHelper;
import com.born2go.lazzybee.db.impl.LearnApiImplements;
import com.born2go.lazzybee.gtools.LazzyBeeSingleton;
import com.born2go.lazzybee.shared.LazzyBeeShare;

import java.util.List;
import java.util.Map;

public class PlacesSuggestionProvider extends ContentProvider {
    private static final String LOG_TAG = "LazzyBee";

    public static final String AUTHORITY = "com.born2go.lazzybee.adapter.search_suggestion_provider";

    // UriMatcher constant for search suggestions
    private static final int SUGGEST_CARD = 1;
    DataBaseHelper dataBaseHelper;

    private static final UriMatcher uriMatcher;

    private static final String[] SUGGEST_COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
    };

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGEST_CARD);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SUGGEST_CARD);
    }

    private Map<String, String> CardMap;

    @Override
    public int delete(Uri uri, String arg1, String[] arg2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SUGGEST_CARD:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues arg1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreate() {
        dataBaseHelper = new DataBaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Log.d(LOG_TAG, "uri = " + uri);
        LearnApiImplements learnApiImplements = LazzyBeeSingleton.learnApiImplements;
        // Use the UriMatcher to see what kind of query we have
        switch (uriMatcher.match(uri)) {
            case SUGGEST_CARD:
                String query = uri.getLastPathSegment().toLowerCase().trim();
                Log.d(LOG_TAG, "query:" + query);
                MatrixCursor cursor = new MatrixCursor(SUGGEST_COLUMNS, 1);
                if (query != null || query.length() > 1) {
                    if (query.equals("search_suggest_query")) {
                        Log.d(LOG_TAG, "query=search_suggest_query");
//                        List<String> textSusggestions = learnApiImplements._getListTextSusggestion();
//                        for (int i = 0; i < textSusggestions.size(); i++) {
//                            String texts = textSusggestions.get(i);
//                            cursor.addRow(new String[]{String.valueOf("-1"), texts, LazzyBeeShare.EMPTY, LazzyBeeShare.EMPTY});
//                        }
                    } else if (query.length() > 1) {
                        Log.d(LOG_TAG, "Search suggestions requested.Query=" + query);
                        List<Card> cards = learnApiImplements._suggestionCard(query);
                        for (int i = 0; i < cards.size(); i++) {
                            Card card = cards.get(i);
                            String meaning = LazzyBeeShare._getValueFromKey(card.getAnswers(), LazzyBeeShare.CARD_MEANING);

                            cursor.addRow(new String[]{String.valueOf(card.getId()), card.getQuestion(), meaning, String.valueOf(card.getId())});
                        }
                        cursor.setNotificationUri(getContext().getContentResolver(), uri);
                    }
                } else {
                    Log.d(LOG_TAG, "query null or empty");
//                    List<String> textSusggestions = learnApiImplements._getListTextSusggestion();
//                    for (int i = 0; i < textSusggestions.size(); i++) {
//                        String texts = textSusggestions.get(i);
//                        cursor.addRow(new String[]{String.valueOf("-1"), texts, LazzyBeeShare.EMPTY, LazzyBeeShare.EMPTY});
//                    }
                }
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues arg1, String arg2, String[] arg3) {
        throw new UnsupportedOperationException();
    }
}