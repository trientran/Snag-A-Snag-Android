package com.digitalnoir.snagasnag.utility;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.digitalnoir.snagasnag.model.Sizzle;

import java.util.List;

/**
 * Loads a list of sizzles by using an AsyncTask to perform the
 * network request to the given URL.
 */

public class SizzleLoader extends AsyncTaskLoader<List<Sizzle>> {

    /** Tag for log messages */
    private static final String LOG_TAG = SizzleLoader.class.getName();

    /** Query URL */
    private String mUrl;

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
        forceLoad();
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
}
