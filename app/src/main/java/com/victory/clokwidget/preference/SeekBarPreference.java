package com.victory.clokwidget.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.victory.clokwidget.R;

import java.util.Locale;

public class SeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener{

    private TextView textValue;
    private SeekBar seekBar;
    public SeekBarPreference(Context context) {
        super(context);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.seekbar, parent, false);
        textValue = view.findViewById(R.id.textValue);
        TextView textTitle = view.findViewById(R.id.textTitle);
        textTitle.setText(getTitle());
        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        SharedPreferences preferences = getSharedPreferences();
        int value = preferences.getInt(getKey(), 100);
        setValue(Integer.parseInt(getSummary().toString()));
        return view;
    }
    public void setValue(int value){
        textValue.setText(String.format(Locale.getDefault(), "%d", value) + "%");
        seekBar.setProgress(value);
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        textValue.setText(String.format(Locale.getDefault(), "%d", i) + "%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        SharedPreferences.Editor editor = getEditor();
        int i = seekBar.getProgress();
        Log.d("BetterClock:", "last progress: " + i);
        editor.putInt(getKey(), i);
        editor.apply();
    }
}
