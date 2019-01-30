package com.victory.clokwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class UpdateServiceOrigin extends Service {

    private int Clock_Width = 1150;
    private int Clock_Height = 800;

    private int Top_Text_Width = 982;
    private int Top_Text_Height = 430;
    private int Center_Text_Width = 778;
    private int Center_Text_Height = 313;
    private int Bottom_Text_Width = 696;
    private int Bottom_Text_Height = 160;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("ClockService: ", intent.getAction());
            if (intent.getAction().compareTo(Intent.ACTION_SCREEN_ON) == 0 ||
                    intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0 ||
                    intent.getAction().compareTo(Intent.ACTION_TIME_CHANGED) == 0 ||
                    intent.getAction().compareTo(Intent.ACTION_TIMEZONE_CHANGED) == 0 ||
                    intent.getAction().compareTo(Intent.ACTION_USER_PRESENT) == 0 ) {
                // update widget time here using System.currentTimeMillis()
                RemoteViews updateViews = buildUpdate(UpdateServiceOrigin.this);
                ComponentName widget = new ComponentName(UpdateServiceOrigin.this,ClockWidgetSmall.class);
                AppWidgetManager manager  = AppWidgetManager.getInstance(UpdateServiceOrigin.this);
                manager.updateAppWidget(widget,updateViews);
            }
        }
    };
    @Override
    public void onCreate() {
        Log.e("ClockService: ", "onCreate");
        super.onCreate();

        RemoteViews updateViews = buildUpdate(this);
        ComponentName widget = new ComponentName(this,ClockWidgetSmall.class);
        AppWidgetManager manager  = AppWidgetManager.getInstance(this);
        manager.updateAppWidget(widget,updateViews);

        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(receiver, filter);

    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("ClockService", "onStart");
        super.onStart(intent, startId);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e("ClockService:", "onStartCommand!");
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Log.e("ClockService:", "onDestroy!");
        unregisterReceiver(this.receiver);
        super.onDestroy();

        Intent broadcastIntent = new Intent("com.victory.clokwidget.RestartSensor");
        sendBroadcast(broadcastIntent);

    }

    private RemoteViews buildUpdate(Context context){
        RemoteViews updateView = new RemoteViews(context.getPackageName(),R.layout.clock_widget);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());



        int myColor_hour = getResources().getColor(R.color.TextColour);
        int myColor_shadow = getResources().getColor(R.color.ShadowColour);
        boolean isShadow = AppUtils.isShadow(this);


        Paint paint = new Paint();
        Rect bounds = new Rect();
        Bitmap myBitmap = Bitmap.createBitmap(Clock_Width,Clock_Height, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);

        int percent = AppUtils.getTransPercent(this); // 0 ~ 100

        //-------------   Set Opacity (Alpha) for View
        int Opacity_alpha = percent;        // 70% — B3  = 179 ,  30% — 4D = 77
        //      --       paint.setAlpha(Opacity_alpha);
        //Helper to setColor(), that only assigns the color's alpha value, leaving its r,g,b values unchanged.

        //Show Time
        paint.setTypeface(getFontTypeface(context,false));
        paint.setTextSize(500);
        paint.setColor(myColor_hour);
        paint.setTextAlign(Paint.Align.CENTER);
        //       --          Opacity          --
        paint.setAlpha(Opacity_alpha);

        if (isShadow)
            paint.setShadowLayer(4, 4, 4, myColor_shadow + 0x01000000 * Opacity_alpha);

        //----set hours and minutes
        String strTime;
        strTime = getTime(c, context);
        //----------------------------
        paint.getTextBounds(strTime, 0, strTime.length(), bounds);
        int xPos = (myCanvas.getWidth() / 2);
        int yPos = (int) ((myCanvas.getHeight() / 2) - ((paint.descent() +
                paint.ascent()) / 2)) ;
        setTextSizeForWidth(paint, Top_Text_Width, strTime);
        myCanvas.drawText(strTime,xPos,yPos-150, paint);
        updateView.setImageViewBitmap(R.id.hoursANDmint, myBitmap);


        //----------------------------
        //Show Date and Year
        String strDate = getDate(c, context);
        paint.setTextSize(180);
        paint.getTextBounds(strDate, 0, strDate.length(), bounds);
        xPos = (myCanvas.getWidth() / 2);
        yPos = (int) ((myCanvas.getHeight() / 2) - ((paint.descent() +
                paint.ascent()) / 2)) ;

        setTextSizeForWidth(paint, Bottom_Text_Width, strDate);
        myCanvas.drawText(strDate,xPos,yPos+270, paint);
        updateView.setImageViewBitmap(R.id.DATEandMONTH, myBitmap);

        //-------------------------
        //-------------------------   DAY name

        int myColor_DAY = getResources().getColor(R.color.Day_textColor);




        String strDayOfWeek = (new SimpleDateFormat("EEEE", AppUtils.getAppLocale(context)).format(c.getTime()));

        if (strDayOfWeek.length() > 0) {
            strDayOfWeek = strDayOfWeek.substring(0,1).toUpperCase(context.getResources().getConfiguration().locale) + strDayOfWeek.substring(1);
        }
        //Show week
        paint.setTypeface(getFontTypeface(context, true));
        paint.setTextSize(400);
        paint.setColor(myColor_DAY);
        paint.setTextAlign(Paint.Align.CENTER);

        if (isShadow)
            paint.setShadowLayer(4, 4, 4, myColor_shadow);

        paint.getTextBounds(strDayOfWeek, 0, strDayOfWeek.length(), bounds);
        xPos = (myCanvas.getWidth() / 2);
        yPos = (int) ((myCanvas.getHeight() / 2) - ((paint.descent() +
                paint.ascent()) / 2)) ;

        setTextSizeForWidth(paint, Center_Text_Width, strDayOfWeek);
        myCanvas.drawText(strDayOfWeek,xPos,yPos+80, paint);
        updateView.setImageViewBitmap(R.id.Day, myBitmap);


        String packageName = AppUtils.getAppClass(this);

        Intent _intent = null;
        if (!TextUtils.isEmpty(packageName)) {

            Intent localIntent = new Intent("android.intent.action.MAIN", null);
            localIntent.addCategory("android.intent.category.LAUNCHER");
            List<ResolveInfo> appList =  getPackageManager().queryIntentActivities(localIntent, 0);
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
        updateView.setOnClickPendingIntent(R.id.button_conf, pendingIntent);//点击跳到主页

        return updateView;
    }
    private static void setTextSizeForWidth(Paint paint, float desiredWidth,
                                            String text) {

        // Pick a reasonably large value for the test. Larger values produce
        // more accurate results, but may cause problems with hardware
        // acceleration. But there are workarounds for that, too; refer to
        // http://stackoverflow.com/questions/6253528/font-size-too-large-to-fit-in-cache
        final float testTextSize = 48f;

        // Get the bounds of the text, using our testTextSize.
        paint.setTextSize(testTextSize);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        // Calculate the desired size as a proportion of our testTextSize.
        float desiredTextSize = testTextSize * desiredWidth / bounds.width();

        // Set the paint for that size.
        paint.setTextSize(desiredTextSize);
    }
    public Typeface getFontTypeface(Context context, boolean isWeek){
        int language = AppUtils.getCurrentLang(context);
        if(isWeek){
            Typeface clock;
            if(language == Globals.Lang_Russian)
                clock = Typeface.createFromAsset(getAssets(),"fonts/8591.ttf");
            else if(language == Globals.Lang_Portuguese)
                clock = Typeface.createFromAsset(getAssets(),"fonts/PortugueseSupportAguafinaScript-Regular.ttf");
            else if(language == Globals.Lang_Spanish)
                clock = Typeface.createFromAsset(getAssets(),"fonts/la Compagnie des Ombres.ttf");
            else
                clock = Typeface.createFromAsset(getAssets(),"fonts/la Compagnie des Ombres.ttf");
            return clock;




        }else{
            Typeface clock;
            if(language == Globals.Lang_Russian)
                clock = Typeface.createFromAsset(getAssets(),"fonts/MultiBaltimoreTypewriterBold Beveled.ttf");
            else if(language == Globals.Lang_Portuguese)
                clock = Typeface.createFromAsset(getAssets(),"fonts/MultiBaltimoreTypewriterBold Beveled.ttf");
            else if(language == Globals.Lang_Spanish)
                clock = Typeface.createFromAsset(getAssets(),"fonts/MultiBaltimoreTypewriterBold Beveled.ttf");
            else
                clock = Typeface.createFromAsset(getAssets(),"fonts/MultiBaltimoreTypewriterBold Beveled.ttf");
            return clock;

        }

    }

    public String getTime(Calendar c, Context context){

        SimpleDateFormat sdf;
        boolean is24Hour = AppUtils.isOption24hour(context);
        boolean isAMPM = AppUtils.isOptionAmpm(context);

        if (is24Hour) {
            // 24小时制
            sdf = new SimpleDateFormat("HH:mm");
        } else {
            // 12小时制
            if (isAMPM)
                sdf = new SimpleDateFormat("h:mm aa", Locale.ENGLISH);
            else
                sdf = new SimpleDateFormat("h:mm");
        }

        return sdf.format(c.getTime());
    }

    public String getDate(Calendar c, Context context)
    {
        //---DATE AND MONTH NAME

        int date = c.get(Calendar.DATE);

        String MonthName = ((new SimpleDateFormat("MMMM", AppUtils.getAppLocale(context)).format(c.getTime()))).toUpperCase();

        String date_andMONTH = MonthName + " " + date+", " + c.get(Calendar.YEAR);

        return date_andMONTH;
    }
}