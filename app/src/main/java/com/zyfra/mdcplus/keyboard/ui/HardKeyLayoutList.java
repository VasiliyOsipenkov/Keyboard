package com.zyfra.mdcplus.keyboard.ui;

import android.app.ExpandableListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import com.zyfra.mdcplus.keyboard.KeyboardSettings;
import com.zyfra.mdcplus.keyboard.R;
import com.zyfra.mdcplus.keyboard.XMLHelper;
import com.zyfra.mdcplus.keyboard.model.KeyLayoutInfo;

public class HardKeyLayoutList extends ExpandableListActivity {
    private static final boolean DEBUG = false;

    private static final int GROUP_BUILTIN = 1;

    private static final int GROUP_DATA = 0;

    private static final int GROUP_SDCARD = 2;

    private static final String TAG = HardKeyLayoutList.class.getSimpleName();

    private SparseArray<String> groups = new SparseArray();

    private SparseArray<ArrayList<KeyLayoutInfo>> items = new SparseArray();

    private ArrayList<KeyLayoutInfo> layouts = new ArrayList<KeyLayoutInfo>();

    private MyExpandableListAdapter mAdapter;

    private SharedPreferences settings;

    private FilenameFilter xmlFilenameFilter = new FilenameFilter() {
        public boolean accept(File param1File, String param1String) {
            return param1String.toLowerCase().endsWith(".xml");
        }
    };

