package es.openkratio.colibribook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import es.openkratio.colibribook.fragments.ContactsListFragment;
import es.openkratio.colibribook.misc.Constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_search:
                // TODO
                return true;
        }
        return false;
    }

    // ================== Other methods ============================== //

    public void updateLoader(Bundle b) {
        ContactsListFragment f;

        f = (ContactsListFragment) getSupportFragmentManager().findFragmentById(
                R.id.fragment_contacts_list);

        getSupportLoaderManager().destroyLoader(Constants.LOADER_CONTACTS);
        getSupportLoaderManager().initLoader(Constants.LOADER_CONTACTS, b, f);
    }

}