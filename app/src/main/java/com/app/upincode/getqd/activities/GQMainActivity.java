package com.app.upincode.getqd.activities;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.app.upincode.getqd.R;
import com.app.upincode.getqd.drawers.FragmentDrawer;

public class GQMainActivity extends GQBaseActivity implements FragmentDrawer.FragmentDrawerListener {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        toolbar.displayView(this, position);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(com.app.upincode.getqd.R.layout.activity_gqmain);

        //Initialize menu
        initMenuButtonToolbar();
        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        drawerFragment.setDrawerListener(this);

        //Show home fragment first
        this.toolbar.displayView(this, 0);
    }

    //TODO remove
//
//    public void OnButtonClickedStaffBookguest(int position) {
//        GQLog.dObj(this, "ONButtonClicked booked guest with position=" + position);
//
//
//
//        Intent intenty = new Intent(getApplicationContext(), GQBookGuestActivity.class);
//        intenty.putExtra("venue", Integer.toString(position)); // start with the Staff fragment
//        startActivity(intenty);
//
//    }
//
//    public void OnButtonClickedStaffMyreso(int position) {
//        GQLog.dObj(this, "ONButtonClicked My reso with position=" + position);
//        Intent intenty = new Intent(getApplicationContext(), GQMyResoActivity.class);
//        intenty.putExtra("position", Integer.toString(position)); // start with the Staff fragment
//        startActivity(intenty);
//    }
//
//    public void OnButtonClickedStaffCheckin(int position) {
//        GQLog.dObj(this, "ONButtonClicked checkin with position=" + position);
//        //the following was just a test to see if we could get the uservenues json.  It worked 7/28/2015
//        //GlobalClass globalVariable = (GlobalClass) getApplicationContext();
//        //GQLog.d("OnButtonClickedStaffCheckin",globalVariable.getUserVenuesJSON());
//        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
//
//        dlgAlert.setMessage("Scan Check In");
//        dlgAlert.setTitle("Scan Check In is not supported at this time");
//        dlgAlert.setPositiveButton("OK", null);
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();
//
//        dlgAlert.setPositiveButton("Ok",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//
//    }
//
//    public void OnButtonClickedStaffSales() {
//        GQLog.dObj(this, "ONButtonClicked sales");
//        Intent intenty = new Intent(getApplicationContext(), GQSalesActivity.class);
//        startActivity(intenty);
//    }
//
//    public void OnButtonClickedStaffStats() {
//        GQLog.dObj(this, "ONButtonClicked Stats");
//        Intent intenty = new Intent(getApplicationContext(), GQStatisticsActivity.class);
//        startActivity(intenty);
//    }
//
//    public void OnButtonClickedForLogout(String herb) {
//        GQLog.dObj(this, "Success Yes Logout =" + herb);
//
//        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
//        // Clean up before loggin out.
//        globalVariable.setloginJSON(null);
//        globalVariable.setUserVenuesJSON(null);
//        globalVariable.setAnalyticsEventStats(null);
//        globalVariable.setProfileValue(getApplicationContext(), "LoggedIn", "no");
//        Intent intenty = new Intent(getApplicationContext(), GQStartActivity.class);
//        startActivity(intenty);
//
//    }
//
//    public void OnButtonClickedForNOLogout(String herb) {
//        GQLog.dObj(this, "Success No Logout =" + herb);
//
//        if (smallMode == false) {
//            onNavigationDrawerItemSelected(4);
//        } else {
//            GQLog.dObj(this, "Success No Log with SmallMode");
//            onNavigationDrawerItemSelected(1);  // go to staff
//        }
//    }

    //TODO remove
//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (smallMode == false) {
//            if (position == 0) {
//                fragmentManager.beginTransaction().replace(com.app.upincode.getqd.R.id.container, GQMain_profile_fragment.newInstance()).commit();
//            } else if (position == 1) {
//                fragmentManager.beginTransaction().replace(com.app.upincode.getqd.R.id.container, GQMain_venue_fragment.newInstance()).commit();
//            } else if (position == 2) {
//                fragmentManager.beginTransaction().replace(com.app.upincode.getqd.R.id.container, GQMain_event_fragment.newInstance()).commit();
//            } else if (position == 3) {
//                fragmentManager.beginTransaction().replace(com.app.upincode.getqd.R.id.container, GQMain_ticket_fragment.newInstance()).commit();
//            } else if (position == 4) {
//                fragmentManager.beginTransaction().replace(com.app.upincode.getqd.R.id.container, GQMain_staff_fragment.newInstance()).commit();
//            } else if (position == 5) {
//                //Go to confirm logout activity
//                Intent intent = new Intent(getApplicationContext(), GQConfirmLogoutActivity.class);
//                startActivity(intent);
//            }
//        } else {
//            if (position == 0) {
//                fragmentManager.beginTransaction().replace(com.app.upincode.getqd.R.id.container, GQMain_profile_fragment.newInstance()).commit();
//            } else if (position == 1) {
//                fragmentManager.beginTransaction().replace(com.app.upincode.getqd.R.id.container, GQMain_staff_fragment.newInstance()).commit();
//            } else if (position == 2) {
//                //Go to confirm logout activity
//                Intent intent = new Intent(getApplicationContext(), GQConfirmLogoutActivity.class);
//                startActivity(intent);
//            }
//        }
//
//    }

    //TODO remove
//    public void onSectionAttached(int number) {
//        if (smallMode == false) {
//            switch (number) {
//                case 1:
//                    mTitle = getString(com.app.upincode.getqd.R.string.title_section1);
//                    break;
//                case 2:
//                    mTitle = getString(com.app.upincode.getqd.R.string.title_section2);
//                    break;
//                case 3:
//                    mTitle = getString(com.app.upincode.getqd.R.string.title_section3);
//                    break;
//                case 4:
//                    mTitle = getString(com.app.upincode.getqd.R.string.title_section4);
//                    break;
//                case 5:
//                    mTitle = "Reps";
//                    break;
//                case 6:
//                    mTitle = getString(com.app.upincode.getqd.R.string.title_section6);
//                    break;
//            }
//        } else {
//            switch (number) {
//                case 1:
//                    mTitle = getString(com.app.upincode.getqd.R.string.title_section1);
//                    ;
//                case 2:
//                    mTitle = "Reps";
//                    break;
//                case 3:
//                    mTitle = getString(com.app.upincode.getqd.R.string.title_section6);
//                    break;
//            }
//        }
//         GQLog.dObj(this, "mTitle=" + mTitle);
//    }

    //TODO remove
//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
//    }


    //TODO remove
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(com.app.upincode.getqd.R.menu.gqmain, menu);
//            restoreActionBar();
//            return true;
//        }
//        return super.onCreateOptionsMenu(menu);
//    }

    //TODO is this important?
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == com.app.upincode.getqd.R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    //TODO important?
//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(com.app.upincode.getqd.R.layout.fragment_gqmain, container, false);
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            ((GQMainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }


}
