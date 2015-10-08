package com.app.upincode.getqd.activities;

import android.databinding.BindingAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.app.upincode.getqd.GlobalClass;
import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.toolbars.GQMainToolbar;

/**
 * Created by jpnauta on 15-09-30.
 */
public abstract class GQBaseActivity extends AppCompatActivity {
    protected GQMainToolbar toolbar;

    public GlobalClass getGlobalClass() {
        return (GlobalClass) getApplicationContext();
    }

    /**
     * Initializes a toolbar without any buttons.
     * <p/>
     * Be sure to include this in the view:
     * <p/>
     * <include android:id="@+id/toolbar" layout="@layout/toolbar" />
     * <p/>
     */
    public void initializeToolbar() {
        //Has an error occurred here? Make sure you add the toolbar to the view (see 'include' above)
        toolbar = (GQMainToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * Creates a toolbar with a back button as its action button
     * <p/>
     * Be sure to include this in the view:
     * <p/>
     * <include android:id="@+id/toolbar" layout="@layout/toolbar" />
     * <p/>
     */
    public void initBackButtonToolbar() {
        initializeToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Add listener to go back when button is clicked
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Creates a toolbar with a menu as its action button
     * <p/>
     * Be sure to include this in the view:
     * <p/>
     * <include android:id="@+id/toolbar" layout="@layout/toolbar" />
     * <p/>
     */
    public void initMenuButtonToolbar() {
        initializeToolbar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
}
