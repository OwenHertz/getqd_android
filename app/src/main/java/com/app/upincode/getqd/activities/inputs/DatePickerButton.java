package com.app.upincode.getqd.activities.inputs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link AppCompatButton} that uses {@link DatePickerDialog} when uses clicks the button.
 */
public class DatePickerButton extends AppCompatButton implements DatePickerDialog.OnDateSetListener {
    private List<DatePickerDialog.OnDateSetListener> dateSetListeners = new ArrayList<>();
    private int year;
    private int monthOfYear;
    private int dayOfMonth;

    public DatePickerButton(Context context) {
        super(context);
        this.init();
    }

    public DatePickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public DatePickerButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    protected void init() {
        this.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginSelectDate();
            }
        });

        this.setValue(new DateTime());
    }

    protected void beginSelectDate() {
        DatePickerDialog dpd = new DatePickerDialog(getContext(), this,
                this.year, this.monthOfYear, this.dayOfMonth);
        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // Update calendar object
        this.setValue(year, monthOfYear, dayOfMonth);

        for (DatePickerDialog.OnDateSetListener listener : dateSetListeners) {
            listener.onDateSet(view, year, monthOfYear, dayOfMonth);
        }
    }

    protected void updateButtonText() {
        DateFormat df = android.text.format.DateFormat.getLongDateFormat(getContext());
        this.setText(df.format(this.getDateTime().toDate()));
    }

    public void setValue(DateTime dt) {
        this.setValue(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
    }

    public void setValue(int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.monthOfYear = monthOfYear;
        this.dayOfMonth = dayOfMonth;
        this.updateButtonText();
    }

    public DateTime getDateTime() {
        return new DateTime().withYear(year).withMonthOfYear(monthOfYear).withDayOfMonth(dayOfMonth);
    }

    /**
     * Adds a listener to subscribe to changes to the selected calendar
     *
     * @param listener
     */
    public void addDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        dateSetListeners.add(listener);
    }
}
