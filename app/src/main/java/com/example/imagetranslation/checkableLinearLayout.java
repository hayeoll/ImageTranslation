package com.example.imagetranslation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RadioButton;


public class checkableLinearLayout extends LinearLayout implements Checkable {

    public checkableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 현재 check 상태 return
    @Override
    public boolean isChecked() {
        CheckBox cb = (CheckBox) findViewById(R.id.checkbox);

        return cb.isChecked();
    }

    // 현재 checked 상태를 바꿈 (UI 반영)
    @Override
    public void toggle() {
        CheckBox cb = (CheckBox) findViewById(R.id.checkbox);

        setChecked(cb.isChecked() ? false : true);
    }

    // checked 상태를 checked 변수대로 설정
    @Override
    public void setChecked(boolean checked) {
        CheckBox cb = (CheckBox) findViewById(R.id.checkbox);

        if(cb.isChecked() != checked) {
            cb.setChecked(checked);
        }
    }
}
