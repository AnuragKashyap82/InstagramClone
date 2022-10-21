package com.anurag.instagram.Model;

public class ModelFeed {

    String caption, uid,  postId, postImage;

    public ModelFeed() {
    }

    public ModelFeed(String caption, String uid, String postId, String postImage) {
        this.caption = caption;
        this.uid = uid;
        this.postId = postId;
        this.postImage = postImage;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}
