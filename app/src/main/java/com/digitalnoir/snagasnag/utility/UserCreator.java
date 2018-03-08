package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import static com.digitalnoir.snagasnag.utility.DataUtil.createNewUser;

/**
 * Created by Troy on 3/8/2018.
 */

public class UserCreator extends AsyncTask<String, Void, String> {

    // use weak context to avoid leaking a context object
    private WeakReference<Context> mWeakContext;
    private String username;

    public UserCreator(Context context, String username) {
        mWeakContext = new WeakReference<>(context);
        this.username = username;
    }

    @Override
    protected String doInBackground(String... params) {

        createNewUser(mWeakContext, username);

        return null;
    }

    @Override
    protected void onPostExecute(String data) {

    }

}