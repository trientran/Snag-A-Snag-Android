package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.digitalnoir.snagasnag.model.Sizzle;

import java.lang.ref.WeakReference;

import static com.digitalnoir.snagasnag.utility.DataUtil.createNewComment;
import static com.digitalnoir.snagasnag.utility.DataUtil.createNewSizzle;

/**
 * Created by Troy on 3/5/2018.
 */

public class CommentCreator extends AsyncTask<String, Void, String> {

    // use weak context to avoid leaking a context object
    private WeakReference<Context> mWeakContext;
    private int mUserId;
    private int mSizzleId;
    private String commentString;


    public CommentCreator(Context context, int mUserId, int mSizzleId, String commentString) {
        mWeakContext = new WeakReference<>(context);
        this.mUserId = mUserId;
        this.mSizzleId = mSizzleId;
        this.commentString = commentString;
    }

    @Override
    protected String doInBackground(String... params) {

        createNewComment(mWeakContext, mUserId, mSizzleId, commentString);

        return null;
    }

    @Override
    protected void onPostExecute(String data) {

    }

}