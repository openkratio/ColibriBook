package es.openkratio.colibribook.misc;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;

import es.openkratio.colibribook.R;

// All credits to NavasMDC
// https://github.com/navasmdc/MaterialDesignLibrary/blob/master/MaterialDesign/src/com/gc/materialdesign/views/ProgressBarCircularIndeterminate.java

public class CustomIndeterminateProgressBar extends RelativeLayout {

    protected final static String MATERIALDESIGNXML = "http://schemas.android.com/apk/res-auto";
    protected final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    protected int minWidth;
    protected int minHeight;

    protected int backgroundColor;
    protected int beforeBackground;
    protected int backgroundResId = -1;

    public CustomIndeterminateProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        onInitDefaultValues();
        setAttributes(attrs);
    }

    protected void onInitDefaultValues() {
        minWidth = 24;
        minHeight = 24;
        backgroundColor = getResources().getColor(R.color.primaryColor);
    }

    protected void setAttributes(AttributeSet attrs) {
        setMinimumHeight(dpToPx(minHeight, getResources()));
        setMinimumWidth(dpToPx(minWidth, getResources()));
        if (backgroundResId != -1 && !isInEditMode()) {
            setBackgroundResource(backgroundResId);
        }
        setBackgroundAttributes(attrs);
        float size = 3;// default ring width
        String width = attrs.getAttributeValue(MATERIALDESIGNXML, "ringWidth");
        if (width != null) {
            size = dipOrDpToFloat(width);
        }
        ringWidth = size;
    }

    /**
     * Make a dark color to ripple effect
     */
    protected int makePressColor() {
        int r = (this.backgroundColor >> 16) & 0xFF;
        int g = (this.backgroundColor >> 8) & 0xFF;
        int b = (this.backgroundColor) & 0xFF;
        return Color.argb(128, r, g, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!firstAnimationOver)
            drawFirstAnimation(canvas);
        if (cont > 0)
            drawSecondAnimation(canvas);
        invalidate();
    }

    private float radius1 = 0;
    private float radius2 = 0;
    private int cont = 0;
    private boolean firstAnimationOver = false;
    private float ringWidth = 4;

    /**
     * Draw first animation of view
     */
    private void drawFirstAnimation(Canvas canvas) {
        if (radius1 < getWidth() / 2) {
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(makePressColor());
            radius1 = (radius1 >= getWidth() / 2) ? (float) getWidth() / 2 : radius1 + 1;
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius1, paint);
        } else {
            Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas temp = new Canvas(bitmap);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(makePressColor());
            temp.drawCircle(getWidth() / 2, getHeight() / 2, getHeight() / 2, paint);
            Paint transparentPaint = new Paint();
            transparentPaint.setAntiAlias(true);
            transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
            transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            if (cont >= 50) {
                radius2 = (radius2 >= getWidth() / 2) ? (float) getWidth() / 2 : radius2 + 1;
            } else {
                radius2 = (radius2 >= getWidth() / 2 - dpToPx(ringWidth, getResources())) ?
                        (float) getWidth() / 2 - dpToPx(ringWidth, getResources()) : radius2 + 1;
            }
            temp.drawCircle(getWidth() / 2, getHeight() / 2, radius2, transparentPaint);
            canvas.drawBitmap(bitmap, 0, 0, new Paint());
            if (radius2 >= getWidth() / 2 - dpToPx(ringWidth, getResources()))
                cont++;
            if (radius2 >= getWidth() / 2)
                firstAnimationOver = true;
        }
    }

    private int arcD = 1;
    private int arcO = 0;
    private float rotateAngle = 0;
    private int limite = 0;

    /**
     * Draw second animation of view
     */
    private void drawSecondAnimation(Canvas canvas) {
        if (arcO == limite)
            arcD += 6;
        if (arcD >= 290 || arcO > limite) {
            arcO += 6;
            arcD -= 6;
        }
        if (arcO > limite + 290) {
            limite = arcO;
            arcO = limite;
            arcD = 1;
        }
        rotateAngle += 4;
        canvas.rotate(rotateAngle, getWidth() / 2, getHeight() / 2);

        Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas temp = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(backgroundColor);
        temp.drawArc(new RectF(0, 0, getWidth(), getHeight()), arcO, arcD, true, paint);
        Paint transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        temp.drawCircle(getWidth() / 2, getHeight() / 2, (getWidth() / 2) - dpToPx(ringWidth, getResources()), transparentPaint);

        canvas.drawBitmap(bitmap, 0, 0, new Paint());
    }

    // Set color of background
    public void setBackgroundColor(int color) {
        super.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        if (isEnabled()) {
            beforeBackground = backgroundColor;
        }
        this.backgroundColor = color;
    }

    public void setRingWidth(float width) {
        ringWidth = width;
    }

    /**
     * Convert Dp to Pixel
     */
    public int dpToPx(float dp, Resources resources) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return (int) px;
    }

    public float dipOrDpToFloat(String value) {
        if (value.indexOf("dp") != -1) {
            value = value.replace("dp", "");
        } else {
            value = value.replace("dip", "");
        }
        return Float.parseFloat(value);
    }

    /**
     * Set background Color
     */
    protected void setBackgroundAttributes(AttributeSet attrs) {
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            // Color by hexadecimal
            int background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
            if (background != -1 && !isInEditMode()) {
                setBackgroundColor(background);
            } else {
                setBackgroundColor(backgroundColor);// 如果没有设置，就用这个颜色
            }
        }
    }

}
