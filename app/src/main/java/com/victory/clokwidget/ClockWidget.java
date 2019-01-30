package com.victory.clokwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.victory.clokwidget.model.ConfigureStruct;
import com.victory.clokwidget.utils.GraphicsUtils;
import com.victory.clokwidget.utils.Intents;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClockWidget extends AppWidgetProvider {
    public void onEnabled(final Context context) {
        super.onEnabled(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, UpdateService.class));
        } else {
            context.startService(new Intent(context, UpdateService.class));
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Log.d("BetterClock:", "onEnabled in ClockWidget");

    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onEnabled(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             context.startForegroundService(new Intent(context, UpdateService.class));
        } else {
            context.startService(new Intent(context, UpdateService.class));
        }
        Log.d("BetterClock:", "onUpdate in ClockWidget");
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        context.stopService(new Intent(context, UpdateService.class));
        Log.d("BetterClock:", "onDeleted in ClockWidget");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public void onReceive(Context context, Intent intent) {
        updateView(context, buildUpdate(context));
    }

    public RemoteViews buildUpdate(Context context) {

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

        SharedPreferences shared = context.getSharedPreferences("userInfo", 0);
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

        Log.d("BetterClock:", "app_class: " + app_class);
        int timeSize = context.getResources().getDimensionPixelSize(R.dimen.time_size);
        int apmSize = context.getResources().getDimensionPixelSize(R.dimen.apm_size);
        int dateSize = context.getResources().getDimensionPixelSize(R.dimen.date_size);
        int weekSize = context.getResources().getDimensionPixelSize(R.dimen.week_size);
        if (AppUtils.getCurrentLang(context) == Globals.Lang_Russian)
            weekSize = context.getResources().getDimensionPixelSize(R.dimen.week_size_russia);
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
        int shadowColor = context.getResources().getColor(R.color.ShadowColour);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), back_color.equals("Black") ? R.layout.main_draw : R.layout.main_draw_transparent);
        if (AppUtils.getCurrentLang(context) == 2)
            remoteViews = new RemoteViews(context.getPackageName(), back_color.equals("Black") ? R.layout.main_draw_ru : R.layout.main_draw_transparent_ru);
        remoteViews.setImageViewBitmap(R.id.time, GraphicsUtils.drawTime(context, new StringBuilder(String.valueOf(hour)).append(":").append(minutes).toString(), timeSize, apm, apmSize, ampm, getColor(time_color, true, false), font, shadow, shadowColor, alpha));
        remoteViews.setImageViewBitmap(R.id.date, GraphicsUtils.drawDate(context, getMonthDayYearString(context), dateSize, getColor(date_color, false, false), font, shadow, shadowColor, alpha));
        remoteViews.setImageViewBitmap(R.id.week, GraphicsUtils.drawWeek(context, getWeekString(context), weekSize, getColor(week_color, false, false), font, shadow, shadowColor, alpha));
        PendingIntent i = getClickActionNew(app_class, context);
        remoteViews.setOnClickPendingIntent(R.id.time, i);
        remoteViews.setOnClickPendingIntent(R.id.widget, i);
        remoteViews.setOnClickPendingIntent(R.id.date, i);
        return remoteViews;
    }

    public void updateView(Context context, RemoteViews view) {
        AppWidgetManager.getInstance(context).updateAppWidget(new ComponentName(context, ClockWidget.class), view);
    }

    protected PendingIntent getClickActionNew(String app_class, Context context) {

        String packageName = app_class;
        if (packageName == null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent("android.intent.action.VIEW", Uri.parse("")), 0);
            return pendingIntent;
        }
        Intent _intent = null;
        if (!TextUtils.isEmpty(packageName)) {

            Intent localIntent = new Intent("android.intent.action.MAIN", null);
            localIntent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> appList = context.getPackageManager().queryIntentActivities(localIntent, 0);
            for (int i = 0; i < appList.size(); i++) {
                ResolveInfo resolveInfo = appList.get(i);
                String packageStr = resolveInfo.activityInfo.packageName;
                if (packageStr.equals(packageName)) {
                    //这个就是你想要的那个Activity
                    _intent = new Intent().setClassName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name);
                    break;
                }
            }
        }

        if (_intent == null) {
            _intent = new Intent(context, ConfigureActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, _intent, 0);
        return pendingIntent;
    }

    public String getWeekString(Context context) {
        Calendar c = Calendar.getInstance();
        String strDayOfWeek = (new SimpleDateFormat("EEEE", AppUtils.getAppLocale(context)).format(c.getTime()));
        return strDayOfWeek;
    }

    public String getMonthDayYearString(Context context) {
        //---DATE AND MONTH NAME
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        Locale.getDefault();
        String MonthName = ((new SimpleDateFormat("MMMM", AppUtils.getAppLocale(context)).format(c.getTime()))).toUpperCase();

        String date_andMONTH = MonthName + " " + date + ", " + c.get(Calendar.YEAR);

        return date_andMONTH;
    }

    protected int getColor(String input, boolean getTimeColour, boolean custom) {
        input = input.replace(" ", "").toLowerCase();
        if (custom) {
            return -1;
        } else if (input.equals("black")) {
            return -16777216;
        } else {
            if (input.equals("white")) {
                return -1;
            }
            if (input.equals("gray")) {
                return -7829368;
            }
            if (input.equals("brown")) {
                return Color.rgb(139, 69, 19);
            }
            if (input.equals("red")) {
                return -65536;
            }
            if (input.equals("orange")) {
                return Color.rgb(255, 140, 0);
            }
            if (input.equals("yellow")) {
                return -256;
            }
            if (input.equals("green")) {
                return Color.rgb(34, 139, 34);
            }
            if (input.equals("blue")) {
                return -16776961;
            }
            if (input.equals("purple")) {
                return Color.rgb(147, 112, 219);
            }
            if (input.equals("hotpink")) {
                return Color.rgb(255, 105, 180);
            }
            return input.equals("deeppink") ? Color.rgb(255, 20, 147) : -1;
        }
    }
}
