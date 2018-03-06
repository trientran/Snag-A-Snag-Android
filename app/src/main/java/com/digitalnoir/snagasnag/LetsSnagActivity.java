package com.digitalnoir.snagasnag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.digitalnoir.snagasnag.utility.LogUtil;

public class LetsSnagActivity extends AppCompatActivity {

    private Button letsSnagBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_snag);

        letsSnagBtn = (Button) findViewById(R.id.letsSnagBtn);

        // set On-Click Listener for letsSnagBtn button
        letsSnagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLetsSnagBtnClick();
            }
        });

    }

    private void onLetsSnagBtnClick() {

        // retrieve username or userId from SharedPreferences
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        String username = mSettings.getString("username", "missing");
        LogUtil.debug("trienshare", username);
        // if username exist, then go straight to map activity, otherwise create a username
        if (!username.equals("missing")) {
            letsSnagBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(intent);

                }
            });

        }

        else {
            Intent intent = new Intent(getApplicationContext(), TakeOathActivity.class);
            startActivity(intent);
        }

    }
}
