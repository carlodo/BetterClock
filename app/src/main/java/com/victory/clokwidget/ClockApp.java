package com.victory.clokwidget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.Intent;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.victory.clokwidget.model.ConfigureStruct;
import com.victory.clokwidget.preference.ColorPickerPreference;
import com.victory.clokwidget.preference.SeekBarPreference;
import com.victory.clokwidget.utils.Intents;
import com.victory.clokwidget.utils.UIUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import io.realm.Realm;

public class ClockApp extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    static final String BACK_COLOR = "color_bg";
    static final String DATE_COLOR = "color_date";
    static final String WEEK_COLOR = "color_week";
    static final String TIME_COLOR = "color_time";
    static final String LANGUAGE = "language";
    static final String OPACITY = "seek_bar";
    static final String APPCLICK = "app_chooser";
    private Realm realm;
    final ConfigureStruct configure = new ConfigureStruct();
    private static String click_app_class = "com.victory.clokwidget";
    private boolean mIsClicked = false;
    private boolean firstLoad = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.updateLocale(getBaseContext());
        addPreferencesFromResource(R.xml.preferences);
        setContentView(R.layout.settings);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        realm = Realm.getDefaultInstance();

        Preference pref = findPreference(APPCLICK);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!mIsClicked) {
                    mIsClicked = true;
                    Log.d("BetterClock:", "open apk list!!!");
                    startActivityForResult(preference.getIntent(), 1234);
                }

                return true;
            }
        });
        ifHuaweiAlert();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(this, UpdateService.class));
        } else {
            startService(new Intent(this, UpdateService.class));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadSummary();
        firstLoad = false;
    }

    private void loadSummary() {
        updateSummary(TIME_COLOR);
        updateSummary(WEEK_COLOR);
        updateSummary(DATE_COLOR);
        updateSummary(LANGUAGE);
        updateSummary(OPACITY);
        updateSummary(APPCLICK);
        updateSummary("ampm");
        updateSummary("text_shadow");
        updateSummary("12hour");
        updateSummary("color_bg");
    }

    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() != 4 && event.getKeyCode() != 3 && event.getKeyCode() != 26) {
            return super.onKeyDown(keyCode, event);
        }
        finish();
        return true;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, "Preview").setIcon(R.drawable.preview);
        menu.add(0, 2, 0, "Instructions").setIcon(R.drawable.instructions);
        menu.add(0, 3, 0, "Read me").setIcon(R.drawable.readme);
        menu.add(0, 4, 0, "About").setIcon(R.drawable.about);
        return true;
    }

    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                boolean curtain;
                int i;
                View view = getListView();
                if (view.getVisibility() == View.VISIBLE) {
                    curtain = true;
                } else {
                    curtain = false;
                }
                if (curtain) {
                    i = 4;
                } else {
                    i = 0;
                }
                view.setVisibility(i);
                Toast.makeText(this, "Enable/Disable curtain in menu option", Toast.LENGTH_SHORT).show();
                return true;
            case 2:
                UIUtils.showInstructionsDialog(this);
                return true;
            case 3:
                UIUtils.showReadMeDialog(this);
                return true;
            case 4:
                UIUtils.showAboutDialog(this);
                return true;
            default:
                return super.onMenuItemSelected(featureId, item);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        updateSummary(key);
        SharedPreferences shared = getSharedPreferences("userInfo", 0);
        SharedPreferences.Editor editor = shared.edit();
        configure.setId(1);
        configure.setOpacity(sharedPreferences.getInt("seek_bar", 100));
        configure.setHour12(sharedPreferences.getBoolean("12hour", true));
        if (key.equals("12hour")) {
            boolean value = sharedPreferences.getBoolean("12hour", true);
            editor.putBoolean("12hour1", value).apply();
        }
        configure.setAmpm(sharedPreferences.getBoolean("ampm", false));
        if (key.equals("ampm")) {
            boolean value = sharedPreferences.getBoolean("ampm", false);
            editor.putBoolean("ampm1", value).apply();
        }
        configure.setShadow(sharedPreferences.getBoolean("text_shadow", false));
        if (key.equals("text_shadow")) {
            boolean value = sharedPreferences.getBoolean("text_shadow", false);
            editor.putBoolean("text_shadow1", value).apply();
        }
        configure.setTimeColor(sharedPreferences.getString("color_time", "White"));
        if (key.equals("color_time")) {
            String color_time = sharedPreferences.getString("color_time", "White");
            editor.putString("color_time1", color_time).apply();
        }
        configure.setWeekColor(sharedPreferences.getString("color_week", "White"));
        if (key.equals("color_week")) {
            String color_week = sharedPreferences.getString("color_week", "White");
            editor.putString("color_week1", color_week).apply();
        }
        configure.setDateColor(sharedPreferences.getString("color_date", "White"));
        if (key.equals("color_date")) {
            String color_date = sharedPreferences.getString("color_date", "White");
            editor.putString("color_date1", color_date).apply();
        }
        configure.setBackColor(sharedPreferences.getString("color_bg", "No background"));
        if (key.equals("color_bg")) {
            String value = sharedPreferences.getString("color_bg", "White");
            editor.putString("color_bg1", value).apply();
        }
        configure.setLanguage(sharedPreferences.getString("language", "English"));
        configure.setAppName(getString(R.string.label_app_name));
        configure.setAppClass(click_app_class);
        Log.d("BetterClock:", "click_app_class: " + click_app_class);



        if (key.equals(LANGUAGE)) {
            String cur_language = sharedPreferences.getString("language", "English");
            AppUtils.setCurrentLang(this, cur_language);
            configurationChanged();
        }
        if (key.equals(OPACITY)) {
            int seek_bar = sharedPreferences.getInt("seek_bar", 50);
            editor.putInt("opacity_value", seek_bar).apply();
        }

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(configure);
            }
        });

        Intent intent = new Intent(this, ClockWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ClockWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);

        //sendBroadcast(new Intent(Intents.ACTION_CLOCK_SETTINGS_CHANGED));
    }

    void configurationChanged() {
//        AppUtils.updateLocale(this);
//        getPreferenceScreen().removeAll();
//        addPreferencesFromResource(R.xml.preferences);
//        loadSummary();
        finish();
        startActivity(new Intent(this,ClockApp.class));
    }

    private void updateSummary(String target) {
        if (target.equals(TIME_COLOR)) {
            ListPreference lt = (ListPreference) getPreferenceManager().findPreference(target);
            lt.setSummary(lt.getEntry());
            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                String aString = shared.getString("color_time1", "white");
                if(lt.getEntry().equals(""))
                lt.setSummary(aString);
                lt.setValue(aString);
            } else {
//                lt.setSummary(lt.getEntry());
            }
        } else if (target.equals(WEEK_COLOR)) {
            ListPreference lt = (ListPreference) getPreferenceManager().findPreference(target);
            lt.setSummary(lt.getEntry());
            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                String aString = shared.getString("color_week1", "white");
                if(lt.getEntry().equals(""))
                lt.setSummary(aString);
                lt.setValue(aString);
            } else {
//                lt.setSummary(lt.getEntry());
            }
        } else if (target.equals(DATE_COLOR)) {
            ListPreference lt = (ListPreference) getPreferenceManager().findPreference(target);
            lt.setSummary(lt.getEntry());
            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                String aString = shared.getString("color_date1", "white");
                if(lt.getEntry().equals(""))
                lt.setSummary(aString);
                lt.setValue(aString);
            } else {
//                lt.setSummary(lt.getEntry());
            }

        } else if (target.equals(LANGUAGE)) {
            ListPreference lt = (ListPreference) getPreferenceManager().findPreference(target);
            CharSequence entry = lt.getEntry();
            if (firstLoad) {
                int currentLang = AppUtils.getCurrentLang(this);
                switch (currentLang) {
                    case 0:
                        lt.setSummary("English");
                        lt.setValue("english");
                        break;
                    case 1:
                        lt.setSummary("Español");
                        lt.setValue("espanol");
                        break;
                    case 2:
                        lt.setSummary("Pусский");
                        lt.setValue("russo");
                        break;
                    case 3:
                        lt.setSummary("Portuguese");
                        lt.setValue("porto");
                        break;
                }
            } else {
                lt.setSummary(entry);
            }

        } else if (target.equals(APPCLICK)) {
            Preference lt = (Preference) getPreferenceManager().findPreference(APPCLICK);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            lt.setSummary(sharedPreferences.getString(APPCLICK, getString(R.string.label_app_name)));
            Log.d("BetterClock:", "app_chooser: " + sharedPreferences.getString(APPCLICK, getString(R.string.label_app_name)));
        } else if (target.equals(OPACITY)) {
            SeekBarPreference sp = (SeekBarPreference) getPreferenceManager().findPreference(target);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                int anInt = shared.getInt("opacity_value", 50);
                sp.setSummary(String.valueOf(anInt));
            } else {
                sp.setSummary(String.valueOf(sharedPreferences.getInt(OPACITY, 50)));
            }
        }else if (target.equals("12hour")) {
            CheckBoxPreference sp = (CheckBoxPreference) getPreferenceManager().findPreference(target);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                boolean value = shared.getBoolean("12hour1", true);
//                sp.setSummary(String.valueOf(value));
                sp.setChecked(value);
            } else {
                sp.setChecked(sharedPreferences.getBoolean("12hour", true));
            }
        }else if (target.equals("ampm")) {
            CheckBoxPreference sp = (CheckBoxPreference) getPreferenceManager().findPreference(target);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                boolean value = shared.getBoolean("ampm1", true);
//                sp.setSummary(String.valueOf(value));
                sp.setChecked(value);
            } else {
                sp.setChecked(sharedPreferences.getBoolean("ampm", true));
            }
        }else if (target.equals("text_shadow")) {
            CheckBoxPreference sp = (CheckBoxPreference) getPreferenceManager().findPreference(target);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                boolean value = shared.getBoolean("text_shadow1", false);
//                sp.setSummary(String.valueOf(value));
                sp.setChecked(value);
            } else {
                sp.setChecked(sharedPreferences.getBoolean("text_shadow", false));
            }
        }else if (target.equals("color_bg")) {
            ListPreference sp = (ListPreference) getPreferenceManager().findPreference(target);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            sp.setSummary(sp.getEntry());

            if (firstLoad) {
                SharedPreferences shared = getSharedPreferences("userInfo", 0);
                String value = shared.getString("color_bg1", "No background");
//                sp.setSummary(String.valueOf(value));
                sp.setValue(value);
            } else {

            }
        }

    }

    private int getColor(CharSequence charSequence) {
        charSequence = charSequence.toString().replace(" ", "").toLowerCase();
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_black).replace(" ", "").toLowerCase())) {
            return -16777216;
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_white).replace(" ", "").toLowerCase())) {
            return -1;
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_gray).replace(" ", "").toLowerCase())) {
            return -7829368;
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_brown).replace(" ", "").toLowerCase())) {
            return Color.rgb(139, 69, 19);
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_red).replace(" ", "").toLowerCase())) {
            return -65536;
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_orange).replace(" ", "").toLowerCase())) {
            return Color.rgb(255, 140, 0);
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_yellow).replace(" ", "").toLowerCase())) {
            return -256;
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_green).replace(" ", "").toLowerCase())) {
            return Color.rgb(34, 139, 34);
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_blue).replace(" ", "").toLowerCase())) {
            return -16776961;
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_purple).replace(" ", "").toLowerCase())) {
            return Color.rgb(147, 112, 219);
        }
        if (charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_hot_pink).replace(" ", "").toLowerCase())) {
            return Color.rgb(255, 105, 180);
        }
        return charSequence.equals(AppUtils.getStringByLocal(this, R.string.label_deep_pink).replace(" ", "").toLowerCase()) ? Color.rgb(255, 20, 147) : -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1234) {
            mIsClicked = false;
            if (data == null)
                return;
            String app_name = data.getStringExtra("app_name");
            String app_class = data.getStringExtra("app_class");
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            //click_app_name = app_name;
            click_app_class = app_class;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(APPCLICK, app_name);
            editor.apply();
            configure.setId(1);
            configure.setOpacity(sharedPreferences.getInt("seek_bar", 100));
            configure.setHour12(sharedPreferences.getBoolean("12hour", true));
            configure.setAmpm(sharedPreferences.getBoolean("ampm", false));
            configure.setShadow(sharedPreferences.getBoolean("text_shadow", false));
            configure.setTimeColor(sharedPreferences.getString("color_time", "White"));
            configure.setWeekColor(sharedPreferences.getString("color_week", "White"));
            configure.setDateColor(sharedPreferences.getString("color_date", "White"));
            configure.setBackColor(sharedPreferences.getString("color_bg", "No background"));
            configure.setLanguage(sharedPreferences.getString("language", "English"));

            configure.setAppName(getString(R.string.label_app_name));
            configure.setAppClass(click_app_class);

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(configure);
                }
            });

            Intent intent = new Intent(this, ClockWidget.class);
            intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), ClockWidget.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);

            Preference lt = getPreferenceManager().findPreference(APPCLICK);
            lt.setSummary(app_name);
        }

    }

    private void ifHuaweiAlert() {
        final SharedPreferences settings = getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                View checkBoxView = View.inflate(this, R.layout.checkbox, null);
                CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
                checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // Save to shared preferences
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();

                    }
                });
                checkBox.setText(R.string.dont_show_agian);

                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Huawei Protected Apps")
                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", getString(R.string.app_name)))
                        .setView(checkBoxView)
                        .setPositiveButton("Protected Apps", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                cmd += " --user " + getUserSerial();
            }
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService(Context.USER_SERVICE);
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }
}
