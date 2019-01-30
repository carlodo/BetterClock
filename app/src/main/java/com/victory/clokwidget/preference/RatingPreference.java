package com.victory.clokwidget.preference;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;

public class RatingPreference extends Preference {
    public RatingPreference(Context context) {
        super(context);
    }

    public RatingPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatingPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void onClick() {
        getContext().startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.victory.clokwidget")));
    }
}
