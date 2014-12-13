package es.openkratio.colibribook;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import es.openkratio.colibribook.fragments.ContactsListFragment;
import es.openkratio.colibribook.fragments.SideFragment;
import es.openkratio.colibribook.fragments.SideFragment.IActivityCallback;
import es.openkratio.colibribook.misc.Constants;

public class MainActivity extends ActionBarActivity implements IActivityCallback {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Colorize status bar
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.action_bar);
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, Preferences.class);
			startActivity(intent);
			return true;
		case R.id.action_search:
			// show search fragment (for android version < honeycomb)
			SideFragment f = new SideFragment();
			FragmentManager fManager = getSupportFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();
			fTransaction.replace(R.id.fragment_contacts_list, f).commit();
			return true;
		}
		return false;
	}

	// ================== Other methods ============================== //

	@Override
	public void updateLoader(Bundle b) {
		ContactsListFragment f = null;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {

			f = (ContactsListFragment) getSupportFragmentManager().findFragmentById(
					R.id.fragment_contacts_list);

			getSupportLoaderManager().destroyLoader(Constants.LOADER_CONTACTS);
			getSupportLoaderManager().initLoader(Constants.LOADER_CONTACTS, b, f);
		} else {
			f = new ContactsListFragment();
			Bundle args = new Bundle();
			args.putBoolean(Constants.BUNDLE_COMING_FROM_SEARCH, true);
			args.putBundle(Constants.BUNDLE_BUNDLE_FOR_LOADER, b);
			f.setArguments(args);
			FragmentManager fManager = getSupportFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();
			fTransaction.replace(R.id.fragment_contacts_list, f).commit();
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