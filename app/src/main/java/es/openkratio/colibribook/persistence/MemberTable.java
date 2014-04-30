package es.openkratio.colibribook.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import es.openkratio.colibribook.misc.Constants;

public class MemberTable {

	// Database table
	public static final String TABLE_MEMBER = "member";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_AVATAR_URL = "avatarurl";
	public static final String COLUMN_CONGRESS_WEB = "congressweb";
	public static final String COLUMN_DIVISION = "division";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_ID_API = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_RESOURCE_URI = "resourceuri";
	public static final String COLUMN_SECONDNAME = "secondname";
	public static final String COLUMN_TWITTER_USER = "twitteruser";
	public static final String COLUMN_VALIDATE = "validate";
	public static final String COLUMN_WEBPAGE = "webpage";
	public static final String COLUMN_PARTY_FK = "party";

	// Database creation SQLite statement
	public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_MEMBER
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_ID_API + " integer not null, " + COLUMN_NAME
			+ " text, " + COLUMN_AVATAR_URL + " text, " + COLUMN_CONGRESS_WEB
			+ " text, " + COLUMN_DIVISION + " text, " + COLUMN_EMAIL
			+ " text, " + COLUMN_RESOURCE_URI + " text, " + COLUMN_SECONDNAME
			+ " text, " + COLUMN_TWITTER_USER + " text, " + COLUMN_VALIDATE
			+ " integer, " + COLUMN_WEBPAGE + " text, " + COLUMN_PARTY_FK
			+ " integer, FOREIGN KEY(" + COLUMN_PARTY_FK + ") REFERENCES "
			+ PartyTable.TABLE_PARTY + "(" + PartyTable.COLUMN_ID_API
			+ ") ON DELETE CASCADE);";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(Constants.TAG, "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMBER);
		onCreate(database);
	}
}