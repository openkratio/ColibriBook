package es.openkratio.colibribook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import es.openkratio.colibribook.misc.Constants;

public class AboutActivity extends Activity implements View.OnClickListener {

	TextView mTitle;

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	// Correctly managed ;)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		mTitle = (TextView) findViewById(R.id.tv_about_title);
		// Get app installed version and show it
		new GetAppVersion().execute();

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setHomeButtonEnabled(true);
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setTitle(
					getResources().getString(R.string.ab_title_about));
		} else {
			setTitle(getResources().getString(R.string.ab_title_about));
		}

		// Change activity background if big screen
		View aboutView = findViewById(R.id.ll_about_main);

		// Obtain screen width, in dpi@
		final float scale = getResources().getDisplayMetrics().density;
		int viewWidthDp = (int) (getResources().getDisplayMetrics().widthPixels / scale);

		// Set background according to API version and screen size
		if (viewWidthDp > 600) {
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				aboutView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.panel_bg_holo_light));
			} else {
				aboutView.setBackground(getResources().getDrawable(
						R.drawable.panel_bg_holo_light));
			}
		}

		// Set clock listeners...
		((ImageView) findViewById(R.id.iv_about_github))
				.setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_about_okio)).setOnClickListener(this);
		((ImageView) findViewById(R.id.iv_about_mail)).setOnClickListener(this);

        final TextView code = (TextView) findViewById(R.id.tv_about_code);
        code.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView license = (TextView) findViewById(R.id.tv_about_license);
        license.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView collaborate = (TextView) findViewById(R.id.tv_about_collaborate);
        collaborate.setMovementMethod(LinkMovementMethod.getInstance());
        final TextView design = (TextView) findViewById(R.id.tv_about_design);
        design.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		switch (v.getId()) {
		case R.id.iv_about_github:
			intent.setData(Uri.parse(getString(R.string.about_web_github)));
			break;
		case R.id.iv_about_okio:
			intent.setData(Uri.parse(getString(R.string.about_web_openkratio)));
			break;
		case R.id.iv_about_mail:
			String mailTo = "mailto:" + getString(R.string.about_mail)
					+ "?subject=" + getString(R.string.about_mail_subject);
			intent.setData(Uri.parse(mailTo));
			break;
		default:
			return;
		}
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, Preferences.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetAppVersion extends AsyncTask<Void, Integer, Integer> {
		private String apkVersion;

		// private int apkVersionCode;

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(
						getPackageName(), PackageManager.GET_META_DATA);
				apkVersion = pInfo.versionName;
				// apkVersionCode = pInfo.versionCode;
			} catch (NameNotFoundException e) {
				Log.e(Constants.TAG, "not installed?", e);
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			String newTitle = getString(R.string.app_name) + " " + apkVersion;
			mTitle.setText(newTitle);
		}
	}
}
