package es.openkratio.colibribook.misc;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Javi Pulido on 11/09/2014.
 */
public class RobotoRegularTextView extends TextView {

    /*
     * Caches typefaces based on their file path and name, so that they don't have to be created every time when they are referenced.
     */
    private static Typeface mTypeface;

    public RobotoRegularTextView(final Context context) {
        this(context, null);
    }

    public RobotoRegularTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RobotoRegularTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        }
        setTypeface(mTypeface);
    }

}