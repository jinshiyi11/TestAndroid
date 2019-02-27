package com.shuai.test.textview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Drawable工具类
 */
public class DrawableUtil {
    /**
     * 将view转为图片
     * @param view 对应的view控件
     * @return 转换后的图片
     */
    public static Drawable convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        view.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = view.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        view.destroyDrawingCache();
        return new BitmapDrawable(view.getResources(), viewBmp);
    }
}
