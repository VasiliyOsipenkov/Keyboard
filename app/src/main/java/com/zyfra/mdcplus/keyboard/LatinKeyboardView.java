package com.zyfra.mdcplus.keyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.util.AttributeSet;

import com.zyfra.mdcplus.keyboard.view.KeyboardView;

public class LatinKeyboardView extends KeyboardView {
    static final int KEYCODE_OPTIONS = -100;

    static final int KEYCODE_SHIFT_LONGPRESS = -101;

    private Keyboard mPhoneKeyboard;

    public LatinKeyboardView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public LatinKeyboardView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    protected boolean onLongPress(Keyboard.Key paramKey) {
        if (paramKey.codes[0] == -2) {
            getOnKeyboardActionListener().onKey(-100, null);
            return true;
        }
        if (paramKey.codes[0] == -1) {
            getOnKeyboardActionListener().onKey(-101, null);
            invalidate();
            return true;
        }
        if (paramKey.codes[0] == 48 && getKeyboard() == this.mPhoneKeyboard) {
            getOnKeyboardActionListener().onKey(43, null);
            return true;
        }
        return super.onLongPress(paramKey);
    }

    public void setPhoneKeyboard(Keyboard paramKeyboard) {
        this.mPhoneKeyboard = paramKeyboard;
    }
}