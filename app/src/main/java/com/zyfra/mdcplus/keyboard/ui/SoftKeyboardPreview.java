package com.zyfra.mdcplus.keyboard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SoftKeyboardPreview extends Activity {
    private ArrayList<String> activeKeyboards = new ArrayList<String>();

    private KeyAdapter adapter;

    CompoundButton.OnCheckedChangeListener checkedlistener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton param1CompoundButton, boolean param1Boolean) {
            if (!(param1CompoundButton.getTag() instanceof Integer))
                return;
            Integer integer = (Integer)param1CompoundButton.getTag();
            (SoftKeyboardPreview.this.adapter.getItem(integer.intValue())).checked = param1Boolean;
        }
    };

    private LatinKeyboardView keyboardView;

    private ArrayList<SoftKeyboardLayout> keyboardsList = new ArrayList<SoftKeyboardLayout>();

    private ListView lvSoftKeyboards;

    private final String packagename = "ru.androidteam.rukeyboard";

    private Resources r;

    private SharedPreferences settings;

    private void createKeyboardsList() {
        String[] arrayOfString1 = this.r.getStringArray(2131099648);
        String[] arrayOfString2 = this.r.getStringArray(2131099649);
        String[] arrayOfString3 = this.r.getStringArray(2131099650);
        int j = arrayOfString1.length;
        for (int i = 0; i < j; i++)
            this.keyboardsList.add(new SoftKeyboardLayout(arrayOfString1[i], arrayOfString2[i], arrayOfString3[i]));
    }

    void loadActiveKeyboardList() {
        String[] arrayOfString = this.settings.getString("key_softkeyboard_list", getString(2131427329)).split(" ");
        this.activeKeyboards.clear();
        int j = arrayOfString.length;
        for (int i = 0; i < j; i++) {
            String str = arrayOfString[i];
            this.activeKeyboards.add(str);
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(2130903044);
        this.r = getResources();
        this.settings = PreferenceManager.getDefaultSharedPreferences((Context)this);
        this.keyboardView = (LatinKeyboardView)findViewById(2131165208);
        this.keyboardView.setOnClickListener(null);
        this.keyboardView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View param1View, MotionEvent param1MotionEvent) {
                return true;
            }
        });
        loadActiveKeyboardList();
        createKeyboardsList();
        this.adapter = new KeyAdapter((Context)this, this.keyboardsList);
        this.lvSoftKeyboards = (ListView)findViewById(2131165207);
        this.lvSoftKeyboards.setAdapter((ListAdapter)this.adapter);
        this.lvSoftKeyboards.setOnItemClickListener(this.adapter);
        this.lvSoftKeyboards.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
                SoftKeyboardPreview.this.setKeboard(param1Int);
            }

            public void onNothingSelected(AdapterView<?> param1AdapterView) {}
        });
        this.lvSoftKeyboards.setSelection(0);
        this.lvSoftKeyboards.setItemsCanFocus(true);
        setKeboard(0);
    }

    protected void onPause() {
        saveActiveKeyboardList();
        super.onPause();
    }

    void saveActiveKeyboardList() {
        StringBuilder stringBuilder = new StringBuilder();
        int j = this.adapter.getCount();
        for (int i = 0; i < j; i++) {
            SoftKeyboardLayout softKeyboardLayout = this.adapter.getItem(i);
            if (softKeyboardLayout.checked)
                stringBuilder.append(softKeyboardLayout.value + " ");
        }
        SharedPreferences.Editor editor = this.settings.edit();
        editor.putString("key_softkeyboard_list", stringBuilder.toString());
        editor.commit();
    }

    void setKeboard(int paramInt) {
        SoftKeyboardLayout softKeyboardLayout = this.adapter.getItem(paramInt);
        if (softKeyboardLayout.keyboard == null)
            softKeyboardLayout.keyboard = new LatinKeyboard(getApplicationContext(), softKeyboardLayout.xmlRes, 2131165223);
        this.keyboardView.setKeyboard((Keyboard)softKeyboardLayout.keyboard);
    }

    private class KeyAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
        protected final LayoutInflater mInflater;

        protected List<SoftKeyboardPreview.SoftKeyboardLayout> mList;

        public KeyAdapter(Context param1Context, List<SoftKeyboardLayout> param1List) {
            this.mInflater = (LayoutInflater)param1Context.getSystemService("layout_inflater");
            this.mList = param1List;
        }

        public int getCount() {
            return (this.mList != null) ? this.mList.size() : 0;
        }

        public SoftKeyboardPreview.SoftKeyboardLayout getItem(int param1Int) {
            return (this.mList != null) ? this.mList.get(param1Int) : null;
        }

        public long getItemId(int param1Int) {
            return param1Int;
        }

        public View getView(int param1Int, View param1View, ViewGroup param1ViewGroup) {
            if (param1View == null) {
                param1View = this.mInflater.inflate(2130903048, null);
                SoftKeyboardPreview.ViewHolder viewHolder1 = new SoftKeyboardPreview.ViewHolder();
                viewHolder1.name = (TextView)param1View.findViewById(2131165188);
                viewHolder1.checked = (CheckBox)param1View.findViewById(2131165214);
                viewHolder1.checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton param2CompoundButton, boolean param2Boolean) {
                        (SoftKeyboardPreview.KeyAdapter.this.getItem(((Integer)param2CompoundButton.getTag()).intValue())).checked = param2Boolean;
                    }
                });
                viewHolder1.flag = (ImageView)param1View.findViewById(2131165215);
                param1View.setTag(viewHolder1);
                viewHolder1.checked.setTag(Integer.valueOf(param1Int));
                SoftKeyboardPreview.SoftKeyboardLayout softKeyboardLayout1 = this.mList.get(param1Int);
                viewHolder1.name.setText(softKeyboardLayout1.name);
                viewHolder1.checked.setChecked(softKeyboardLayout1.checked);
                viewHolder1.flag.setImageDrawable(softKeyboardLayout1.icon);
                return param1View;
            }
            SoftKeyboardPreview.ViewHolder viewHolder = (SoftKeyboardPreview.ViewHolder)param1View.getTag();
            viewHolder.checked.setTag(Integer.valueOf(param1Int));
            SoftKeyboardPreview.SoftKeyboardLayout softKeyboardLayout = this.mList.get(param1Int);
            viewHolder.name.setText(softKeyboardLayout.name);
            viewHolder.checked.setChecked(softKeyboardLayout.checked);
            viewHolder.flag.setImageDrawable(softKeyboardLayout.icon);
            return param1View;
        }

        public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
            if (this.mList.size() == 0)
                return;
            SoftKeyboardPreview.this.setKeboard(param1Int);
            param1AdapterView.requestFocus();
            param1AdapterView.setSelection(param1Int);
        }
    }

    class null implements CompoundButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton param1CompoundButton, boolean param1Boolean) {
            (this.this$1.getItem(((Integer)param1CompoundButton.getTag()).intValue())).checked = param1Boolean;
        }
    }

    class SoftKeyboardLayout {
        boolean checked;

        Drawable icon;

        LatinKeyboard keyboard = null;

        String name;

        String value;

        int xmlRes;

        SoftKeyboardLayout(String param1String1, String param1String2, String param1String3) {
            this(param1String1, param1String2, false);
            this.xmlRes = SoftKeyboardPreview.this.r.getIdentifier(param1String2, "xml", "ru.androidteam.rukeyboard");//fix
            this.icon = SoftKeyboardPreview.this.r.getDrawable(SoftKeyboardPreview.this.r.getIdentifier(param1String3, "drawable", "ru.androidteam.rukeyboard"));//fix
            if (SoftKeyboardPreview.this.activeKeyboards.contains(param1String2))
                this.checked = true;
        }

        SoftKeyboardLayout(String param1String1, String param1String2, boolean param1Boolean) {
            this.name = param1String1;
            this.checked = param1Boolean;
            this.value = param1String2;
        }
    }

    private class ViewHolder {
        CheckBox checked;

        ImageView flag;

        TextView name;

        private ViewHolder() {}
    }
}
