package es.openkratio.colibribook.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
		}
		mCallback.updateLoader(b);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sidebar_dosearch:
			dosearch(etSearchQuery.getText().toString(),
					spSearchby.getSelectedItemPosition());
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
}
