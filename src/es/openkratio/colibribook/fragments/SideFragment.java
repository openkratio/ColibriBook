package es.openkratio.colibribook.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import es.openkratio.colibribook.R;
import es.openkratio.colibribook.misc.Constants;
import es.openkratio.colibribook.persistence.MemberTable;

public class SideFragment extends Fragment implements OnClickListener {

	IActivityCallback mCallback;
	private EditText etSearchQuery;
	private Spinner spSearchby;

	public interface IActivityCallback {
		void updateLoader(Bundle b);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_sidebar, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		bindViewsAndSetOnClickListeners();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (IActivityCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement IActivityCallback");
		}
	}

	public void dosearch(String query, int positionSelected) {
		// Highly coupled switch

		Bundle b = new Bundle();
		if (positionSelected == 0) {
			// Search by name
			b.putString(Constants.LOADER_BUNDLE_ARGS_SELECTION, MemberTable.COLUMN_NAME + " LIKE '"
					+ query + "'");
		} else if (positionSelected == 1) {
			// Search by division
			b.putString(Constants.LOADER_BUNDLE_ARGS_SELECTION, MemberTable.COLUMN_DIVISION
					+ " LIKE '" + query + "'");
		}
		mCallback.updateLoader(b);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sidebar_dosearch:
			dosearch(etSearchQuery.getText().toString(), spSearchby.getSelectedItemPosition());
			break;
		case R.id.btn_sidebar_reset:
			mCallback.updateLoader(null);
			break;
		case R.id.btn_sidebar_mydivision:
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
			String division = prefs.getString(Constants.PREFS_MY_DIVISION, "Madrid");
			dosearch(division, 1);// The second parameter means search by
									// division
			break;
		default:
			break;
		}
	}

	void bindViewsAndSetOnClickListeners() {
		// Gives value to fields
		etSearchQuery = (EditText) getActivity().findViewById(R.id.et_sidebar_query);
		spSearchby = (Spinner) getActivity().findViewById(R.id.spinner_sidebar_searchby);

		// Set OnClickListeners...
		((Button) getActivity().findViewById(R.id.btn_sidebar_dosearch)).setOnClickListener(this);
		((Button) getActivity().findViewById(R.id.btn_sidebar_reset)).setOnClickListener(this);
		((Button) getActivity().findViewById(R.id.btn_sidebar_mydivision)).setOnClickListener(this);
	}
}
