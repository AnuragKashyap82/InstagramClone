package com.anurag.instagram.Model;

public class ModelUsers {

    String uid, email, username, online, timestamp, profileImage, name, bio, website;

    public ModelUsers() {
    }

    public ModelUsers(String uid, String email, String username, String online, String timestamp, String profileImage, String name, String bio, String website) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.online = online;
        this.timestamp = timestamp;
        this.profileImage = profileImage;
        this.name = name;
        this.bio = bio;
        this.website = website;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
