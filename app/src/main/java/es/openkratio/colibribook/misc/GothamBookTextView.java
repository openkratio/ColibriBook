package es.openkratio.colibribook.misc;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Javi Pulido on 20/04/2014.
 */
public class GothamBookTextView extends TextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
    private static Typeface mTypeface;

    public GothamBookTextView(final Context context) {
        this(context, null);
    }

    public GothamBookTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GothamBookTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "Gotham-Medium.otf");
        }
        setTypeface(mTypeface);
    }

}