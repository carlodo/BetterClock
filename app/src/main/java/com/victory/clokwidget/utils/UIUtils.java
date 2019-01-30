package com.victory.clokwidget.utils;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.victory.clokwidget.R;

public class UIUtils {

    public static SharedPreferences prefs;
    static class C00171 implements OnClickListener {
        private final /* synthetic */ Context mContext;

        C00171(Context context) {
            this.mContext = context;
        }

        public void onClick(DialogInterface dialog, int which) {
            UIUtils.showChangeLogDialog(this.mContext);
        }
    }

    public static void showIntroNotification(Context context) {
    }

    public static void showInstructionsDialog(Context context) {
        Builder builder = new Builder(context);
        builder.setIcon(R.drawable.icon_small);
        builder.setTitle("To Install:");
        builder.setMessage(R.string.rules);
        builder.setNeutralButton("OK", null);
        builder.show();
    }

    public static void showReadMeDialog(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.readme, null);
        Builder builder = new Builder(context);
        builder.setIcon(R.drawable.icon_small);
        builder.setTitle(R.string.ChangeTitle);
        builder.setView(layout);
        builder.setNeutralButton("OK", null);
        builder.show();
    }

    public static void showAboutDialog(Context context) {
        View layout = LayoutInflater.from(context).inflate(R.layout.about, null);
        Builder builder = new Builder(context);
        builder.setTitle("About");
        builder.setView(layout);
        builder.setPositiveButton("Thank you", null);
        builder.setNegativeButton("Changes", new C00171(context));
        builder.show();
    }

    public static void sendEmail(Context context) {
        Intent sendIntent = new Intent("android.intent.action.SEND");
        sendIntent.putExtra("android.intent.extra.EMAIL", new String[]{"MaizeLabs@gmail.com"});
        sendIntent.putExtra("android.intent.extra.SUBJECT", "Digital Clock Widget ");
        sendIntent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(sendIntent, "Title"));
    }

    public static boolean changeLogCheck(Context context) {
        try {
            int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            SharedPreferences settings = context.getSharedPreferences("KEY_CHANGELOG_VERSION", 0);
            if (settings.getInt("KEY_CHANGELOG_VERSION_VIEWED", 0) < versionCode) {
                Editor editor = settings.edit();
                editor.putInt("KEY_CHANGELOG_VERSION_VIEWED", versionCode);
                editor.apply();
                showChangeLogDialog(context);
                return true;
            }
        } catch (NameNotFoundException e) {
            Log.w("", e);
        }
        return false;
    }

    public static void showChangeLogDialog(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            Builder builder = new Builder(context);
            builder.setTitle("Ver. " + packageInfo.versionName + " change log");
            builder.setIcon(R.drawable.icon_small);
            builder.setMessage("+ Fixed memory leak issue\n+ Fixed widget text size issue\n-----------------------\nNote: Big update coming soon!");
            builder.setPositiveButton("Thanks!", null);
            builder.setCancelable(false);
            builder.show();
        } catch (NameNotFoundException e) {
            Log.w("", e);
        }
    }
}
