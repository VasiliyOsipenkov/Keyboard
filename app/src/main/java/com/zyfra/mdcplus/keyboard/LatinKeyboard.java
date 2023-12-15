package com.zyfra.mdcplus.keyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;

public class LatinKeyboard extends Keyboard {
    private static final int SHIFT_LOCKED = 2;

    private static final int SHIFT_OFF = 0;

    private static final int SHIFT_ON = 1;

    static int sSpacebarVerticalCorrection;

    private int icon;

    private Keyboard.Key mEnterKey;

    private Drawable mOldShiftIcon;

    private Drawable mOldShiftPreviewIcon;

    private Keyboard.Key mShiftKey;

    private Drawable mShiftLockIcon;

    private Drawable mShiftLockPreviewIcon;

    private int mShiftState = 0;

    public LatinKeyboard(Context paramContext, int paramInt) {
        this(paramContext, paramInt, 0);
    }

    public LatinKeyboard(Context paramContext, int paramInt1, int paramInt2) {
        super(paramContext, paramInt1, paramInt2);
        Resources resources = paramContext.getResources();
        this.mShiftLockIcon = paramContext.getResources().getDrawable(2130837557);
        this.mShiftLockPreviewIcon = paramContext.getResources().getDrawable(2130837539);
        this.mShiftLockPreviewIcon.setBounds(0, 0, this.mShiftLockPreviewIcon.getIntrinsicWidth(), this.mShiftLockPreviewIcon.getIntrinsicHeight());
        sSpacebarVerticalCorrection = resources.getDimensionPixelOffset(2131492869);
    }

    public LatinKeyboard(Context paramContext, int paramInt1, CharSequence paramCharSequence, int paramInt2, int paramInt3) {
        super(paramContext, paramInt1, paramCharSequence, paramInt2, paramInt3);
    }

    protected Keyboard.Key createKeyFromXml(Resources paramResources, Keyboard.Row paramRow, int paramInt1, int paramInt2, XmlResourceParser paramXmlResourceParser) {
        LatinKey latinKey = new LatinKey(paramResources, paramRow, paramInt1, paramInt2, paramXmlResourceParser);
        if (latinKey.codes[0] == 10)
            this.mEnterKey = latinKey;
        return latinKey;
    }

    void enableShiftLock() {
        int i = getShiftKeyIndex();
        if (i >= 0) {
            this.mShiftKey = getKeys().get(i);
            if (this.mShiftKey instanceof LatinKey)
                ((LatinKey)this.mShiftKey).enableShiftLock();
            this.mOldShiftIcon = this.mShiftKey.icon;
            this.mOldShiftPreviewIcon = this.mShiftKey.iconPreview;
        }
    }

    public int getIcon() {
        return this.icon;
    }

    boolean isShiftLocked() {
        return (this.mShiftState == 2);
    }

    public boolean isShifted() {
        return (this.mShiftKey != null) ? ((this.mShiftState != 0)) : super.isShifted();
    }

    public void setIcon(int paramInt) {
        this.icon = paramInt;
    }

