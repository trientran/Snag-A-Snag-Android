package com.digitalnoir.snagasnag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import static com.digitalnoir.snagasnag.utility.DataUtil.createNewUser;

public class TakeOathActivity extends AppCompatActivity {

    /** Tag for the log messages */
    private static final String LOG_TAG = TakeOathActivity.class.getSimpleName();

    private TextView oathTitleTextView;
    private TextView createUserTextView;
    private EditText usernameInput;
    private Button takeOathBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_oath);

        oathTitleTextView = (TextView) findViewById(R.id.oathTitleTv);
        createUserTextView = (TextView) findViewById(R.id.createUserTv);
        usernameInput = (EditText) findViewById(R.id.usernameInput);
        takeOathBtn = (Button) findViewById(R.id.takeOathBtn);

        // set text to appropriate views
        oathTitleTextView.setText(formatTextNoParam(getString(R.string.oath_title)));
        createUserTextView.setText(formatTextNoParam(getString(R.string.create_username_hint)));

        takeOathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakeOathBtnClick();
            }
        });

    }


    /**
     * Handle Take the Oath button click. If the input username contain the word snag, then go to swear activity
     * if not, display a toast message to warn user
     */
    private void onTakeOathBtnClick() {

        String username = String.valueOf(usernameInput.getText());

        if (username.toLowerCase().contains("snag")) {

            createNewUser(this, username);

            Intent intent = new Intent(this, SwearActivity.class);
            startActivity(intent);
        }

        else {

            Toast.makeText(this, "Username must contain the word snag", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Helper method to format text nicely.
     */
    public static Spanned formatTextNoParam(String styledText) {
        return Html.fromHtml(styledText);
    }

}
