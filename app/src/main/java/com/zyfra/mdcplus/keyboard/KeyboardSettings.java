package com.zyfra.mdcplus.keyboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import com.zyfra.mdcplus.keyboard.model.KeyLayoutInfo;
import com.zyfra.mdcplus.keyboard.ui.HardKeyLayoutList;
import com.zyfra.mdcplus.keyboard.ui.HardKeyLayoutPreview;
import com.zyfra.mdcplus.keyboard.ui.HowToActivate;
import com.zyfra.mdcplus.keyboard.ui.KeyboardTestActivity;
import com.zyfra.mdcplus.keyboard.ui.LangToggleListActivity;
import com.zyfra.mdcplus.keyboard.ui.SoftKeyboardPreview;

public class KeyboardSettings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEYBOARD_QWERTZ = "keyboard_qwertz";

    public static final String KEY_HARD_KEYBOARD_LIST = "hard_layout_list";

    public static final String KEY_HOW_TO_ACTIVATE = "how_to_activate";

    public static final String KEY_KEYBOARD_TEST = "pref_key_keyboard_test";

    public static final String KEY_KEYHARD_LAYOUT_PREVIEW = "pref_keyhard_layout_preview";

    public static final String KEY_KEYSOFT_LAYOUT_PREVIEW = "pref_key_layout_preview";

    public static final String KEY_SOFT_KEYBOARD_LIST = "key_softkeyboard_list";

    public static final String KEY_SWAP_ZY = "swap_zy";

    public static final String MOTOROLA_MILESTONE_RU = "motorola_milestone_ru";

    public static final String PREF_HARD_LAYOUT = "pref_hard_layout";

    public static final String PREF_KEY_DELAY = "key_delay";

    public static final String PREF_KEY_DELAY_TIME = "key_delay_time";

    public static final String PREF_KEY_EXTERNAL_LAYOUT_PATH = "key_external_layout_path";

    public static final String PREF_KEY_LANG_TOGGLE = "key_lang_toggle";

    public static final String PREF_KEY_LANG_TOGGLE_CUSTOM = "key_lang_toggle_custom";

    public static final String PREF_KEY_SOFT_VIBRATE_TIME = "pref_vibrate_time";

    public static final String PREF_PROGRAM_VERSION = "pref_program_version";

    public static final String PREF_SHOW_STATUS_ICON = "pref_show_status_icon";

    private ListPreference delayTimeList;

    private EditTextPreference editPrefExternalPath;

    private EditTextPreference edtPrefVersion;

    private Preference hardLayoutPreference;

    private Preference mLangToggle;

    private ArrayList<String> mLangToggleEntries = new ArrayList<String>();

    private ArrayList<String> mLangToggleValues = new ArrayList<String>();

    private SharedPreferences mSettings;

    public static String getDefaultHardLayout(Context paramContext, SharedPreferences paramSharedPreferences) {
        String str = paramSharedPreferences.getString("pref_hard_layout", paramContext.getString(R.string.default_hard_keyboard));
        boolean bool = false;
        if (str.startsWith("/")) {
            File file = new File("/data/data/com.zyfra.mdcplus.keyboard/files/current.xml");
            if (file.exists() && file.isFile())
                return file.getAbsolutePath();
            file = new File(str);
            if (!file.exists() || !file.isFile())
                bool = true;
        } else {
            try {
                boolean bool1 = Arrays.<String>asList(paramContext.getAssets().list("hard")).contains(str);
                if (!bool1)
                    bool = true;
                if (bool) {
                    String str1 = paramContext.getString(R.string.default_hard_keyboard);
                    Toast.makeText(paramContext, String.format(paramContext.getString(R.string.hard_layout_cant_be_found), new Object[] { str }), 1).show();
                    paramSharedPreferences.edit().putString("pref_hard_layout", str1).commit();
                    return str1;
                }
            } catch (IOException iOException) {
                iOException.printStackTrace();
                if (bool) {
                    String str1 = paramContext.getString(R.string.default_hard_keyboard);
                    Toast.makeText(paramContext, String.format(paramContext.getString(R.string.hard_layout_cant_be_found), new Object[] { str }), 1).show();
                    paramSharedPreferences.edit().putString("pref_hard_layout", str1).commit();
                    return str1;
                }
            }
            return str;
        }
        if (bool) {
            String str1 = paramContext.getString(R.string.default_hard_keyboard);
            Toast.makeText(paramContext, String.format(paramContext.getString(R.string.hard_layout_cant_be_found), new Object[] { str }), 1).show();
            paramSharedPreferences.edit().putString("pref_hard_layout", str1).commit();
            return str1;
        }
    }

    private void updateDelayTimeSummary() {
        if (this.delayTimeList != null)
            this.delayTimeList.setSummary(this.delayTimeList.getEntry() + "мс");
    }

    private void updateExternalPathSummary() {
        // Byte code:
        //   0: aload_0
        //   1: getfield editPrefExternalPath : Landroid/preference/EditTextPreference;
        //   4: ifnull -> 83
        //   7: aload_0
        //   8: getfield editPrefExternalPath : Landroid/preference/EditTextPreference;
        //   11: invokevirtual getText : ()Ljava/lang/String;
        //   14: astore_2
        //   15: aload_2
        //   16: ifnull -> 33
        //   19: aload_2
        //   20: astore_1
        //   21: aload_2
        //   22: invokevirtual trim : ()Ljava/lang/String;
        //   25: ldc ''
        //   27: invokevirtual equals : (Ljava/lang/Object;)Z
        //   30: ifeq -> 75
        //   33: invokestatic getExternalStorageDirectory : ()Ljava/io/File;
        //   36: invokevirtual getAbsolutePath : ()Ljava/lang/String;
        //   39: astore_1
        //   40: aload_0
        //   41: getfield mSettings : Landroid/content/SharedPreferences;
        //   44: invokeinterface edit : ()Landroid/content/SharedPreferences$Editor;
        //   49: astore_2
        //   50: aload_2
        //   51: ldc 'key_external_layout_path'
        //   53: aload_1
        //   54: invokeinterface putString : (Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;
        //   59: pop
        //   60: aload_2
        //   61: invokeinterface commit : ()Z
        //   66: pop
        //   67: aload_0
        //   68: getfield editPrefExternalPath : Landroid/preference/EditTextPreference;
        //   71: aload_1
        //   72: invokevirtual setText : (Ljava/lang/String;)V
        //   75: aload_0
        //   76: getfield editPrefExternalPath : Landroid/preference/EditTextPreference;
        //   79: aload_1
        //   80: invokevirtual setSummary : (Ljava/lang/CharSequence;)V
        //   83: return
    }

    private void updateLangToggleSummary() {
        String str;
        if (this.mLangToggle != null) {
            str = getString(R.string.default_lang_toggle);
            str = this.mSettings.getString("key_lang_toggle", str);
            if (str.equals("custom")) {
                int k = this.mSettings.getInt("key_lang_toggle_custom", -1);
                this.mLangToggle.setSummary(getString(R.string.lang_toggle_my_keycode) + " : " + k);
                return;
            }
        } else {
            return;
        }
        int j = this.mLangToggleValues.size();
        int i = 0;
        while (true) {
            if (i < j) {
                if (((String)this.mLangToggleValues.get(i)).equals(str)) {
                    this.mLangToggle.setSummary(this.mLangToggleEntries.get(i));
                    return;
                }
                i++;
                continue;
            }
            return;
        }
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        addPreferencesFromResource(R.xml.prefs);
        PackageManager packageManager = getPackageManager();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        this.mSettings = getPreferenceScreen().getSharedPreferences();
        this.edtPrefVersion = (EditTextPreference)preferenceScreen.findPreference("pref_program_version");
        if (this.edtPrefVersion != null)
            try {
                PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
                String str = getString(R.string.version) + " " + packageInfo.versionName;
                this.edtPrefVersion.setTitle(str);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        this.hardLayoutPreference = preferenceScreen.findPreference("hard_layout_list");
        Preference preference = preferenceScreen.findPreference("pref_key_layout_preview");
        if (preference != null)
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference param1Preference) {
                    Intent intent = new Intent();
                    intent.setClass((Context)KeyboardSettings.this, HardKeyLayoutPreview.class);
                    KeyboardSettings.this.startActivity(intent);
                    return true;
                }
            });
        preference = preferenceScreen.findPreference("pref_keyhard_layout_preview");
        if (preference != null)
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference param1Preference) {
                    KeyboardSettings.this.startActivity(new Intent((Context)KeyboardSettings.this, SoftKeyboardPreview.class));
                    return true;
                }
            });
        preference = preferenceScreen.findPreference("pref_key_keyboard_test");
        if (preference != null)
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference param1Preference) {
                    KeyboardSettings.this.startActivity(new Intent((Context)KeyboardSettings.this, KeyboardTestActivity.class));
                    return true;
                }
            });
        preference = preferenceScreen.findPreference("how_to_activate");
        if (preference != null)
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference param1Preference) {
                    KeyboardSettings.this.startActivity(new Intent((Context)KeyboardSettings.this, HowToActivate.class));
                    return true;
                }
            });
        preference = preferenceScreen.findPreference("hard_layout_list");
        if (preference != null)
            preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference param1Preference) {
                    KeyboardSettings.this.startActivity(new Intent((Context)KeyboardSettings.this, HardKeyLayoutList.class));
                    return true;
                }
            });
        this.editPrefExternalPath = (EditTextPreference)preferenceScreen.findPreference("key_external_layout_path");
        updateExternalPathSummary();
        this.mLangToggleEntries.addAll(Arrays.asList(getResources().getStringArray(R.array.lang_toggle_entries)));
        this.mLangToggleValues.addAll(Arrays.asList(getResources().getStringArray(R.array.lang_toggle_values)));
        this.mLangToggle = preferenceScreen.findPreference("key_lang_toggle");
        if (this.mLangToggle != null)
            this.mLangToggle.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference param1Preference) {
                    KeyboardSettings.this.startActivity(new Intent((Context)KeyboardSettings.this, LangToggleListActivity.class));
                    return true;
                }
            });
        this.delayTimeList = (ListPreference)preferenceScreen.findPreference("key_delay_time");
        updateDelayTimeSummary();
    }

    protected void onPause() {
        this.mSettings.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
        updateLangToggleSummary();
        if (this.hardLayoutPreference != null) {
            String str = getDefaultHardLayout((Context)this, this.mSettings);
            KeyLayoutInfo keyLayoutInfo = XMLHelper.getLayoutInfo(getApplicationContext(), str);
            this.hardLayoutPreference.setSummary(keyLayoutInfo.layoutName);
        }
        this.mSettings.registerOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString) {
        if (paramString.equals("key_delay_time")) {
            updateDelayTimeSummary();
            return;
        }
        if (paramString.equals("key_lang_toggle")) {
            updateLangToggleSummary();
            return;
        }
        if (paramString.equals("key_external_layout_path")) {
            updateExternalPathSummary();
            return;
        }
    }
}
