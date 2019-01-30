package com.victory.clokwidget.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.Log;

import com.victory.clokwidget.AppUtils;


public class GraphicsUtils {
    public static Bitmap drawTime(Context context, String time, int timeSize, String apm, int apmSize, boolean ampm, int color, boolean usefont, boolean useShadow, int shadowColor, int alpha) {
        usefont = true;
        TextPaint timePaint = new TextPaint(65);
        timePaint.setColor(color);
        timePaint.setTextSize((float) timeSize);
        timePaint.setTextAlign(Align.CENTER);
        timePaint.setStyle(Style.FILL);
        timePaint.setAlpha(alpha);
        if (useShadow)
            timePaint.setShadowLayer(4, 14, 14, shadowColor);
        TextPaint apmPaint = new TextPaint(65);
        apmPaint.setColor(color);
        apmPaint.setTextSize((float) apmSize);
        apmPaint.setStyle(Style.FILL);
        apmPaint.setAlpha(alpha);
        if (useShadow)
            apmPaint.setShadowLayer(4, 14, 14, shadowColor);
        if (usefont) {
            Typeface font = Typeface.createFromAsset(context.getAssets(), "fonts/steelfish rg.ttf");
            timePaint.setTypeface(font);
            apmPaint.setTypeface(font);
        }
        int width = (int) timePaint.measureText(time);
        color = (int) apmPaint.measureText(apm);
        Bitmap clockBitmap = Bitmap.createBitmap((color * 2) + width, timeSize + 10, Config.ARGB_8888);

        Canvas canvas = new Canvas(clockBitmap);
        canvas.drawText(time, (float) ((width / 2) + color), (float) timeSize, timePaint);
        if (ampm) {
            canvas.drawText(apm, (float) (width + color), (float) timeSize, apmPaint);
        }
        return clockBitmap;
    }

    public static Bitmap drawDate(Context context, String date, int dateSize, int color, boolean usefont, boolean useShadow, int shadowColor, int alpha) {

        usefont = true;
        TextPaint datePaint = new TextPaint(65);
        if (usefont) {
            datePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/steelfish rg.ttf"));
        }
        datePaint.setColor(color);
        datePaint.setTextSize((float) dateSize);
        datePaint.setTextAlign(Align.CENTER);
        datePaint.setStyle(Style.FILL);
        datePaint.setAlpha(alpha);
        if (useShadow)
            datePaint.setShadowLayer(4, 14, 14, shadowColor);
        int width = (int) datePaint.measureText(date);
        Bitmap dateBitmap = Bitmap.createBitmap(width + 1, dateSize + 5, Config.ARGB_8888);
        new Canvas(dateBitmap).drawText(date, (float) (width / 2), (float) dateSize, datePaint);
        return dateBitmap;
    }

    public static Bitmap drawWeek(Context context, String week, int weekSize, int color, boolean usefont, boolean useShadow, int shadowColor, int alpha) {
        week = week.substring(0, 1).toUpperCase() + week.substring(1);
        usefont = true;
        TextPaint weekPaint = new TextPaint(65);
        if (usefont) {
            if (AppUtils.getCurrentLang(context) == 2)
                weekPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OlgaCTT.ttf"));
            else
                weekPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/la Compagnie des Ombres.ttf"));

//            weekPaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/steelfish rg.ttf"));
        }
        weekPaint.setColor(color);
        weekPaint.setTextSize((float) weekSize);
        weekPaint.setTextAlign(Align.CENTER);
        weekPaint.setStyle(Style.FILL);
//        weekPaint.setAlpha(alpha);
        if (useShadow)
            weekPaint.setShadowLayer(4, 14, 14, shadowColor);
        int width = (int) weekPaint.measureText(week);
        Bitmap dateBitmap = Bitmap.createBitmap(width + 1, weekSize + 65, Config.ARGB_8888);
        new Canvas(dateBitmap).drawText(week, (float) (width / 2), (float) weekSize, weekPaint);
        return dateBitmap;
    }
}
