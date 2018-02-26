package com.digitalnoir.snagasnag.utility;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.User;
import com.google.android.gms.common.api.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Troy on 2/26/2018.
 */

/*
Extend the Application class
to create your own application class.
*/

public class SizzleUpstream {


   /* public static void postNewComment(Context context, final User user, final String comment, final int blogId, final int postId){

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                getResources().getString(R.string.base_url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        MyFunctions.toastShort(LoginActivity.this, response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("volley", "Error: " + error.getMessage());
                error.printStackTrace();
                MyFunctions.croutonAlert(LoginActivity.this,
                        MyFunctions.parseVolleyError(error));
                loading.setVisibility(View.GONE);
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", etUname.getText().toString().trim());
                params.put("password", etPass.getText().toString().trim());
                return params;
            }
        };
        queue.add(sr);
    }
*/

}
