package com.app.upincode.getqd.activities.inputs;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link AppCompatButton} that uses {@link TimePickerDialog} when uses clicks the button.
 */
public class TimePickerButton extends AppCompatButton implements TimePickerDialog.OnTimeSetListener {
    private List<TimePickerDialog.OnTimeSetListener> timeSetListeners = new ArrayList<>();
    private int hourOfDay;
    private int minute;

    public TimePickerButton(Context context) {
        super(context);
        this.init();
    }

    public TimePickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public TimePickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    protected void init() {
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                beginSelectTime();
            }
        });

        this.setValue(new DateTime());
    }

    protected void beginSelectTime() {
        TimePickerDialog tpd = new TimePickerDialog(getContext(), this,
                this.hourOfDay, this.minute, DateFormat.is24HourFormat(getContext()));
        tpd.show();
    }

    protected void updateButtonText() {
        java.text.DateFormat df = android.text.format.DateFormat.getTimeFormat(getContext());
        this.setText(df.format(this.getDateTime().toDate()));
    }

    public void setValue(DateTime dt) {
        this.setValue(dt.getHourOfDay(), dt.getMinuteOfHour());
    }

    public void setValue(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay;
        this.minute = minute;
        this.updateButtonText();
    }

    public DateTime getDateTime() {
        return new DateTime().withHourOfDay(hourOfDay).withMinuteOfHour(minute);
    }

    /**
     * Adds a listener to subscribe to changes to the selected calendar
     *
     * @param listener
     */
    public void addTimeSetListener(TimePickerDialog.OnTimeSetListener listener) {
        timeSetListeners.add(listener);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Update calendar object
        this.setValue(hourOfDay, minute);

        for (TimePickerDialog.OnTimeSetListener listener : timeSetListeners) {
            listener.onTimeSet(view, hourOfDay, minute);
        }
    }
}
