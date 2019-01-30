package com.victory.clokwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.victory.clokwidget.utils.Intents;
import com.victory.clokwidget.utils.LoadingHandler;

public class ConfigureActivity extends Activity {

    private RadioGroup radio_group;
    private Button select_language;
    private TextView text_value;
    private SeekBar seekbar_trans;

    private CheckBox check_lang_eng, check_lang_span, check_shadow, check_format, check_ampm;
    private View layout_ampm;
    private TextView text_apps;
    private int CurrentLanguage = 0;
    AlertDialog alertDialog1;
    SensorRestarterBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        initView();

        Globals._loadingHandler = new LoadingHandler(this);
        CurrentLanguage = AppUtils.getCurrentLang(this);


        AppUtils.setCurrentLang(this, CurrentLanguage);
        AppUtils.updateLocale(this);

    }

    void initView() {

        text_value = (TextView) findViewById(R.id.text_value);
        seekbar_trans = (SeekBar) findViewById(R.id.seekbar_trans);

        seekbar_trans.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                updateProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar_trans.setProgress(AppUtils.getTransPercent(this));

        View button_ok = findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View layout_select_app = findViewById(R.id.layout_select_app);
        layout_select_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSelectApp();
            }
        });

        text_apps = (TextView) findViewById(R.id.text_apps);
        text_apps.setText(AppUtils.getAppName(this));

        check_shadow = (CheckBox) findViewById(R.id.check_shadow);
        check_shadow.setChecked(AppUtils.isShadow(this));
        check_shadow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppUtils.setIsShadow(ConfigureActivity.this, b);
                updateWidget();
            }
        });

        check_format = (CheckBox) findViewById(R.id.check_format);
        check_format.setChecked(AppUtils.isOption24hour(this));
        check_format.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppUtils.setOption24hour(ConfigureActivity.this, b);
                refreshAMPM();
                updateWidget();
            }
        });

        //Temporarily hided
        radio_group = (RadioGroup) findViewById(R.id.radio_group);
        if(CurrentLanguage == Globals.Lang_English)
            radio_group.check(R.id.radio_lang_english);
        else if(CurrentLanguage == Globals.Lang_Spanish)
            radio_group.check(R.id.radio_lang_spanish);
        else if(CurrentLanguage == Globals.Lang_Russian)
            radio_group.check(R.id.radio_lang_russian);
        else if(CurrentLanguage == Globals.Lang_Portuguese)
            radio_group.check(R.id.radio_lang_portuguese);

        select_language = (Button) findViewById(R.id.select_language);
        if(CurrentLanguage == Globals.Lang_English)
            select_language.setText(getResources().getString(R.string.lang_english));
        else if(CurrentLanguage == Globals.Lang_Spanish)
            select_language.setText(getResources().getString(R.string.lang_spanish));
        else if(CurrentLanguage == Globals.Lang_Russian)
            select_language.setText(getResources().getString(R.string.lang_russian));
        else if(CurrentLanguage == Globals.Lang_Portuguese)
            select_language.setText(getResources().getString(R.string.lang_portugese));


        select_language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAlertDialogWithRadioButtonGroup();
            }
        });


        layout_ampm = findViewById(R.id.layout_ampm);
        check_ampm  = (CheckBox) findViewById(R.id.check_ampm);
        check_ampm.setChecked(AppUtils.isOptionAmpm(this));
        check_ampm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppUtils.setOptionAmpm(ConfigureActivity.this, b);
                updateWidget();
            }
        });

        refreshAMPM();
    }
    public void CreateAlertDialogWithRadioButtonGroup(){

        CharSequence[] values = {getResources().getString(R.string.lang_english),
                getResources().getString(R.string.lang_spanish),
                getResources().getString(R.string.lang_russian),
                getResources().getString(R.string.lang_portugese)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.label_select_the_language));

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch(item)
                {
                    case 0:
                        select_language.setText(getResources().getString(R.string.lang_english));
                        CurrentLanguage = Globals.Lang_English;
                        clickLang(CurrentLanguage);
                        break;
                    case 1:
                        select_language.setText(getResources().getString(R.string.lang_spanish));
                        CurrentLanguage = Globals.Lang_Spanish;
                        clickLang(CurrentLanguage);
                        break;
                    case 2:
                        select_language.setText(getResources().getString(R.string.lang_russian));
                        CurrentLanguage = Globals.Lang_Russian;
                        clickLang(CurrentLanguage);
                        break;
                    case 3:
                        select_language.setText(getResources().getString(R.string.lang_portugese));
                        CurrentLanguage = Globals.Lang_Portuguese;
                        clickLang(CurrentLanguage);
                        break;
                }
                alertDialog1.dismiss();
            }
        });
        alertDialog1 = builder.create();
        alertDialog1.show();

    }

    void refreshAMPM() {
        boolean is24Hours = AppUtils.isOption24hour(this);

        if (is24Hours) {
            layout_ampm.setVisibility(View.GONE);
        } else {
            layout_ampm.setVisibility(View.VISIBLE);
        }
    }

    void clickSelectApp() {

        Globals._loadingHandler.showLoading();
        Intent intent = new Intent(this, ApkListActivity.class);
        startActivityForResult(intent, REQUEST_APP_PICK);
    }

    void updateProgress(int progress) {
        text_value.setText((progress) + "");
        AppUtils.setTransPercent(ConfigureActivity.this, progress);
        updateWidget();
    }
    void clickLang(int selectedLanguage) {

        AppUtils.setCurrentLang(this, selectedLanguage);
        AppUtils.updateLocale(this);

        updateWidget();
        configurationChanged();

    }
    void configurationChanged() {
        setContentView(R.layout.activity_configure);
        initView();
    }

    void updateWidget() {
//        Intent intent = new Intent(com.victory.clokwidget.ClockWidgetSmall.CLOCK_WIDGET_UPDATE);
//        sendBroadcast(intent);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//        startService(new Intent(this, UpdateServiceOrigin.class));
        sendBroadcast(new Intent(Intents.ACTION_CLOCK_SETTINGS_CHANGED));
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_lang_english:
                if (checked){
                    CurrentLanguage = Globals.Lang_English;
                    clickLang(CurrentLanguage);
                }
                break;
            case R.id.radio_lang_spanish:
                if (checked){
                    CurrentLanguage = Globals.Lang_Spanish;
                    clickLang(CurrentLanguage);

                }
                break;
            case R.id.radio_lang_russian:
                if (checked){
                    CurrentLanguage = Globals.Lang_Russian;
                    clickLang(CurrentLanguage);

                }
                break;
            case R.id.radio_lang_portuguese:
                if (checked){
                    CurrentLanguage = Globals.Lang_Portuguese;
                    clickLang(CurrentLanguage);

                }
                break;
        }
    }

    final int REQUEST_APP_PICK = 2;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == REQUEST_APP_PICK) {
                String app_name = data.getStringExtra("app_name");
                String app_class = data.getStringExtra("app_class");
                AppUtils.setAppName(this, app_name);
                AppUtils.setAppClass(this, app_class);
                text_apps.setText(app_name);
                updateWidget();
            }
        }
    }
}
