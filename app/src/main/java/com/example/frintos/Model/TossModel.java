package com.example.frintos.Model;

public class TossModel {
    String picture;
    boolean reported;
    String sender;
    String sender_id;
    String thumb;
    String toss_mes;
    boolean upvoted;

    public TossModel() {
    }

    public TossModel(String picture, boolean reported, String sender, String sender_id, String thumb, String toss_mes, boolean upvoted) {
        this.picture = picture;
        this.reported = reported;
        this.sender = sender;
        this.sender_id = sender_id;
        this.thumb = thumb;
        this.toss_mes = toss_mes;
        this.upvoted = upvoted;
    }

    public String getToss_mes() {
        return toss_mes;
    }

    public void setToss_mes(String toss_mes) {
        this.toss_mes = toss_mes;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSender_id() {
        return sender_id;
    }

    public void setSender_id(String sender_id) {
        this.sender_id = sender_id;
    }

    public boolean isUpvoted() {
        return upvoted;
    }

    public void setUpvoted(boolean upvoted) {
        this.upvoted = upvoted;
    }

    public boolean isReported() {
        return reported;
    }

    public void setReported(boolean reported) {
        this.reported = reported;
    }
}
