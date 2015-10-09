package com.app.upincode.getqd.activities.inputs;

import android.view.View;
import android.widget.AdapterView;

public abstract class NoDefaultSpinnerItemSelectedAdapter implements AdapterView.OnItemSelectedListener {

    public abstract void onRealItemSelected(AdapterView<?> parent, View view, int position, long id);

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            onRealItemSelected(parent, view, position - 1, id);
        } else {
            onNothingSelected(parent);
        }
    }
}