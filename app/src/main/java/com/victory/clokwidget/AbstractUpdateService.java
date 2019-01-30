package com.victory.clokwidget;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.victory.clokwidget.utils.Intents;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public abstract class AbstractUpdateService extends Service {
    private Object lock = new Object();
    protected final BroadcastReceiver receiver = new ClockUpdateReciever();
    protected Realm realm;
    protected SharedPreferences prefs;

    class ClockUpdateReciever extends BroadcastReceiver {
        ClockUpdateReciever() {

        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("BetterClock:", "Action: " + action);
            if ("android.intent.action.TIME_SET".equals(action) ||
                    "android.intent.action.TIMEZONE_CHANGED".equals(action) ||
                    "android.intent.action.TIME_TICK".equals(action) ||
                    "android.intent.action.SCREEN_OFF".equals(action) ||
                    "android.intent.action.SCREEN_ON".equals(action) ||
                    "android.intent.action.USER_PRESENT".equals(action) ||
                    Intents.ACTION_CLOCK_SETTINGS_CHANGED.equals(action)) {
                Log.d("BetterClock:", "Update broadcast received.");
                updateView(buildUpdate());
            }
        }
    }

    public abstract RemoteViews buildUpdate();

    public abstract void updateView(RemoteViews remoteViews);

    public boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    @Override
    public void onCreate() {
        Log.d("BetterClock:", "onCreate");
        super.onCreate();

        realm = Realm.getDefaultInstance();
        updateView(buildUpdate());
        IntentFilter filter = new IntentFilter();

        filter.addAction("android.intent.action.TIME_SET");
        filter.addAction("android.intent.action.TIMEZONE_CHANGED");
        filter.addAction("android.intent.action.TIME_TICK");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.USER_PRESENT");
        filter.addAction(Intents.ACTION_CLOCK_SETTINGS_CHANGED);
        registerReceiver(receiver, filter);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("BetterClock:", "onStart");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("BetterClock:", "onStartCommand");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.app_name))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setSmallIcon(R.drawable.about)
                .setAutoCancel(true);

        Notification notification = builder.build();

        startForeground(1, notification);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("BetterClock:", "onDestroy");
        unregisterReceiver(receiver);
        Intent broadcastIntent = new Intent("com.victory.clockwidget.RestartSensor");
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    protected int getDayofWeek(int dayNum) {
        switch (dayNum) {
            case 0:
                return R.string.Sunday;
            case 1:
                return R.string.Monday;
            case 2:
                return R.string.Tuesday;
            case 3:
                return R.string.Wednesday;
            case 4:
                return R.string.Thursday;
            case 5:
                return R.string.Friday;
            case 6:
                return R.string.Saturday;
            default:
                return -1;
        }
    }

    protected int getMonth(int monthNum) {
        switch (monthNum) {
            case 0:
                return R.string.January;
            case 1:
                return R.string.February;
            case 2:
                return R.string.March;
            case 3:
                return R.string.April;
            case 4:
                return R.string.May;
            case 5:
                return R.string.June;
            case 6:
                return R.string.July;
            case 7:
                return R.string.August;
            case 8:
                return R.string.September;
            case 9:
                return R.string.October;
            case 10:
                return R.string.November;
            case 11:
                return R.string.December;
            default:
                return -1;
        }
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

    protected String getDateFormatString(String format, Date now, boolean hour12) {
        String day = new SimpleDateFormat("d").format(now);
        if (format.equals("EE, MM dd")) {
            return getString(getDayofWeek(now.getDay())) + ", " + getString(getMonth(now.getMonth())) + " " + day;
        }
        if (format.equals("EE, dd MM")) {
            return getString(getDayofWeek(now.getDay())) + ", " + day + " " + getString(getMonth(now.getMonth()));
        }
        if (format.equals("MM-dd-yyyy")) {
            return new SimpleDateFormat(format).format(now);
        }
        if (format.equals("MM/dd/yyyy")) {
            return new SimpleDateFormat(format).format(now);
        }
        if (format.equals("dd-MM-yyyy")) {
            return new SimpleDateFormat(format).format(now);
        }
        if (format.equals("dd/MM/yyyy")) {
            return new SimpleDateFormat(format).format(now);
        }
        if (format.equals("yyyy-MM-dd")) {
            return new SimpleDateFormat(format).format(now);
        }
        return "";
    }

    public String getMonthDayYearString() {
        //---DATE AND MONTH NAME
        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        Locale.getDefault();
        String MonthName = ((new SimpleDateFormat("MMMM", AppUtils.getAppLocale(this)).format(c.getTime()))).toUpperCase();

        String date_andMONTH = MonthName + " " + date + ", " + c.get(Calendar.YEAR);

        return date_andMONTH;
    }

    public String getWeekString() {
        Calendar c = Calendar.getInstance();
        String strDayOfWeek = (new SimpleDateFormat("EEEE", AppUtils.getAppLocale(this)).format(c.getTime()));
        return strDayOfWeek;
    }

    protected PendingIntent getClickAction(String action, Context context) {
        PendingIntent pendingIntent;
        Intent defineIntent = new Intent("android.intent.action.MAIN");
        if (action.equals("Bring me here")) {
            defineIntent.setClassName("com.victory.clokwidget", "com.victory.clokwidget.ClockApp");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
        } else if (action.equals("Bring me to alarm")) {
            defineIntent.setClassName("com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            if (isIntentAvailable(context, defineIntent)) {
                return pendingIntent;
            }
            defineIntent.setClassName("com.android.deskclock", "com.android.deskclock.AlarmClock");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            if (isIntentAvailable(context, defineIntent)) {
                return pendingIntent;
            }
            defineIntent.setClassName("com.google.android.deskclock", "com.android.deskclock.AlarmClock");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            if (isIntentAvailable(context, defineIntent)) {
                return pendingIntent;
            }
            defineIntent.setClassName("com.motorola.blur.alarmclock", "com.motorola.blur.alarmclock.AlarmClock");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            if (isIntentAvailable(context, defineIntent)) {
                return pendingIntent;
            }
            defineIntent.setClassName("com.sec.android.app.clockpackage", "com.sec.android.app.clockpackage.ClockPackage");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            if (isIntentAvailable(context, defineIntent)) {
                return pendingIntent;
            }
            defineIntent.setClassName("com.android.alarmclock", "com.android.alarmclock.AlarmClock");
            return PendingIntent.getActivity(context, 0, defineIntent, 0);
        } else if (action.equals("Bring me to calendar")) {
            defineIntent.setClassName("com.htc.calendar", "com.htc.calendar.LaunchActivity");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            if (isIntentAvailable(context, defineIntent)) {
                return pendingIntent;
            }
            defineIntent.setClassName("com.google.android.calendar", "com.android.calendar.LaunchActivity");
            pendingIntent = PendingIntent.getActivity(context, 0, defineIntent, 0);
            if (isIntentAvailable(context, defineIntent)) {
                return pendingIntent;
            }
            defineIntent.setClassName("com.android.calendar", "com.android.calendar.LaunchActivity");
            return PendingIntent.getActivity(context, 0, defineIntent, 0);
        } else {
            pendingIntent = PendingIntent.getActivity(context, 0, new Intent("android.intent.action.VIEW", Uri.parse("")), 0);
        }
        return pendingIntent;
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
            List<ResolveInfo> appList = getPackageManager().queryIntentActivities(localIntent, 0);
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
            _intent = new Intent(this, ConfigureActivity.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, _intent, 0);
        return pendingIntent;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
