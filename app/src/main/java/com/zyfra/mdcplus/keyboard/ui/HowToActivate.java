package com.zyfra.mdcplus.keyboard.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class HowToActivate extends Activity {
    private PackageManager pm;

    private TextView tvName;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(2130903040);//xml
        this.pm = getPackageManager();
        this.tvName = (TextView)findViewById(2131165188);//xml
        try {
            PackageInfo packageInfo = this.pm.getPackageInfo(getPackageName(), 0);
            this.tvName.setText(this.pm.getApplicationLabel(packageInfo.applicationInfo) + " v" + packageInfo.versionName);
            return;
        } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
            this.tvName.setText(2131427331);//xml
            nameNotFoundException.printStackTrace();
            return;
        }
    }
}
