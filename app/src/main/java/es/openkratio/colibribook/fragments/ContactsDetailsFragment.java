package es.openkratio.colibribook.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.koushikdutta.ion.Ion;

import es.openkratio.colibribook.R;
import es.openkratio.colibribook.bean.Member;
import es.openkratio.colibribook.misc.Constants;
import es.openkratio.colibribook.persistence.ContactsContentProvider;
import es.openkratio.colibribook.persistence.MemberTable;
import es.openkratio.colibribook.persistence.PartyTable;

// Lint warnings are caused for using setBackgroundDrawable(...)
@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class ContactsDetailsFragment extends Fragment implements
		OnClickListener {

	Member item;
    private boolean loadImages;
    private Cursor c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        loadImages = prefs.getBoolean(Constants.PREFS_LOAD_IMAGES, true);

		if (getArguments().containsKey(Constants.INTENT_CONTACT_ID)) {

			Uri singleUri = ContentUris.withAppendedId(
					ContactsContentProvider.CONTENT_URI_MEMBERANDPARTY, getArguments()
							.getLong(Constants.INTENT_CONTACT_ID));

			String[] projection = { MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_DIVISION,
                    MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_NAME, MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_CONGRESS_WEB,
                    MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_EMAIL, MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_SECONDNAME,
                    MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_TWITTER_USER, MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_WEBPAGE,
                    MemberTable.TABLE_MEMBER + "." + MemberTable.COLUMN_AVATAR_URL, PartyTable.TABLE_PARTY + "." + PartyTable.COLUMN_LOGO_URL};

			c = getActivity().getContentResolver().query(singleUri,
					projection, null, null, null);
			if (c.moveToFirst()) {
				item = new Member();
				item.setDivision(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_DIVISION)));
				item.setName(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_NAME)));
				item.setCongressWeb(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_CONGRESS_WEB)));
				item.setEmail(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_EMAIL)));
				item.setSecondName(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_SECONDNAME)));
				item.setTwitterUser(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_TWITTER_USER)));
				item.setWebpage(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_WEBPAGE)));
				item.setAvatarUrl(c.getString(c
						.getColumnIndex(MemberTable.COLUMN_AVATAR_URL)));
			}
		} else {
			Toast.makeText(getActivity(),
					"Error al acceder a los detalles del diputado",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setOnClickListeners();
		setTitles();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_details, container,
				false);

		// Obtain screen width, in dpi
		final float scale = getResources().getDisplayMetrics().density;
		int viewWidthDp = (int) (getResources().getDisplayMetrics().widthPixels / scale);

		// Set background according to API version and screen size
		if (viewWidthDp > 600) {
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
				rootView.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.panel_bg_holo_light));
			} else {
				rootView.setBackground(getResources().getDrawable(
						R.drawable.panel_bg_holo_light));
			}
		}

		if (item != null) {
            ImageView avatar, party;
            avatar = (ImageView) rootView.findViewById(R.id.detail_contact_avatar);
            party= (ImageView) rootView.findViewById(R.id.detail_contact_party);
            if (loadImages && c.moveToFirst()) {
                Ion.with(avatar).placeholder(R.drawable.ic_contact).load(c.getString(c
                        .getColumnIndex(MemberTable.COLUMN_AVATAR_URL)));
                String partyLogo = Constants.URL_CONGRESO + c.getString(
                        c.getColumnIndex(PartyTable.COLUMN_LOGO_URL));
                Ion.with(party).load(partyLogo);
            } else {
                avatar.setImageResource(R.drawable.ic_contact);
                party.setImageResource(R.drawable.ic_ab_icon);
            }



			/*Ion.with((ImageView) rootView
                    .findViewById(R.id.iv_details_avatar))
                    .placeholder(R.drawable.ic_contact)
					.load(item.getAvatarUrl());*/

			((TextView) rootView.findViewById(R.id.tv_details_second_name))
					.setText(item.getSecondName());
			((TextView) rootView.findViewById(R.id.tv_details_name))
					.setText(item.getName());
			if (item.getDivision() != null && !item.getDivision().equals("")) {
				((TextView) rootView.findViewById(R.id.tv_details_division))
						.setText(item.getDivision());
			} else {
				rootView.findViewById(R.id.fl_details_division).setVisibility(
						View.GONE);
				rootView.findViewById(R.id.details_shadow_division)
						.setVisibility(View.GONE);
			}
			if (item.getCongressWeb() != null
					&& !item.getCongressWeb().equals("")) {
				((TextView) rootView.findViewById(R.id.tv_details_congress_web))
						.setText(getActivity().getString(
								R.string.details_congress_web));
			} else {
				rootView.findViewById(R.id.fl_details_congress_web)
						.setVisibility(View.GONE);
				rootView.findViewById(R.id.details_shadow_congress_web)
						.setVisibility(View.GONE);
			}
			if (item.getEmail() != null && !item.getEmail().equals("")) {
				((TextView) rootView.findViewById(R.id.tv_details_email))
						.setText(item.getEmail());
			} else {
				rootView.findViewById(R.id.fl_details_email).setVisibility(
						View.GONE);
				rootView.findViewById(R.id.details_shadow_email).setVisibility(
						View.GONE);
			}
			if (item.getTwitterUrl() != null
					&& !item.getTwitterUrl().equals("")) {
				((TextView) rootView.findViewById(R.id.tv_details_twitter))
						.setText("@" + item.getTwitterUser());
			} else {
				rootView.findViewById(R.id.fl_details_twitter).setVisibility(
						View.GONE);
				rootView.findViewById(R.id.details_shadow_twitter)
						.setVisibility(View.GONE);
			}
			if (item.getWebpage() != null && !item.getWebpage().equals("")) {
				((TextView) rootView.findViewById(R.id.tv_details_web))
						.setText(item.getWebpage());
			} else {
				rootView.findViewById(R.id.fl_details_web).setVisibility(
						View.GONE);
				rootView.findViewById(R.id.details_shadow_web).setVisibility(
						View.GONE);
			}
		}
		return rootView;
	}

	void setOnClickListeners() {
		getActivity().findViewById(R.id.fl_details_congress_web)
				.setOnClickListener(this);
		getActivity().findViewById(R.id.fl_details_division)
				.setOnClickListener(this);
		getActivity().findViewById(R.id.fl_details_email).setOnClickListener(
				this);
		getActivity().findViewById(R.id.fl_details_web)
				.setOnClickListener(this);
		getActivity().findViewById(R.id.fl_details_twitter).setOnClickListener(
				this);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	void setTitles() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			getActivity().getActionBar().setTitle(R.string.details_header);
		} else {
			getActivity().setTitle(R.string.details_header);
		}
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.fl_details_congress_web:
				Uri uriCWeb = Uri.parse(item.getCongressWeb());
				Intent intentCWeb = new Intent();
				intentCWeb.setAction(Intent.ACTION_VIEW);
				intentCWeb.setData(uriCWeb);
				startActivity(intentCWeb);
				break;
			case R.id.fl_details_division:
				// do nothing
				break;
			case R.id.fl_details_email:
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
						Uri.fromParts("mailto", item.getEmail(), null));
				// emailIntent.putExtra(Intent.EXTRA_SUBJECT, "...");
				startActivity(Intent.createChooser(emailIntent,
						"Env�a un correo"));
				break;
			case R.id.fl_details_web:
				Uri uriWeb = Uri.parse(item.getWebpage());
				Intent intentWeb = new Intent();
				intentWeb.setAction(Intent.ACTION_VIEW);
				intentWeb.setData(uriWeb);
				startActivity(intentWeb);
				break;
			case R.id.fl_details_twitter:
				Uri uriT = Uri.parse("https://twitter.com/intent/tweet?text=@"
						+ item.getTwitterUrl() + "&via=colibribook");
				Intent intentT = new Intent();
				intentT.setAction(Intent.ACTION_VIEW);
				intentT.setData(uriT);
				startActivity(intentT);
				break;
			}
		} catch (ActivityNotFoundException ane) {
			ane.printStackTrace();
			Toast.makeText(
					getActivity(),
					getActivity().getString(
							R.string.details_intent_activity_not_found),
					Toast.LENGTH_SHORT).show();
		}
	}
}
