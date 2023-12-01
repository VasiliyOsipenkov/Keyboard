package com.zyfra.mdcplus.keyboard.model;

public class KeyLayoutInfo {
    public String authorEmail;

    public String authorName;

    public String authorWebSite;

    public String flag;

    public boolean isChecked;

    public String layoutDescription;

    public String layoutID;

    public String layoutName;

    public int versionCode;

    public String versionName;

    public int compareTo(KeyLayoutInfo paramKeyLayoutInfo) {
        return (this.layoutName == null || paramKeyLayoutInfo == null || paramKeyLayoutInfo.layoutName == null) ? 0 : this.layoutName.compareTo(paramKeyLayoutInfo.layoutName);
    }
}
