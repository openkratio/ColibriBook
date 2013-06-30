package es.openkratio.colibribook.persistence;

import es.openkratio.colibribook.misc.Constants;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PartyTable {

	// Database table
	public static final String TABLE_PARTY = "party";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ID_API = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_LOGO_URL = "logourl";
	public static final String COLUMN_RESOURCE_URI = "resourceuri";
	public static final String COLUMN_VALIDATE = "validate";
	public static final String COLUMN_WEBPAGE = "webpage";

	// Database creation SQLite statement
	public static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_PARTY
			+ "(" + COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_ID_API + " integer not null unique, " + COLUMN_NAME
			+ " text, " + COLUMN_LOGO_URL + " text, " + COLUMN_RESOURCE_URI
			+ " text, " + COLUMN_VALIDATE + " integer," + COLUMN_WEBPAGE
			+ " text);";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(Constants.TAG, "Upgrading database from version " + oldVersion
				+ " to " + newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_PARTY);
		onCreate(database);
	}
}