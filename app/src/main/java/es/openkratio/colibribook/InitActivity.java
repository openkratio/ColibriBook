package es.openkratio.colibribook;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import es.openkratio.colibribook.bean.Member;
import es.openkratio.colibribook.bean.MemberResponse;
import es.openkratio.colibribook.bean.Party;
import es.openkratio.colibribook.bean.PartyResponse;
import es.openkratio.colibribook.misc.Constants;
import es.openkratio.colibribook.misc.ProgressBarAnimation;
import es.openkratio.colibribook.persistence.ContactsContentProvider;
import es.openkratio.colibribook.persistence.MemberTable;
import es.openkratio.colibribook.persistence.PartyTable;

/**
 * This splashscreen loads all the needed data on a local database, using a
 * ContentProvider. I know that surely there are better forms to do that, but at
 * least for the beggining, i'll leave it doing in that way. Because the members
 * data is a relatively static info, the app is getting all the members once a
 * month, and after this point will work totally locally.
 */

public class InitActivity extends AppCompatActivity {

    SharedPreferences thisActivityScopePreferences;
    ContentValues[] valuesMembers, valuesParties;
    ProgressBar pbLoading;
    int mProgressPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        pbLoading = (ProgressBar) findViewById(R.id.pb_init);
        pbLoading.setMax(100);
        thisActivityScopePreferences = getPreferences(Context.MODE_PRIVATE);

