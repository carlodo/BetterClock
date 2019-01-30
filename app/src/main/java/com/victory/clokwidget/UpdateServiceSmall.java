package com.victory.clokwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.view.View;
import android.widget.RemoteViews;

import com.victory.clokwidget.model.ConfigureStruct;
import com.victory.clokwidget.utils.GraphicsUtils;

import java.util.Date;

public class UpdateServiceSmall extends AbstractUpdateService {
    public void updateView(RemoteViews view) {
        AppWidgetManager.getInstance(this).updateAppWidget(new ComponentName(this, ClockWidget.class), view);
    }

    public RemoteViews buildUpdate() {
        boolean hour12 = true;
        boolean ampm = false;
        boolean shadow= false;
        String action = "Do Nothing";
        String time_color = "White";
        String date_color = "White";
        String back_color = "no background";
        String format = "EE, MM dd";
        String language = "English";
        String app_class = "";
        int opacity = 100;
        ConfigureStruct obj = realm.where(ConfigureStruct.class).equalTo("id", 1).findFirst();
        if (obj != null) {
            hour12 = obj.isHour12();
            ampm = obj.isAmpm();
            shadow = obj.isShadow();
            time_color = obj.getTimeColor();
            date_color = obj.getDateColor();
            back_color = obj.getBackColor();
            language = obj.getLanguage();
            app_class = obj.getAppClass();
            opacity = obj.getOpacity();
            if(!hour12)
                ampm = false;
        }

        int timeSize = getResources().getDimensionPixelSize(R.dimen.time_size);
        int apmSize = getResources().getDimensionPixelSize(R.dimen.apm_size);
        int dateSize = getResources().getDimensionPixelSize(R.dimen.date_size);
        Date now = new Date();
        String hour = String.valueOf(now.getHours());
        String minutes = String.valueOf(now.getMinutes());
        if (now.getHours() > 12 && hour12) {
            hour = String.valueOf(now.getHours() - 12);
        }
        if (now.getHours() == 0 && hour12) {
            hour = "12";
        }
        if (now.getMinutes() < 10) {
            minutes = "0" + minutes;
        }
        String apm = " AM";
        if (now.getHours() >= 12) {
            apm = " PM";
        }
        boolean font = true;
        int alpha = opacity * 255;
        int shadowColor = getResources().getColor(R.color.ShadowColour);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), back_color.equals("Black") ? R.layout.main_draw : R.layout.main_draw_transparent);
        if (AppUtils.getCurrentLang(this) == 2)
            remoteViews = new RemoteViews(getPackageName(), back_color.equals("Black") ? R.layout.main_draw_ru : R.layout.main_draw_transparent_ru);

        remoteViews.setImageViewBitmap(R.id.time, GraphicsUtils.drawTime(this, new StringBuilder(String.valueOf(hour)).append(":").append(minutes).toString(), timeSize, apm, apmSize, ampm, getColor(time_color, true, false), font, shadow, shadowColor, alpha));
        remoteViews.setImageViewBitmap(R.id.date, GraphicsUtils.drawDate(this, getDateFormatString(format, now, hour12), dateSize, getColor(date_color, false, false), font, shadow,shadowColor, alpha));
        remoteViews.setViewVisibility(R.id.date, View.VISIBLE);
//        PendingIntent i = getClickAction(action, this);
        PendingIntent i = getClickActionNew(app_class, this);
        remoteViews.setOnClickPendingIntent(R.id.time, i);
        remoteViews.setOnClickPendingIntent(R.id.widget, i);
        remoteViews.setOnClickPendingIntent(R.id.date, i);
        return remoteViews;
    }

    protected int getDayofWeek(int dayNum) {
        switch (dayNum) {
            case 0:
                return R.string.Sunday_short;
            case 1:
                return R.string.Monday_short;
            case 2:
                return R.string.Tuesday_short;
            case 3:
                return R.string.Wednesday_short;
            case 4:
                return R.string.Thursday_short;
            case 5:
                return R.string.Friday_short;
            case 6:
                return R.string.Saturday_short;
            default:
                return -1;
        }
    }
}
