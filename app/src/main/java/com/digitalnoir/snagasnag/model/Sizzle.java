package com.digitalnoir.snagasnag.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by Troy on 2/20/2018.
 */

public class Sizzle implements Parcelable{

    // Sizzle variables
    private int sizzleId;
    private String latitude;
    private String longitude;
    private String name;
    private String address;
    private String detail;
    private String photoUrl;
    private Rating rating;
    private List<Comment> comments;


    public Sizzle(String latitude, String longitude, String name, String address, String detail) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.detail = detail;
    }

    public Sizzle(int sizzleId, String latitude, String longitude, String name, String address, String detail, String photoUrl, Rating rating, List<Comment> comments) {
        this.sizzleId = sizzleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.detail = detail;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.comments = comments;
    }

    /**
     * Use when reconstructing Sizzle object from parcel
     * This will be used only by the 'CREATOR'
     * @param in a parcel to read this object
     */
    public Sizzle(Parcel in) {

        this.sizzleId = in.readInt();
        this.latitude = in.readString();
        this.longitude =in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.detail = in.readString();
        this.photoUrl = in.readString();
    }

    public int getSizzleId() {
        return sizzleId;
    }

    public void setSizzleId(int sizzleId) {
        this.sizzleId = sizzleId;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(sizzleId);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(detail);
        dest.writeString(photoUrl);
        dest.writeTypedList(comments);
        dest.writeValue(rating);

    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     *
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Sizzle> CREATOR = new Parcelable.Creator<Sizzle>() {

        public Sizzle createFromParcel(Parcel in) {
            return new Sizzle(in);
        }

        public Sizzle[] newArray(int size) {
            return new Sizzle[size];
        }
    };
}
