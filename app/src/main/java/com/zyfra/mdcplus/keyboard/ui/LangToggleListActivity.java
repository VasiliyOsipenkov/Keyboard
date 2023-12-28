package com.zyfra.mdcplus.keyboard.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zyfra.mdcplus.keyboard.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LangToggleListActivity extends Activity {
    private ArrayAdapter<String> mAdapter;

    private int mCurrentClickedPosition = -1;

    private int mCurrentCustomKeyCode = -1;

    private View mCustomToggleView;

    private List<String> mEntries = new ArrayList<String>();

    private String mLangMyKeyCode;

    private ListView mListView;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View param1View) {// Опять дичь
            switch (param1View.getId()) {
                default:
                    return;
                case 2131165203:
                    LangToggleListActivity.this.finish();
                    return;
                case 2131165204:
                    break;
            }
            int i = LangToggleListActivity.this.mListView.getCheckedItemPosition();
            if (i != -1) {
                String str = LangToggleListActivity.this.mValues.get(i);
                SharedPreferences.Editor editor = LangToggleListActivity.this.settings.edit();
                if (str.equals("custom") && LangToggleListActivity.this.mCurrentCustomKeyCode != -1) {
                    editor.putString("key_lang_toggle", str);
                    editor.putInt("key_lang_toggle_custom", LangToggleListActivity.this.mCurrentCustomKeyCode);
                } else {
                    editor.putString("key_lang_toggle", str);
                }
                editor.commit();
                LangToggleListActivity.this.finish();
                return;
            }
        }
    };

    private List<String> mValues = new ArrayList<String>();

    private SharedPreferences settings;

    private boolean isVisible(View paramView) {
        return (paramView.getVisibility() == 0);
    }

    public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
        int i = paramKeyEvent.getKeyCode();
        if (i == 4 && isVisible(this.mCustomToggleView)) {
            this.mCustomToggleView.setVisibility(8);
            return true;
        }
        if (this.mCustomToggleView.getVisibility() == 0) {
            this.mCurrentCustomKeyCode = i;
            this.mEntries.set(this.mCurrentClickedPosition, this.mLangMyKeyCode + " : " + i);
            this.mCustomToggleView.setVisibility(8);
            this.mAdapter.notifyDataSetChanged();
            return true;
        }
        return super.dispatchKeyEvent(paramKeyEvent);
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.activity_lang_toggle_settings);
        this.mLangMyKeyCode = getString(R.string.lang_toggle_my_keycode);
        this.settings = PreferenceManager.getDefaultSharedPreferences((Context)this);
        this.mListView = (ListView)findViewById(R.id.listView);//xml
        this.mListView.setChoiceMode(1);
        String[] arrayOfString = getResources().getStringArray(R.array.lang_toggle_entries);
        Collections.addAll(this.mEntries, arrayOfString);
        this.mCurrentCustomKeyCode = this.settings.getInt("key_lang_toggle_custom", -1);
        if (this.mCurrentCustomKeyCode == -1) {
            this.mEntries.add(this.mLangMyKeyCode);
        } else {
            this.mEntries.add(this.mLangMyKeyCode + " : " + this.mCurrentCustomKeyCode);
        }
        arrayOfString = getResources().getStringArray(R.array.lang_toggle_values);
        Collections.addAll(this.mValues, arrayOfString);
        this.mValues.add("custom");
        this.mAdapter = new ArrayAdapter((Context)this, 17367055, this.mEntries);//xml нету
        this.mListView.setAdapter((ListAdapter)this.mAdapter);
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> param1AdapterView, View param1View, int param1Int, long param1Long) {
                if (((String)LangToggleListActivity.this.mValues.get(param1Int)).equals("custom")) {
                    LangToggleListActivity.access$102(LangToggleListActivity.this, param1Int);//?
                    LangToggleListActivity.this.mCustomToggleView.setVisibility(0);
                }
            }
        });
        String str = getString(R.string.default_lang_toggle);//xml
        str = this.settings.getString("key_lang_toggle", str);
        int j = this.mValues.size();
        int i = 0;
        while (true) {
            if (i < j)
                if (((String)this.mValues.get(i)).equals(str)) {
                    this.mListView.setItemChecked(i, true);
                } else {
                    i++;
                    continue;
                }
            this.mCustomToggleView = findViewById(R.id.customToggleView);
            this.mCustomToggleView.setVisibility(8);
            findViewById(R.id.cancel).setOnClickListener(this.mOnClickListener);
            findViewById(R.id.ok).setOnClickListener(this.mOnClickListener);
            return;
        }
    }
}
