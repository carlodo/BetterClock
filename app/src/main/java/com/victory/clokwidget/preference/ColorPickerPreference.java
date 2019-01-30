package com.victory.clokwidget.preference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.victory.clokwidget.preference.ColorPickerDialog.OnColorChangedListener;

public class ColorPickerPreference extends Preference implements OnPreferenceClickListener, OnColorChangedListener{
    private static final String androidns = "http://schemas.android.com/apk/res/android";
    private boolean mAlphaSliderEnabled = false;
    int mDefaultValue = -16777216;
    private float mDensity = 0.0f;
    private int mValue = -16777216;
    View mView;

    public ColorPickerPreference(Context context) {
        super(context);
        init(context, null);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        this.mDensity = getContext().getResources().getDisplayMetrics().density;
        setOnPreferenceClickListener(this);
        if (attrs != null) {
            String defaultValue = attrs.getAttributeValue(androidns, "defaultValue");
            if (defaultValue.startsWith("#")) {
                try {
                    this.mDefaultValue = convertToColorInt(defaultValue);
                } catch (NumberFormatException e) {
                    Log.e("ColorPickerPreference", "Wrong color: " + defaultValue);
                    this.mDefaultValue = convertToColorInt("#FF000000");
                }
            } else {
                int resourceId = attrs.getAttributeResourceValue(androidns, "defaultValue", 0);
                if (resourceId != 0) {
                    this.mDefaultValue = context.getResources().getInteger(resourceId);
                }
            }
            this.mAlphaSliderEnabled = attrs.getAttributeBooleanValue(null, "alphaSlider", false);
        }
        this.mValue = this.mDefaultValue;
    }

    protected void onBindView(View view) {
        super.onBindView(view);
        this.mView = view;
        setPreviewColor();
    }

    private void setPreviewColor() {
        if (this.mView != null) {
            ImageView iView = new ImageView(getContext());
            LinearLayout widgetFrameView =  (LinearLayout) this.mView.findViewById(android.R.id.widget_frame);
            if (widgetFrameView != null) {
                widgetFrameView.setPadding(widgetFrameView.getPaddingLeft(), widgetFrameView.getPaddingTop(), (int) (this.mDensity * 8.0f), widgetFrameView.getPaddingBottom());
                int count = widgetFrameView.getChildCount();
                if (count > 0) {
                    widgetFrameView.removeViews(0, count);
                }
                widgetFrameView.addView(iView);
                iView.setBackgroundDrawable(new AlphaPatternDrawable((int) (5.0f * this.mDensity)));
                iView.setImageBitmap(getPreviewBitmap());
            }
        }
    }

    private Bitmap getPreviewBitmap() {
        int d = (int) (this.mDensity * 31.0f);
        int color = getValue();
        Bitmap bm = Bitmap.createBitmap(d, d, Config.ARGB_8888);
        int w = bm.getWidth();
        int h = bm.getHeight();
        int c = color;
        int i = 0;
        while (i < w) {
            int j = i;
            while (j < h) {
                if (i <= 1 || j <= 1 || i >= w - 2 || j >= h - 2) {
                    c = -7829368;
                } else {
                    c = color;
                }
                bm.setPixel(i, j, c);
                if (i != j) {
                    bm.setPixel(j, i, c);
                }
                j++;
            }
            i++;
        }
        return bm;
    }

    public int getValue() {
        try {
            if (isPersistent()) {
                this.mValue = getPersistedInt(this.mDefaultValue);
            }
        } catch (ClassCastException e) {
            this.mValue = this.mDefaultValue;
        }
        return this.mValue;
    }

    public void onColorChanged(int color) {
        Log.d("coooo", String.valueOf(color));
        if (isPersistent()) {
            persistInt(color);
        }
        this.mValue = color;
        setPreviewColor();
        try {
            getOnPreferenceChangeListener().onPreferenceChange(this, Integer.valueOf(color));
        } catch (NullPointerException e) {
        }
    }

    public boolean onPreferenceClick(Preference preference) {
        ColorPickerDialog picker = new ColorPickerDialog(getContext(), getValue());
        picker.setOnColorChangedListener(this);
        if (this.mAlphaSliderEnabled) {
            picker.setAlphaSliderVisible(true);
        }
        picker.show();
        return false;
    }

    public void setAlphaSliderEnabled(boolean enable) {
        this.mAlphaSliderEnabled = enable;
    }

    public static String convertToARGB(int color) {
        String alpha = Integer.toHexString(Color.alpha(color));
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));
        if (alpha.length() == 1) {
            alpha = "0" + alpha;
        }
        if (red.length() == 1) {
            red = "0" + red;
        }
        if (green.length() == 1) {
            green = "0" + green;
        }
        if (blue.length() == 1) {
            blue = "0" + blue;
        }
        return "#" + alpha + red + green + blue;
    }

    public static int convertToColorInt(String argb) throws NumberFormatException {
        if (argb.startsWith("#")) {
            argb = argb.replace("#", "");
        }
        int alpha = 0;
        int red = 0;
        int green = 0;
        int blue = 0;
        if (argb.length() == 8) {
            alpha = Integer.parseInt(argb.substring(0, 2), 16);
            red = Integer.parseInt(argb.substring(2, 4), 16);
            green = Integer.parseInt(argb.substring(4, 6), 16);
            blue = Integer.parseInt(argb.substring(6, 8), 16);
        } else if (argb.length() == 6) {
            alpha = 255;
            red = Integer.parseInt(argb.substring(0, 2), 16);
            green = Integer.parseInt(argb.substring(2, 4), 16);
            blue = Integer.parseInt(argb.substring(4, 6), 16);
        }
        return Color.argb(alpha, red, green, blue);
    }
}
