package com.digitalnoir.snagasnag.utility;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.digitalnoir.snagasnag.model.Sizzle;

import java.util.List;

/**
 * Loads a list of sizzles by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class SizzleLoader extends AsyncTaskLoader<List<Sizzle>> {

    /** Tag for log messages */
    private static final String LOG_TAG = SizzleLoader.class.getName();

    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;

    /** Query URL */
    private String mUrl;

    private List<Sizzle> mSizzles;
    /**
     * Constructs a new {@link SizzleLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public SizzleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (mSizzles != null) {
            // Use cached data
            deliverResult(mSizzles);
        }

        if (sharedPreferenceChangeListener == null) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

            sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    onContentChanged();
                }
            };
            prefs.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        }

        if (takeContentChanged() || mSizzles == null) {
            // Something has changed or we have no data,
            // so kick off loading it
            forceLoad();
        }
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<Sizzle> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of sizzles.
        List<Sizzle> sizzles = DataUtil.fetchSizzleData(mUrl);
        return sizzles;
    }

    @Override
    public void deliverResult(List<Sizzle> sizzles) {
        // We’ll save the data for later retrieval
        mSizzles = sizzles;
        // We can do any pre-processing we want here as this is on the UI thread so nothing lengthy
        super.deliverResult(sizzles);
    }
}
