package com.digitalnoir.snagasnag.model;

/**
 * Created by Troy on 2/20/2018.
 */

public class Comment {

    private String userId;
    private String sizzleId;
    private String comment;

    public Comment(String userId, String sizzleId, String comment) {
        this.userId = userId;
        this.sizzleId = sizzleId;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