        long lastFetchData = thisActivityScopePreferences.getLong(
                Constants.PREFS_LAST_FETCH, 1L);
        if ((System.currentTimeMillis() - lastFetchData) > Constants.MILLIS_IN_MONTH) {
            fetchData();
        } else {
            nextActivityAndFinish();
        }
    }

    void fetchData() {
        toggleBottomLayoutVisibility(true);
        ((TextView) findViewById(R.id.tv_init_bottom))
                .setText(getString(R.string.tv_init_fetching_data));

        mProgressPercent = 0;
        animateProgressBarTo(5);

        Ion.with(InitActivity.this).load(Constants.URL_REST_PARTY)
            .setHeader("Accept", "application/json")
            .as(new TypeToken<PartyResponse>() {
            }).setCallback(new FutureCallback<PartyResponse>() {
               @Override
               public void onCompleted(Exception e, PartyResponse result) {
                   if (e == null) {
                       animateProgressBarTo(40);
                       if (result != null && !result.getObjects().isEmpty()) {
                           valuesParties = new ContentValues[result.getObjects().size()];
                           for (int i = 0; i < result.getObjects().size(); i++) {
                               Party p = result.getObjects().get(i);
                               ContentValues cv = new ContentValues();
                               cv.put(PartyTable.COLUMN_ID_API, p.getId());
                               try {
                                   cv.put(PartyTable.COLUMN_LOGO_URL, URLDecoder.decode(p.getLogoURL(), "UTF-8"));
                               } catch (UnsupportedEncodingException uee) {
                                   uee.printStackTrace();
                               }
                               cv.put(PartyTable.COLUMN_NAME, p.getName());
                               cv.put(PartyTable.COLUMN_WEBPAGE, p.getWebURL());
                               valuesParties[i] = cv;
                           }
                           ContentResolver cr = getContentResolver();
                           cr.delete(ContactsContentProvider.CONTENT_URI_PARTY, null, null);
                           cr.bulkInsert(ContactsContentProvider.CONTENT_URI_PARTY, valuesParties);

                           animateProgressBarTo(45);

                           Ion.with(InitActivity.this).load(Constants.URL_REST_GROUP_MEMBER)
                               .setHeader("Accept", "application/json")
                               .as(new TypeToken<MemberResponse>() {
                               })
                               .setCallback(new FutureCallback<MemberResponse>() {
                                   @Override
                                   public void onCompleted(final Exception e, final MemberResponse result) {
                                       animateProgressBarTo(45,90);
                                       final Handler dataHandler = new Handler() {
                                           @Override
                                           public void handleMessage(Message msg) {
                                               animateProgressBarTo(100);
                                               nextActivityAndFinish();
                                           }
                                       };

                                       Thread dataFetcher = new Thread(new Runnable() {
                                           public void run() {
                                               if (e == null && result != null) {
                                                   valuesMembers = new ContentValues[result.count()];
                                                   for (int i = 0; i < result.count(); i++) {
                                                       Member m = result.getMember(i);
                                                       ContentValues cv = new ContentValues();
                                                       cv.put(MemberTable.COLUMN_AVATAR_URL, m.getAvatarUrl());
                                                       cv.put(MemberTable.COLUMN_CONGRESS_WEB, m.getCongressWeb());
                                                       cv.put(MemberTable.COLUMN_DIVISION, m.getDivision());
                                                       cv.put(MemberTable.COLUMN_EMAIL, m.getEmail());
                                                       cv.put(MemberTable.COLUMN_ID_API, m.getId());
                                                       cv.put(MemberTable.COLUMN_NAME, m.getName());
                                                       cv.put(MemberTable.COLUMN_RESOURCE_URI, m.getResourceURI());
                                                       cv.put(MemberTable.COLUMN_SECONDNAME, m.getSecondName());
                                                       cv.put(MemberTable.COLUMN_TWITTER_USER, m.getTwitterUser());
                                                       cv.put(MemberTable.COLUMN_VALIDATE, m.isValidateInt());
                                                       cv.put(MemberTable.COLUMN_WEBPAGE, m.getWebpage());
                                                       cv.put(MemberTable.COLUMN_PARTY_FK, result.getPartyId(i));

                                                       valuesMembers[i] = cv;
                                                   }
                                                   ContentResolver cr = getContentResolver();
                                                   cr.delete(ContactsContentProvider.CONTENT_URI_MEMBER, null, null);
                                                   cr.bulkInsert(ContactsContentProvider.CONTENT_URI_MEMBER, valuesMembers);

                                                   SharedPreferences.Editor editor = thisActivityScopePreferences.edit();
                                                   editor.putLong(Constants.PREFS_LAST_FETCH, System.currentTimeMillis());
                                                   editor.commit();
                                                   // send to our Handler
                                                   Message msg = new Message();
                                                   dataHandler.sendMessage(msg);
                                               } else {
                                                   toggleRetryViewVisibility(true);
                                               }
                                           }
                                       });
                                       dataFetcher.start();
                                   }
                               });
                       } else {
                           // Exception on second petition
                           toggleRetryViewVisibility(true);
                       }
                   } else {
                       // Exception on first petition
                       e.printStackTrace();
                       toggleRetryViewVisibility(true);
                   }
               }
           }
        );
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
        // TODO prompt for user division at splashcreen, or use reverse geocoding to do it
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(InitActivity.this);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        // Second parameter is the default value
        prefsEditor.putString(Constants.PREFS_MY_DIVISION, "Madrid");
        prefsEditor.putBoolean(Constants.PREFS_LOAD_IMAGES, true);
        prefsEditor.putString(Constants.PREF_CURRENT_SELECTION, "");
        prefsEditor.commit();
    }

    void toggleRetryViewVisibility(boolean show) {
        findViewById(R.id.ll_init_error).setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.tv_init_title).setVisibility(!show ? View.VISIBLE : View.GONE);
    }

    public void retry(View v) {
        fetchData();
        toggleRetryViewVisibility(false);
    }

    public void animateProgressBarTo(int newProgress) {
        ProgressBarAnimation anim = new ProgressBarAnimation(pbLoading, mProgressPercent, newProgress);
        anim.setDuration(1000);
        pbLoading.startAnimation(anim);
    }

    public void animateProgressBarTo(int oldProgress, int newProgress) {
        ProgressBarAnimation anim = new ProgressBarAnimation(pbLoading, oldProgress, newProgress);
        anim.setDuration(1000);
        pbLoading.startAnimation(anim);
    }
}