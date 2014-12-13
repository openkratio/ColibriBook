package es.openkratio.colibribook.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
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
    private int mShortAnimationDuration;
    private Animator mCurrentAnimator;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Retrieve and cache the system's default "short" animation time.
        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
            avatar = (ImageView) rootView.findViewById(R.id.contact_avatar);
            party = (ImageView) rootView.findViewById(R.id.detail_contact_party);
            if (loadImages && c.moveToFirst()) {
                Ion.with(avatar).placeholder(R.drawable.ic_contact).load(c.getString(c
                        .getColumnIndex(MemberTable.COLUMN_AVATAR_URL))).setCallback(new FutureCallback<ImageView>() {
                    @Override
                    public void onCompleted(Exception e, final ImageView result) {
                        final Bitmap avatarBM;

                        result.setDrawingCacheEnabled(true);

                        result.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        result.layout(0, 0, result.getMeasuredWidth(), result.getMeasuredHeight());

                        result.buildDrawingCache(true);
                        avatarBM = Bitmap.createBitmap(result.getDrawingCache());
                        result.setDrawingCacheEnabled(false); // clear drawing cache

                        result.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                zoomImageFromThumb(result, avatarBM);
                            }
                        });
                    }
                });
                /*
                String partyLogo = Constants.URL_CONGRESO + c.getString(
                        c.getColumnIndex(PartyTable.COLUMN_LOGO_URL));
                Ion.with(party).load(partyLogo);
                */

            } else {
                avatar.setImageResource(R.drawable.ic_contact);
                party.setImageResource(R.drawable.ic_ab_icon);
            }

            // Touch-to-zoom stuff
            rootView.findViewById(R.id.iv_details_zoomed).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().findViewById(R.id.container).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.iv_details_zoomed).setVisibility(View.GONE);
                }
            });

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
            ((ActionBarActivity)getActivity()).getSupportActionBar().setTitle(R.string.details_header);
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
						"Enviar correo"));
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

    private void zoomImageFromThumb(final View thumbView, Bitmap bImage) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) getActivity().findViewById(R.id.iv_details_zoomed);
        expandedImageView.setImageBitmap(bImage);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the
        // container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getActivity().findViewById(R.id.container).getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) startBounds.width()
                / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
