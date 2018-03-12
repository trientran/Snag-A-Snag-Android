/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.digitalnoir.snagasnag.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalnoir.snagasnag.R;
import com.digitalnoir.snagasnag.model.Comment;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.digitalnoir.snagasnag.utility.TextValidation.formatCommentDateTime;

/**
 * {@link CommentAdapter} showcases the comment list with a RecyclerView
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentAdapterViewHolder> {

    /* The context we use to utility methods, app resources and layout inflaters */
    private final Context mContext;

    private List<Comment> mComments = new ArrayList<>();

    /**
     * Creates a CommentAdapter.
     *
     * @param context Used to talk to the UI and app resources
     */
    public CommentAdapter(@NonNull Context context) {
        mContext = context;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (like ours does) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new CommentAdapterViewHolder that holds the View for each list item
     */
    @NonNull
    @Override
    public CommentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);

        view.setFocusable(true);

        return new CommentAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the comment
     * for this particular position, using the "position" argument that is conveniently
     * passed into us.
     *
     * @param commentAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                 contents of the item at the given position in the data set.
     * @param position                 The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CommentAdapterViewHolder commentAdapterViewHolder, int position) {

        // fetch each comment and set text for each comment layout's  item
        Comment comment = mComments.get(position);


        String formattedDatetime = "";
        try {
            formattedDatetime = formatCommentDateTime(comment.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        commentAdapterViewHolder.usernameTv.setText(comment.getUsername());
        commentAdapterViewHolder.timeLapseTv.setText(formattedDatetime);
        commentAdapterViewHolder.commentTv.setText(comment.getCommentString());
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    @Override
    public int getItemCount() {
        return mComments.size();
    }

    /**
     * Swaps the comment list used by the CommentAdapter for its comment data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the comment data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newComments the new comment list to use as CommentAdapter's data source
     */
    public void swapCommentData(List<Comment> newComments) {

        mComments = newComments;
        notifyDataSetChanged();
    }

    public void onChildAdded(Comment newComment, final RecyclerView recyclerView, final NestedScrollView nestedScrollView) {

        // add new comment and notify the recycler view to insert the new item to last position
        mComments.add(newComment);
        notifyDataSetChanged();

        // scroll to top of RecyclerView (where newest comment is)
        nestedScrollView.post(new Runnable() {

            @Override
            public void run() {
                nestedScrollView.smoothScrollTo(0, recyclerView.getTop());
            }
        });
    }

    /**
     * A ViewHolder is a required part of the pattern for RecyclerViews. It mostly behaves as
     * a cache of the child views for a comment item. It's also a convenient place to set an
     * OnClickListener, since it has access to the adapter and the views.
     */
    class CommentAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView usernameTv;
        TextView timeLapseTv;
        TextView commentTv;

        CommentAdapterViewHolder(View itemView) {
            super(itemView);

            usernameTv = (TextView) itemView.findViewById(R.id.usernameTv);
            timeLapseTv = (TextView) itemView.findViewById(R.id.timeLapseTv);
            commentTv = (TextView) itemView.findViewById(R.id.commentTv);
        }
    }


}