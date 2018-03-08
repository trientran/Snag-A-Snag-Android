package com.digitalnoir.snagasnag.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Troy on 2/20/2018.
 */

public class Rating implements Parcelable{

    private int userId;
    private int sizzleId;
    private double sausage;
    private double bread;
    private double onion;
    private double sauce;
    private double aggregateRating;


    public Rating(double sausage, double bread, double onion, double sauce, double aggregateRating) {
        this.sausage = sausage;
        this.bread = bread;
        this.onion = onion;
        this.sauce = sauce;
        this.aggregateRating = aggregateRating;
    }

    public Rating(int userId, int sizzleId, double sausage, double bread, double onion, double sauce) {
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

        this.sausage = in.readDouble();
        this.bread = in.readDouble();
        this.onion =in.readDouble();
        this.sauce = in.readDouble();
        this.aggregateRating = in.readDouble();
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

    public double getSausage() {
        return sausage;
    }

    public void setSausage(double sausage) {
        this.sausage = sausage;
    }

    public double getBread() {
        return bread;
    }

    public void setBread(double bread) {
        this.bread = bread;
    }

    public double getOnion() {
        return onion;
    }

    public void setOnion(double onion) {
        this.onion = onion;
    }

    public double getSauce() {
        return sauce;
    }

    public void setSauce(double sauce) {
        this.sauce = sauce;
    }

    public double getAggregateRating() {
        return aggregateRating;
    }

    public void setAggregateRating(double aggregateRating) {
        this.aggregateRating = aggregateRating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeDouble(sausage);
        dest.writeDouble(bread);
        dest.writeDouble(onion);
        dest.writeDouble(sauce);
        dest.writeDouble(aggregateRating);
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
