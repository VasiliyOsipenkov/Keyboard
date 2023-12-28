package com.zyfra.mdcplus.keyboard.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Vibrator;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.zyfra.mdcplus.keyboard.R;

public class NumberPickerPreference extends DialogPreference implements NumberPicker.OnChangedListener {
    private int currentValue;

    private Vibrator mVibrator;

    private NumberPicker np;

    public NumberPickerPreference(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        initialize();
    }

    public NumberPickerPreference(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        initialize();
    }

    private void initialize() {
        setPersistent(true);
    }

    private void setValue(int paramInt) {
        this.currentValue = paramInt;
        persistInt(paramInt);
    }

    protected void onBindDialogView(View paramView) {
        super.onBindDialogView(paramView);
        this.np.setCurrent(this.currentValue);
    }

    public void onChanged(NumberPicker paramNumberPicker, int paramInt1, int paramInt2) {
        persistInt(paramInt2);
    }

    protected View onCreateDialogView() {
        View view = ((LayoutInflater)getContext().getSystemService("layout_inflater")).inflate(R.layout.number_picker_prefs, null);
        this.np = (NumberPicker)view.findViewById(R.id.picker);
        this.np.setOnChangeListener(this);
        this.np.setRange(0, 100);
        view.findViewById(R.id.testVibrate).setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                if (NumberPickerPreference.this.mVibrator == null)
                    NumberPickerPreference.access$002(NumberPickerPreference.this, (Vibrator)NumberPickerPreference.this.getContext().getSystemService("vibrator"));
                NumberPickerPreference.this.mVibrator.vibrate(NumberPickerPreference.this.np.getCurrent());
            }
        });
        return view;
    }

    protected void onDialogClosed(boolean paramBoolean) {
        super.onDialogClosed(paramBoolean);
        if (paramBoolean) {
            int i = this.np.getCurrent();
            if (callChangeListener(Integer.valueOf(i)))
                setValue(i);
            notifyChanged();
        }
    }

    protected Object onGetDefaultValue(TypedArray paramTypedArray, int paramInt) {
        return Integer.valueOf(paramTypedArray.getInt(paramInt, 16));
    }

    protected void onSetInitialValue(boolean paramBoolean, Object paramObject) {
        int i;
        if (paramBoolean) {
            i = getPersistedInt(this.currentValue);
        } else {
            i = ((Integer)paramObject).intValue();
        }
        setValue(i);
    }
}

