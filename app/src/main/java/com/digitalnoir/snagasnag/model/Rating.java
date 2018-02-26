package com.digitalnoir.snagasnag.model;

/**
 * Created by Troy on 2/20/2018.
 */

public class Rating {

    private String userId;
    private String sizzleId;
    private String sausage;
    private String bread;
    private String onion;
    private String sauce;

    public Rating(String userId, String sizzleId, String sausage, String bread, String onion, String sauce) {
        this.userId = userId;
        this.sizzleId = sizzleId;
        this.sausage = sausage;
        this.bread = bread;
        this.onion = onion;
        this.sauce = sauce;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSizzleId() {
        return sizzleId;
    }

    public void setSizzleId(String sizzleId) {
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
}