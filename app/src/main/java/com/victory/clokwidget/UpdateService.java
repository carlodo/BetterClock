package com.victory.clokwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.victory.clokwidget.model.ConfigureStruct;
import com.victory.clokwidget.utils.GraphicsUtils;

import java.util.Date;

public class UpdateService extends AbstractUpdateService {
    @Override
    public RemoteViews buildUpdate() {

        boolean hour12 = true;
        boolean ampm = false;
        boolean shadow = false;
        String action = "Do Nothing";
        String time_color = "White";
        String week_color = "White";
        String date_color = "White";
        String back_color = "No background";
        String format = "dd/MM/yyyy";
        String language = "English";
        String app_class = "com.victory.clokwidget";
        int opacity = 100;

        SharedPreferences shared = getSharedPreferences("userInfo", 0);
        time_color = shared.getString("color_time1", "White");
        week_color = shared.getString("color_week1", "White");
        date_color = shared.getString("color_date1", "White");
        opacity = shared.getInt("opacity_value", 50);
        hour12 = shared.getBoolean("12hour1", true);
        ampm = shared.getBoolean("ampm1", false);
        shadow = shared.getBoolean("text_shadow1", false);
        back_color = shared.getString("color_bg1", "No background");
        if (!hour12)
            ampm = false;
        if (realm != null) {
            ConfigureStruct obj = realm.where(ConfigureStruct.class).equalTo("id", 1).findFirst();
            if (obj != null) {
//                hour12 = obj.isHour12();
//                ampm = obj.isAmpm();
//                shadow = obj.isShadow();
//                time_color = obj.getTimeColor();
//                week_color = obj.getWeekColor();
//                date_color = obj.getDateColor();
//                back_color = obj.getBackColor();
//                language = obj.getLanguage();
                app_class = obj.getAppClass();
//                opacity = obj.getOpacity();

                if (app_class.equals(""))
                    app_class = "com.victory.clokwidget";
            }else{

            }
        }

        Log.d("BetterClock:", "app_class: " + app_class);
        int timeSize = getResources().getDimensionPixelSize(R.dimen.time_size);
        int apmSize = getResources().getDimensionPixelSize(R.dimen.apm_size);
        int dateSize = getResources().getDimensionPixelSize(R.dimen.date_size);
        int weekSize = getResources().getDimensionPixelSize(R.dimen.week_size);
        if(AppUtils.getCurrentLang(this)==Globals.Lang_Russian)
            weekSize = getResources().getDimensionPixelSize(R.dimen.week_size_russia);
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
        int alpha = opacity * 255 / 100;
        int shadowColor = getResources().getColor(R.color.ShadowColour);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), back_color.equals("Black") ? R.layout.main_draw : R.layout.main_draw_transparent);
        if (AppUtils.getCurrentLang(this) == 2)
            remoteViews = new RemoteViews(getPackageName(), back_color.equals("Black") ? R.layout.main_draw_ru : R.layout.main_draw_transparent_ru);

        remoteViews.setImageViewBitmap(R.id.time, GraphicsUtils.drawTime(this, new StringBuilder(String.valueOf(hour)).append(":").append(minutes).toString(), timeSize, apm, apmSize, ampm, getColor(time_color, true, false), font, shadow, shadowColor, alpha));
        remoteViews.setImageViewBitmap(R.id.date, GraphicsUtils.drawDate(this, getMonthDayYearString(), dateSize, getColor(date_color, false, false), font, shadow, shadowColor, alpha));
        remoteViews.setImageViewBitmap(R.id.week, GraphicsUtils.drawWeek(this, getWeekString(), weekSize, getColor(week_color, false, false), font, shadow, shadowColor, alpha));
        PendingIntent i = getClickActionNew(app_class, this);
        remoteViews.setOnClickPendingIntent(R.id.time, i);
        remoteViews.setOnClickPendingIntent(R.id.widget, i);
        remoteViews.setOnClickPendingIntent(R.id.date, i);
        return remoteViews;
    }

    @Override
    public void updateView(RemoteViews view) {
        AppWidgetManager.getInstance(this).updateAppWidget(new ComponentName(this, ClockWidget.class), view);
    }
}
