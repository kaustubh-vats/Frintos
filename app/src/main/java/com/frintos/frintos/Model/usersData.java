package com.frintos.frintos.Model;

public class usersData {
    String name;
    String online;
    String status;
    String picture;
    String thumb;
    String token;
    String upvotes;

    public usersData(String name, String online, String status, String picture, String thumb, String token, String upvotes) {
        this.name = name;
        this.online = online;
        this.status = status;
        this.picture = picture;
        this.thumb = thumb;
        this.token = token;
        this.upvotes = upvotes;
    }

    public usersData(String name, Long online, String status, String picture, String thumb, String token, String upvotes) {
        this.name = name;
        this.status = status;
        this.picture = picture;
        this.thumb = thumb;
        this.token = token;
        this.upvotes = upvotes;
        if(online == 1) this.online = "true";
        else if(online == 0) this.online = "false";
        else if(online == -1) this.online = "hidden";
        else this.online = online.toString();
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

    public void setOnline(Object online) {
        if(online == null) {
            this.online = "false";
            return;
        }
        if(online instanceof Long){
            Long myOnline = (Long)online;
            if(myOnline == 1) this.online = "true";
            else if(myOnline == 0) this.online = "false";
            else if(myOnline == -1) this.online = "hidden";
            else this.online = myOnline.toString();
        } else {
            this.online = (String) online.toString();
        }
    }
}
