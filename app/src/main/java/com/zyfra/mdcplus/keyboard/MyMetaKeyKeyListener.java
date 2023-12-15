package com.zyfra.mdcplus.keyboard;

import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Spannable;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;

public abstract class MyMetaKeyKeyListener {
    private static final Object ALT;

    private static final Object CAP = new NoCopySpan.Concrete();

    private static final int LOCKED = 67108881;

    private static final int LOCKED_SHIFT = 8;

    public static final int META_ALT_LOCKED = 512;

    private static final long META_ALT_MASK = 2207646745090L;

    public static final int META_ALT_ON = 2;

    private static final long META_ALT_PRESSED = 8589934592L;

    private static final long META_ALT_RELEASED = 2199023255552L;

    private static final long META_ALT_USED = 33554432L;

    public static final int META_CAP_LOCKED = 256;

    private static final long META_CAP_PRESSED = 4294967296L;

    private static final long META_CAP_RELEASED = 1099511627776L;

    private static final long META_CAP_USED = 16777216L;

    public static final int META_SELECTING = 65536;

    private static final long META_SHIFT_MASK = 1103823372545L;

    public static final int META_SHIFT_ON = 1;

    public static final int META_SYM_LOCKED = 1024;

    private static final long META_SYM_MASK = 4415293490180L;

    public static final int META_SYM_ON = 4;

    private static final long META_SYM_PRESSED = 17179869184L;

    private static final long META_SYM_RELEASED = 4398046511104L;

    private static final long META_SYM_USED = 67108864L;

    private static final int PRESSED = 16777233;

    private static final int PRESSED_SHIFT = 32;

    private static final int RELEASED = 33554449;

    private static final int RELEASED_SHIFT = 40;

    private static final Object SELECTING;

    private static final Object SYM;

    private static final int USED = 50331665;

    private static final int USED_SHIFT = 24;

    static {
        ALT = new NoCopySpan.Concrete();
        SYM = new NoCopySpan.Concrete();
        SELECTING = new NoCopySpan.Concrete();
    }

    private static long adjust(long paramLong1, int paramInt, long paramLong2) {
        if ((paramInt << 32L & paramLong1) != 0L)
            return (paramLong2 ^ 0xFFFFFFFFFFFFFFFFL) & paramLong1 | paramInt | paramInt << 24L;
        long l = paramLong1;
        return ((paramInt << 40L & paramLong1) != 0L) ? (paramLong1 & (paramLong2 ^ 0xFFFFFFFFFFFFFFFFL)) : l;
    }

    private static void adjust(Spannable paramSpannable, Object paramObject) {
        int i = paramSpannable.getSpanFlags(paramObject);
        if (i == 16777233) {
            paramSpannable.setSpan(paramObject, 0, 0, 50331665);
            return;
        }
        if (i == 33554449) {
            paramSpannable.removeSpan(paramObject);
            return;
        }
    }

    public static long adjustMetaAfterKeypress(long paramLong) {
        return adjust(adjust(adjust(paramLong, 1, 1103823372545L), 2, 2207646745090L), 4, 4415293490180L);
    }

    public static void adjustMetaAfterKeypress(Spannable paramSpannable) {
        adjust(paramSpannable, CAP);
        adjust(paramSpannable, ALT);
        adjust(paramSpannable, SYM);
    }

    public static void clearMetaKeyState(Editable paramEditable, int paramInt) {
        if ((paramInt & 0x1) != 0)
            paramEditable.removeSpan(CAP);
        if ((paramInt & 0x2) != 0)
            paramEditable.removeSpan(ALT);
        if ((paramInt & 0x4) != 0)
            paramEditable.removeSpan(SYM);
        if ((0x10000 & paramInt) != 0)
            paramEditable.removeSpan(SELECTING);
    }

    private static int getActive(long paramLong, int paramInt1, int paramInt2, int paramInt3) {
        return (((paramInt1 << 8) & paramLong) != 0L) ? paramInt3 : (((paramInt1 & paramLong) != 0L) ? paramInt2 : 0);
    }

