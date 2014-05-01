package es.openkratio.colibribook.fragments;

import android.app.Activity;
import android.content.ContentUris;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import java.lang.reflect.ParameterizedType;

import es.openkratio.colibribook.R;
import es.openkratio.colibribook.misc.Constants;
import es.openkratio.colibribook.persistence.ContactsContentProvider;
import es.openkratio.colibribook.persistence.MemberTable;
import es.openkratio.colibribook.persistence.PartyTable;

public class SideFragment extends Fragment implements OnClickListener {

	IActivityCallback mCallback;
	private EditText etSearchQuery;
	private Spinner spSearchby;
    private Spinner spParties;
    private Cursor partyCursor;


	public interface IActivityCallback {
		void updateLoader(Bundle b);
	}
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sidebar, container,
				false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		bindViewsAndSetOnClickListeners();
		customizeEditText();

        // Init spParties:  - Create a cursor to populate with a SimpleCursorAdapter the spinner with parties with member
        //                  - Add a listener on spSearchBy to show / hide spParties according with the selected item
        initSpinnerPartySearch();


    }

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (IActivityCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement IActivityCallback");
		}
	}

	public void dosearch(String query, int positionSelected) {
		// Highly coupled switch

		Bundle b = new Bundle();
		if (positionSelected == 0) {
			// Search by name or surname
			b.putString(Constants.LOADER_BUNDLE_ARGS_SELECTION,
                    MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_NAME + " LIKE '%"  + query + "%' OR "
							+  MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_SECONDNAME + " LIKE '%"
							+ query + "%'");
		} else if (positionSelected == 1) {
			// Search by division
			b.putString(Constants.LOADER_BUNDLE_ARGS_SELECTION,
                    MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_DIVISION + " LIKE '%" + query + "%'");
		} else if (positionSelected == 2) {
            // Search by party
            b.putString(Constants.LOADER_BUNDLE_ARGS_SELECTION,
                    PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_NAME + " = '" + query + "'");
        }

		mCallback.updateLoader(b);
	}

	@Override
	public void onClick(View v) {
        // Reset the search preference
        SharedPreferences.Editor prefsEdit = PreferenceManager
                .getDefaultSharedPreferences(getActivity()).edit();
        prefsEdit.putString(Constants.PREF_CURRENT_SELECTION, "");
        prefsEdit.commit();

		switch (v.getId()) {
            case R.id.btn_sidebar_dosearch:
                if (spSearchby.getSelectedItemId() == 2) {
                    //  Searching by party, get cursor from spinner selected item
                    Cursor c = (Cursor)spParties.getItemAtPosition(spParties.getSelectedItemPosition());
                    dosearch(c.getString(c.getColumnIndex("name")),
                            spSearchby.getSelectedItemPosition());
                } else {
                    dosearch(etSearchQuery.getText().toString(),
                            spSearchby.getSelectedItemPosition());
                }
			    break;
            case R.id.btn_sidebar_reset:
                mCallback.updateLoader(null);
                break;
            case R.id.btn_sidebar_mydivision:
                SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String division = prefs.getString(Constants.PREFS_MY_DIVISION,
                        "Madrid");
                dosearch(division, 1);// The second parameter means search by
                                        // division
                break;
            default:
                break;
        }
	}

	void bindViewsAndSetOnClickListeners() {
		// Gives value to fields
		etSearchQuery = (EditText) getActivity().findViewById(
				R.id.et_sidebar_query);
		spSearchby = (Spinner) getActivity().findViewById(
				R.id.spinner_sidebar_searchby);
        spParties = (Spinner) getActivity().findViewById(
                R.id.spinner_sidebar_parties);

		// Set OnClickListeners...
		((Button) getActivity().findViewById(R.id.btn_sidebar_dosearch))
				.setOnClickListener(this);
		((Button) getActivity().findViewById(R.id.btn_sidebar_reset))
				.setOnClickListener(this);
		((Button) getActivity().findViewById(R.id.btn_sidebar_mydivision))
				.setOnClickListener(this);
	}

	private void customizeEditText() {
		// Clearable editText for searchs
		String value = "";
		final String viewMode = "editing";// never | editing | unlessEditing |
											// always
		final String viewSide = "right"; // left | right
		final EditText et = (EditText) getActivity().findViewById(
				R.id.et_sidebar_query);
		final Drawable x = getResources().getDrawable(
				android.R.drawable.presence_offline);
		x.setBounds(0, 0, x.getIntrinsicWidth(), x.getIntrinsicHeight());
		Drawable x2 = viewMode.equals("never") ? null : viewMode
				.equals("always") ? x : viewMode.equals("editing") ? (value
				.equals("") ? null : x)
				: viewMode.equals("unlessEditing") ? (value.equals("") ? x
						: null) : null;
		et.setCompoundDrawables(viewSide.equals("left") ? x2 : null, null,
				viewSide.equals("right") ? x2 : null, null);
		et.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (et.getCompoundDrawables()[viewSide.equals("left") ? 0 : 2] == null) {
					return false;
				}
				if (event.getAction() != MotionEvent.ACTION_UP) {
					return false;
				}
				// x pressed
				if ((viewSide.equals("left") && event.getX() < et
						.getPaddingLeft() + x.getIntrinsicWidth())
						|| (viewSide.equals("right") && event.getX() > et
								.getWidth()
								- et.getPaddingRight()
								- x.getIntrinsicWidth())) {
					Drawable x3 = viewMode.equals("never") ? null : viewMode
							.equals("always") ? x
							: viewMode.equals("editing") ? null : viewMode
									.equals("unlessEditing") ? x : null;
					et.setText("");
					et.setCompoundDrawables(
							viewSide.equals("left") ? x3 : null, null,
							viewSide.equals("right") ? x3 : null, null);
				}
				return false;
			}
		});
		et.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Drawable x4 = viewMode.equals("never") ? null : viewMode
						.equals("always") ? x
						: viewMode.equals("editing") ? (et.getText().toString()
								.equals("") ? null : x) : viewMode
								.equals("unlessEditing") ? (et.getText()
								.toString().equals("") ? x : null) : null;
				et.setCompoundDrawables(viewSide.equals("left") ? x4 : null,
						null, viewSide.equals("right") ? x4 : null, null);
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		});
	}

