package es.openkratio.colibribook;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class Preferences extends PreferenceActivity {

	@SuppressLint("NewApi")
	// Managed correctly ;)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Dear Google developers, I know it's deprecated, tell me why, or give
		// me a working replacement ;)
		addPreferencesFromResource(R.xml.general_prefs);

		// Customize action bar if android version > 3.0
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getActionBar();
			ab.setHomeButtonEnabled(true);
			ab.setDisplayHomeAsUpEnabled(true);
			ab.setTitle(getResources().getString(R.string.ab_title_prefs));
		} else {
			setTitle(getResources().getString(R.string.ab_title_prefs));
		}
	}

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		String pref = preference.getKey();
		if (pref.equals(getString(R.string.prefs_key_about))) {
			Intent intent = new Intent(Preferences.this, AboutActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
