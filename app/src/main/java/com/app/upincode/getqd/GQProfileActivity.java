package com.app.upincode.getqd;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class GQProfileActivity extends ActionBarActivity {
    TextView tvName = null;
    TextView tvEmail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.app.upincode.getqd.R.layout.activity_gqprofile);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        String UserName = globalVariable.getProfileValue(getApplicationContext(), "UserName");
        String Email = globalVariable.getProfileValue(getApplicationContext(), "Email");

        tvName = (TextView) findViewById(com.app.upincode.getqd.R.id.ProfileFirstLastName);
        tvName.setText(UserName);
        tvEmail = (TextView) findViewById(com.app.upincode.getqd.R.id.ProfileEmail);
        tvEmail.setText(Email);

        final Button button1 = (Button) findViewById(com.app.upincode.getqd.R.id.RegCancelButton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenty = new Intent(getApplicationContext(), GQStartActivity.class);
                startActivity(intenty);

                //viewFlipper.showNext();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.app.upincode.getqd.R.menu.menu_gqprofile, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.app.upincode.getqd.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
