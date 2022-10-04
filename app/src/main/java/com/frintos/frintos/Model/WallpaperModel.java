package com.frintos.frintos.Model;

public class WallpaperModel {
    String url;
    String setFrom;
    Long timestamp;
    public WallpaperModel(){
        // Do nothing
    }

    public WallpaperModel(String url, String setFrom, Long timestamp) {
        this.url = url;
        this.setFrom = setFrom;
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSetFrom() {
        return setFrom;
    }

    public void setSetFrom(String setFrom) {
        this.setFrom = setFrom;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