    private static int getActive(CharSequence paramCharSequence, Object paramObject, int paramInt1, int paramInt2) {
        if (!(paramCharSequence instanceof Spanned))
            return 0;
        int i = ((Spanned)paramCharSequence).getSpanFlags(paramObject);
        return (i != 67108881) ? ((i != 0) ? paramInt1 : 0) : paramInt2;
    }

    public static final int getMetaState(long paramLong) {
        return getActive(paramLong, 1, 1, 256) | getActive(paramLong, 2, 2, 512) | getActive(paramLong, 4, 4, 1024);
    }

    public static final int getMetaState(long paramLong, int paramInt) {
        switch (paramInt) {
            default:
                return 0;
            case 1:
                return getActive(paramLong, paramInt, 1, 2);
            case 2:
                return getActive(paramLong, paramInt, 1, 2);
            case 4:
                break;
        }
        return getActive(paramLong, paramInt, 1, 2);
    }

    public static final int getMetaState(CharSequence paramCharSequence) {
        return getActive(paramCharSequence, CAP, 1, 256) | getActive(paramCharSequence, ALT, 2, 512) | getActive(paramCharSequence, SYM, 4, 1024) | getActive(paramCharSequence, SELECTING, 65536, 65536);
    }

    public static final int getMetaState(CharSequence paramCharSequence, int paramInt) {
        switch (paramInt) {
            default:
                return 0;
            case 1:
                return getActive(paramCharSequence, CAP, 1, 2);
            case 2:
                return getActive(paramCharSequence, ALT, 1, 2);
            case 4:
                return getActive(paramCharSequence, SYM, 1, 2);
            case 65536:
                break;
        }
        return getActive(paramCharSequence, SELECTING, 1, 2);
    }

