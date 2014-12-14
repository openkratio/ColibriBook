package es.openkratio.colibribook;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import es.openkratio.colibribook.misc.Constants;

public class AboutActivity extends ActionBarActivity implements View.OnClickListener {

    TextView mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

        // Put default title
        mTitle = (TextView) findViewById(R.id.tv_about_title);
        mTitle.setText(R.string.app_name);

		// Get app installed version and show it
		new GetAppVersion().execute();

        // Colorize status bar
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.primaryColor);
        }

        // Set-up toolbar
        Toolbar toolbar =  (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        ((TextView) findViewById(R.id.toolbar_title)).setText(getString(R.string.ab_title_about));
        setSupportActionBar(toolbar);

		// Set click listeners...
		findViewById(R.id.iv_about_github).setOnClickListener(this);
		findViewById(R.id.iv_about_okio).setOnClickListener(this);
		findViewById(R.id.iv_about_mail).setOnClickListener(this);

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
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetAppVersion extends AsyncTask<Void, Void, Void> {

		private String apkVersion;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(
						getPackageName(), PackageManager.GET_META_DATA);
				apkVersion = pInfo.versionName;
            } catch (NameNotFoundException e) {
				Log.e(Constants.TAG, "not installed?", e);
			}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String newTitle = getString(R.string.app_name) + " " + apkVersion;
            mTitle.setText(newTitle);
        }
	}

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int transStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= transStatus;
        } else {
            winParams.flags &= ~transStatus;
        }
        win.setAttributes(winParams);
    }
}