    void setImeOptions(Resources paramResources, int paramInt1, int paramInt2) {
        if (this.mEnterKey != null) {
            this.mEnterKey.popupCharacters = null;
            this.mEnterKey.popupResId = 0;
            this.mEnterKey.text = null;
            switch (0x400000FF & paramInt2) {
                default:
                    if (paramInt1 == 6) {
                        this.mEnterKey.icon = null;
                        this.mEnterKey.iconPreview = null;
                        this.mEnterKey.label = ":-)";
                        this.mEnterKey.text = ":-) ";
                        this.mEnterKey.popupResId = 2130968588;
                    } else {
                        break;
                    }
                    if (this.mEnterKey.iconPreview != null)
                        this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
                    return;
                case 2:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = paramResources.getText(2131427334);
                    if (this.mEnterKey.iconPreview != null)
                        this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
                    return;
                case 5:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = paramResources.getText(2131427335);
                    if (this.mEnterKey.iconPreview != null)
                        this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
                    return;
                case 6:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = paramResources.getText(2131427336);
                    if (this.mEnterKey.iconPreview != null)
                        this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
                    return;
                case 3:
                    this.mEnterKey.iconPreview = paramResources.getDrawable(2130837537);
                    this.mEnterKey.icon = paramResources.getDrawable(2130837555);
                    this.mEnterKey.label = null;
                    if (this.mEnterKey.iconPreview != null)
                        this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
                    return;
                case 4:
                    this.mEnterKey.iconPreview = null;
                    this.mEnterKey.icon = null;
                    this.mEnterKey.label = paramResources.getText(2131427337);
                    if (this.mEnterKey.iconPreview != null)
                        this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
                    return;
            }
        } else {
            return;
        }
        this.mEnterKey.iconPreview = paramResources.getDrawable(2130837536);
        this.mEnterKey.icon = paramResources.getDrawable(2130837554);
        this.mEnterKey.label = null;
        if (this.mEnterKey.iconPreview != null)
            this.mEnterKey.iconPreview.setBounds(0, 0, this.mEnterKey.iconPreview.getIntrinsicWidth(), this.mEnterKey.iconPreview.getIntrinsicHeight());
    }

    void setShiftLocked(boolean paramBoolean) {
        if (this.mShiftKey != null) {
            if (paramBoolean) {
                this.mShiftKey.on = true;
                this.mShiftKey.icon = this.mShiftLockIcon;
                this.mShiftState = 2;
                return;
            }
        } else {
            return;
        }
        this.mShiftKey.on = false;
        this.mShiftKey.icon = this.mShiftLockIcon;
        this.mShiftState = 1;
    }

    public boolean setShifted(boolean paramBoolean) {
        boolean bool = false;
        if (this.mShiftKey != null) {
            if (!paramBoolean) {
                if (this.mShiftState != 0) {
                    paramBoolean = true;
                } else {
                    paramBoolean = false;
                }
                this.mShiftState = 0;
                this.mShiftKey.on = false;
                this.mShiftKey.icon = this.mOldShiftIcon;
                return paramBoolean;
            }
            paramBoolean = bool;
            if (this.mShiftState == 0) {
                if (this.mShiftState == 0) {
                    paramBoolean = true;
                } else {
                    paramBoolean = false;
                }
                this.mShiftState = 1;
                this.mShiftKey.icon = this.mShiftLockIcon;
            }
            return paramBoolean;
        }
        return super.setShifted(paramBoolean);
    }

    static class LatinKey extends Keyboard.Key {
        private boolean mShiftLockEnabled;

        public LatinKey(Resources param1Resources, Keyboard.Row param1Row, int param1Int1, int param1Int2, XmlResourceParser param1XmlResourceParser) {
            super(param1Resources, param1Row, param1Int1, param1Int2, param1XmlResourceParser);
            if (this.popupCharacters != null && this.popupCharacters.length() == 0)
                this.popupResId = 0;
        }

        void enableShiftLock() {
            this.mShiftLockEnabled = true;
        }

        public boolean isInside(int param1Int1, int param1Int2) {
            int k = this.codes[0];
            if (k == -1 || k == -5) {
                int i1 = param1Int2 - this.height / 10;
                param1Int2 = param1Int1;
                if (k == -1)
                    param1Int2 = param1Int1 + this.width / 6;
                int m = param1Int2;
                int n = i1;
                if (k == -5) {
                    m = param1Int2 - this.width / 6;
                    n = i1;
                }
                return super.isInside(m, n);
            }
            int i = param1Int1;
            int j = param1Int2;
            if (k == 32) {
                j = param1Int2 + LatinKeyboard.sSpacebarVerticalCorrection;
                i = param1Int1;
            }
            return super.isInside(i, j);
        }

        public void onReleased(boolean param1Boolean) {
            if (!this.mShiftLockEnabled) {
                super.onReleased(param1Boolean);
                return;
            }
            if (!this.pressed) {
                param1Boolean = true;
            } else {
                param1Boolean = false;
            }
            this.pressed = param1Boolean;
        }
    }
}

