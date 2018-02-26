package com.digitalnoir.snagasnag;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitalnoir.snagasnag.utility.LogUtils;

import java.util.HashMap;
import java.util.Map;

public class TakeOathActivity extends AppCompatActivity {

    private TextView oathTitleTextView;
    private TextView createUserTextView;
    private EditText usernameInput;
    private Button takeOathBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_oath);

        oathTitleTextView = (TextView) findViewById(R.id.oathTitle);
        createUserTextView = (TextView) findViewById(R.id.createUserTV);
        oathTitleTextView.setText(formatText(getString(R.string.oath_title)));
        createUserTextView.setText(formatText(getString(R.string.create_username_hint)));

        usernameInput = (EditText) findViewById(R.id.usernameInput);
        takeOathBtn = (Button) findViewById(R.id.letsSnagBtn);

        takeOathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakeOathBtnClick();
            }
        });

        createNewUser(this);
    }

    /**
     * Handle Take the Oath button click.
     */
    private void onTakeOathBtnClick() {


    }

    /**
     * Helper method to format text nicely.
     */
    private static Spanned formatText(String styledText) {
        return Html.fromHtml(styledText);
    }

    public void createNewUser(Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, "http://snag.digitalnoirtest.net.au/snag-create-user/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        LogUtils.debug("trien", response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtils.debug("volley", "Error: " + error.getMessage());
                error.printStackTrace();
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "TroySnag1");
                params.put("unique_key", "qfP2ixc0fBIxoxiDfx3jbgaq");
                return params;
            }
        };
        queue.add(jsonObjRequest);
    }

}
