package com.noy.finalprojectdesign;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by noy on 28/05/2016.
 */
public class TimeEditText extends EditText implements TimePickerListener.onTimeSetListener {
    int hour;
    int minute;

    public TimeEditText(Context context) {
        super(context);
        init();
    }

    public TimeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimeEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setInputType(0);
        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);
        setText();

    }

    public void setText() {
        setText("" + ((hour < 10) ? "0" : "") + hour + ":" + ((minute < 10) ? "0" : "") + minute);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            TimePickerListener tpf = new TimePickerListener();
            tpf.setOnTimeSetListener(this);
            tpf.setTime(hour, minute);
            tpf.show(((Activity) getContext()).getFragmentManager(), "TAG");
        }
        return true;
    }

    @Override
    public void onTimeSet(int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        setText();
    }
}
