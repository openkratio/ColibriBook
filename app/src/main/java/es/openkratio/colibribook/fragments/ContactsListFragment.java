package es.openkratio.colibribook.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;


import com.koushikdutta.ion.Ion;

import es.openkratio.colibribook.ContactDetailsActivity;
import es.openkratio.colibribook.MainActivity;
import es.openkratio.colibribook.R;
import es.openkratio.colibribook.misc.Constants;
import es.openkratio.colibribook.misc.CustomAlphabetIndexer;
import es.openkratio.colibribook.persistence.ContactsContentProvider;
import es.openkratio.colibribook.persistence.MemberTable;
import es.openkratio.colibribook.persistence.PartyTable;

public class ContactsListFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ContactsListAdapter mAdapter;
	private boolean loadImages;
	public CustomAlphabetIndexer alphaIndexer;
	public ListView mList;
	boolean mListShown;
	View mProgressContainer;
	View mListContainer;
	View mEmptyView;

	// Alphabet used in the indexer
	public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	// Lint warnings are caused for using setBackgroundDrawable(...)
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Obtain screen width, in dpi
		final float scale = getResources().getDisplayMetrics().density;
		int viewWidthDp = (int) (getResources().getDisplayMetrics().widthPixels / scale);

		// Set background according to API version and screen size
		if (viewWidthDp > 600) {
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				mList.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.panel_bg_holo_light));
			} else {
				mList.setBackground(getResources().getDrawable(
						R.drawable.panel_bg_holo_light));
			}
		}

		// Bind data to list
		mAdapter = new ContactsListAdapter(getActivity(), null, false);
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
			getActivity().getSupportLoaderManager().initLoader(
					Constants.LOADER_CONTACTS, null, this);
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		loadImages = prefs.getBoolean(Constants.PREFS_LOAD_IMAGES, true);

		mEmptyView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).updateLoader(null);
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		int INTERNAL_EMPTY_ID = 0x00ff0001;
		View root = inflater.inflate(R.layout.list_contacts, null, false);
		(root.findViewById(R.id.internalEmpty)).setId(INTERNAL_EMPTY_ID);
		mList = (ListView) root.findViewById(android.R.id.list);
		mListContainer = root.findViewById(R.id.listContainer);
		mProgressContainer = root.findViewById(R.id.progressContainer);
		mEmptyView = root.findViewById(INTERNAL_EMPTY_ID);
		mList.setEmptyView(mEmptyView);
		mListShown = true;
		return root;
	}

	@Override
	public void onResume() {
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		loadImages = prefs.getBoolean(Constants.PREFS_LOAD_IMAGES, true);
		int index = prefs.getInt(Constants.PREFS_KEY_INDEX, 0);
		mList.setSelectionFromTop(index, 0);
	}

	@Override
	public void onPause() {
		super.onPause();
		int index = mList.getFirstVisiblePosition();
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(getActivity()).edit();
		editor.putInt(Constants.PREFS_KEY_INDEX, index);
		editor.commit();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(getActivity(), ContactDetailsActivity.class);
		intent.putExtra(Constants.INTENT_CONTACT_ID,
				mAdapter.getItemId(position));
		startActivity(intent);
	}

	// Creates a new loader after the initLoader () call
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		String[] projection = { MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_ID,
                MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_ID_API,
                MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_NAME,
				MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_SECONDNAME,
                MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_DIVISION,
				MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_AVATAR_URL,
                PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_LOGO_URL };
		String selection = null;
		if (args != null) {
			selection = args.getString(Constants.LOADER_BUNDLE_ARGS_SELECTION);
		}
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				ContactsContentProvider.CONTENT_URI_MEMBERANDPARTY, projection,
				selection, null, MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_SECONDNAME
						+ " COLLATE LOCALIZED ASC");
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

		data.moveToPosition(-1);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// data is not available anymore, delete reference
		mAdapter.swapCursor(null);
	}

	private class ContactsListAdapter extends CursorAdapter implements
			SectionIndexer {

		public ContactsListAdapter(Context context, Cursor c,
				boolean autoRequery) {
			super(context, c, false);
			if (c != null) {
				initializeIndexer(c);
			}
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View rowView = LayoutInflater.from(context).inflate(
					R.layout.row_contact, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.fName = (TextView) rowView.findViewById(R.id.row_contact_name);
			holder.lName = (TextView) rowView.findViewById(R.id.row_contact_second_name);
			holder.avatar = (ImageView) rowView.findViewById(R.id.row_contact_avatar);
            holder.party= (ImageView) rowView.findViewById(R.id.row_contact_party);
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
			if (loadImages) {
                Ion.with(holder.avatar).placeholder(R.drawable.ic_contact).load(cursor.getString(cursor
                        .getColumnIndex(MemberTable.COLUMN_AVATAR_URL)));
                String partyLogo = Constants.URL_CONGRESO + cursor.getString(
                        cursor.getColumnIndex(PartyTable.COLUMN_LOGO_URL));
                Ion.with(holder.party).load(partyLogo);
			} else {
				holder.avatar.setImageResource(R.drawable.ic_contact);
			}
            //view.setBackgroundResource(cursor.getPosition() % 2 == 0 ? R.color.list_bg_1 : R.color.list_bg_2);
		}

		class ViewHolder {
			TextView fName, lName;// , division;
			ImageView avatar, party;
		}

		@Override
		public int getPositionForSection(int section) {
			return alphaIndexer.getPositionForSection(section);
		}

		@Override
		public int getSectionForPosition(int position) {
			return alphaIndexer.getSectionForPosition(position);
		}

		@Override
		public Object[] getSections() {
			return alphaIndexer.getSections();
		}

		@Override
		public Cursor swapCursor(Cursor newCursor) {
			if (alphaIndexer == null) {
				initializeIndexer(newCursor);
			} else {
				alphaIndexer.setCursor(newCursor);
			}
			return super.swapCursor(newCursor);
		}

		private void initializeIndexer(Cursor c) {
			alphaIndexer = new CustomAlphabetIndexer(c,
					c.getColumnIndex(MemberTable.COLUMN_SECONDNAME), ALPHABET);
		}
	}

	// Utility methods for showing a progress bar when loading
	public void setListShown(boolean shown, boolean animate) {
		if (mListShown == shown) {
			return;
		}
		mListShown = shown;
		if (shown) {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_out));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_in));
			}
			mProgressContainer.setVisibility(View.GONE);
			mListContainer.setVisibility(View.VISIBLE);
		} else {
			if (animate) {
				mProgressContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_in));
				mListContainer.startAnimation(AnimationUtils.loadAnimation(
						getActivity(), android.R.anim.fade_out));
			}
			mProgressContainer.setVisibility(View.VISIBLE);
			mListContainer.setVisibility(View.INVISIBLE);
		}
	}

	public void setListShown(boolean shown) {
		setListShown(shown, true);
	}

	public void setListShownNoAnimation(boolean shown) {
		setListShown(shown, false);
	}
}