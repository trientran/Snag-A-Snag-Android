package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.Sizzle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database utilities to CRUD JSON data
 */

public class DataUtil {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = DataUtil.class.getSimpleName();

    /**
     * Base URL for sizzle data from the web service
     */
    public static final String SIZZLE_BASE_URL = "http://snag.digitalnoirtest.net.au/";

    /**
     * Appended URL tag for creating user
     */
    public static final String CREATE_USER_URL_TAG = "snag-create-user/";

    /**
     * Appended URL tag for creating sizzle
     */
    public static final String CREATE_SIZZLE_URL_TAG = "snag-create-post/";

    /**
     * Appended URL tag for creating comment
     */
    public static final String CREATE_COMMENT_URL_TAG = "snag-create-comment/";

    /**
     * Appended URL tag for creating rating
     */
    public static final String CREATE_RATING_URL_TAG = "snag-create-rating/";

    /**
     * Appended URL tag for displaying all sizzle
     */
    public static final String DISPLAY_SIZZLE_URL_TAG = "snag-display-sizzle/";

    /**
     * UNIQUE_KEY for accessing the web service
     */
    public static final String UNIQUE_KEY = "qfP2ixc0fBIxoxiDfx3jbgaq";


    /**
     * Default constructor
     */
    private DataUtil() {
    }