    private static void copyFile(Context paramContext, String paramString1, String paramString2) {
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(paramString1));
            FileOutputStream fileOutputStream = paramContext.openFileOutput(paramString2, 1);//ХЗ
            byte[] arrayOfByte = new byte[1024];
            while (true) {
                int i = fileInputStream.read(arrayOfByte);
                if (i > 0) {
                    fileOutputStream.write(arrayOfByte, 0, i);
                    continue;
                }
                fileInputStream.close();
                fileOutputStream.close();
                return;
            }
        } catch (FileNotFoundException fileNotFoundException) {
            return;
        } catch (IOException iOException) {
            return;
        }
    }

    public static void expandGroup(ExpandableListView paramExpandableListView, boolean paramBoolean) {
        ExpandableListAdapter expandableListAdapter = paramExpandableListView.getExpandableListAdapter();
        if (expandableListAdapter != null)
            for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
                if (paramBoolean) {
                    paramExpandableListView.expandGroup(i);
                } else {
                    paramExpandableListView.collapseGroup(i);
                }
            }
    }

    public boolean onChildClick(ExpandableListView paramExpandableListView, View paramView, int paramInt1, int paramInt2, long paramLong) {
        KeyLayoutInfo keyLayoutInfo = this.mAdapter.getChild(paramInt1, paramInt2);
        if (keyLayoutInfo.layoutID != null && !keyLayoutInfo.layoutID.equals("")) {
            if (keyLayoutInfo.layoutID.startsWith("/"))
                copyFile((Context)this, keyLayoutInfo.layoutID, "current.xml");
            SharedPreferences.Editor editor = this.settings.edit();
            editor.putString("pref_hard_layout", keyLayoutInfo.layoutID);
            editor.commit();
            finish();
        }
        return true;
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        this.settings = PreferenceManager.getDefaultSharedPreferences((Context)this);
        String str = KeyboardSettings.getDefaultHardLayout((Context)this, this.settings);
        boolean bool1 = false;
        boolean bool2 = false;
        if (str.startsWith("/data/data")) {
            ArrayList<KeyLayoutInfo> arrayList1 = new ArrayList();
            try {
                KeyLayoutInfo keyLayoutInfo = XMLHelper.getLayoutInfo(str, new FileInputStream(str));
                keyLayoutInfo.isChecked = true;
                arrayList1.add(keyLayoutInfo);
                bool2 = true;
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
            bool1 = bool2;
            if (arrayList1.size() > 0) {
                this.groups.append(0, getString(R.string.hard_layouts_in_data));
                this.items.append(0, arrayList1);
                bool1 = bool2;
            }
        }
        ArrayList<KeyLayoutInfo> arrayList = new ArrayList();
        bool2 = bool1;
        try {
            String[] arrayOfString = getAssets().list("hard");
            bool2 = bool1;
            int j = arrayOfString.length;
            int i = 0;
            while (true) {
                bool2 = bool1;
                if (i < j) {
                    String str1 = arrayOfString[i];
                    bool2 = bool1;
                    KeyLayoutInfo keyLayoutInfo = XMLHelper.getLayoutInfo(str1, getAssets().open("hard/" + str1));
                    boolean bool = bool1;
                    if (!bool1) {
                        bool = bool1;
                        bool2 = bool1;
                        if (str.equals(keyLayoutInfo.layoutID)) {
                            bool2 = bool1;
                            keyLayoutInfo.isChecked = true;
                            bool = true;
                        }
                    }
                    bool2 = bool;
                    arrayList.add(keyLayoutInfo);
                    i++;
                    bool1 = bool;
                    continue;
                }
                break;
            }
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    public class MyExpandableListAdapter extends BaseExpandableListAdapter {
        private SparseArray<ArrayList<KeyLayoutInfo>> mChildren;

        private SparseArray<String> mGroups;

        MyExpandableListAdapter(SparseArray<String> param1SparseArray, SparseArray<ArrayList<KeyLayoutInfo>> param1SparseArray1) {
            SparseArray<String> sparseArray1 = param1SparseArray;
            if (param1SparseArray == null)
                sparseArray1 = new SparseArray();
            this.mGroups = sparseArray1;
            SparseArray<ArrayList<KeyLayoutInfo>> sparseArray = param1SparseArray1;
            if (param1SparseArray1 == null)
                sparseArray = new SparseArray();
            this.mChildren = sparseArray;
        }

        public KeyLayoutInfo getChild(int param1Int1, int param1Int2) {
            param1Int1 = this.mChildren.keyAt(param1Int1);
            return ((ArrayList<KeyLayoutInfo>)this.mChildren.get(param1Int1)).get(param1Int2);
        }

        public long getChildId(int param1Int1, int param1Int2) {
            return param1Int2;
        }

        public View getChildView(int param1Int1, int param1Int2, boolean param1Boolean, View param1View, ViewGroup param1ViewGroup) {
            KeyLayoutInfo keyLayoutInfo = getChild(param1Int1, param1Int2);
            View view = param1View;
            if (param1View == null)
                view = HardKeyLayoutList.this.getLayoutInflater().inflate(R.layout.simple_list_item_single_choice, null);
            CheckedTextView checkedTextView = (CheckedTextView)view;
            checkedTextView.setText(keyLayoutInfo.layoutName);
            checkedTextView.setChecked(keyLayoutInfo.isChecked);
            return view;
        }

        public int getChildrenCount(int param1Int) {
            param1Int = this.mChildren.keyAt(param1Int);
            return ((ArrayList)this.mChildren.get(param1Int)).size();
        }

        public Object getGroup(int param1Int) {
            Log.d(HardKeyLayoutList.TAG, "getGroup: " + param1Int);
            return this.mGroups.get(this.mGroups.keyAt(param1Int));
        }

        public int getGroupCount() {
            return this.mGroups.size();
        }

        public long getGroupId(int param1Int) {
            return param1Int;
        }

        public View getGroupView(int param1Int, boolean param1Boolean, View param1View, ViewGroup param1ViewGroup) {
            View view = param1View;
            if (param1View == null)
                view = HardKeyLayoutList.this.getLayoutInflater().inflate(R.layout.item_hard_layout_group, null);
            ((TextView)view).setText(getGroup(param1Int).toString());
            return view;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int param1Int1, int param1Int2) {
            return true;
        }

        public void onGroupCollapsed(int param1Int) {
            HardKeyLayoutList.this.getExpandableListView().expandGroup(param1Int);
        }
    }
}
