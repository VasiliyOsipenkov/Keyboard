package com.zyfra.mdcplus.keyboard.model;

import java.util.ArrayList;

public class KeyboardLayout {
    public String flag = null;

    public ArrayList<Integer> globalKeys = new ArrayList<Integer>();

    public Key keysMap = Key.createEmptyKey();

    public int langToggleKey = -1;

    private boolean mIsContainsGlobalKeys;

    private boolean mIsContainsUpKeys;

    public ArrayList<Integer> upKeys = new ArrayList<Integer>();

    public boolean isContainsGlobalKeys() {
        return this.mIsContainsGlobalKeys;
    }

    public boolean isContainsUpKeys() {
        return this.mIsContainsUpKeys;
    }

    public void updateCounts() {
        boolean bool1;
        boolean bool2 = true;
        if (this.globalKeys.size() > 0) {
            bool1 = true;
        } else {
            bool1 = false;
        }
        this.mIsContainsGlobalKeys = bool1;
        if (this.upKeys.size() > 0) {
            bool1 = bool2;
        } else {
            bool1 = false;
        }
        this.mIsContainsUpKeys = bool1;
    }
}
