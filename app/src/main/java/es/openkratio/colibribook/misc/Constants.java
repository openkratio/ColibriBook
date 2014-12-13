package es.openkratio.colibribook.misc;

public class Constants {

	public static final String TAG = "COL";

	public static final String URL_REST_MAIN = "http://proyectocolibri.es/api/v1/";
	public static final String URL_REST_GROUP = URL_REST_MAIN + "group/";
    public static final String URL_REST_GROUP_MEMBER = URL_REST_MAIN + "groupmember/";
    public static final String URL_REST_GROUP_INITIATIVE = URL_REST_MAIN + "initiative/";
	public static final String URL_REST_PARTY = URL_REST_MAIN + "party/";
	public static final String URL_REST_MEMBER = URL_REST_MAIN + "member/";
    public static final String URl_REST_SESSION = URL_REST_MAIN + "session/";
	public static final String URL_REST_VOTE = URL_REST_MAIN + "vote/";
    public static final String URL_REST_VOTE_FULL = URL_REST_MAIN + "vote_full/";
	public static final String URL_REST_VOTING = URL_REST_MAIN + "voting/";
    public static final String URL_REST_VOTING_FULL = URL_REST_MAIN + "voting_full/";
    public static final String URL_REST_SIMPLE_GROUP = URL_REST_MAIN + "simple_group/";
    public static final String URL_REST_SESSION = URL_REST_MAIN + "session/";
    public static final String URL_REST_COMMISSION = URL_REST_MAIN + "commission/";
	public static final String URL_PARAMS_LIMIT_500 = "?limit=500";
    public static final String URL_CONGRESO = "http://www.congreso.es";

	// Preferences
	public static final String PREFS_LAST_FETCH = "lastfetch";
	public static final long MILLIS_IN_MONTH = 2628000000L;
	public static final long MILLIS_IN_WEEK = 604800000L;
	public static final String PREFS_MY_DIVISION = "mydivision";
	public static final String PREFS_LOAD_IMAGES = "loadavatars";
	public static final String PREFS_KEY_INDEX = "listindex";
    public static final String PREF_CURRENT_SELECTION = "selection";

	// Intent tags
	public static final String INTENT_CONTACT_ID = "contact_id";
	public static final String BUNDLE_COMING_FROM_SEARCH = "fromdosearch";
	public static final String BUNDLE_BUNDLE_FOR_LOADER = "bundleforloader";

	// Loader
	public static final int LOADER_CONTACTS = 0xFF1;
	public static final String LOADER_BUNDLE_ARGS_SELECTION = "querywhereclause";
}