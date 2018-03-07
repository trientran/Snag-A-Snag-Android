package com.digitalnoir.snagasnag.model;

/**
 * Created by Troy on 2/20/2018.
 */

public class Rating {

    private int userId;
    private int sizzleId;
    private int sausage;
    private int bread;
    private int onion;
    private int sauce;


    public Rating(int sausage, int bread, int onion, int sauce) {
        this.sausage = sausage;
        this.bread = bread;
        this.onion = onion;
        this.sauce = sauce;
    }

    public Rating(int userId, int sizzleId, int sausage, int bread, int onion, int sauce) {
        this.userId = userId;
        this.sizzleId = sizzleId;
        this.sausage = sausage;
        this.bread = bread;
        this.onion = onion;
        this.sauce = sauce;
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

    public int getSausage() {
        return sausage;
    }

    public void setSausage(int sausage) {
        this.sausage = sausage;
    }

    public int getBread() {
        return bread;
    }

    public void setBread(int bread) {
        this.bread = bread;
    }

    public int getOnion() {
        return onion;
    }

    public void setOnion(int onion) {
        this.onion = onion;
    }

    public int getSauce() {
        return sauce;
    }

    public void setSauce(int sauce) {
        this.sauce = sauce;
    }
}
