package com.zyfra.mdcplus.keyboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Button;

class NumberPickerButton extends Button {
    private NumberPicker mNumberPicker;

    public NumberPickerButton(Context paramContext) {
        super(paramContext);
    }

    public NumberPickerButton(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public NumberPickerButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    private void cancelLongpress() {
        if (2131165218 == getId()) {
            this.mNumberPicker.cancelIncrement();
            return;
        }
        if (2131165220 == getId()) {
            this.mNumberPicker.cancelDecrement();
            return;
        }
    }

    private void cancelLongpressIfRequired(MotionEvent paramMotionEvent) {
        if (paramMotionEvent.getAction() == 3 || paramMotionEvent.getAction() == 1)
            cancelLongpress();
    }

    public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 23 || paramInt == 66)
            cancelLongpress();
        return super.onKeyUp(paramInt, paramKeyEvent);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent) {
        cancelLongpressIfRequired(paramMotionEvent);
        return super.onTouchEvent(paramMotionEvent);
    }

    public boolean onTrackballEvent(MotionEvent paramMotionEvent) {
        cancelLongpressIfRequired(paramMotionEvent);
        return super.onTrackballEvent(paramMotionEvent);
    }

    public void setNumberPicker(NumberPicker paramNumberPicker) {
        this.mNumberPicker = paramNumberPicker;
    }
}

