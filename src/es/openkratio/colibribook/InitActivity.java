package es.openkratio.colibribook;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.openkratio.colibribook.bean.Member;
import es.openkratio.colibribook.misc.Constants;
import es.openkratio.colibribook.persistence.ContactsContentProvider;
import es.openkratio.colibribook.persistence.MemberTable;

/**
 * This splashscreen loads all the needed data on a local database, using a
 * ContentProvider. I know that surely there are better forms to do that, but at
 * least for the beggining, i'll leave it doing in that way. Because the members
 * data is a relatively static info, the app is getting all the members once a
 * month, and after this point will work totally locally.
 */

public class InitActivity extends Activity {

	SharedPreferences thisActivityScopePreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splashscreen);

		thisActivityScopePreferences = getPreferences(Context.MODE_PRIVATE);

		long lastFetchData = thisActivityScopePreferences.getLong(
				Constants.PREFS_LAST_FETCH, 1L);
		if ((System.currentTimeMillis() - lastFetchData) > Constants.MILLIS_IN_MONTH) {
			new PopulateContactsTask().execute();
		} else {
			nextActivityAndFinish();
		}
	}

	class PopulateContactsTask extends
			AsyncTask<Void, Void, Collection<Member>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Shows the 'loading' spinner when running the AsyncTask
			toggleBottomLayoutVisibility(true);
			((TextView) findViewById(R.id.tv_init_bottom))
					.setText(getString(R.string.tv_init_fetching_data));
		}

		@Override
		protected Collection<Member> doInBackground(Void... params) {

			try {

				// Here we define our base request object which we will
				// send to our REST service via HttpClient
				URI uriMembers = URI.create(Constants.URL_REST_MEMBER
						+ Constants.URL_PARAMS_LIMIT_500);
				HttpRequestBase requestMembers = new HttpGet(uriMembers);
				// URI uriParties = URI.create(Constants.URL_REST_PARTY
				// + Constants.URL_PARAMS_LIMIT_500);
				// HttpRequestBase requestParties = new HttpGet(uriParties);

				// Tell the server to return JSON
				requestMembers.setHeader("Accept", "application/json");
				// requestParties.setHeader("Accept", "application/json");
				HttpClient client = new DefaultHttpClient();

				// Let's send some useful debug information so we can
				// monitor things in LogCat
				if (BuildConfig.DEBUG) {
					Log.d(Constants.TAG,
							"Executing request: " + uriMembers.toString());
					// Log.d(Constants.TAG,
					// "Executing request: " + uriParties.toString());
				}

				// Finally, we send our request using HTTP. This is the
				// synchronous long operation that we need to run on this
				// thread
				HttpResponse responseMembers = client.execute(requestMembers);
				// HttpResponse responseParties =
				// client.execute(requestParties);

				HttpEntity responseEntityMembers = responseMembers.getEntity();
				StatusLine responseStatusMembers = responseMembers
						.getStatusLine();
				int statusCodeMembers = responseStatusMembers != null ? responseStatusMembers
						.getStatusCode() : 0;
				// HttpEntity responseEntityParties =
				// responseParties.getEntity();
				// StatusLine responseStatusParties = responseParties
				// .getStatusLine();
				// int statusCodeParties = responseStatusParties != null ?
				// responseStatusParties
				// .getStatusCode() : 0;
				if (BuildConfig.DEBUG) {
					Log.i(Constants.TAG,
							"Members request, HTTP response status code: "
									+ statusCodeMembers);
					// Log.i(Constants.TAG,
					// "Parties request, HTTP response status code: "
					// + statusCodeMembers);
				}
				// Here we create our response
				String restResponseMembers = responseEntityMembers != null ? EntityUtils
						.toString(responseEntityMembers) : null;
				// String restResponseParties = responseEntityParties != null ?
				// EntityUtils
				// .toString(responseEntityParties) : null;

				// Here we save the fetched data to the local db
				// if (restResponseMembers != null && restResponseParties !=
				// null) {
				if (restResponseMembers != null) {
					String jsonMembers = ((JSONObject) new JSONTokener(
							restResponseMembers).nextValue()).getJSONArray(
							"objects").toString();
					// String jsonParties = ((JSONObject) new JSONTokener(
					// restResponseParties).nextValue()).getJSONArray("objects")
					// .toString();

					// Gson gson = new Gson();
					Gson gson = new Gson();
					Type collectionTypeMembers = new TypeToken<Collection<Member>>() {
					}.getType();
					Collection<Member> members = gson.fromJson(jsonMembers,
							collectionTypeMembers);
					// Type collectionTypeParties = new
					// TypeToken<Collection<Party>>() {
					// }.getType();
					// Collection<Party> parties = gson.fromJson(jsonParties,
					// collectionTypeParties);

					ContentValues[] valuesMembers = new ContentValues[members
							.size()];
					// ContentValues[] valuesParties = new
					// ContentValues[parties.size()];
					int i = 0;

					for (Member m : members) {
						ContentValues cv = new ContentValues();
						cv.put(MemberTable.COLUMN_AVATAR_URL, m.getAvatarUrl());
						cv.put(MemberTable.COLUMN_CONGRESS_WEB,
								m.getCongressWeb());
						cv.put(MemberTable.COLUMN_DIVISION, m.getDivision());
						cv.put(MemberTable.COLUMN_EMAIL, m.getEmail());
						cv.put(MemberTable.COLUMN_ID_API, m.getId());
						cv.put(MemberTable.COLUMN_NAME, m.getName());
						cv.put(MemberTable.COLUMN_RESOURCE_URI,
								m.getResourceURI());
						cv.put(MemberTable.COLUMN_SECONDNAME, m.getSecondName());
						cv.put(MemberTable.COLUMN_TWITTER_USER,
								m.getTwitterUser());
						cv.put(MemberTable.COLUMN_VALIDATE, m.isValidateInt());
						cv.put(MemberTable.COLUMN_WEBPAGE, m.getWebpage());
						valuesMembers[i] = cv;
						i++;
					}
					ContentResolver cr = getContentResolver();
					cr.delete(ContactsContentProvider.CONTENT_URI_MEMBER, null,
							null);
					cr.bulkInsert(ContactsContentProvider.CONTENT_URI_MEMBER,
							valuesMembers);

					return members;
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				Log.e(Constants.TAG, "Failed to parse JSON.", e);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Collection<Member> result) {
			super.onPostExecute(result);
			toggleBottomLayoutVisibility(false);

			if (result != null) {
				// Set last fetch timestamp in preferences
				SharedPreferences.Editor editor = thisActivityScopePreferences
						.edit();
				editor.putLong(Constants.PREFS_LAST_FETCH,
						System.currentTimeMillis());
				editor.commit();

				nextActivityAndFinish();
			} else {
				toggleRetryViewVisibility(true);
			}
		}
	}

	public void toggleBottomLayoutVisibility(boolean visible) {
		findViewById(R.id.ll_init_bottom).setVisibility(
				visible ? View.VISIBLE : View.GONE);
	}

	public void nextActivityAndFinish() {
		setPreferences();
		this.finish();
		Intent intent = new Intent(InitActivity.this, MainActivity.class);
		startActivity(intent);
		this.overridePendingTransition(0, 0);// Removes the transition between
												// activities
	}

	void setPreferences() {
		// By now, hardcode preferences
		// TODO prompt for user division at splashcreen
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(InitActivity.this);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(Constants.PREFS_MY_DIVISION, "Madrid");// Second
																		// parameter
																		// is
																		// the
																		// default
																		// value
		prefsEditor.putBoolean(Constants.PREFS_LOAD_IMAGES, true);
		prefsEditor.commit();
	}

	void toggleRetryViewVisibility(boolean show) {
		findViewById(R.id.ll_init_error).setVisibility(
				show ? View.VISIBLE : View.GONE);
		findViewById(R.id.tv_init_title).setVisibility(
				!show ? View.VISIBLE : View.GONE);
	}

	public void retry(View v) {
		new PopulateContactsTask().execute();
		toggleRetryViewVisibility(false);
	}

}
