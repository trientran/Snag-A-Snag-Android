package com.digitalnoir.snagasnag.utility;

import android.text.TextUtils;
import android.util.Log;

import com.digitalnoir.snagasnag.model.Sizzle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Database utilities to CRUD JSON data
 */

public class DataUtil {
    /** Tag for the log messages */
    private static final String LOG_TAG = DataUtil.class.getSimpleName();

    /**
     * Base URL for sizzle data from the web service
     */
    public static final String SIZZLE_BASE_URL =
            "http://snag.digitalnoirtest.net.au/";


    /** Default constructor */
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

                // Extract the value for the key called "place"
                int sizzleId = currentSizzle.getInt("id");

                // Extract the value for the key called "url"
                String latitude = currentSizzle.getString("lat");

                // Extract the value for the key called "url"
                String longitude = currentSizzle.getString("lng");

                // Extract the value for the key called "url"
                String name = currentSizzle.getString("name");

                // Extract the value for the key called "url"
                String address = currentSizzle.getString("address");

                // Extract the value for the key called "url"
                String detail = currentSizzle.getString("details");

                // Extract the value for the key called "url"
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


}
