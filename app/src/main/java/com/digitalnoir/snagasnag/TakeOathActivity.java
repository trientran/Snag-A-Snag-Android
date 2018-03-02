package com.digitalnoir.snagasnag;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.regex.Pattern;

import static com.digitalnoir.snagasnag.utility.DataUtil.createNewUser;

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

        String username = String.valueOf(usernameInput.getText());
        username = username.trim().replace("  ", " ");

        // use this Regular Expression pattern to validate when required
        Pattern pattern = Pattern.compile("[A-Za-z0-9 _]+");
        boolean valid = (username != null) && pattern.matcher(username).matches();

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        // validate input username
        if (TextUtils.isEmpty(username)) {

            Toast.makeText(this, getString(R.string.toast_username_empty), Toast.LENGTH_SHORT).show();
        }

        else if (!username.toLowerCase().contains("snag")) {

            Toast.makeText(this, getString(R.string.toast_username_lacking_snag), Toast.LENGTH_SHORT).show();
        }

        else if (!valid) {

            Toast.makeText(this, getString(R.string.toast_invalid_character), Toast.LENGTH_SHORT).show();
        }

        else if (!(networkInfo != null && networkInfo.isConnected())) {

            Log.e(LOG_TAG, "Error loading, no internet connection");
            Toast.makeText(this, getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show();
        }


        // If there is a network connection and text validation is ok then send a request to create username
        else {

            createNewUser(this, username);
            Intent intent = new Intent(this, SwearActivity.class);
            intent.putExtra(EXTRA_USER_NAME, username);
            startActivity(intent);
        }
    }

    /**
     * Helper method to format text nicely.
     */
    public static Spanned formatTextNoParam(String styledText) {
        return Html.fromHtml(styledText);
    }

}