/* Init spParties:  - Create a cursor to populate with a SimpleCursorAdapter the spinner with parties with member
                    - Add a listener on spSearchBy to show / hide spParties according with the selected item
 */
private void initSpinnerPartySearch() {

    // Creating a cursor to populate with a SimpleCursorAdapter the spinner with parties with member
    String[] projection = { PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_ID,
            PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_NAME};
    partyCursor = getActivity().getContentResolver().query(ContactsContentProvider.CONTENT_URI_MEMBERANDPARTY,
            projection, null, null, PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_NAME
            + " COLLATE LOCALIZED ASC");

    if (partyCursor.moveToFirst()) {
        SimpleCursorAdapter adapterParty = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, partyCursor,
                new String[] {PartyTable.COLUMN_NAME}, new int[] {android.R.id.text1});

        adapterParty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spParties.setAdapter(adapterParty);
    }

    // Adding a listener on spSearchBy to show / hide spParties according with the selected item
    spSearchby.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                int position, long id) {
            if (parentView.getItemAtPosition(position).toString().equals(getString(R.string.searchby_party))) {
                // hide EditText and show spinner spParties
                etSearchQuery.setVisibility(View.INVISIBLE);
                spParties.setVisibility(View.VISIBLE);
            } else {
                etSearchQuery.setVisibility(View.VISIBLE);
                spParties.setVisibility(View.INVISIBLE);
            }

        }

        public void onNothingSelected(AdapterView<?> parentView) {

        }
    });

}


}
