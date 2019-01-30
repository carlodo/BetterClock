package com.victory.clokwidget.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.victory.clokwidget.utils.UIUtils;

public class AboutPreference extends Preference {
    public AboutPreference(Context context) {
        super(context);
    }

    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AboutPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onClick() {
        UIUtils.showAboutDialog(getContext());
    }
}
