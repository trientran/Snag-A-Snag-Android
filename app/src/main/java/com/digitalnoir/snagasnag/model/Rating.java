package com.digitalnoir.snagasnag.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Troy on 2/20/2018.
 */

public class Rating implements Parcelable{

    private int userId;
    private int sizzleId;
    private String sausage;
    private String bread;
    private String onion;
    private String sauce;
    private String aggregateRating;


    public Rating(String sausage, String bread, String onion, String sauce, String aggregateRating) {
        this.sausage = sausage;
        this.bread = bread;
        this.onion = onion;
        this.sauce = sauce;
        this.aggregateRating = aggregateRating;
    }

    public Rating(int userId, int sizzleId, String sausage, String bread, String onion, String sauce) {
        this.userId = userId;
        this.sizzleId = sizzleId;
        this.sausage = sausage;
        this.bread = bread;
        this.onion = onion;
        this.sauce = sauce;
    }

    /**
     * Use when reconstructing Rating object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    public Rating(Parcel in) {

        this.sausage = in.readString();
        this.bread = in.readString();
        this.onion =in.readString();
        this.sauce = in.readString();
        this.aggregateRating = in.readString();
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

    public String getSausage() {
        return sausage;
    }

    public void setSausage(String sausage) {
        this.sausage = sausage;
    }

    public String getBread() {
        return bread;
    }

    public void setBread(String bread) {
        this.bread = bread;
    }

    public String getOnion() {
        return onion;
    }

    public void setOnion(String onion) {
        this.onion = onion;
    }

    public String getSauce() {
        return sauce;
    }

    public void setSauce(String sauce) {
        this.sauce = sauce;
    }

    public String getAggregateRating() {
        return aggregateRating;
    }

    public void setAggregateRating(String aggregateRating) {
        this.aggregateRating = aggregateRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(sausage);
        dest.writeString(bread);
        dest.writeString(onion);
        dest.writeString(sauce);
        dest.writeString(aggregateRating);
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     *
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() {

        public Rating createFromParcel(Parcel in) {
            return new Rating(in);
        }

        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
