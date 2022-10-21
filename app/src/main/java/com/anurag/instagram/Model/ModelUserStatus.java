package com.anurag.instagram.Model;

import java.util.ArrayList;

public class ModelUserStatus {
    private String name, username, profileImage;
    private long lastUpdated;
    private ArrayList<ModelStatus> modelStatuses;

    public ModelUserStatus() {
    }

    public ModelUserStatus(String name, String username, String profileImage, long lastUpdated, ArrayList<ModelStatus> modelStatuses) {
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.lastUpdated = lastUpdated;
        this.modelStatuses = modelStatuses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<ModelStatus> getStatuses() {
        return modelStatuses;
    }

    public void setStatuses(ArrayList<ModelStatus> modelStatuses) {
        this.modelStatuses = modelStatuses;
    }
}