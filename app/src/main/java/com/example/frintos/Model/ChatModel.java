package com.example.frintos.Model;

public class ChatModel {
    String myuid;
    String message;
    String from;
    String type;
    String thumb;
    String messageId;
    long timestamp;
    boolean seen;

    public ChatModel() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public ChatModel(String myuid, String message, String from, String type, String thumb, String messageId, long timestamp, boolean seen) {
        this.myuid = myuid;
        this.message = message;
        this.from = from;
        this.type = type;
        this.thumb = thumb;
        this.messageId = messageId;
        this.timestamp = timestamp;
        this.seen = seen;
    }

    public String getMyuid() {
        return myuid;
    }

    public void setMyuid(String myuid) {
        this.myuid = myuid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
