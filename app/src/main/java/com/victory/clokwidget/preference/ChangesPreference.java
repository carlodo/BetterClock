package com.victory.clokwidget.preference;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;

import com.victory.clokwidget.utils.UIUtils;

public class ChangesPreference extends Preference {
    public ChangesPreference(Context context) {
        super(context);
    }

    public ChangesPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChangesPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void onClick() {
        UIUtils.showChangeLogDialog(getContext());
    }
}
