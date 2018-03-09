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
import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.Comment;
import com.digitalnoir.snagasnag.model.Rating;
import com.digitalnoir.snagasnag.model.Sizzle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.digitalnoir.snagasnag.utility.HttpClientDownStream.createUrl;
import static com.digitalnoir.snagasnag.utility.HttpClientDownStream.makeHttpRequest;

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
     * Create sizzle
     */
    public static void createNewSizzle(WeakReference<Context> mWeakContext, int userId, Sizzle newSizzle, Bitmap bitmap) {

        String url = SIZZLE_BASE_URL + CREATE_SIZZLE_URL_TAG;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (bitmap == null) {
            // call mWeakContext.get() to get main context
            bitmap = BitmapFactory.decodeResource(mWeakContext.get().getResources(), R.drawable.ic_default_sizzle);
        } else {
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
        }

        try {
            HttpClientUpStream client = new HttpClientUpStream(url);
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

            // parse response to get the new sizzle Id, then save it to SharePref
            String response = client.getResponse();
            int sizzleId = parseSizzleCreationResponse(mWeakContext, response);
            saveSizzleAsPreference(mWeakContext, sizzleId);
            Log.d("triennewSIzzle", sizzleId + response);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Create user
     */
    public static void createNewUser(WeakReference<Context> mWeakContext, String username) {

        String url = SIZZLE_BASE_URL + CREATE_USER_URL_TAG;

        try {
            HttpClientUpStream client = new HttpClientUpStream(url);
            client.connectForMultipart();
            client.addFormPart("unique_key", UNIQUE_KEY);
            client.addFormPart("username", username);
            client.finishMultipart();

            // parse response to get the new sizzle Id, then save it to SharePref
            String response = client.getResponse();

            int userId = parseUserCreationResponse(mWeakContext.get(), response);
            saveUserAsPreference(mWeakContext.get(), username, userId);
            LogUtil.debug(LOG_TAG, response);
            LogUtil.debug(LOG_TAG, String.valueOf(userId));

        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * Create comment. Comment object needs to be created using this constructor: userId, sizzleId...;
     */
    public static void createNewComment(WeakReference<Context> mWeakContext, Comment comment) {

        String url = SIZZLE_BASE_URL + CREATE_COMMENT_URL_TAG;

        try {
            HttpClientUpStream client = new HttpClientUpStream(url);
            client.connectForMultipart();
            client.addFormPart("unique_key", UNIQUE_KEY);
            client.addFormPart("user_id", String.valueOf(comment.getUserId()));
            client.addFormPart("sizzle_id", String.valueOf(comment.getSizzleId()));
            client.addFormPart("comment", comment.getCommentString());
            client.finishMultipart();

            String response = client.getResponse();
            // save Comment As Preference to trigger MapsActivity refreshing data
            saveCommentAsPreference(mWeakContext, comment.getCommentString());
            LogUtil.debug(LOG_TAG, response);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * Create rating. Rating object needs to be created using this constructor: userId, sizzleId...;
     */
    public static void createNewRating(WeakReference<Context> mWeakContext, Rating rating) {

        String url = SIZZLE_BASE_URL + CREATE_RATING_URL_TAG;

        try {
            HttpClientUpStream client = new HttpClientUpStream(url);
            client.connectForMultipart();
            client.addFormPart("unique_key", UNIQUE_KEY);
            client.addFormPart("user_id", String.valueOf(rating.getUserId()));
            client.addFormPart("sizzle_id", String.valueOf(rating.getSizzleId()));
            client.addFormPart("sausage", rating.getSausage());
            client.addFormPart("bread", rating.getBread());
            client.addFormPart("onion", rating.getOnion());
            client.addFormPart("sauce", rating.getSauce());
            client.finishMultipart();

            String response = client.getResponse();
            // save Comment As Preference to trigger MapsActivity refreshing data
            saveRatingAsPreference(mWeakContext, rating.getSausage());
            LogUtil.debug(LOG_TAG, response);

        } catch (Throwable t) {
            t.printStackTrace();
        }
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

                /* *******************************************
                Extract the value for the key called "rating" */
                JSONObject ratingObject = currentSizzle.getJSONObject("rating");

                // Extract the value for the key called "sausage"
                String sausage = ratingObject.get("sausage").toString();

                // Extract the value for the key called "bread"
                String bread = ratingObject.get("bread").toString();

                // Extract the value for the key called "onion"
                String onion = ratingObject.get("onion").toString();

                // Extract the value for the key called "sauce"
                String sauce = ratingObject.get("sauce").toString();

                // Extract the value for the key called "aggregate_rating"
                String aggregateRating = ratingObject.get("aggregate_rating").toString();

                LogUtil.debug("trien", String.valueOf(sausage));

                // Create a Rating object
                Rating rating = new Rating(sausage, bread, onion, sauce, aggregateRating);

                /* *******************************************
                Extract the value for the key called "comments" */
                JSONArray commentArray = currentSizzle.getJSONArray("comments");

                // create a Comment array
                List<Comment> comments = new ArrayList<>();

                // For each comment in the commentArray, create an {@link Comment} object
                for (int a = 0; a < commentArray.length(); a++) {

                    JSONObject commentObject = commentArray.getJSONObject(a);

                    // Extract the value for the key called "username"
                    String username = commentObject.getString("username");

                    // Extract the value for the key called "date"
                    String date = commentObject.getString("date");

                    // Extract the value for the key called "sauce"
                    String commentString = commentObject.getString("comment");

                    // Create a Comment object
                    Comment comment = new Comment(username, date, commentString);

                    // Add the new {@link Comment} to the list of comments.
                    comments.add(comment);
                }

                // Create a new {@link Sizzle} object with the magnitude, location, time,
                // and url from the JSON response.
                Sizzle sizzle = new Sizzle(sizzleId, latitude, longitude, name, address, detail, photoUrl, rating, comments);

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
     * handle User Creation Response
     */
    private static int parseUserCreationResponse(Context context, String response) {

        if (response.contains("user_id")) {
            // After sending post method to create new user, the http response format will be: {"user_id":1076}
            // We extract userId here
            String text = response.split(":")[1];
            int indexOfCurlyBracket = text.indexOf("}");

            return Integer.parseInt(text.substring(0, indexOfCurlyBracket));
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
            // We extract userId here
            String text = response.split(":")[1];
            int indexOfCurlyBracket = text.indexOf("}");

            return Integer.parseInt(text.substring(0, indexOfCurlyBracket));
        } else {
            Toast.makeText(context.get(), "Error creating sizzle", Toast.LENGTH_SHORT).show();
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
     * save sizzle in SharedPreferences
     */
    private static void saveSizzleAsPreference(WeakReference<Context> context, int sizzleId) {

        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context.get());
        SharedPreferences.Editor editor = mSettings.edit();

        editor.putInt("sizzleId", sizzleId);
        editor.apply(); // apply is async
        // editor.commit(); // commit is synchronous
    }

    /**
     * save comment in SharedPreferences
     */
    private static void saveCommentAsPreference(WeakReference<Context> context, String newComment) {

        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context.get());
        SharedPreferences.Editor editor = mSettings.edit();

        editor.putString("comment", newComment);
        editor.apply(); // apply is async
        // editor.commit(); // commit is synchronous
    }

    /**
     * save comment in SharedPreferences
     */
    private static void saveRatingAsPreference(WeakReference<Context> context, String sausageRate) {

        SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(context.get());
        SharedPreferences.Editor editor = mSettings.edit();

        editor.putString("sausage", sausageRate);
        editor.apply(); // apply is async
        // editor.commit(); // commit is synchronous
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
