package com.example.frintos.Model;

public class usersData {
    String name;
    String online;
    String status;
    String picture;
    String thumb;
    String token;
    String upvotes;

    public usersData(String name,String online, String status, String picture, String thumb, String token, String upvotes) {
        this.name = name;
        this.online = online;
        this.status = status;
        this.picture = picture;
        this.thumb = thumb;
        this.token = token;
        this.upvotes = upvotes;
    }

    public usersData() {
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

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }
}
