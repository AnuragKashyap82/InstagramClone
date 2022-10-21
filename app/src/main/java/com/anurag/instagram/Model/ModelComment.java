package com.anurag.instagram.Model;

public class ModelComment {
    String comment, uid, timestamp, postId;

    public ModelComment() {
    }

    public ModelComment(String comment, String uid, String timestamp, String postId) {
        this.comment = comment;
        this.uid = uid;
        this.timestamp = timestamp;
        this.postId = postId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
