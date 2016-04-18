package co.jaypandya.myaddressplus;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import co.jaypandya.myaddressplus.DatabaseHandler;
import co.jaypandya.myaddressplus.PeopleTableHandler;

public class MyPeopleContentProvider extends ContentProvider {

    // database
    private DatabaseHandler database;

    // Used for the UriMacher
    private static final int PEOPLE = 10;
    private static final int PERSON_ID = 20;

    private static final String AUTHORITY = "co.jaypandya.myaddressplus.persons.contentprovider";

    private static final String BASE_PATH = "persons";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/persons";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/person";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, PEOPLE);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", PERSON_ID);
    }

    @Override
    public boolean onCreate() {
        database = new DatabaseHandler(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(PeopleTableHandler.TABLE_PEOPLE);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PEOPLE:
                break;
            case PERSON_ID:
                // Adding the ID to the original query
                queryBuilder.appendWhere(PeopleTableHandler.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        //int rowsDeleted = 0;
        long id = 0;

        switch (uriType) {
            case PEOPLE:
                id = sqlDB.insert(PeopleTableHandler.TABLE_PEOPLE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case PEOPLE:
                rowsDeleted = sqlDB.delete(PeopleTableHandler.TABLE_PEOPLE, selection, selectionArgs);
                break;
            case PERSON_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(PeopleTableHandler.TABLE_PEOPLE, PeopleTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(PeopleTableHandler.TABLE_PEOPLE, PeopleTableHandler.COLUMN_ID + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;

        switch (uriType) {
            case PEOPLE:
                rowsUpdated = sqlDB.update(PeopleTableHandler.TABLE_PEOPLE, values, selection, selectionArgs);
                break;
            case PERSON_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PeopleTableHandler.TABLE_PEOPLE, values,
                            PeopleTableHandler.COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(PeopleTableHandler.TABLE_PEOPLE,
                            values, PeopleTableHandler.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { PeopleTableHandler.COLUMN_FIRST_NAME, PeopleTableHandler.COLUMN_LAST_NAME, PeopleTableHandler.COLUMN_ADDRESS, PeopleTableHandler.COLUMN_PROVINCE, PeopleTableHandler.COLUMN_COUNTRY, PeopleTableHandler.COLUMN_POSTAL_CODE, PeopleTableHandler.COLUMN_DESIGNATION, PeopleTableHandler.COLUMN_ID };

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}
