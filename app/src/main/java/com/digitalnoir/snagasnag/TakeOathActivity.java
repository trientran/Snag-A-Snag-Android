package com.digitalnoir.snagasnag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digitalnoir.snagasnag.utility.UserCreator;

import static com.digitalnoir.snagasnag.utility.DataUtil.isInternetConnected;
import static com.digitalnoir.snagasnag.utility.TextValidation.validateTextWithPattern;

public class TakeOathActivity extends AppCompatActivity {

    /** Tag for the log messages */
    private static final String LOG_TAG = TakeOathActivity.class.getSimpleName();
    public static final String EXTRA_USER_NAME = "extra_username";


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

        // validate username with common pattern first
        String username = validateTextWithPattern(this, usernameInput, getString(R.string.username_field_name));

        if (isInternetConnected(this) && username != null) {

            // check if username contain the word snag
            if (!username.toLowerCase().contains("snag")) {

                Toast.makeText(this, getString(R.string.toast_username_lacking_snag), Toast.LENGTH_SHORT).show();
            }

            // If there is a network connection and text validation is ok then send a request to create username
            else {

                UserCreator t = new UserCreator(this, username);
                t.execute();

                Intent intent = new Intent(this, SwearActivity.class);
                intent.putExtra(EXTRA_USER_NAME, username);
                startActivity(intent);
            }
        }
    }

    /**
     * Helper method to format text nicely.
     */
    public static Spanned formatTextNoParam(String styledText) {
        return Html.fromHtml(styledText);
    }

}
