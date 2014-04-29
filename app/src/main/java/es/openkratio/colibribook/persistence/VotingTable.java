package es.openkratio.colibribook.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import es.openkratio.colibribook.misc.Constants;

public class VotingTable {

	// Database table
	public static final String TABLE_VOTING = "voting";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ID_API = "id";
    public static final String COLUMN_ABSTAINS = "abstains";
    public static final String COLUMN_AGAINST_VOTES = "against_votes";
    public static final String COLUMN_ASSENT = "assent";
    public static final String COLUMN_ATTENDEE = "attendee";
    public static final String COLUMN_FOR_VOTES = "for_votes";
    public static final String COLUMN_NO_VOTES = "no_votes";
    public static final String COLUMN_NUMBER = "number";
    public static final String COLUMN_RECORD_TEXT = "record_text";
    public static final String COLUMN_RESOURCE_URI = "resource_uri";
    public static final String COLUMN_SESSION = "session";
    public static final String COLUMN_TITLE = "title";


	// Database creation SQLite statement
	public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_VOTING
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_ID_API + " integer not null unique, " + COLUMN_ABSTAINS
			+ " integer, " + COLUMN_AGAINST_VOTES + " integer, " + COLUMN_ASSENT
			+ " integer, " + COLUMN_ATTENDEE + " integer," + COLUMN_FOR_VOTES
			+ " integer, " + COLUMN_NO_VOTES + " integer," + COLUMN_NUMBER
            + " integer, " + COLUMN_RECORD_TEXT + " text," + COLUMN_RESOURCE_URI
            + " text, " + COLUMN_SESSION + " text," + COLUMN_TITLE + " text);";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(Constants.TAG, "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_VOTING);
		onCreate(database);
	}
}