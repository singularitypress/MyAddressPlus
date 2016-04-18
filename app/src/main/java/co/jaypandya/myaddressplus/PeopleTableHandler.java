package co.jaypandya.myaddressplus;

/**
 * Created by Jay on 4/17/2016.
 */

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PeopleTableHandler {

    //Database Table
    public static final String TABLE_PEOPLE = "myTable";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DESIGNATION = "designation";
    public static final String COLUMN_FIRST_NAME = "firstname";
    public static final String COLUMN_LAST_NAME = "lastname";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_PROVINCE = "province";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_POSTAL_CODE = "postalcode";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PEOPLE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DESIGNATION + " text not null, "
            + COLUMN_FIRST_NAME + " text not null, "
            + COLUMN_LAST_NAME + " text not null, "
            + COLUMN_ADDRESS + " text not null, "
            + COLUMN_PROVINCE + " text not null, "
            + COLUMN_COUNTRY + " text not null, "
            + COLUMN_POSTAL_CODE + " text not null, "
            + ");";

    public static void onCreate(SQLiteDatabase database){
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){
        Log.w(PeopleTableHandler.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);
        onCreate(database);
    }
}
