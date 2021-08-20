package com.frintos.frintos.Model;

public class MyUserData {
    String name;
    String online;
    String status;
    String picture;
    String thumb;
    String token;
    String upvotes;
    String uid;

    public MyUserData(String name,String online, String status, String picture, String thumb, String token, String upvotes, String uid) {
        this.name = name;
        this.online = online;
        this.status = status;
        this.picture = picture;
        this.thumb = thumb;
        this.token = token;
        this.upvotes = upvotes;
        this.uid = uid;
    }

    public MyUserData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(String upvotes) {
        this.upvotes = upvotes;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
