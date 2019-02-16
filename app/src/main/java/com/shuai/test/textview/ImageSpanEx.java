package com.shuai.test.textview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ReplacementSpan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
public class ImageSpanEx extends ReplacementSpan {
    @Retention(RetentionPolicy.SOURCE)
    public @interface BetterImageSpanAlignment {}
    public static final int ALIGN_BOTTOM = 0;
    public static final int ALIGN_BASELINE = 1;
    public static final int ALIGN_CENTER = 2;

    /**
     * A helper function to allow dropping in BetterImageSpan as a replacement to ImageSpan,
     * and allowing for center alignment if passed in.
     */
    public static final @BetterImageSpanAlignment
    int normalizeAlignment(int alignment) {
        switch (alignment) {
            case DynamicDrawableSpan.ALIGN_BOTTOM:
                return ALIGN_BOTTOM;
            case ALIGN_CENTER:
                return ALIGN_CENTER;
            case DynamicDrawableSpan.ALIGN_BASELINE:
            default:
                return ALIGN_BASELINE;
        }
    }

    private int mDrawableWidth;
    private int mHeight;
    private Rect mBounds;
    private final int mAlignment;
    private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();
    private final Drawable mDrawable;
    private int mLeftMargin;
    private int mRightMargin;

    public ImageSpanEx(Drawable drawable) {
        this(drawable, ALIGN_BASELINE);
    }

    public ImageSpanEx(Drawable drawable, @BetterImageSpanAlignment int verticalAlignment) {
        mDrawable = drawable;
        mAlignment = verticalAlignment;
        updateBounds();
    }

    public ImageSpanEx setLeftMargin(int leftMargin) {
        mLeftMargin = leftMargin;
        return this;
    }

    public ImageSpanEx setRightMargin(int rightMargin) {
        mRightMargin = rightMargin;
        return this;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Returns the width of the image span and increases the height if font metrics are available.
     */
    @Override
    public int getSize(
            Paint paint,
            CharSequence text,
            int start,
            int end,
            Paint.FontMetricsInt fontMetrics) {
        updateBounds();
        if (fontMetrics == null) {
            return mDrawableWidth +mLeftMargin+mRightMargin;
        }

        int offsetAbove = getOffsetAboveBaseline(fontMetrics);
        int offsetBelow = mHeight + offsetAbove;
        if (offsetAbove < fontMetrics.ascent) {
            fontMetrics.ascent = offsetAbove;
        }

        if (offsetAbove < fontMetrics.top) {
            fontMetrics.top = offsetAbove;
        }

        if (offsetBelow > fontMetrics.descent) {
            fontMetrics.descent = offsetBelow;
        }

        if (offsetBelow > fontMetrics.bottom) {
            fontMetrics.descent = offsetBelow;
        }

        return mDrawableWidth +mLeftMargin+mRightMargin;
    }

    @Override
    public void draw(
            Canvas canvas,
            CharSequence text,
            int start,
            int end,
            float x,
            int top,
            int y,
            int bottom,
            Paint paint) {
        paint.getFontMetricsInt(mFontMetricsInt);
        int iconTop = y + getOffsetAboveBaseline(mFontMetricsInt);
        x=x+mLeftMargin;
        canvas.translate(x, iconTop);
        mDrawable.draw(canvas);
        canvas.translate(-x, -iconTop);
    }

    public void updateBounds() {
        mBounds = mDrawable.getBounds();

        mDrawableWidth = mBounds.width();
        mHeight = mBounds.height();
    }

    private int getOffsetAboveBaseline(Paint.FontMetricsInt fm) {
        switch (mAlignment) {
            case ALIGN_BOTTOM:
                return fm.descent - mHeight;
            case ALIGN_CENTER:
                int textHeight = fm.descent - fm.ascent;
                int offset = (textHeight - mHeight) / 2;
                return fm.ascent + offset;
            case ALIGN_BASELINE:
            default:
                return -mHeight;
        }
    }
}
