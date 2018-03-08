package com.digitalnoir.snagasnag.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.digitalnoir.snagasnag.model.Comment;
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
    private Comment comment;

    // comment object needs to be created using these properties: userId, sizzleId and commentString;
    public CommentCreator(Context context, Comment comment) {
        mWeakContext = new WeakReference<>(context);
        this.comment = comment;
    }

    @Override
    protected String doInBackground(String... params) {

        createNewComment(mWeakContext, comment);

        return null;
    }

    @Override
    protected void onPostExecute(String data) {

    }

}