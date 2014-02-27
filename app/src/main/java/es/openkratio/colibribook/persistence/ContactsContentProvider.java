package es.openkratio.colibribook.persistence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ContactsContentProvider extends ContentProvider {

    // database
    private DatabaseHelper dbHelper;

    // Used for the UriMacher
    private static final int MEMBERS = 10;
    private static final int MEMBER_ID = 20;
    private static final int PARTIES = 30;
    private static final int PARTY_ID = 40;
    private static final int MEMBERSANDPARTIES = 50;
    private static final int MEMBERANDPARTY_ID = 60;

    private static final String AUTHORITY = "es.openkratio.colibri.provider";

    private static final String BASE_PATH_MEMBER = "member";
    private static final String BASE_PATH_PARTY = "party";
    private static final String BASE_PATH_MEMBERANDPARTY = "memberandparty";

    public static final Uri CONTENT_URI_MEMBER = Uri.parse("content://"
            + AUTHORITY + "/" + BASE_PATH_MEMBER);
    public static final Uri CONTENT_URI_PARTY = Uri.parse("content://"
            + AUTHORITY + "/" + BASE_PATH_PARTY);
    public static final Uri CONTENT_URI_MEMBERANDPARTY = Uri.parse("content://"
            + AUTHORITY + "/" + BASE_PATH_MEMBERANDPARTY);

    public static final String CONTENT_MEMBER_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/members";
    public static final String CONTENT_MEMBER_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/member";
    public static final String CONTENT_PARTY_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/parties";
    public static final String CONTENT_PARTY_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/party";
    public static final String CONTENT_MEMBERPARTY_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/membersandparties";
    public static final String CONTENT_MEMBERPARTY_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/memberandparty";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MEMBER, MEMBERS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MEMBER + "/#", MEMBER_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PARTY, PARTIES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_PARTY + "/#", PARTY_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MEMBERANDPARTY, MEMBERSANDPARTIES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH_MEMBERANDPARTY + "/#", MEMBERANDPARTY_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return false;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        sqlDB.setLocale(new Locale("es", "es"));
        long id = 0;
        Uri toReturn;
        switch (uriType) {
            case MEMBERS:
                id = sqlDB.insert(MemberTable.TABLE_MEMBER, "<empty>", values);
                toReturn = Uri.parse(BASE_PATH_MEMBER + "/" + id);
                break;
            case PARTIES:
                id = sqlDB.insert(PartyTable.TABLE_PARTY, "<empty>", values);
                toReturn = Uri.parse(BASE_PATH_MEMBER + "/" + id);
                break;
            case MEMBERSANDPARTIES:
                throw new RuntimeException("This URI must not be used for insertions");
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return toReturn;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // Check if the caller has requested a column which does not exists
        //checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case MEMBERS:
                break;
            case MEMBER_ID:
                // Adding the ID to the original query
                queryBuilder.setTables(MemberTable.TABLE_MEMBER);
                queryBuilder.appendWhere(MemberTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case MEMBERSANDPARTIES:
                queryBuilder.setTables(MemberTable.TABLE_MEMBER + " INNER JOIN " +
                        PartyTable.TABLE_PARTY + " ON " + MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_PARTY_FK + " = "
                        + PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_ID_API);
                break;
            case MEMBERANDPARTY_ID:
                queryBuilder.setTables(MemberTable.TABLE_MEMBER + " INNER JOIN " +
                        PartyTable.TABLE_PARTY + " ON " + MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_PARTY_FK + " = "
                        + PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_ID_API);
                queryBuilder.appendWhere(MemberTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setLocale(new Locale("es", "es"));
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count;

        // If there is a specified ID, we use WHERE in the clause
        String where = selection;
        if (sURIMatcher.match(uri) == MEMBER_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setLocale(new Locale("es", "es"));

        count = db.update(MemberTable.TABLE_MEMBER, values, where,
                selectionArgs);

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count;

        // If there is a specified ID, we use WHERE in the clause
        String where = selection;
        if (sURIMatcher.match(uri) == MEMBER_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.setLocale(new Locale("es", "es"));

        count = db.delete(MemberTable.TABLE_MEMBER, where, selectionArgs);

        return count;
    }

    @Override
    public String getType(Uri uri) {
        // Sort of MIME type to help Android identify it
        int match = sURIMatcher.match(uri);

        switch (match) {
            case MEMBERS:
                return CONTENT_MEMBER_TYPE;
            case MEMBER_ID:
                return CONTENT_MEMBER_ITEM_TYPE;
            case PARTIES:
                return CONTENT_PARTY_TYPE;
            case PARTY_ID:
                return CONTENT_PARTY_ITEM_TYPE;
            case MEMBERSANDPARTIES:
                return CONTENT_MEMBERPARTY_TYPE;
            case MEMBERANDPARTY_ID:
                return CONTENT_MEMBERPARTY_ITEM_TYPE;
            default:
                return null;
        }
    }

    private void checkColumns(String[] projection) {
        String[] available = {MemberTable.COLUMN_AVATAR_URL,
                MemberTable.COLUMN_CONGRESS_WEB, MemberTable.COLUMN_DIVISION,
                MemberTable.COLUMN_EMAIL, MemberTable.COLUMN_ID,
                MemberTable.COLUMN_NAME, MemberTable.COLUMN_ID_API,
                MemberTable.COLUMN_RESOURCE_URI, MemberTable.COLUMN_SECONDNAME,
                MemberTable.COLUMN_TWITTER_USER, MemberTable.COLUMN_VALIDATE,
                MemberTable.COLUMN_WEBPAGE, MemberTable.COLUMN_PARTY_FK, PartyTable.COLUMN_ID, PartyTable.COLUMN_ID_API,
                PartyTable.COLUMN_LOGO_URL, PartyTable.COLUMN_NAME, PartyTable.COLUMN_RESOURCE_URI,
                PartyTable.COLUMN_VALIDATE, PartyTable.COLUMN_WEBPAGE};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }

}
