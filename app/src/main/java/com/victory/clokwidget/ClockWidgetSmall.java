package com.victory.clokwidget;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


/**
 * Implementation of App Widget functionality.
 */
public class ClockWidgetSmall extends AppWidgetProvider {

    public void onEnabled(Context context) {
        super.onEnabled(context);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, UpdateServiceSmall.class));
//        } else {
            context.startService(new Intent(context, UpdateServiceSmall.class));
//        }
//        Log.d("BetterClock:", "onEnabled in ClockWidgetSmall");
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onEnabled(context);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, UpdateServiceSmall.class));
//        } else {
            context.startService(new Intent(context, UpdateServiceSmall.class));
//        }
//        Log.d("BetterClock:", "onUpdate  in ClockWidgetSmall");
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        context.stopService(new Intent(context, UpdateServiceSmall.class));
        Log.d("BetterClock:", "onDeleted  in ClockWidgetSmall");

    }
}

