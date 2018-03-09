package com.digitalnoir.snagasnag;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.digitalnoir.snagasnag.TakeOathActivity.EXTRA_USER_NAME;
import static com.digitalnoir.snagasnag.TakeOathActivity.formatTextNoParam;

public class SwearActivity extends AppCompatActivity {

    private TextView swearTitleTextView;
    private TextView swearBodyTextView;
    private Button iDoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swear);

        Intent intent = getIntent();
        String username = intent.getStringExtra(EXTRA_USER_NAME);

        swearTitleTextView = (TextView) findViewById(R.id.swearTitle);
        swearBodyTextView = (TextView) findViewById(R.id.swearBodyTV);

        // set text to appropriate views
        swearTitleTextView.setText(formatTextNoParam(getString(R.string.swear_title)));
        swearBodyTextView.setText(formatText(getResources(), username));

        iDoBtn = (Button) findViewById(R.id.iDoBtn);

        // go to MapsActivity after swearing
        iDoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);

            }
        });

    }

    /**
     * Helper method to format text nicely.
     */
    private static Spanned formatText(Resources res, CharSequence username) {

        return Html.fromHtml(res.getString(R.string.swear_body, username));
    }


}
