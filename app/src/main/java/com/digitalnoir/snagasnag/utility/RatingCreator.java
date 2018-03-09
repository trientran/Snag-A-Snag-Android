package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.digitalnoir.snagasnag.model.Rating;

import java.lang.ref.WeakReference;

import static com.digitalnoir.snagasnag.utility.DataUtil.createNewRating;

/**
 * Created by Troy on 3/5/2018.
 */

public class RatingCreator extends AsyncTask<String, Void, String> {

    // use weak context to avoid leaking a context object
    private WeakReference<Context> mWeakContext;
    private Rating rating;

    // comment object needs to be created using these properties: userId, sizzleId ..;
    public RatingCreator(Context context, Rating rating) {
        mWeakContext = new WeakReference<>(context);
        this.rating = rating;
    }

    @Override
    protected String doInBackground(String... params) {

        createNewRating(mWeakContext, rating);

        return null;
    }

    @Override
    protected void onPostExecute(String data) {

    }

}