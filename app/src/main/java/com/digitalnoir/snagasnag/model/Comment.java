package com.digitalnoir.snagasnag.model;

/**
 * Created by Troy on 2/20/2018.
 */

public class Comment {

    private int userId;
    private int sizzleId;
    private String comment;

    public Comment(int userId, int sizzleId, String comment) {
        this.userId = userId;
        this.sizzleId = sizzleId;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
