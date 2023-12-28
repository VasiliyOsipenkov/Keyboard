package com.zyfra.mdcplus.keyboard;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.inputmethodservice.InputMethodService;
import android.media.AudioManager;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
import com.zyfra.mdcplus.keyboard.model.Key;
import com.zyfra.mdcplus.keyboard.model.KeyboardLayout;
import com.zyfra.mdcplus.keyboard.view.KeyboardView;

public class SoftKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private static boolean DEBUG = false;

    private static boolean DEBUG_META = false;

    static final int KEYCODE_ENTER = 10;

    static final int KEYCODE_SPACE = 32;

    private static final String LANG_TOGGLE_ALT_SHIFT = "alt_shift";

    private static final String LANG_TOGGLE_ALT_SPACE = "alt_space";

    private static final String LANG_TOGGLE_CTRL_SHIFT = "ctrl_shift";

    public static final String LANG_TOGGLE_CUSTOM = "custom";

    private static final String LANG_TOGGLE_SHIFT_SPACE = "shift_space";

    private static final int POS_METHOD = 1;

    private static final int POS_SETTINGS = 0;

    private static final String PREF_AUTO_CAP_HARD = "auto_cap_hard";

    private static final String PREF_AUTO_CAP_SOFT = "auto_cap_soft";

    private static final String PREF_SOUND_ON = "sound_on";

    private static final String PREF_VIBRATE_ON = "vibrate_on";

    private static final String PREF_VIBRATE_ON_HARD_TOGGLE = "pref_hard_vibrate_on_toggle";

    private static final int SHOW_NEXT_ALPHA_KEYBOARD = -11;

    private static final String TAG = SoftKeyboard.class.getSimpleName();

    private final float FX_VOLUME = 1.0F;

    private final int META_ACTIVE_ALT = 514;

    private final int META_ACTIVE_SHIFT = 257;

    Configuration curConfig = null;

    private int hardIcon = 0;

    private String hardLayout;

    boolean in_down = false;

    boolean in_up = false;

    private Key keyRoot;

    private LANG_TOGGLE langToggle = LANG_TOGGLE.SHIFT_SPACE;

    private AudioManager mAudioManager;

    private boolean mAutoCapHard;

    private boolean mAutoCapSoft;

    private boolean mAutoSpace;

    private CandidateView mCandidateView;

    private boolean mCapsLock;

    private boolean mCompletionOn;

    private CompletionInfo[] mCompletions;

    private StringBuilder mComposing = new StringBuilder();

    private int mCurrentCustomCode = -1;

    private LatinKeyboardView mInputView;

    private boolean mIsTranslitKeys;

    private boolean mKeyDelay;

    private int mKeyDelayTime;

    private KeyboardSwitcher mKeyboardSwitcher;

    private int mLangToggle = -1;

    private long mLastKeyTime;

    private long mLastShiftTime;

    private long mMetaState;

    private NotificationManager mNotificationManager;

    private AlertDialog mOptionsDialog;

    private boolean mPasswordMode;

    private boolean mPredictionOn;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            SoftKeyboard.this.updateRingerMode();
        }
    };

    private boolean mShowFlag;

    private boolean mSilentMode;

    private boolean mSoundOn;

    private boolean mSwapYZ;

    private long mVibrateDuration;

    private boolean mVibrateOn;

    private boolean mVibrateOnHardToggle;

    private Vibrator mVibrator;

    private String mWordSeparators;

    private KeyboardLayout ruKeyboardLayout;

    private SharedPreferences settings;

    SharedPreferences.OnSharedPreferenceChangeListener sharedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences param1SharedPreferences, String param1String) {
            if (param1String.equals("pref_hard_layout")) {
                SoftKeyboard.this.onInitializeInterface();
                SoftKeyboard.this.updateFlagState();
                return;
            }
            if (param1String.equals("key_softkeyboard_list")) {
                SoftKeyboard.this.mKeyboardSwitcher.loadActiveKeyboardList();
                SoftKeyboard.this.updateFlagState();
                return;
            }
            if (param1String.equals("vibrate_on")) {
                SoftKeyboard.access$302(SoftKeyboard.this, SoftKeyboard.this.settings.getBoolean("vibrate_on", true));
                return;
            }
            if (param1String.equals("pref_vibrate_time")) {
                int i = SoftKeyboard.this.getResources().getInteger(R.integer.vibrate_duration_ms);
                SoftKeyboard.access$502(SoftKeyboard.this, param1SharedPreferences.getInt(param1String, i));
                return;
            }
            if (param1String.equals("auto_cap_soft")) {
                SoftKeyboard.access$602(SoftKeyboard.this, SoftKeyboard.this.settings.getBoolean("auto_cap_soft", true));
                return;
            }
            if (param1String.equals("auto_cap_hard")) {
                SoftKeyboard.access$702(SoftKeyboard.this, SoftKeyboard.this.settings.getBoolean("auto_cap_hard", true));
                return;
            }
            if (param1String.equals("sound_on")) {
                SoftKeyboard.access$802(SoftKeyboard.this, SoftKeyboard.this.settings.getBoolean("sound_on", false));
                return;
            }
            if (param1String.equals("pref_show_status_icon")) {
                SoftKeyboard.access$902(SoftKeyboard.this, SoftKeyboard.this.settings.getBoolean("pref_show_status_icon", true));
                if (SoftKeyboard.this.mKeyboardSwitcher != null)
                    SoftKeyboard.this.mKeyboardSwitcher.showFlag = SoftKeyboard.this.mShowFlag;
                SoftKeyboard.this.updateFlagState();
                return;
            }
            if (param1String.equals("swap_zy")) {
                SoftKeyboard.access$1002(SoftKeyboard.this, SoftKeyboard.this.settings.getBoolean("swap_zy", false));
                return;
            }
            if (param1String.equals("key_lang_toggle")) {
                SoftKeyboard.this.loadLangToggleSettings();
                return;
            }
            SoftKeyboard.this.loadSimpleSettings();
        }
    };

    boolean shiftForDouble = false;

    private void changeKeyboardMode() {
        this.mKeyboardSwitcher.toggleSymbols();
        if (this.mCapsLock && this.mKeyboardSwitcher.isAlphabetMode())
            ((LatinKeyboard)this.mInputView.getKeyboard()).setShiftLocked(this.mCapsLock);
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void changeLayout() {
        int i;
        boolean bool;
        if (!this.mIsTranslitKeys) {
            bool = true;
        } else {
            bool = false;
        }
        this.mIsTranslitKeys = bool;
        if (this.mVibrateOnHardToggle)
            vibrate();
        KeyboardSwitcher keyboardSwitcher = this.mKeyboardSwitcher;
        if (this.mIsTranslitKeys) {
            i = this.hardIcon;
        } else {
            i = R.drawable.us_flag;
        }
        keyboardSwitcher.setKeyboardFlag(i);
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null)
            inputConnection.clearMetaKeyStates(1);
    }

    private void checkToggleCapsLock() {
        if (this.mInputView.getKeyboard().isShifted())
            toggleCapsLock();
    }

    private void commitTyped(InputConnection paramInputConnection) {
        if (DEBUG)
            Log.d(TAG, "commitTyped");
        if (this.mComposing.length() > 0) {
            paramInputConnection.commitText(this.mComposing, this.mComposing.length());
            this.mComposing.setLength(0);
            updateCandidates();
        }
    }

    private void deleteLeftSymbol() {
        getCurrentInputConnection().deleteSurroundingText(1, 0);
    }

    private String getMetaKeysStates(String paramString) {
        int i = MyMetaKeyKeyListener.getMetaState(this.mMetaState, 1);
        int j = MyMetaKeyKeyListener.getMetaState(this.mMetaState, 2);
        int k = MyMetaKeyKeyListener.getMetaState(this.mMetaState, 4);
        return "Meta keys state at " + paramString + "- SHIFT:" + i + ", ALT:" + j + " SYM:" + k + " bits:" + MyMetaKeyKeyListener.getMetaState(this.mMetaState) + " state:" + this.mMetaState;
    }

    private String getWordSeparators() {
        return this.mWordSeparators;
    }

    private void handleBackspace() {
        if (DEBUG)
            Log.d(TAG, "handleBackspace");
        int i = this.mComposing.length();
        if (i > 1) {
            this.mComposing.delete(i - 1, i);
            getCurrentInputConnection().setComposingText(this.mComposing, 1);
        } else if (i > 0) {
            this.mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
        } else {
            keyDownUp(67);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleCharacter(int paramInt, int[] paramArrayOfint) {
        if (DEBUG)
            Log.d(TAG, "+++ handleCharacter +++");
        int i = paramInt;
        if (isInputViewShown()) {
            i = paramInt;
            if (this.mInputView.isShifted())
                i = Character.toUpperCase(paramInt);
        }
        if (isAlphabet(i) && this.mPredictionOn) {
            this.mComposing.append((char)i);
            getCurrentInputConnection().setComposingText(this.mComposing, 1);
        } else {
            getCurrentInputConnection().commitText(String.valueOf((char)i), 1);
        }
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void handleClose() {
        if (DEBUG)
            Log.d(TAG, "handleClose");
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        this.mInputView.closing();
    }

    private void handleSeparator(int paramInt) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null)
            inputConnection.beginBatchEdit();
        sendKeyChar((char)paramInt);
        updateShiftKeyState(getCurrentInputEditorInfo());
        if (inputConnection != null)
            inputConnection.endBatchEdit();
    }

    private void handleShift() {
        if (this.mKeyboardSwitcher.isAlphabetMode()) {
            boolean bool;
            checkToggleCapsLock();
            LatinKeyboardView latinKeyboardView = this.mInputView;
            if (this.mCapsLock || !this.mInputView.isShifted()) {
                bool = true;
            } else {
                bool = false;
            }
            latinKeyboardView.setShifted(bool);
            return;
        }
        this.mKeyboardSwitcher.toggleShift();
    }

    private boolean isAlphabet(int paramInt) {
        if (DEBUG)
            Log.d(TAG, "isAlphabet");
        return Character.isLetter(paramInt);
    }

    private boolean isShifted() {
        byte b = 0;
        EditorInfo editorInfo = getCurrentInputEditorInfo();
        int i = b;
        if (editorInfo != null) {
            i = b;
            if (editorInfo.inputType != 0)
                i = getCurrentInputConnection().getCursorCapsMode(editorInfo.inputType);
        }
        return (i != 0 && this.mAutoCapHard);
    }

    private void keyDownUp(int paramInt) {
        if (DEBUG)
            Log.d(TAG, "keyDownUp key:" + paramInt);
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(0, paramInt));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(1, paramInt));
    }

    private void launchSettings() {
        handleClose();
        Intent intent = new Intent();
        intent.setClass((Context)this, KeyboardSettings.class);
        intent.setFlags(268435456);
        startActivity(intent);
    }

    private void loadLangToggleSettings() {
        String str = getString(R.string.default_lang_toggle);
        str = this.settings.getString("key_lang_toggle", str);
        if (str.equals("alt_shift")) {
            this.langToggle = LANG_TOGGLE.ALT_SHIFT;
            return;
        }
        if (str.equals("ctrl_shift")) {
            this.langToggle = LANG_TOGGLE.CTRL_SHIFT;
            return;
        }
        if (str.equals("alt_space")) {
            this.langToggle = LANG_TOGGLE.ALT_SPACE;
            return;
        }
        if (str.equals("custom")) {
            this.langToggle = LANG_TOGGLE.CUSTOM;
            this.mCurrentCustomCode = this.settings.getInt("key_lang_toggle_custom", -1);
            return;
        }
        this.langToggle = LANG_TOGGLE.SHIFT_SPACE;
    }

    private void loadSettings() {
        this.settings = PreferenceManager.getDefaultSharedPreferences((Context)this);
        this.mVibrateOn = this.settings.getBoolean("vibrate_on", true);
        int i = getResources().getInteger(R.integer.vibrate_duration_ms;
        this.mVibrateDuration = this.settings.getInt("pref_vibrate_time", i);
        this.mSoundOn = this.settings.getBoolean("sound_on", false);
        this.mAutoCapSoft = this.settings.getBoolean("auto_cap_soft", true);
        this.mAutoCapHard = this.settings.getBoolean("auto_cap_hard", true);
        this.mVibrateOnHardToggle = this.settings.getBoolean("pref_hard_vibrate_on_toggle", false);
        this.mSwapYZ = this.settings.getBoolean("swap_zy", false);
        this.mShowFlag = this.settings.getBoolean("pref_show_status_icon", true);
        if (this.mKeyboardSwitcher != null)
            this.mKeyboardSwitcher.showFlag = this.mShowFlag;
        updateFlagState();
        loadLangToggleSettings();
        loadSimpleSettings();
    }

    private void loadSimpleSettings() {
        this.mKeyDelay = this.settings.getBoolean("key_delay", true);
        this.mKeyDelayTime = Integer.valueOf(this.settings.getString("key_delay_time", getString(R.string.delay_default_value))).intValue();
    }

    private void playKeyClick(int paramInt) {
        if (this.mAudioManager == null && this.mInputView != null)
            updateRingerMode();
        if (this.mSoundOn && !this.mSilentMode) {
            byte b = 5;
            switch (paramInt) {
                default:
                    paramInt = b;
                    if (this.mAudioManager != null)
                        this.mAudioManager.playSoundEffect(paramInt, 1.0F);
                    return;
                case -5:
                    paramInt = 7;
                    if (this.mAudioManager != null)
                        this.mAudioManager.playSoundEffect(paramInt, 1.0F);
                    return;
                case 10:
                    paramInt = 8;
                    if (this.mAudioManager != null)
                        this.mAudioManager.playSoundEffect(paramInt, 1.0F);
                    return;
                case 32:
                    break;
            }
        } else {
            return;
        }
        paramInt = 6;
        if (this.mAudioManager != null)
            this.mAudioManager.playSoundEffect(paramInt, 1.0F);
    }

    private void sendBackspace() {
        onKey(-5, (int[])null);
    }

    private void sendChar(char paramChar, boolean paramBoolean) {
        // Byte code:
        //   0: iload_2
        //   1: ifeq -> 11
        //   4: aload_0
        //   5: invokevirtual isShiftActive : ()Z
        //   8: ifne -> 27
        //   11: aload_0
        //   12: invokespecial isShifted : ()Z
        //   15: ifne -> 27
        //   18: iload_1
        //   19: istore_3
        //   20: aload_0
        //   21: getfield shiftForDouble : Z
        //   24: ifeq -> 37
        //   27: iload_1
        //   28: invokestatic toUpperCase : (C)C
        //   31: istore_3
        //   32: aload_0
        //   33: iconst_1
        //   34: putfield shiftForDouble : Z
        //   37: aload_0
        //   38: iload_3
        //   39: invokespecial sendKey : (I)V
        //   42: return
    }

    private void sendDelKey() {
        if (this.mComposing.length() > 0)
            onKey(-5, (int[])null);
    }

    private void sendKey(int paramInt) {
        if (DEBUG)
            Log.d(TAG, "sendKey: " + paramInt);
        switch (paramInt) {
            default:
                if (paramInt >= 48 && paramInt <= 57) {
                    keyDownUp(paramInt - 48 + 7);
                    return;
                }
                break;
            case 10:
                keyDownUp(66);
                return;
        }
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            if (DEBUG)
                Log.d(TAG, " commitText: " + String.valueOf((char)paramInt));
            inputConnection.commitText(String.valueOf((char)paramInt), 1);
            return;
        }
    }

    private void setInputConnectionMetaState() {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection != null) {
            int j = 0;
            if (MyMetaKeyKeyListener.getMetaState(this.mMetaState, 2) == 0)
                j = 0 + 2;
            int i = j;
            if (MyMetaKeyKeyListener.getMetaState(this.mMetaState, 1) == 0)
                i = j + 1;
            j = i;
            if (MyMetaKeyKeyListener.getMetaState(this.mMetaState, 4) == 0)
                j = i + 4;
            inputConnection.clearMetaKeyStates(j);
        }
    }

    private void showNextKeyboard() {
        if (DEBUG)
            Log.d(TAG, "showNextKeyboard ");
        this.mKeyboardSwitcher.setNextTextKeyboard((getCurrentInputEditorInfo()).imeOptions, false);
        if (this.mCapsLock && this.mKeyboardSwitcher.isAlphabetMode())
            ((LatinKeyboard)this.mInputView.getKeyboard()).setShiftLocked(this.mCapsLock);
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    private void showOptionsMenu() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)this);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_dialog_keyboard);
        builder.setNegativeButton(R.string.cancel, null);
        String str1 = getString(R.string.russian_ime_settings);
        String str2 = getString(R.string.ime_settings);
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface param1DialogInterface, int param1Int) {
                param1DialogInterface.dismiss();
                switch (param1Int) {
                    default:
                        return;
                    case 0:
                        SoftKeyboard.this.launchSettings();
                        return;
                    case 1:
                        break;
                }
                ((InputMethodManager)SoftKeyboard.this.getApplicationContext().getSystemService("input_method")).showInputMethodPicker();
            }
        };
        builder.setItems(new CharSequence[] { str1, str2 }, onClickListener);
        builder.setTitle(getResources().getString(R.string.ime_name));
        this.mOptionsDialog = builder.create();
        Window window = this.mOptionsDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.token = this.mInputView.getWindowToken();
        layoutParams.type = 1003;
        window.setAttributes(layoutParams);
        window.addFlags(131072);
        this.mOptionsDialog.show();
    }

    private void switchHardLayout(InputConnection paramInputConnection) {
        if (paramInputConnection != null)
            paramInputConnection.clearMetaKeyStates(2147483647);
        this.mMetaState = 0L;
        this.shiftForDouble = false;
        changeLayout();
    }

    private void toggleCapsLock() {
        boolean bool;
        if (!this.mCapsLock) {
            bool = true;
        } else {
            bool = false;
        }
        this.mCapsLock = bool;
        if (this.mKeyboardSwitcher.isAlphabetMode())
            ((LatinKeyboard)this.mInputView.getKeyboard()).setShiftLocked(this.mCapsLock);
    }

    private boolean translitKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        // Byte code:
        //   0: iconst_0
        //   1: istore #6
        //   3: aload_0
        //   4: getfield mKeyDelay : Z
        //   7: ifeq -> 48
        //   10: invokestatic currentTimeMillis : ()J
        //   13: lstore #7
        //   15: aload_0
        //   16: getfield mLastKeyTime : J
        //   19: aload_0
        //   20: getfield mKeyDelayTime : I
        //   23: i2l
        //   24: ladd
        //   25: lload #7
        //   27: lcmp
        //   28: ifge -> 42
        //   31: aload_0
        //   32: aload_0
        //   33: getfield ruKeyboardLayout : Lru/androidteam/rukeyboard/model/KeyboardLayout;
        //   36: getfield keysMap : Lru/androidteam/rukeyboard/model/Key;
        //   39: putfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   42: aload_0
        //   43: lload #7
        //   45: putfield mLastKeyTime : J
        //   48: aload_0
        //   49: getfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   52: getfield map : Ljava/util/HashMap;
        //   55: ifnull -> 86
        //   58: aload_0
        //   59: getfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   62: getfield map : Ljava/util/HashMap;
        //   65: iload_1
        //   66: invokestatic valueOf : (I)Ljava/lang/Integer;
        //   69: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
        //   72: checkcast ru/androidteam/rukeyboard/model/Key
        //   75: astore #10
        //   77: aload #10
        //   79: astore #9
        //   81: aload #10
        //   83: ifnonnull -> 116
        //   86: aload_0
        //   87: aload_0
        //   88: getfield ruKeyboardLayout : Lru/androidteam/rukeyboard/model/KeyboardLayout;
        //   91: getfield keysMap : Lru/androidteam/rukeyboard/model/Key;
        //   94: putfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   97: aload_0
        //   98: getfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   101: getfield map : Ljava/util/HashMap;
        //   104: iload_1
        //   105: invokestatic valueOf : (I)Ljava/lang/Integer;
        //   108: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
        //   111: checkcast ru/androidteam/rukeyboard/model/Key
        //   114: astore #9
        //   116: aload #9
        //   118: ifnonnull -> 123
        //   121: iconst_0
        //   122: ireturn
        //   123: aload #9
        //   125: aload_0
        //   126: getfield mIsTranslitKeys : Z
        //   129: invokevirtual getCharDefault : (Z)C
        //   132: istore_3
        //   133: aload #9
        //   135: aload_0
        //   136: getfield mIsTranslitKeys : Z
        //   139: invokevirtual getCharAlt : (Z)C
        //   142: istore #4
        //   144: aload #9
        //   146: aload_0
        //   147: getfield mIsTranslitKeys : Z
        //   150: invokevirtual getCharShift : (Z)C
        //   153: istore #5
        //   155: aload_0
        //   156: invokevirtual isAltActive : ()Z
        //   159: ifeq -> 190
        //   162: aload_2
        //   163: invokevirtual isAltPressed : ()Z
        //   166: ifeq -> 190
        //   169: iload #4
        //   171: getstatic ru/androidteam/rukeyboard/model/Key.EMPTY_CHAR : C
        //   174: if_icmpne -> 211
        //   177: aload_0
        //   178: aload_0
        //   179: getfield ruKeyboardLayout : Lru/androidteam/rukeyboard/model/KeyboardLayout;
        //   182: getfield keysMap : Lru/androidteam/rukeyboard/model/Key;
        //   185: putfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   188: iconst_0
        //   189: ireturn
        //   190: aload_0
        //   191: invokevirtual isAltActive : ()Z
        //   194: ifne -> 121
        //   197: aload_0
        //   198: invokevirtual isShiftActive : ()Z
        //   201: ifeq -> 262
        //   204: aload_2
        //   205: invokevirtual isShiftPressed : ()Z
        //   208: ifeq -> 262
        //   211: aload_0
        //   212: getfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   215: getfield code : I
        //   218: ifeq -> 297
        //   221: aload_2
        //   222: invokevirtual isAltPressed : ()Z
        //   225: ifne -> 297
        //   228: aload_0
        //   229: invokespecial deleteLeftSymbol : ()V
        //   232: aload #9
        //   234: getfield map : Ljava/util/HashMap;
        //   237: ifnull -> 305
        //   240: aload_0
        //   241: aload #9
        //   243: putfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   246: aload_2
        //   247: invokevirtual isAltPressed : ()Z
        //   250: ifeq -> 319
        //   253: aload_0
        //   254: iload #4
        //   256: iconst_0
        //   257: invokespecial sendChar : (CZ)V
        //   260: iconst_1
        //   261: ireturn
        //   262: iload_3
        //   263: getstatic ru/androidteam/rukeyboard/model/Key.EMPTY_CHAR : C
        //   266: if_icmpne -> 211
        //   269: aload_0
        //   270: getfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   273: getfield code : I
        //   276: ifeq -> 121
        //   279: aload_0
        //   280: aload_0
        //   281: getfield ruKeyboardLayout : Lru/androidteam/rukeyboard/model/KeyboardLayout;
        //   284: getfield keysMap : Lru/androidteam/rukeyboard/model/Key;
        //   287: putfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   290: aload_0
        //   291: iload_1
        //   292: aload_2
        //   293: invokespecial translitKeyDown : (ILandroid/view/KeyEvent;)Z
        //   296: ireturn
        //   297: aload_0
        //   298: iconst_0
        //   299: putfield shiftForDouble : Z
        //   302: goto -> 232
        //   305: aload_0
        //   306: aload_0
        //   307: getfield ruKeyboardLayout : Lru/androidteam/rukeyboard/model/KeyboardLayout;
        //   310: getfield keysMap : Lru/androidteam/rukeyboard/model/Key;
        //   313: putfield keyRoot : Lru/androidteam/rukeyboard/model/Key;
        //   316: goto -> 246
        //   319: aload_2
        //   320: invokevirtual isShiftPressed : ()Z
        //   323: ifeq -> 344
        //   326: iload #5
        //   328: getstatic ru/androidteam/rukeyboard/model/Key.EMPTY_CHAR : C
        //   331: if_icmpeq -> 344
        //   334: aload_0
        //   335: iload #5
        //   337: iconst_0
        //   338: invokespecial sendChar : (CZ)V
        //   341: goto -> 260
        //   344: iload_3
        //   345: getstatic ru/androidteam/rukeyboard/model/Key.EMPTY_CHAR : C
        //   348: if_icmpeq -> 121
        //   351: iload #5
        //   353: getstatic ru/androidteam/rukeyboard/model/Key.EMPTY_CHAR : C
        //   356: if_icmpne -> 362
        //   359: iconst_1
        //   360: istore #6
        //   362: aload_0
        //   363: iload_3
        //   364: iload #6
        //   366: invokespecial sendChar : (CZ)V
        //   369: goto -> 260
    }

    private void updateCandidates() {
        if (DEBUG)
            Log.d(TAG, "updateCandidates");
        if (!this.mCompletionOn) {
            if (this.mComposing.length() > 0) {
                ArrayList<String> arrayList = new ArrayList();
                arrayList.add(this.mComposing.toString());
                setSuggestions(arrayList, true, true);
                return;
            }
        } else {
            return;
        }
        setSuggestions((List<String>)null, false, false);
    }

    private void updateRingerMode() {
        if (this.mAudioManager == null)
            this.mAudioManager = (AudioManager)getSystemService("audio");
        if (this.mAudioManager != null) {
            boolean bool;
            if (this.mAudioManager.getRingerMode() != 2) {
                bool = true;
            } else {
                bool = false;
            }
            this.mSilentMode = bool;
        }
    }

    private void vibrate() {
        if (!this.mVibrateOn)
            return;
        if (this.mVibrator == null)
            this.mVibrator = (Vibrator)getSystemService("vibrator");
        this.mVibrator.vibrate(this.mVibrateDuration);
    }

    public boolean isAltActive() {
        return ((MetaKeyKeyListener.getMetaState(this.mMetaState) & 0x202) != 0);
    }

    public boolean isShiftActive() {
        return ((MetaKeyKeyListener.getMetaState(this.mMetaState) & 0x101) != 0);
    }

    public boolean isWordSeparator(int paramInt) {
        return getWordSeparators().contains(String.valueOf((char)paramInt));
    }

    public void onCreate() {
        super.onCreate();
        this.curConfig = getResources().getConfiguration();
        this.mKeyboardSwitcher = new KeyboardSwitcher(this);
        this.mWordSeparators = getResources().getString(R.string.word_separators);
        IntentFilter intentFilter = new IntentFilter("android.media.RINGER_MODE_CHANGED");
        registerReceiver(this.mReceiver, intentFilter);
        this.mNotificationManager = (NotificationManager)getSystemService("notification");
        this.settings = PreferenceManager.getDefaultSharedPreferences((Context)this);
        this.settings.registerOnSharedPreferenceChangeListener(this.sharedListener);
    }

    public View onCreateCandidatesView() {
        if (DEBUG)
            Log.d(TAG, "onCreateCandidatesView");
        this.mKeyboardSwitcher.makeKeyboards(true);
        this.mCandidateView = new CandidateView((Context)this);
        this.mCandidateView.setService(this);
        return this.mCandidateView;
    }

    public View onCreateInputView() {
        if (DEBUG)
            Log.d(TAG, "onCreateInputView");
        this.mInputView = (LatinKeyboardView)getLayoutInflater().inflate(R.layout.input, null);
        this.mKeyboardSwitcher.setInputView(this.mInputView);
        this.mKeyboardSwitcher.makeKeyboards(true);
        this.mInputView.setOnKeyboardActionListener(this);
        this.mKeyboardSwitcher.setKeyboardMode(1, 0);
        return (View)this.mInputView;
    }

    public void onDestroy() {
        this.mNotificationManager.cancelAll();
        unregisterReceiver(this.mReceiver);
        this.settings.unregisterOnSharedPreferenceChangeListener(this.sharedListener);
        super.onDestroy();
    }

    public boolean onEvaluateFullscreenMode() {
        return (super.onEvaluateFullscreenMode() && getResources().getBoolean(R.bool.config_use_fullscreen_mode));
    }

    public void onFinishInput() {
        super.onFinishInput();
        if (DEBUG)
            Log.d(TAG, "onFinishInput");
        this.mComposing.setLength(0);
        updateCandidates();
        setCandidatesViewShown(false);
        if (this.mInputView != null)
            this.mInputView.closing();
    }

    public void onInitializeInterface() {
        if (DEBUG)
            Log.d(TAG, "onInitializeInterface!");
        loadSettings();
        String str = KeyboardSettings.getDefaultHardLayout((Context)this, this.settings);
        if (DEBUG)
            Log.d(TAG, "[Loading hardware layout] old: " + this.hardLayout + ", new: " + str);
        if (this.hardLayout == null || !this.hardLayout.equals(str)) {
            KeyboardLayout keyboardLayout;
            this.hardLayout = str;
            str = null;
            try {
                KeyboardLayout keyboardLayout1 = (new KeyLayoutLoader((Context)this)).load(this.hardLayout);
                keyboardLayout = keyboardLayout1;
            } catch (XmlPullParserException xmlPullParserException) {
                Log.e(TAG, "Ill-formatted xml file");
                xmlPullParserException.printStackTrace();
            }
            if (keyboardLayout != null) {
                String str1;
                this.ruKeyboardLayout = keyboardLayout;
                if (this.ruKeyboardLayout.flag == null) {
                    str1 = "flag_unknown";
                } else {
                    str1 = this.ruKeyboardLayout.flag;
                }
                this.hardIcon = getResources().getIdentifier(str1, "drawable", getPackageName());
            }
        }
        this.keyRoot = this.ruKeyboardLayout.keysMap;
    }

    public void onKey(int paramInt, int[] paramArrayOfint) {
        if (DEBUG)
            Log.d(TAG, "[onKey] primaryCode: " + paramInt);
        switch (paramInt) {
            default:
                if (isWordSeparator(paramInt)) {
                    handleSeparator(paramInt);
                    return;
                }
                break;
            case -5:
                handleBackspace();
                return;
            case -1:
                handleShift();
                return;
            case -3:
                handleClose();
                return;
            case -100:
                showOptionsMenu();
                return;
            case -101:
                if (this.mCapsLock) {
                    handleShift();
                    return;
                }
                toggleCapsLock();
                return;
            case -2:
                changeKeyboardMode();
                return;
            case -11:
                showNextKeyboard();
                return;
        }
        handleCharacter(paramInt, paramArrayOfint);
    }

    public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
        int i;
        EditorInfo editorInfo;
        if (DEBUG)
            Log.d(TAG, "onKeyDown event: " + paramKeyEvent.toString());
        InputConnection inputConnection = getCurrentInputConnection();
        if (DEBUG)
            Log.d(TAG, "langToggleKey: " + this.ruKeyboardLayout.langToggleKey);
        if (paramInt == this.ruKeyboardLayout.langToggleKey) {
            switchHardLayout(inputConnection);
            return true;
        }
        if (this.langToggle == LANG_TOGGLE.CUSTOM && this.mCurrentCustomCode != -1 && paramInt == this.mCurrentCustomCode) {
            switchHardLayout(inputConnection);
            return true;
        }
        int j = paramInt;
        switch (paramInt) {
            default:
                i = paramInt;
                if (this.mSwapYZ) {
                    i = paramInt;
                    if (!isAltActive())
                        if (this.mIsTranslitKeys) {
                            if (paramInt == 53) {
                                i = 54;
                            } else {
                                i = paramInt;
                                if (paramInt == 54)
                                    i = 53;
                            }
                        } else {
                            if (paramInt == 53) {
                                keyDownUp(54);
                                return true;
                            }
                            i = paramInt;
                            if (paramInt == 54) {
                                keyDownUp(53);
                                return true;
                            }
                        }
                }
                editorInfo = getCurrentInputEditorInfo();
                if (!this.ruKeyboardLayout.upKeys.contains(Integer.valueOf(i))) {
                    if (editorInfo != null && editorInfo.inputType != 0 && (editorInfo.inputType == 2 || editorInfo.inputType == 3)) {
                        if (DEBUG)
                            Log.d(TAG, "Field type is one of the next: TYPE_CLASS_NUMBER | TYPE_CLASS_DATETIME | TYPE_CLASS_PHONE");
                        return super.onKeyDown(i, paramKeyEvent);
                    }
                    break;
                }
                return true;
            case 4:
                j = paramInt;
                if (paramKeyEvent.getRepeatCount() == 0) {
                    j = paramInt;
                    if (this.mInputView != null) {
                        j = paramInt;
                        if (this.mInputView.handleBack()) {
                            if (inputConnection != null)
                                inputConnection.clearMetaKeyStates(2147483647);
                            this.mMetaState = 0L;
                            return true;
                        }
                    }
                }
            case 67:
                this.keyRoot = this.ruKeyboardLayout.keysMap;
                this.shiftForDouble = false;
                this.mMetaState = MyMetaKeyKeyListener.adjustMetaAfterKeypress(this.mMetaState);
                j = paramInt;
                if (this.mComposing.length() > 0) {
                    onKey(-5, (int[])null);
                    return true;
                }
            case 59:
            case 60:
                this.shiftForDouble = true;
                this.mMetaState = MyMetaKeyKeyListener.handleKeyDown(this.mMetaState, paramInt, paramKeyEvent);
                if (this.langToggle == LANG_TOGGLE.ALT_SHIFT && paramKeyEvent.isAltPressed()) {
                    if (DEBUG)
                        Log.d(TAG, "Switching lang on Alt+Shift");
                    switchHardLayout(inputConnection);
                    return true;
                }
                j = paramInt;
                if (Build.VERSION.SDK_INT >= 11) {
                    j = paramInt;
                    if (this.langToggle == LANG_TOGGLE.CTRL_SHIFT) {
                        j = paramInt;
                        if (paramKeyEvent.isCtrlPressed()) {
                            if (DEBUG)
                                Log.d(TAG, "Switching lang on Ctrl+Shift");
                            switchHardLayout(inputConnection);
                            return true;
                        }
                    }
                }
            case 57:
            case 58:
                this.mMetaState = MyMetaKeyKeyListener.handleKeyDown(this.mMetaState, paramInt, paramKeyEvent);
                j = paramInt;
                if (this.langToggle == LANG_TOGGLE.ALT_SHIFT) {
                    j = paramInt;
                    if (paramKeyEvent.isShiftPressed()) {
                        if (DEBUG)
                            Log.d(TAG, "Switching lang on Shift+Alt");
                        switchHardLayout(inputConnection);
                        return true;
                    }
                }
            case 63:
                this.mMetaState = MyMetaKeyKeyListener.handleKeyDown(this.mMetaState, paramInt, paramKeyEvent);
                j = paramInt;
            case 82:
                if (paramKeyEvent.isPrintingKey())
                    this.mMetaState = MyMetaKeyKeyListener.adjustMetaAfterKeypress(this.mMetaState);
                return super.onKeyDown(j, paramKeyEvent);
            case 113:
            case 114:
                this.mMetaState = MyMetaKeyKeyListener.handleKeyDown(this.mMetaState, paramInt, paramKeyEvent);
                j = paramInt;
                if (this.langToggle == LANG_TOGGLE.CTRL_SHIFT) {
                    j = paramInt;
                    if (paramKeyEvent.isShiftPressed()) {
                        if (DEBUG)
                            Log.d(TAG, "Switching lang on Ctrl+Shift");
                        switchHardLayout(inputConnection);
                        return true;
                    }
                }
            case 66:
                return false;
            case 62:
                this.keyRoot = this.ruKeyboardLayout.keysMap;
                if (this.langToggle == LANG_TOGGLE.SHIFT_SPACE && paramKeyEvent.isShiftPressed()) {
                    switchHardLayout(inputConnection);
                    return true;
                }
                if (this.langToggle == LANG_TOGGLE.ALT_SPACE && paramKeyEvent.isAltPressed()) {
                    switchHardLayout(inputConnection);
                    return true;
                }
                return false;
            case 84:
                if (this.curConfig != null && this.curConfig.hardKeyboardHidden == 2)
                    return false;
                j = paramInt;
                if (this.hardLayout.equals("motorola_milestone_ru")) {
                    j = paramInt;
                    if (!KeyCharacterMap.deviceHasKey(this.ruKeyboardLayout.langToggleKey)) {
                        switchHardLayout(inputConnection);
                        return true;
                    }
                }
        }
        j = i;
        if (inputConnection != null) {
            j = i;
            if (translitKeyDown(i, paramKeyEvent)) {
                this.mMetaState = MyMetaKeyKeyListener.adjustMetaAfterKeypress(this.mMetaState);
                return true;
            }
        }
    }

    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
        if (DEBUG)
            Log.d(TAG, "onKeyUp keyCode: " + paramInt + ", event: " + paramKeyEvent.toString());
        if (DEBUG_META)
            Log.d(TAG, "META before: " + MyMetaKeyKeyListener.getMetaState(this.mMetaState));
        this.mMetaState = MyMetaKeyKeyListener.handleKeyUp(this.mMetaState, paramInt, paramKeyEvent);
        if (DEBUG_META)
            Log.d(TAG, getMetaKeysStates("onKeyUp after handleKeyUp "));
        if (!isShiftActive()) {
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null)
                inputConnection.clearMetaKeyStates(1);
        }
        if (isShiftActive() && paramInt == 67)
            this.mMetaState = MyMetaKeyKeyListener.adjustMetaAfterKeypress(this.mMetaState);
        switch (paramInt) {
            default:
                setInputConnectionMetaState();
                if (this.ruKeyboardLayout.isContainsUpKeys() && this.ruKeyboardLayout.upKeys.contains(Integer.valueOf(paramInt)) && getCurrentInputConnection() != null && translitKeyDown(paramInt, paramKeyEvent)) {
                    this.mMetaState = MyMetaKeyKeyListener.adjustMetaAfterKeypress(this.mMetaState);
                    return true;
                }
                return super.onKeyUp(paramInt, paramKeyEvent);
            case 19:
            case 20:
            case 21:
            case 22:
            case 62:
            case 84:
                break;
        }
        if (paramKeyEvent.isAltPressed() || isAltActive())
            this.mMetaState = 0L;
    }

    public void onPress(int paramInt) {
        vibrate();
        playKeyClick(paramInt);
    }

    public void onRelease(int paramInt) {}

    public void onStartInputView(EditorInfo paramEditorInfo, boolean paramBoolean) {
        if (DEBUG)
            Log.d(TAG, "onStartInputView");
        if (this.mInputView == null)
            return;
        this.mKeyboardSwitcher.makeKeyboards(true);
        this.mPredictionOn = false;
        this.mCompletionOn = false;
        this.mCompletions = null;
        this.mCapsLock = false;
        switch (paramEditorInfo.inputType & 0xF) {
            default:
                this.mKeyboardSwitcher.setKeyboardMode(1, paramEditorInfo.imeOptions);
                updateShiftKeyState(paramEditorInfo);
                this.mInputView.closing();
                this.mComposing.setLength(0);
                setCandidatesViewShown(false);
                this.mKeyboardSwitcher.updateKeyboardFlag();
                this.mPredictionOn = false;
                return;
            case 2:
            case 4:
                this.mKeyboardSwitcher.setKeyboardMode(1, paramEditorInfo.imeOptions);
                this.mKeyboardSwitcher.toggleSymbols();
                this.mInputView.closing();
                this.mComposing.setLength(0);
                setCandidatesViewShown(false);
                this.mKeyboardSwitcher.updateKeyboardFlag();
                this.mPredictionOn = false;
                return;
            case 3:
                this.mKeyboardSwitcher.setKeyboardMode(3, paramEditorInfo.imeOptions);
                this.mInputView.closing();
                this.mComposing.setLength(0);
                setCandidatesViewShown(false);
                this.mKeyboardSwitcher.updateKeyboardFlag();
                this.mPredictionOn = false;
                return;
            case 1:
                break;
        }
        this.mKeyboardSwitcher.setKeyboardMode(1, paramEditorInfo.imeOptions);
        this.mPredictionOn = true;
        int i = paramEditorInfo.inputType & 0xFF0;
        if (i == 128 || i == 144) {
            this.mPasswordMode = true;
            this.mPredictionOn = false;
        } else {
            this.mPasswordMode = false;
        }
        if (i == 32 || i == 96) {
            this.mAutoSpace = false;
        } else {
            this.mAutoSpace = true;
        }
        if (i == 32) {
            this.mPredictionOn = false;
            this.mKeyboardSwitcher.setKeyboardMode(5, paramEditorInfo.imeOptions);
        } else if (i == 16) {
            this.mPredictionOn = false;
            this.mKeyboardSwitcher.setKeyboardMode(4, paramEditorInfo.imeOptions);
        } else if (i == 64) {
            this.mKeyboardSwitcher.setKeyboardMode(6, paramEditorInfo.imeOptions);
        } else if (i == 176) {
            this.mPredictionOn = false;
        }
        if ((paramEditorInfo.inputType & 0x10000) != 0) {
            this.mPredictionOn = false;
            this.mCompletionOn = isFullscreenMode();
        }
        updateShiftKeyState(paramEditorInfo);
        this.mInputView.closing();
        this.mComposing.setLength(0);
        setCandidatesViewShown(false);
        this.mKeyboardSwitcher.updateKeyboardFlag();
        this.mPredictionOn = false;
    }

    public void onText(CharSequence paramCharSequence) {
        if (DEBUG)
            Log.d(TAG, "onText");
        if (DEBUG)
            Log.d(TAG, "Meta state: " + MetaKeyKeyListener.getMetaState(paramCharSequence));
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null)
            return;
        inputConnection.beginBatchEdit();
        if (this.mComposing.length() > 0)
            commitTyped(inputConnection);
        inputConnection.commitText(paramCharSequence, 0);
        inputConnection.endBatchEdit();
        updateShiftKeyState(getCurrentInputEditorInfo());
    }

    public void onUpdateSelection(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6) {
        super.onUpdateSelection(paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6);
        if (DEBUG)
            Log.d(TAG, "onUpdateSelection");
        if (this.mComposing.length() > 0 && (paramInt3 != paramInt6 || paramInt4 != paramInt6)) {
            this.mComposing.setLength(0);
            updateCandidates();
            InputConnection inputConnection = getCurrentInputConnection();
            if (inputConnection != null)
                inputConnection.finishComposingText();
        }
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int paramInt) {
        if (this.mCompletionOn && this.mCompletions != null && paramInt >= 0 && paramInt < this.mCompletions.length) {
            CompletionInfo completionInfo = this.mCompletions[paramInt];
            getCurrentInputConnection().commitCompletion(completionInfo);
            if (this.mCandidateView != null)
                this.mCandidateView.clear();
            updateShiftKeyState(getCurrentInputEditorInfo());
            return;
        }
        if (this.mComposing.length() > 0) {
            commitTyped(getCurrentInputConnection());
            return;
        }
    }

    public void setSuggestions(List<String> paramList, boolean paramBoolean1, boolean paramBoolean2) {
        if (DEBUG)
            Log.d(TAG, "setSuggestions");
        if (this.mPredictionOn) {
            if (paramList != null && paramList.size() > 0) {
                setCandidatesViewShown(true);
            } else if (isExtractViewShown()) {
                setCandidatesViewShown(true);
            }
            if (this.mCandidateView != null)
                this.mCandidateView.setSuggestions(paramList, paramBoolean1, paramBoolean2);
        }
    }

    public void swipeDown() {
        handleClose();
    }

    public void swipeLeft() {
        handleBackspace();
    }

    public void swipeRight() {
        if (this.mCompletionOn)
            pickDefaultCandidate();
    }

    public void swipeUp() {}

    void updateFlagState() {
        if (this.mKeyboardSwitcher != null) {
            if (this.curConfig != null) {
                if (this.curConfig.hardKeyboardHidden == 1) {
                    int i;
                    KeyboardSwitcher keyboardSwitcher = this.mKeyboardSwitcher;
                    if (this.mIsTranslitKeys) {
                        i = this.hardIcon;
                    } else {
                        i = R.drawable.us_flag;
                    }
                    keyboardSwitcher.setKeyboardFlag(i);
                    return;
                }
                return;
            }
            this.mKeyboardSwitcher.updateKeyboardFlag();
            return;
        }
    }

    public void updateShiftKeyState(EditorInfo paramEditorInfo) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (paramEditorInfo != null && this.mInputView != null && this.mKeyboardSwitcher.isAlphabetMode() && inputConnection != null) {
            boolean bool;
            byte b = 0;
            EditorInfo editorInfo = getCurrentInputEditorInfo();
            int i = b;
            if (this.mAutoCapSoft) {
                i = b;
                if (editorInfo != null) {
                    i = b;
                    if (editorInfo.inputType != 0)
                        i = inputConnection.getCursorCapsMode(paramEditorInfo.inputType);
                }
            }
            LatinKeyboardView latinKeyboardView = this.mInputView;
            if (this.mCapsLock || i != 0) {
                bool = true;
            } else {
                bool = false;
            }
            latinKeyboardView.setShifted(bool);
        }
    }

    private enum LANG_TOGGLE {
        ALT_SHIFT, ALT_SPACE, CTRL_SHIFT, CUSTOM, SHIFT_SPACE;

        static {
            ALT_SHIFT = new LANG_TOGGLE("ALT_SHIFT", 3);
            CUSTOM = new LANG_TOGGLE("CUSTOM", 4);
            $VALUES = new LANG_TOGGLE[] { SHIFT_SPACE, ALT_SPACE, CTRL_SHIFT, ALT_SHIFT, CUSTOM };
        }
    }
}

