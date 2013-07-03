package es.openkratio.colibribook.fragments;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import es.openkratio.colibribook.ContactDetailsActivity;
import es.openkratio.colibribook.R;
import es.openkratio.colibribook.misc.Constants;
import es.openkratio.colibribook.persistence.ContactsContentProvider;
import es.openkratio.colibribook.persistence.MemberTable;

public class ContactsListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private MyAdapter mAdapter;
	private boolean loadImages;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getActivity().getString(R.string.contacts_list_empty_view));
		getListView().setDivider(new ColorDrawable(0xE5E5E5));

		mAdapter = new MyAdapter(getActivity(), null, false);
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		Bundle b = getArguments();
		if (b != null) {
			boolean fromSearch = b.getBoolean(
					Constants.BUNDLE_COMING_FROM_SEARCH, false);

			if (fromSearch) {
				Bundle b1 = b.getBundle(Constants.BUNDLE_BUNDLE_FOR_LOADER);
				getActivity().getSupportLoaderManager().destroyLoader(
						Constants.LOADER_CONTACTS);
				getActivity().getSupportLoaderManager().initLoader(
						Constants.LOADER_CONTACTS, b1, this);
			}
		} else {
			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			// getLoaderManager().initLoader(0, null, this);
			getActivity().getSupportLoaderManager().initLoader(
					Constants.LOADER_CONTACTS, null, this);
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		loadImages = prefs.getBoolean(Constants.PREFS_LOAD_IMAGES, true);
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		loadImages = prefs.getBoolean(Constants.PREFS_LOAD_IMAGES, true);
		int index = prefs.getInt(Constants.PREFS_KEY_INDEX, 0);
		getListView().setSelectionFromTop(index, 0);
	}

	@Override
	public void onPause() {
		super.onPause();
		int index = getListView().getFirstVisiblePosition();
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(getActivity()).edit();
		editor.putInt(Constants.PREFS_KEY_INDEX, index);
		editor.commit();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Insert desired behavior here.
		Log.i("FragmentComplexList", "Item clicked: " + id);
		Intent intent = new Intent(getActivity(), ContactDetailsActivity.class);
		intent.putExtra(Constants.INTENT_CONTACT_ID,
				mAdapter.getItemId(position));
		startActivity(intent);
	}

	// Creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { MemberTable.COLUMN_ID, MemberTable.COLUMN_NAME,
				MemberTable.COLUMN_SECONDNAME, MemberTable.COLUMN_DIVISION,
				MemberTable.COLUMN_AVATAR_URL };
		String selection = null;
		if (args != null) {
			selection = args.getString(Constants.LOADER_BUNDLE_ARGS_SELECTION);
		}
		Activity a = getActivity();
		a.getAssets();
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				ContactsContentProvider.CONTENT_URI_MEMBER, projection,
				selection, null, null);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);

		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		mAdapter.swapCursor(null);
	}

	private class MyAdapter extends CursorAdapter {

		public MyAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View rowView = LayoutInflater.from(context).inflate(
					R.layout.row_contact, parent, false);

			ViewHolder holder = new ViewHolder();
			holder.fName = (TextView) rowView
					.findViewById(R.id.row_contact_name);
			holder.lName = (TextView) rowView
					.findViewById(R.id.row_contact_second_name);
			holder.division = (TextView) rowView
					.findViewById(R.id.row_contact_division);
			holder.avatar = (ImageView) rowView
					.findViewById(R.id.row_contact_avatar);
			rowView.setTag(holder);
			return rowView;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.fName.setText(cursor.getString(cursor
					.getColumnIndex(MemberTable.COLUMN_NAME)));
			holder.lName.setText(cursor.getString(cursor
					.getColumnIndex(MemberTable.COLUMN_SECONDNAME)));
			holder.division.setText(cursor.getString(cursor
					.getColumnIndex(MemberTable.COLUMN_DIVISION)));
			if (loadImages) {
				UrlImageViewHelper
						.setUrlDrawable(
								holder.avatar,
								cursor.getString(cursor
										.getColumnIndex(MemberTable.COLUMN_AVATAR_URL)),
								R.drawable.ic_contact);
			} else {
				holder.avatar.setImageResource(R.drawable.ic_contact);
			}
		}

		class ViewHolder {
			TextView fName, lName, division;
			ImageView avatar;
		}

	}

}
