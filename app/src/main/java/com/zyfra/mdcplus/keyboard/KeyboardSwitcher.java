package com.zyfra.mdcplus.keyboard;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KeyboardSwitcher {
    private static boolean DEBUG = false;

    public static final int KEYBOARDMODE_EMAIL = R.id.mode_email;

    public static final int KEYBOARDMODE_IM = R.id.mode_im;

    public static final int KEYBOARDMODE_NORMAL = R.id.mode_normal;

    public static final int KEYBOARDMODE_URL = R.id.mode_url;

    public static final int MODE_EMAIL = 5;

    public static final int MODE_IM = 6;

    public static final int MODE_NEXT = 8;

    public static final int MODE_PHONE = 3;

    public static final int MODE_RUSSIAN = 7;

    public static final int MODE_SYMBOLS = 2;

    public static final int MODE_TEXT = 1;

    public static final int MODE_TEXT_ALPHA = 1;

    public static final int MODE_TEXT_COUNT = 2;

    public static final int MODE_TEXT_QWERTY = 0;

    public static final int MODE_URL = 4;

    public static final int NOTIFICATION_ID = 1;

    private static final int SYMBOLS_MODE_STATE_BEGIN = 1;

    private static final int SYMBOLS_MODE_STATE_NONE = 0;

    private static final int SYMBOLS_MODE_STATE_SYMBOL = 2;

    private static final String TAG = KeyboardSwitcher.class.getSimpleName();

    private int curTextKeyboardID = 0;

    SoftKeyboard mContext;

    private KeyboardId mCurrentId;

    private int mImeOptions;

    LatinKeyboardView mInputView;

    private boolean mIsSymbols;

    private Map<KeyboardId, LatinKeyboard> mKeyboards;

    private int mLastDisplayWidth;

    private int mMode;

    private NotificationManager mNotificationManager;

    private boolean mPreferSymbols;

    private KeyboardId mSymbolsId;

    private int mSymbolsModeState = 0;

    private KeyboardId mSymbolsShiftedId;

    private int mTextMode = 0;

    private final String packagename = "com.zyfra.mdcplus.keyboard";

    private SharedPreferences settings;

    public boolean showFlag;

    private ArrayList<KeyboardXML> textKeyboards;

    KeyboardSwitcher(SoftKeyboard paramSoftKeyboard) {
        this.mContext = paramSoftKeyboard;
        this.mKeyboards = new HashMap<KeyboardId, LatinKeyboard>();
        this.mSymbolsId = new KeyboardId(R.xml.kbd_symbols);
        this.mSymbolsShiftedId = new KeyboardId(R.xml.kbd_symbols_shift);
        this.textKeyboards = new ArrayList<KeyboardXML>();
        this.mNotificationManager = (NotificationManager)paramSoftKeyboard.getSystemService("notification");
        this.settings = PreferenceManager.getDefaultSharedPreferences((Context)paramSoftKeyboard);
        loadActiveKeyboardList();
    }

    private KeyboardXML getCurrentKeyboardXML() {
        return this.textKeyboards.get(this.curTextKeyboardID);
    }

    private LatinKeyboard getKeyboard(KeyboardId paramKeyboardId) {
        if (!this.mKeyboards.containsKey(paramKeyboardId)) {
            LatinKeyboard latinKeyboard = new LatinKeyboard((Context)this.mContext, paramKeyboardId.mXml, paramKeyboardId.mMode);
            if (paramKeyboardId.mEnableShiftLock)
                latinKeyboard.enableShiftLock();
            this.mKeyboards.put(paramKeyboardId, latinKeyboard);
        }
        return this.mKeyboards.get(paramKeyboardId);
    }

    private KeyboardId getKeyboardId(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
        if (paramBoolean)
            return (paramInt2 == 3) ? new KeyboardId(R.xml.kbd_phone_symbols) : new KeyboardId(R.xml.kbd_symbols);
        switch (paramInt2) {
            default:
                return null;
            case 1:
                if (this.mTextMode == 0)
                    return new KeyboardId(paramInt1, R.id.mode_normal, true);
                if (this.mTextMode == 1)
                    return new KeyboardId(R.xml.kbd_alpha, R.id.mode_normal, true);
            case 2:
                return new KeyboardId(R.xml.kbd_symbols);
            case 3:
                return new KeyboardId(R.xml.kbd_phone);
            case 4:
                return new KeyboardId(paramInt1, R.id.mode_url, true);
            case 5:
                return new KeyboardId(paramInt1, R.id.mode_email, true);
            case 6:
                break;
        }
        return new KeyboardId(paramInt1, R.id.mode_im, true);
    }

    private KeyboardId getKeyboardId(int paramInt1, int paramInt2, boolean paramBoolean) {
        return getKeyboardId(R.xml.kbd_qwerty, paramInt1, paramInt2, paramBoolean);
    }

    private int getNextKeyboardID() {
        this.curTextKeyboardID++;
        if (this.curTextKeyboardID >= this.textKeyboards.size())
            this.curTextKeyboardID = 0;
        return this.curTextKeyboardID;
    }

    private KeyboardXML getNextKeyboardXML() {
        return this.textKeyboards.get(getNextKeyboardID());
    }

    int getKeyboardMode() {
        return this.mMode;
    }

    int getTextMode() {
        return this.mTextMode;
    }

    int getTextModeCount() {
        return 2;
    }

    boolean isAlphabetMode() {
        KeyboardId keyboardId = this.mCurrentId;
        return (keyboardId.mMode == R.id.mode_normal || keyboardId.mMode == R.id.mode_url || keyboardId.mMode == R.id.mode_email || keyboardId.mMode == R.id.mode_im);
    }

    boolean isTextMode() {
        return (this.mMode == 1);
    }

    public void loadActiveKeyboardList() {
        if (DEBUG)
            Log.d(TAG, "loadActiveKeyboardList");
        this.textKeyboards.clear();
        this.textKeyboards.add(new KeyboardXML(R.xml.kbd_qwerty, R.drawable.us_flag));
        Resources resources = this.mContext.getResources();
        String str = this.settings.getString("key_softkeyboard_list", this.mContext.getString(R.string.default_soft_keyboard));
        String[] arrayOfString1 = resources.getStringArray(R.array.softkeys_xmlnames);
        String[] arrayOfString2 = resources.getStringArray(R.array.softkeys_flags);
        int j = arrayOfString1.length;
        for (int i = 0; i < j; i++) {
            if (str.contains(arrayOfString1[i])) {
                int k = resources.getIdentifier(arrayOfString1[i], "xml", "com.zyfra.mdcplus.keyboard");
                int m = resources.getIdentifier(arrayOfString2[i], "drawable", "com.zyfra.mdcplus.keyboard");
                this.textKeyboards.add(new KeyboardXML(k, m));
            }
        }
        this.curTextKeyboardID = 0;
    }

    void makeKeyboards(boolean paramBoolean) {
        if (paramBoolean)
            this.mKeyboards.clear();
        int i = this.mContext.getMaxWidth();
        if (i == this.mLastDisplayWidth)
            return;
        this.mLastDisplayWidth = i;
        if (!paramBoolean)
            this.mKeyboards.clear();
        this.mSymbolsId = new KeyboardId(R.xml.kbd_symbols);
        this.mSymbolsShiftedId = new KeyboardId(R.xml.kbd_symbols_shift);
    }

    boolean onKey(int paramInt) {
        switch (this.mSymbolsModeState) {
            default:
                return false;
            case 1:
                if (paramInt != 32 && paramInt != 10 && paramInt > 0)
                    this.mSymbolsModeState = 2;
            case 2:
                break;
        }
        if (paramInt == 10 || paramInt == 32)
            return true;
    }

    void setInputView(LatinKeyboardView paramLatinKeyboardView) {
        this.mInputView = paramLatinKeyboardView;
    }

    public void setKeyboardFlag(int paramInt) {
        if (this.showFlag) {
            Notification notification = new Notification(paramInt, null, System.currentTimeMillis());
            Intent intent = new Intent();
            intent.setClassName("com.zyfra.mdcplus.keyboard", "com.zyfra.mdcplus.keyboard.KeyboardSettings");
            PendingIntent pendingIntent = PendingIntent.getActivity((Context)this.mContext, 0, intent, 0);
            notification.setLatestEventInfo((Context)this.mContext, "Клавиатура для Android", "Открыть настройки", pendingIntent);
                    notification.flags |= 0x2;
            this.mNotificationManager.notify(1, notification);
            return;
        }
        this.mNotificationManager.cancelAll();
    }

    void setKeyboardMode(int paramInt1, int paramInt2) {
        boolean bool1 = true;
        boolean bool2 = false;
        this.mSymbolsModeState = 0;
        if (paramInt1 == 2)
            bool2 = true;
        this.mPreferSymbols = bool2;
        if (paramInt1 == 2)
            paramInt1 = bool1;
        setKeyboardMode(paramInt1, paramInt2, this.mPreferSymbols);
    }

    void setKeyboardMode(int paramInt1, int paramInt2, boolean paramBoolean) {
        setKeyboardMode(getCurrentKeyboardXML(), paramInt1, paramInt2, paramBoolean);
    }

    void setKeyboardMode(KeyboardXML paramKeyboardXML, int paramInt1, int paramInt2, boolean paramBoolean) {
        this.mMode = paramInt1;
        this.mImeOptions = paramInt2;
        this.mIsSymbols = paramBoolean;
        this.mInputView.setPreviewEnabled(true);
        KeyboardId keyboardId = getKeyboardId(paramKeyboardXML.mXmlRes, paramInt1, paramInt2, paramBoolean);
        LatinKeyboard latinKeyboard = getKeyboard(keyboardId);
        if (paramInt1 == 3) {
            this.mInputView.setPhoneKeyboard(latinKeyboard);
            this.mInputView.setPreviewEnabled(false);
        }
        this.mCurrentId = keyboardId;
        this.mInputView.setKeyboard(latinKeyboard);
        latinKeyboard.setShifted(false);
        latinKeyboard.setShiftLocked(latinKeyboard.isShiftLocked());
        latinKeyboard.setImeOptions(this.mContext.getResources(), this.mMode, paramInt2);
        if (!paramBoolean)
            setKeyboardFlag(paramKeyboardXML.mIconRes);
    }

    void setNextTextKeyboard(int paramInt, boolean paramBoolean) {
        setKeyboardMode(getNextKeyboardXML(), this.mMode, paramInt, paramBoolean);
    }

    void setTextMode(int paramInt) {
        if (paramInt < 2 && paramInt >= 0)
            this.mTextMode = paramInt;
        if (isTextMode())
            setKeyboardMode(1, this.mImeOptions);
    }

    void toggleShift() {
        if (this.mCurrentId.equals(this.mSymbolsId)) {
            LatinKeyboard latinKeyboard1 = getKeyboard(this.mSymbolsId);
            LatinKeyboard latinKeyboard2 = getKeyboard(this.mSymbolsShiftedId);
            latinKeyboard1.setShifted(true);
            this.mCurrentId = this.mSymbolsShiftedId;
            this.mInputView.setKeyboard(latinKeyboard2);
            latinKeyboard2.setShifted(true);
            latinKeyboard2.setImeOptions(this.mContext.getResources(), this.mMode, this.mImeOptions);
            return;
        }
        if (this.mCurrentId.equals(this.mSymbolsShiftedId)) {
            LatinKeyboard latinKeyboard = getKeyboard(this.mSymbolsId);
            getKeyboard(this.mSymbolsShiftedId).setShifted(false);
            this.mCurrentId = this.mSymbolsId;
            this.mInputView.setKeyboard(getKeyboard(this.mSymbolsId));
            latinKeyboard.setShifted(false);
            latinKeyboard.setImeOptions(this.mContext.getResources(), this.mMode, this.mImeOptions);
            return;
        }
    }

    void toggleSymbols() {
        boolean bool;
        int i = this.mMode;
        int j = this.mImeOptions;
        if (!this.mIsSymbols) {
            bool = true;
        } else {
            bool = false;
        }
        setKeyboardMode(i, j, bool);
        if (this.mIsSymbols && !this.mPreferSymbols) {
            this.mSymbolsModeState = 1;
            return;
        }
        this.mSymbolsModeState = 0;
    }

    public void updateKeyboardFlag() {
        setKeyboardFlag((getCurrentKeyboardXML()).mIconRes);
    }

    private static class KeyboardId {
        public boolean mEnableShiftLock;

        public int mMode;

        public int mXml;

        public KeyboardId(int param1Int) {
            this(param1Int, 0, false);
        }

        public KeyboardId(int param1Int1, int param1Int2, boolean param1Boolean) {
            this.mXml = param1Int1;
            this.mMode = param1Int2;
            this.mEnableShiftLock = param1Boolean;
        }

        public boolean equals(Object param1Object) {
            return (param1Object instanceof KeyboardId && equals((KeyboardId)param1Object));
        }

        public boolean equals(KeyboardId param1KeyboardId) {
            return (param1KeyboardId.mXml == this.mXml && param1KeyboardId.mMode == this.mMode);
        }

        public int hashCode() {
            int i = this.mXml;
            int j = this.mMode;
            if (this.mEnableShiftLock) {
                byte b1 = 2;
                return b1 * (j + 1) * (i + 1);
            }
            byte b = 1;
            return b * (j + 1) * (i + 1);
        }
    }

    private class KeyboardXML {
        int mIconRes;

        int mXmlRes;

        KeyboardXML(int param1Int1, int param1Int2) {
            this.mXmlRes = param1Int1;
            this.mIconRes = param1Int2;
        }
    }
}

