package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.digitalnoir.snagasnag.model.Sizzle;
import java.lang.ref.WeakReference;
import static com.digitalnoir.snagasnag.utility.DataUtil.createNewSizzle;
import static com.digitalnoir.snagasnag.utility.DataUtil.parseSizzleCreationResponse;

/**
 * Created by Troy on 3/5/2018.
 */

public class SizzleCreater extends AsyncTask<String, Void, String> {

    // use weak context to avoid leaking a context object
    private WeakReference<Context> mWeakContext;
    private int mUserId;
    private Sizzle mNewSizzle;
    private Bitmap mBitmap;

    public SizzleCreater(Context context, int userId, Sizzle newSizzle, Bitmap bitmap) {
        mWeakContext = new WeakReference<>(context);
        this.mUserId = userId;
        this.mNewSizzle = newSizzle;
        this.mBitmap = bitmap;
    }

    @Override
    protected String doInBackground(String... params) {

        createNewSizzle(mWeakContext, mUserId, mNewSizzle, mBitmap);

        return null;
    }

    @Override
    protected void onPostExecute(String data) {

        mNewSizzle.setSizzleId(parseSizzleCreationResponse(mWeakContext, data));
    }

}