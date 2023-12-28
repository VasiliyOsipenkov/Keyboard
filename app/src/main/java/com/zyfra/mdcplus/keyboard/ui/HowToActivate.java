package com.zyfra.mdcplus.keyboard.ui;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.zyfra.mdcplus.keyboard.R;

public class HowToActivate extends Activity {
    private PackageManager pm;

    private TextView tvName;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_how_to_activate);
        this.pm = getPackageManager();
        this.tvName = (TextView)findViewById(R.id.tvName);
        try {
            PackageInfo packageInfo = this.pm.getPackageInfo(getPackageName(), 0);
            this.tvName.setText(this.pm.getApplicationLabel(packageInfo.applicationInfo) + " v" + packageInfo.versionName);
            return;
        } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
            this.tvName.setText(R.string.ime_name);//xml
            nameNotFoundException.printStackTrace();
            return;
        }
    }
}
