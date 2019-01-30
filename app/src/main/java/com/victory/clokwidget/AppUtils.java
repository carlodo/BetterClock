package com.victory.clokwidget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.util.Locale;

public class AppUtils {
    private static SharedPreferences shared;
    private static SharedPreferences.Editor editor;

    private final static String TRANS_PERCENT = "TRANS_PERCENT_ME";
    private final static String OPTION_LANG = "OPTION_LANG_ME";
    private final static String APP_CLASS = "APP_CLASS";
    private final static String APP_NAME = "APP_NAME";
    private final static String OPTION_SHADOW = "OPTION_SHADOW";
    private final static String OPTION_AMPM = "OPTION_AMPM";
    private final static String OPTION_24HOUR = "OPTION_24HOUR";

    private final static String Select_Lang = "SELECT_LANG";

    public static boolean isOptionAmpm(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getBoolean(OPTION_AMPM, false);
    }

    public static void setOptionAmpm(Context context, boolean isAmpm) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putBoolean(OPTION_AMPM, isAmpm).apply();
    }

    public static boolean isOption24hour(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getBoolean(OPTION_24HOUR, false);
    }

    public static void setOption24hour(Context context, boolean is24Hour) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putBoolean(OPTION_24HOUR, is24Hour).apply();
    }

    public static String getAppName(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getString(APP_NAME, "");
    }

    public static void setAppName(Context context, String appName) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putString(APP_NAME, appName).apply();
    }

    public static String getAppClass(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getString(APP_CLASS, "");
    }

    public static void setAppClass(Context context, String className) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putString(APP_CLASS, className).apply();
    }

    public static int getTransPercent(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getInt(TRANS_PERCENT, 60);
    }

    public static void setTransPercent(Context context, int width) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putInt(TRANS_PERCENT, width).apply();
    }

    public static Locale getAppLocale(Context context) {
        if (getCurrentLang(context) == Globals.Lang_Spanish)
            return new Locale("es", "ES");
        else if (getCurrentLang(context) == Globals.Lang_Russian)
            return new Locale("ru", "RU");
        else if (getCurrentLang(context) == Globals.Lang_Portuguese)
            return new Locale("pt", "PT");
        else
            return Locale.ENGLISH;
    }

    public static void setCurrentLang(Context context, int whichLang) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putInt(Select_Lang, whichLang).apply();
    }

    public static void setCurrentLang(Context context, String language) {
        int languageToLoad = 0;
        if (language.equals("english"))
            languageToLoad = 0;
        else if (language.equals("espanol"))
            languageToLoad = 1;
        else if (language.equals("russo"))
            languageToLoad = 2;
        else if (language.equals("porto"))
            languageToLoad = 3;

        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putInt(Select_Lang, languageToLoad).apply();
    }

    public static int getCurrentLang(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        int anInt = shared.getInt(Select_Lang, 0);
        return anInt;
    }


    public static boolean isShadow(Context context) {
        shared = context.getSharedPreferences("userInfo", 0);
        return shared.getBoolean(OPTION_SHADOW, false);
    }

    public static void setIsShadow(Context context, boolean isEnglish) {
        shared = context.getSharedPreferences("userInfo", 0);
        editor = shared.edit();

        editor.putBoolean(OPTION_SHADOW, isEnglish).apply();
    }

    public static void updateLocale(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();

        if (getCurrentLang(context) == Globals.Lang_English)
            config.locale = Locale.ENGLISH;
        else if (getCurrentLang(context) == Globals.Lang_Spanish)
            config.locale = new Locale("es", "ES");
        else if (getCurrentLang(context) == Globals.Lang_Russian)
            config.locale = new Locale("ru", "RU");
        else if (getCurrentLang(context) == Globals.Lang_Portuguese)
            config.locale = new Locale("pt", "PT");

        resources.updateConfiguration(config, dm);
    }

    public static void updateLocale(Context context, String language) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();


        if (language.equals("english"))
            config.locale = Locale.ENGLISH;
        else if (language.equals("espanol"))
            config.locale = new Locale("es", "ES");
        else if (language.equals("russo"))
            config.locale = new Locale("ru", "RU");
        else if (language.equals("porto"))
            config.locale = new Locale("pt", "PT");

        resources.updateConfiguration(config, dm);

    }

    public static void setLanguage(Context context, String language) {

        String languageToLoad = "en";
        if (language.equals(context.getResources().getString(R.string.lang_english)))
            languageToLoad = "en";
        else if (language.equals(context.getResources().getString(R.string.lang_spanish)))
            languageToLoad = "es";
        else if (language.equals(context.getResources().getString(R.string.lang_russian)))
            languageToLoad = "ru";
        else if (language.equals(context.getResources().getString(R.string.lang_portugese)))
            languageToLoad = "pt";

        Locale locale = new Locale(languageToLoad); //e.g "sv"
        Locale systemLocale = context.getResources().getConfiguration().locale;
        if (systemLocale != null && systemLocale.equals(locale)) {

            return;
        }

        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

    }

    public static void clearSharedPreferences(Context ctx) {
        File dir = new File(ctx.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            // clear each of the prefrances
            ctx.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().commit();
        }
        // Make sure it has enough time to save all the commited changes
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        for (int i = 0; i < children.length; i++) {
            // delete the files
            new File(dir, children[i]).delete();
        }
    }

    @NonNull
    public static String getStringByLocal(Activity context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            return getStringByLocalPlus17(context, resId, "en");
        else
            return getStringByLocalBefore17(context, resId, "en");
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static String getStringByLocalPlus17(Activity context, int resId, String locale) {
        Configuration configuration = new Configuration(context.getResources().getConfiguration());
        configuration.setLocale(new Locale(locale));
        String string = context.createConfigurationContext(configuration).getResources().getString(resId);
        return string;
    }

    private static String getStringByLocalBefore17(Context context, int resId, String language) {
        Resources currentResources = context.getResources();
        AssetManager assets = currentResources.getAssets();
        DisplayMetrics metrics = currentResources.getDisplayMetrics();
        Configuration config = new Configuration(currentResources.getConfiguration());
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        config.locale = locale;
        /*
         * Note: This (temporarily) changes the devices locale! TODO find a
         * better way to get the string in the specific locale
         */
        Resources defaultLocaleResources = new Resources(assets, metrics, config);
        String string = defaultLocaleResources.getString(resId);
        // Restore device-specific locale
        new Resources(assets, metrics, currentResources.getConfiguration());
        return string;
    }
}
