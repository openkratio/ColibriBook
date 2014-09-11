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

public class MainActivity extends FragmentActivity implements IActivityCallback {

	DrawerLayout mDrawerLayout;
	ActionBarDrawerToggle mDrawerToggle;
	EditText etSearchQuery;
	Spinner spSearchby;

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.action_bar);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
			mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
					R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

				// Called when a drawer has settled in a completely closed state
				public void onDrawerClosed(View view) {
					getActionBar().setTitle(
							getString(R.string.drawer_title_closed));
					// creates call to onPrepareOptionsMenu()
					invalidateOptionsMenu();
				}

				// Called when a drawer has settled in a completely open state
				public void onDrawerOpened(View drawerView) {
					getActionBar().setTitle(getString(R.string.drawer_title_open));
					// creates call to onPrepareOptionsMenu()
					invalidateOptionsMenu();
				}
			};

			// Set the drawer toggle as the DrawerListener
			mDrawerLayout.setDrawerListener(mDrawerToggle);

            if(getActionBar() != null) {
                getActionBar().setDisplayHomeAsUpEnabled(true);
                getActionBar().setHomeButtonEnabled(true);
                getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_action_bar));
            }
		} else {
			// Gingerbread or earlier
			ContactsListFragment f = new ContactsListFragment();
			FragmentManager fManager = getSupportFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();
			fTransaction.add(R.id.fl_main_fragment_container, f).commit();
		}
	}

	// ===================== Menus stuff ==========================

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
			mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
			mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			if (mDrawerToggle.onOptionsItemSelected(item))
				return true;
		}
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(MainActivity.this, Preferences.class);
			startActivity(intent);
			return true;
		case R.id.action_search:
			// show search fragment
			SideFragment f = new SideFragment();
			FragmentManager fManager = getSupportFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();
			fTransaction.replace(R.id.fl_main_fragment_container, f).commit();
			return true;
		}
		return false;
	}

	// ================== Other methods ==============================

	@Override
	public void updateLoader(Bundle b) {
		ContactsListFragment f = null;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {

			f = (ContactsListFragment) getSupportFragmentManager().findFragmentById(
					R.id.fragment_contacts_list);

			getSupportLoaderManager().destroyLoader(Constants.LOADER_CONTACTS);
			getSupportLoaderManager().initLoader(Constants.LOADER_CONTACTS, b, f);

			mDrawerLayout.closeDrawers();
		} else {
			f = new ContactsListFragment();
			Bundle args = new Bundle();
			args.putBoolean(Constants.BUNDLE_COMING_FROM_SEARCH, true);
			args.putBundle(Constants.BUNDLE_BUNDLE_FOR_LOADER, b);
			f.setArguments(args);
			FragmentManager fManager = getSupportFragmentManager();
			FragmentTransaction fTransaction = fManager.beginTransaction();
			fTransaction.replace(R.id.fl_main_fragment_container, f).commit();
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