package com.digitalnoir.snagasnag.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Troy on 2/20/2018.
 */

public class Comment implements Parcelable {

    private int userId;
    private int sizzleId;

    private String username;
    private String date;
    private String commentString;

    public Comment(String comment) {
        this.commentString = comment;
    }

    public Comment(String username, String date, String comment) {
        this.username = username;
        this.date = date;
        this.commentString = comment;
    }

    public Comment(int userId, int sizzleId, String comment) {
        this.userId = userId;
        this.sizzleId = sizzleId;
        this.commentString = comment;
    }

    /**
     * Use when reconstructing Comment object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    public Comment(Parcel in) {

        this.username = in.readString();
        this.date = in.readString();
        this.commentString =in.readString();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSizzleId() {
        return sizzleId;
    }

    public void setSizzleId(int sizzleId) {
        this.sizzleId = sizzleId;
    }

    public String getCommentString() {
        return commentString;
    }

    public void setCommentString(String commentString) {
        this.commentString = commentString;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(username);
        dest.writeString(date);
        dest.writeString(commentString);
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     *
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {

        public Comment createFromParcel(Parcel in) {
            return new Comment(in);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