    public static long handleKeyDown(long paramLong, int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 59 || paramInt == 60)
            return press(paramLong, 1, 1103823372545L);
        if (paramInt == 57 || paramInt == 58 || paramInt == 78)
            return press(paramLong, 2, 2207646745090L);
        long l = paramLong;
        return (paramInt == 63) ? press(paramLong, 4, 4415293490180L) : l;
    }

    public static long handleKeyUp(long paramLong, int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 59 || paramInt == 60)
            return release(paramLong, 1, 1103823372545L);
        if (paramInt == 57 || paramInt == 58 || paramInt == 78)
            return release(paramLong, 2, 2207646745090L);
        long l = paramLong;
        return (paramInt == 63) ? release(paramLong, 4, 4415293490180L) : l;
    }

    public static boolean isMetaTracker(CharSequence paramCharSequence, Object paramObject) {
        return (paramObject == CAP || paramObject == ALT || paramObject == SYM || paramObject == SELECTING);
    }

    public static boolean isSelectingMetaTracker(CharSequence paramCharSequence, Object paramObject) {
        return (paramObject == SELECTING);
    }

    private static long press(long paramLong1, int paramInt, long paramLong2) {
        if ((paramInt << 32L & paramLong1) == 0L) {
            if ((paramInt << 40L & paramLong1) != 0L)
                return (paramLong2 ^ 0xFFFFFFFFFFFFFFFFL) & paramLong1 | paramInt | paramInt << 8L;
            if ((paramInt << 24L & paramLong1) == 0L)
                return ((paramInt << 8L & paramLong1) != 0L) ? (paramLong1 & (paramLong2 ^ 0xFFFFFFFFFFFFFFFFL)) : ((paramInt | paramLong1 | paramInt << 32L) & (paramInt << 40L ^ 0xFFFFFFFFFFFFFFFFL));
        }
        return paramLong1;
    }

    private void press(Editable paramEditable, Object paramObject) {
        int i = paramEditable.getSpanFlags(paramObject);
        if (i != 16777233) {
            if (i == 33554449) {
                paramEditable.setSpan(paramObject, 0, 0, 67108881);
                return;
            }
            if (i != 50331665) {
                if (i == 67108881) {
                    paramEditable.removeSpan(paramObject);
                    return;
                }
                paramEditable.setSpan(paramObject, 0, 0, 16777233);
                return;
            }
        }
    }

    private static long release(long paramLong1, int paramInt, long paramLong2) {
        if ((paramInt << 24L & paramLong1) != 0L)
            return paramLong1 & (paramLong2 ^ 0xFFFFFFFFFFFFFFFFL);
        paramLong2 = paramLong1;
        return ((paramInt << 32L & paramLong1) != 0L) ? ((paramInt | paramLong1 | paramInt << 40L) & (paramInt << 32L ^ 0xFFFFFFFFFFFFFFFFL)) : paramLong2;
    }

    private void release(Editable paramEditable, Object paramObject) {
        int i = paramEditable.getSpanFlags(paramObject);
        if (i == 50331665) {
            paramEditable.removeSpan(paramObject);
            return;
        }
        if (i == 16777233) {
            paramEditable.setSpan(paramObject, 0, 0, 33554449);
            return;
        }
    }

    private static long resetLock(long paramLong1, int paramInt, long paramLong2) {
        long l = paramLong1;
        if ((paramInt << 8L & paramLong1) != 0L)
            l = paramLong1 & (0xFFFFFFFFFFFFFFFFL ^ paramLong2);
        return l;
    }

    private static void resetLock(Spannable paramSpannable, Object paramObject) {
        if (paramSpannable.getSpanFlags(paramObject) == 67108881)
            paramSpannable.removeSpan(paramObject);
    }

    public static long resetLockedMeta(long paramLong) {
        return resetLock(resetLock(resetLock(paramLong, 1, 1103823372545L), 2, 2207646745090L), 4, 4415293490180L);
    }

    protected static void resetLockedMeta(Spannable paramSpannable) {
        resetLock(paramSpannable, CAP);
        resetLock(paramSpannable, ALT);
        resetLock(paramSpannable, SYM);
        resetLock(paramSpannable, SELECTING);
    }

    public static void resetMetaState(Spannable paramSpannable) {
        paramSpannable.removeSpan(CAP);
        paramSpannable.removeSpan(ALT);
        paramSpannable.removeSpan(SYM);
        paramSpannable.removeSpan(SELECTING);
    }

    public static void startSelecting(View paramView, Spannable paramSpannable) {
        paramSpannable.setSpan(SELECTING, 0, 0, 16777233);
    }

    public static void stopSelecting(View paramView, Spannable paramSpannable) {
        paramSpannable.removeSpan(SELECTING);
    }

    public long clearMetaKeyState(long paramLong, int paramInt) {
        long l = paramLong;
        if ((paramInt & 0x1) != 0)
            l = resetLock(paramLong, 1, 1103823372545L);
        paramLong = l;
        if ((paramInt & 0x2) != 0)
            paramLong = resetLock(l, 2, 2207646745090L);
        l = paramLong;
        if ((paramInt & 0x4) != 0)
            l = resetLock(paramLong, 4, 4415293490180L);
        return l;
    }

    public void clearMetaKeyState(View paramView, Editable paramEditable, int paramInt) {
        clearMetaKeyState(paramEditable, paramInt);
    }

    public boolean onKeyDown(View paramView, Editable paramEditable, int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 59 || paramInt == 60) {
            press(paramEditable, CAP);
            return true;
        }
        if (paramInt == 57 || paramInt == 58 || paramInt == 78) {
            press(paramEditable, ALT);
            return true;
        }
        if (paramInt == 63) {
            press(paramEditable, SYM);
            return true;
        }
        return false;
    }

    public boolean onKeyUp(View paramView, Editable paramEditable, int paramInt, KeyEvent paramKeyEvent) {
        if (paramInt == 59 || paramInt == 60) {
            release(paramEditable, CAP);
            return true;
        }
        if (paramInt == 57 || paramInt == 58 || paramInt == 78) {
            release(paramEditable, ALT);
            return true;
        }
        if (paramInt == 63) {
            release(paramEditable, SYM);
            return true;
        }
        return false;
    }
}

