package es.openkratio.colibribook.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import es.openkratio.colibribook.misc.Constants;

/**
 * Created by PulidF01 on 22/04/2014.
 */
public class VoteTable {

    // Database table
    public static final String TABLE_VOTE = "vote";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ID_API = "id";
    public static final String COLUMN_MEMBER = "member";
    public static final String COLUMN_SESSION = "session";
    public static final String COLUMN_VOTE = "vote";

    // Database creation SQLite statement
    public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_VOTE
            + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ID_API + " integer not null unique, " + COLUMN_MEMBER
            + " text, " + COLUMN_SESSION + " text, " + COLUMN_VOTE
            + " text,);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(Constants.TAG, "Upgrading database from version " + oldVersion
                + " to " + newVersion + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTE);
        onCreate(database);
    }



}
