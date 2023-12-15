package com.zyfra.mdcplus.keyboard.view;

import android.content.Context;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NumberPicker extends LinearLayout {
    private static final char[] DIGIT_CHARACTERS;

    public static final Formatter TWO_DIGIT_FORMATTER = new Formatter() {
        final Object[] mArgs = new Object[1];

        final StringBuilder mBuilder = new StringBuilder();

        final java.util.Formatter mFmt = new java.util.Formatter(this.mBuilder);

        public String toString(int param1Int) {
            this.mArgs[0] = Integer.valueOf(param1Int);
            this.mBuilder.delete(0, this.mBuilder.length());
            this.mFmt.format("%02d", this.mArgs);
            return this.mFmt.toString();
        }
    };

    private int mCurrent;

    private boolean mDecrement;

    private NumberPickerButton mDecrementButton;

    private String[] mDisplayedValues;

    private int mEnd;

    private Formatter mFormatter;

    private final Handler mHandler;

    private boolean mIncrement;

    private NumberPickerButton mIncrementButton;

    private OnChangedListener mListener;

    private final InputFilter mNumberInputFilter;

    private int mPrevious;

    private final Runnable mRunnable = new Runnable() {
        public void run() {
            if (NumberPicker.this.mIncrement) {
                NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent + 1);
                NumberPicker.this.mHandler.postDelayed(this, NumberPicker.this.mSpeed);
                return;
            }
            if (NumberPicker.this.mDecrement) {
                NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent - 1);
                NumberPicker.this.mHandler.postDelayed(this, NumberPicker.this.mSpeed);
                return;
            }
        }
    };

    private long mSpeed = 300L;

    private int mStart;

    private final EditText mText;

    static {
        DIGIT_CHARACTERS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    }

    public NumberPicker(Context paramContext) {
        this(paramContext, (AttributeSet)null);
    }

    public NumberPicker(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        setOrientation(1);
        ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(2130903051, (ViewGroup)this, true);
        this.mHandler = new Handler();
        View.OnClickListener onClickListener = new View.OnClickListener() {
            public void onClick(View param1View) {
                NumberPicker.this.validateInput((View)NumberPicker.this.mText);
                if (!NumberPicker.this.mText.hasFocus())
                    NumberPicker.this.mText.requestFocus();
                if (2131165218 == param1View.getId()) {
                    NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent + 1);
                    return;
                }
                if (2131165220 == param1View.getId()) {
                    NumberPicker.this.changeCurrent(NumberPicker.this.mCurrent - 1);
                    return;
                }
            }
        };
        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            public void onFocusChange(View param1View, boolean param1Boolean) {
                if (!param1Boolean)
                    NumberPicker.this.validateInput(param1View);
            }
        };
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            public boolean onLongClick(View param1View) {
                NumberPicker.this.mText.clearFocus();
                if (2131165218 == param1View.getId()) {
                    NumberPicker.access$002(NumberPicker.this, true);
                    NumberPicker.this.mHandler.post(NumberPicker.this.mRunnable);
                    return true;
                }
                if (2131165220 == param1View.getId()) {
                    NumberPicker.access$402(NumberPicker.this, true);
                    NumberPicker.this.mHandler.post(NumberPicker.this.mRunnable);
                    return true;
                }
                return true;
            }
        };
        NumberPickerInputFilter numberPickerInputFilter = new NumberPickerInputFilter();
        this.mNumberInputFilter = (InputFilter)new NumberRangeKeyListener();
        this.mIncrementButton = (NumberPickerButton)findViewById(2131165218);
        this.mIncrementButton.setOnClickListener(onClickListener);
        this.mIncrementButton.setOnLongClickListener(onLongClickListener);
        this.mIncrementButton.setNumberPicker(this);
        this.mDecrementButton = (NumberPickerButton)findViewById(2131165220);
        this.mDecrementButton.setOnClickListener(onClickListener);
        this.mDecrementButton.setOnLongClickListener(onLongClickListener);
        this.mDecrementButton.setNumberPicker(this);
        this.mText = (EditText)findViewById(2131165219);
        this.mText.setOnFocusChangeListener(onFocusChangeListener);
        this.mText.setFilters(new InputFilter[] { numberPickerInputFilter });
        this.mText.setRawInputType(2);
        if (!isEnabled())
            setEnabled(false);
    }

    private String formatNumber(int paramInt) {
        return (this.mFormatter != null) ? this.mFormatter.toString(paramInt) : String.valueOf(paramInt);
    }

    private int getSelectedPos(String paramString) {
        if (this.mDisplayedValues == null) {
            try {
                return Integer.parseInt(paramString);
            } catch (NumberFormatException numberFormatException) {}
        } else {
            String str;
            int i;
            for (i = 0; i < this.mDisplayedValues.length; i++) {
                str = numberFormatException.toLowerCase();
                if (this.mDisplayedValues[i].toLowerCase().startsWith(str))
                    return this.mStart + i;
            }
            try {
                return Integer.parseInt(str);
            } catch (NumberFormatException numberFormatException1) {}
        }
        return this.mStart;
    }

    private void notifyChange() {
        if (this.mListener != null)
            this.mListener.onChanged(this, this.mPrevious, this.mCurrent);
    }

    private void updateView() {
        if (this.mDisplayedValues == null) {
            this.mText.setText(formatNumber(this.mCurrent));
        } else {
            this.mText.setText(this.mDisplayedValues[this.mCurrent - this.mStart]);
        }
        this.mText.setSelection(this.mText.getText().length());
    }

    private void validateCurrentView(CharSequence paramCharSequence) {
        int i = getSelectedPos(paramCharSequence.toString());
        if (i >= this.mStart && i <= this.mEnd && this.mCurrent != i) {
            this.mPrevious = this.mCurrent;
            this.mCurrent = i;
            notifyChange();
        }
        updateView();
    }

    private void validateInput(View paramView) {
        String str = String.valueOf(((TextView)paramView).getText());
        if ("".equals(str)) {
            updateView();
            return;
        }
        validateCurrentView(str);
    }

    public void cancelDecrement() {
        this.mDecrement = false;
    }

    public void cancelIncrement() {
        this.mIncrement = false;
    }

    protected void changeCurrent(int paramInt) {
        int i;
        if (paramInt > this.mEnd) {
            i = this.mStart;
        } else {
            i = paramInt;
            if (paramInt < this.mStart)
                i = this.mEnd;
        }
        this.mPrevious = this.mCurrent;
        this.mCurrent = i;
        notifyChange();
        updateView();
    }

    protected int getBeginRange() {
        return this.mStart;
    }

    public int getCurrent() {
        return this.mCurrent;
    }

    protected int getEndRange() {
        return this.mEnd;
    }

    public void setCurrent(int paramInt) {
        if (paramInt < this.mStart || paramInt > this.mEnd)
            throw new IllegalArgumentException("current should be >= start and <= end");
        this.mCurrent = paramInt;
        updateView();
    }

    public void setEnabled(boolean paramBoolean) {
        super.setEnabled(paramBoolean);
        this.mIncrementButton.setEnabled(paramBoolean);
        this.mDecrementButton.setEnabled(paramBoolean);
        this.mText.setEnabled(paramBoolean);
    }

    public void setFormatter(Formatter paramFormatter) {
        this.mFormatter = paramFormatter;
    }

    public void setOnChangeListener(OnChangedListener paramOnChangedListener) {
        this.mListener = paramOnChangedListener;
    }

    public void setRange(int paramInt1, int paramInt2) {
        setRange(paramInt1, paramInt2, (String[])null);
    }

    public void setRange(int paramInt1, int paramInt2, String[] paramArrayOfString) {
        this.mDisplayedValues = paramArrayOfString;
        this.mStart = paramInt1;
        this.mEnd = paramInt2;
        this.mCurrent = paramInt1;
        updateView();
    }

    public void setSpeed(long paramLong) {
        this.mSpeed = paramLong;
    }

    public static interface Formatter {
        String toString(int param1Int);
    }

    private class NumberPickerInputFilter implements InputFilter {
        private NumberPickerInputFilter() {}

        public CharSequence filter(CharSequence param1CharSequence, int param1Int1, int param1Int2, Spanned param1Spanned, int param1Int3, int param1Int4) {
            if (NumberPicker.this.mDisplayedValues == null)
                return NumberPicker.this.mNumberInputFilter.filter(param1CharSequence, param1Int1, param1Int2, param1Spanned, param1Int3, param1Int4);
            String str2 = String.valueOf(param1CharSequence.subSequence(param1Int1, param1Int2));
            String str1 = String.valueOf(String.valueOf(param1Spanned.subSequence(0, param1Int3)) + str2 + param1Spanned.subSequence(param1Int4, param1Spanned.length())).toLowerCase();
            String[] arrayOfString = NumberPicker.this.mDisplayedValues;
            param1Int2 = arrayOfString.length;
            param1Int1 = 0;
            while (param1Int1 < param1Int2) {
                param1CharSequence = str2;
                if (!arrayOfString[param1Int1].toLowerCase().startsWith(str1)) {
                    param1Int1++;
                    continue;
                }
                return param1CharSequence;
            }
            return "";
        }
    }

    private class NumberRangeKeyListener extends NumberKeyListener {
        private NumberRangeKeyListener() {}

        public CharSequence filter(CharSequence param1CharSequence, int param1Int1, int param1Int2, Spanned param1Spanned, int param1Int3, int param1Int4) {
            CharSequence charSequence2 = super.filter(param1CharSequence, param1Int1, param1Int2, param1Spanned, param1Int3, param1Int4);
            CharSequence charSequence1 = charSequence2;
            if (charSequence2 == null)
                charSequence1 = param1CharSequence.subSequence(param1Int1, param1Int2);
            param1CharSequence = String.valueOf(param1Spanned.subSequence(0, param1Int3)) + charSequence1 + param1Spanned.subSequence(param1Int4, param1Spanned.length());
            return "".equals(param1CharSequence) ? param1CharSequence : ((NumberPicker.this.getSelectedPos((String)param1CharSequence) > NumberPicker.this.mEnd) ? "" : charSequence1);
        }

        protected char[] getAcceptedChars() {
            return NumberPicker.DIGIT_CHARACTERS;
        }

        public int getInputType() {
            return 2;
        }
    }

    public static interface OnChangedListener {
        void onChanged(NumberPicker param1NumberPicker, int param1Int1, int param1Int2);
    }
}
