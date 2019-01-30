package com.victory.clokwidget.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.victory.clokwidget.utils.UIUtils;

public class ReadMePreference extends Preference {
    public ReadMePreference(Context context) {
        super(context);
    }

    public ReadMePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadMePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onClick() {
        UIUtils.showReadMeDialog(getContext());
    }
}
