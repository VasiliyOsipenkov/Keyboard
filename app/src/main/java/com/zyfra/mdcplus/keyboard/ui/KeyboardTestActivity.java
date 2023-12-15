package com.zyfra.mdcplus.keyboard.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

public class KeyboardTestActivity extends Activity {
    private View codeTitleView;

    private TextView codeView;

    private TextView deviceInfoView;

    private EditText inputView;

    private TextView metaInfoView;

    private View scanCodeTitleView;

    private TextView scanCodeView;

    private CheckBox showScanCodeView;

    private void updateViews(KeyEvent paramKeyEvent) {
        this.codeView.setText(String.valueOf(paramKeyEvent.getKeyCode()));
        this.scanCodeView.setText(String.valueOf(paramKeyEvent.getScanCode()));
        this.metaInfoView.setText("alt:" + paramKeyEvent.isAltPressed() + ", shift:" + paramKeyEvent.isShiftPressed());
    }

    public void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(2130903042);//xml
        this.codeView = (TextView)findViewById(2131165198);//xml
        this.codeTitleView = findViewById(2131165196);//xml
        this.scanCodeView = (TextView)findViewById(2131165197);//xml
        this.scanCodeTitleView = findViewById(2131165195);//xml
        this.showScanCodeView = (CheckBox)findViewById(2131165194);//xml
        this.showScanCodeView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton param1CompoundButton, boolean param1Boolean) {
                byte b;
                boolean bool = false;
                TextView textView = KeyboardTestActivity.this.scanCodeView;
                if (param1Boolean) {
                    b = 0;
                } else {
                    b = 8;
                }
                textView.setVisibility(b);
                View view = KeyboardTestActivity.this.scanCodeTitleView;
                if (param1Boolean) {
                    b = bool;
                } else {
                    b = 8;
                }
                view.setVisibility(b);
            }
        });
        this.inputView = (EditText)findViewById(2131165201);//xml
        this.inputView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View param1View, int param1Int, KeyEvent param1KeyEvent) {
                KeyboardTestActivity.this.updateViews(param1KeyEvent);
                return false;
            }
        });
        this.inputView.requestFocus();
        this.metaInfoView = (TextView)findViewById(2131165200);//xml
        this.deviceInfoView = (TextView)findViewById(2131165199);//xml
        this.deviceInfoView.append("board: " + Build.BOARD);
        this.deviceInfoView.append("\n");
        this.deviceInfoView.append("product: " + Build.PRODUCT);
        this.deviceInfoView.append("\n");
        this.deviceInfoView.append("device: " + Build.DEVICE);
        this.deviceInfoView.append("\n");
        this.deviceInfoView.append("display: " + Build.DISPLAY);
        this.deviceInfoView.append("\n");
        this.deviceInfoView.append("brand: " + Build.BRAND);
    }
}
