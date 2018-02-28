package com.digitalnoir.snagasnag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LetsSnagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_snag);

        Button letsSnagBtn;
        letsSnagBtn = (Button) findViewById(R.id.letsSnagBtn);

        // retrieve username or userId from SharedPreferences
        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        String username = mSettings.getString("username", "missing");
        //int userId = mSettings.getInt("userId", 0);

        // if username exist, then go straight to map activity, otherwise create a username
        if (!username.equals("missing")) {
            letsSnagBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                    startActivity(intent);

                }
            });

        }

        else {
            Intent intent = new Intent(getBaseContext(), TakeOathActivity.class);
            startActivity(intent);
        }
    }
}