    /**
     * Query the Sizzle dataset and return a list of {@link Sizzle} objects.
     */
    public static List<Sizzle> fetchSizzleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Sizzle}s
        List<Sizzle> sizzles = extractFeatureFromJson(jsonResponse);
        // Return the list of {@link Sizzle}s
        return sizzles;
    }

    /**
     * Create user
     */
    public static void createNewUser(Context context, String username) {

        Map<String, String> params = new HashMap <String, String>();
        params.put("unique_key", UNIQUE_KEY);
        params.put("username", username);

        sendPostRequest(context, CREATE_USER_URL_TAG, username, params);
    }

    /**
     * Create sizzle
     */
    public static void createNewSizzle(WeakReference<Context> mWeakContext, int userId, Sizzle newSizzle, Bitmap bitmap) {

       // int sizzleId = 0;

        String url = SIZZLE_BASE_URL + CREATE_SIZZLE_URL_TAG;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (bitmap == null) {
            // call mWeakContext.get() to get main context
            bitmap = BitmapFactory.decodeResource(mWeakContext.get().getResources(), R.drawable.ic_default_sizzle);
        }
        else {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        }

        try {
            HttpClient client = new HttpClient(url);
            client.connectForMultipart();
            client.addFormPart("unique_key", UNIQUE_KEY);
            client.addFormPart("user_id", String.valueOf(userId));
            client.addFormPart("latitude", newSizzle.getLatitude());
            client.addFormPart("longitude", newSizzle.getLongitude());
            client.addFormPart("name", newSizzle.getName());
            client.addFormPart("address", newSizzle.getAddress());
            client.addFormPart("details", newSizzle.getDetail());
            client.addFilePart("photo", "unnamed-file.png", baos.toByteArray());
            client.finishMultipart();

            // parse response and get the new sizzle Id
            String response = client.getResponse();
           // sizzleId = parseSizzleCreationResponse(mWeakContext, response);

            Log.d("triennewSIzzle", response);
        } catch (Throwable t) {
            t.printStackTrace();
        }

        //return sizzleId;
    }

    /**
     * Create comment
     */
    public static void createNewComment(Context context, int userId, int sizzleId, String comment) {
        Map<String, String> params = new HashMap <String, String>();
        params.put("unique_key", UNIQUE_KEY);
        params.put("user_id", String.valueOf(userId));
        params.put("sizzle_id", String.valueOf(sizzleId));
        params.put("comment", comment);

        sendPostRequest(context, CREATE_COMMENT_URL_TAG, null, params);
    }

    /**
     * Create rating
     */
    public static void createNewRating(Context context, int userId, int sizzleId, String sausage,
                                       String bread, String onion, String sauce) {
        Map<String, String> params = new HashMap <String, String>();
        params.put("unique_key", UNIQUE_KEY);
        params.put("user_id", String.valueOf(userId));
        params.put("sizzle_id", String.valueOf(sizzleId));
        params.put("sausage", sausage);
        params.put("bread", bread);
        params.put("onion", onion);
        params.put("sauce", sauce);

        sendPostRequest(context, CREATE_RATING_URL_TAG, null, params);
    }

    /**
     * Universal method to send a post request to web server
     */
    public static void sendPostRequest(final Context context,
                                       final String appendedURL,
                                       final String username,
                                       final Map<String, String> params
                                       ) {

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST, SIZZLE_BASE_URL + appendedURL

                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                LogUtil.debug(LOG_TAG, response);

                // handle response properly according to type of post request
                switch (appendedURL) {
                    case CREATE_USER_URL_TAG:

                        int userId = parseUserCreationResponse(context, response);
                        saveUserAsPreference(context, username, userId);
                        LogUtil.debug(LOG_TAG, String.valueOf(userId));

                        break;
                    case CREATE_SIZZLE_URL_TAG:

                        break;
                    case CREATE_COMMENT_URL_TAG:

                        break;
                    case CREATE_RATING_URL_TAG:

                        break;
                }
            }
        }

                , new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                LogUtil.debug("volley", "Error: " + error.getMessage());
                error.printStackTrace();

            }
        })

        {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return params;
            }
        };

        queue.add(jsonObjRequest);
    }

    /**
     * handle User Creation Response
     */
    private static int parseUserCreationResponse(Context context, String response) {

        if (response.contains("user_id")) {
            // After sending post method to create new user, the http response format will be: {"user_id":1076}
            // We extract userId here
            String text = response.split(":")[1];
            return Integer.parseInt(text.substring(0, text.length() - 1));
        } else {
            Toast.makeText(context, "Error creating username", Toast.LENGTH_SHORT).show();
            return 0;
        }

    }

    /**
     * handle User Creation Response
     */
    public static int parseSizzleCreationResponse(WeakReference<Context> context, String response) {

        if (response.contains("sizzle_id")) {
            // After sending post method to create new user, the http response format will be: {"user_id":1076}
            // We extract sizzle Id here
            String text1 = response.split("}")[0];
            String text2 = text1.split(":")[1];
            return Integer.parseInt(text2);
        } else {
            // call mWeakContext.get() to get main context
            Toast.makeText(context.get(), "Error creating new sizzle", Toast.LENGTH_SHORT).show();
            return 0;
        }

    }

    /**
     * save username and userId in SharedPreferences
     */
    private static void saveUserAsPreference(Context context, String username, int userId) {

        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mSettings.edit();

        editor.putString("username", username);
        editor.putInt("userId", userId);
        editor.apply(); // apply is async
        // editor.commit(); // commit is synchronous
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Sizzle JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Sizzle} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Sizzle> extractFeatureFromJson(String sizzleJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(sizzleJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding Sizzles to
        List<Sizzle> sizzles = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {


            JSONArray sizzleArray = new JSONArray(sizzleJSON);

            // For each sizzle in the sizzleArray, create an {@link Sizzle} object
            for (int i = 0; i < sizzleArray.length(); i++) {

                // Get a single sizzle at position i within the list of sizzles
                JSONObject currentSizzle = sizzleArray.getJSONObject(i);

                /*
                 * For a given sizzle, extract all needed values associated with each sizzle.
                 */

                // Extract the value for the key called "id"
                int sizzleId = currentSizzle.getInt("id");

                // Extract the value for the key called "lat"
                String latitude = currentSizzle.getString("lat");

                // Extract the value for the key called "lng"
                String longitude = currentSizzle.getString("lng");

                // Extract the value for the key called "name"
                String name = currentSizzle.getString("name");

                // Extract the value for the key called "address"
                String address = currentSizzle.getString("address");

                // Extract the value for the key called "details"
                String detail = currentSizzle.getString("details");

                // Extract the value for the key called "img"
                String photoUrl = currentSizzle.getString("img");

                // Create a new {@link Sizzle} object with the magnitude, location, time,
                // and url from the JSON response.
                Sizzle sizzle = new Sizzle(sizzleId, latitude, longitude, name, address, detail, photoUrl);

                // Add the new {@link Sizzle} to the list of sizzles.
                sizzles.add(sizzle);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the Sizzle JSON results", e);
        }

        // Return the list of sizzles
        return sizzles;
    }

    /**
     * Helper method to check if the device is connected to the Internet
     */
    public static boolean isInternetConnected(Context context) {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                (context.getSystemService(Context.CONNECTIVITY_SERVICE));
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (!(networkInfo != null && networkInfo.isConnected())) {

            Log.e(LOG_TAG, "Error loading, no internet connection");
            Toast.makeText(context, context.getString(R.string.toast_check_internet), Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }

}
