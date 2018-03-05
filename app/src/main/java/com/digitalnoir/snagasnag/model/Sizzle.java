package com.digitalnoir.snagasnag.model;

/**
 * Created by Troy on 2/20/2018.
 */

public class Sizzle {

    // Sizzle variables
    private int sizzleId;
    private String latitude;
    private String longitude;
    private String name;
    private String address;
    private String detail;
    private String photoUrl;


    public Sizzle(String latitude, String longitude, String name, String address, String detail) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.detail = detail;
    }

    public Sizzle(int sizzleId, String latitude, String longitude, String name, String address, String detail, String photoUrl) {
        this.sizzleId = sizzleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.address = address;
        this.detail = detail;
        this.photoUrl = photoUrl;
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
}
