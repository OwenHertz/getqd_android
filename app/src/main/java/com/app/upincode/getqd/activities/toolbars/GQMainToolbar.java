package com.app.upincode.getqd.activities.toolbars;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

import com.app.upincode.getqd.R;
import com.app.upincode.getqd.activities.frags.GQMainEventsFragment;
import com.app.upincode.getqd.activities.frags.GQMainHomeFragment;
import com.app.upincode.getqd.activities.frags.GQMainLogoutFragment;
import com.app.upincode.getqd.activities.frags.GQMyTicketsFragment;
import com.app.upincode.getqd.activities.frags.GQMainProfileFragment;
import com.app.upincode.getqd.activities.frags.GQMainVenueFragment;
import com.app.upincode.getqd.activities.frags.MenuActionFragment;

/**
 * Created by jpnauta on 15-10-03.
 */
public class GQMainToolbar extends Toolbar {

    public GQMainToolbar(Context context) {
        super(context);
    }

    public GQMainToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GQMainToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void displayView(AppCompatActivity activity, int position) {
        MenuActionFragment fragment = null;
        String title = activity.getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new GQMainHomeFragment();
                title = activity.getString(R.string.nav_item_home);
                break;
            case 1:
                fragment = new GQMainProfileFragment();
                title = activity.getString(R.string.nav_item_profile);
                break;
            case 2:
                fragment = new GQMainVenueFragment();
                title = activity.getString(R.string.nav_item_venue);
                break;
            case 3:
                fragment = new GQMainEventsFragment();
                title = activity.getString(R.string.nav_item_events);
                break;
            case 4:
                fragment = new GQMyTicketsFragment();
                title = activity.getString(R.string.nav_item_my_tickets);
                break;
            case 5:
                fragment = new GQMainLogoutFragment();
                title = activity.getString(R.string.nav_item_logout);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragment.performChange(activity, fragmentManager, R.id.container_body);

            // set the toolbar title
            this.setTitle(title);
        }
    }
}
