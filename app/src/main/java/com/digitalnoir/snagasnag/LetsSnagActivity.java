package com.digitalnoir.snagasnag;

import android.content.Intent;
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

        letsSnagBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), TakeOathActivity.class);
                startActivity(intent);

            }
        });
    }
}