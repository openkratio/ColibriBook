package es.openkratio.colibribook;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import es.openkratio.colibribook.fragments.ContactsListFragment;
import es.openkratio.colibribook.misc.Constants;

public class MainActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            //Menu
            mToolbar.inflateMenu(R.menu.menu_main);

            mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.action_settings:
                            Intent intent = new Intent(MainActivity.this, Preferences.class);
                            startActivity(intent);
                            return true;
                        case R.id.action_search:
                            // TODO
                            return true;
                    }
                    return false;
                }
            });
        }

        // Colorize status bar
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.primaryColorDark);
        }

        ContactsListFragment f = new ContactsListFragment();
        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.add(R.id.fl_main_fragment_container, f).commit();
    }

    // ================== Other methods ============================== //

    public void updateLoader(Bundle b) {
        ContactsListFragment f = (ContactsListFragment) getSupportFragmentManager().findFragmentById(
                R.id.fl_main_fragment_container);

        getSupportLoaderManager().destroyLoader(Constants.LOADER_CONTACTS);
        getSupportLoaderManager().initLoader(Constants.LOADER_CONTACTS, b, f);
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