package com.anurag.instagram.Model;

public class ModelFollowers {

    String uid, userUid;

    public ModelFollowers() {
    }

    public ModelFollowers(String uid, String userUid) {
        this.uid = uid;
        this.userUid = userUid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}