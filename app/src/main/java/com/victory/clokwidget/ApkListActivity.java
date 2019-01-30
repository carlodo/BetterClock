package com.victory.clokwidget;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ApkListActivity extends Activity
        implements OnItemClickListener {

    PackageManager packageManager;
    ListView apkList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apklist);

        packageManager = getPackageManager();
        List<PackageInfo> packageList = packageManager
                .getInstalledPackages(PackageManager.GET_META_DATA);

        List<PackageInfo> packageList1 = new ArrayList<>();

        /*To filter out System apps*/
        for(PackageInfo pi : packageList) {
            boolean b = isSystemPackage(pi);
//            if(!b) {
//                packageList1.add(pi);
//            }
            Intent intent = packageManager.getLaunchIntentForPackage(pi.applicationInfo.packageName);
            if(intent != null)
                packageList1.add(pi);

        }
        Collections.sort(packageList1, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo o1, PackageInfo o2) {
                String s1 = (String)packageManager.getApplicationLabel(o1.applicationInfo);
                String s2 = (String)packageManager.getApplicationLabel(o2.applicationInfo);
                return s1.compareToIgnoreCase(s2);
            }
        });

//        Globals._loadingHandler.hideLoading();
        apkList = (ListView) findViewById(R.id.applist);
        apkList.setAdapter(new ApkAdapter(this, packageList1, packageManager));

        apkList.setOnItemClickListener(this);
    }

    /**
     * Return whether the given PackgeInfo represents a system package or not.
     * User-installed packages (Market or otherwise) should not be denoted as
     * system packages.
     *
     * @param pkgInfo
     * @return boolean
     */
    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long row) {

        PackageInfo packageInfo = (PackageInfo) parent
                .getItemAtPosition(position);

        //Intent appInfo = new Intent(getApplicationContext(), ApkInfo.class);
        //startActivity(appInfo);

        Intent intent = new Intent();

        intent.putExtra("app_name", getPackageManager().getApplicationLabel(packageInfo.applicationInfo).toString());
        intent.putExtra("app_class", packageInfo.applicationInfo.packageName);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.activity_apk_list, menu);
        return true;
    }
}