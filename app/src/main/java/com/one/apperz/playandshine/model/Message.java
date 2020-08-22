package com.one.apperz.playandshine.model;
import com.google.firebase.Timestamp;

public class Message {

    private String from;
    private String body;
    private Timestamp timestamp;

    public Message() {
    }

    public Message(String from, String body, Timestamp timestamp) {
        this.from = from;
        this.body = body;
        this.timestamp = timestamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
