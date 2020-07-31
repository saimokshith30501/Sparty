package com.developer.sparty.Models;

import com.google.firebase.database.PropertyName;

public class Modelmessage {
    String sender,receiver,message,timestamp;
    boolean isSeen;

    public Modelmessage(String sender, String receiver, String message, String timestamp, boolean isSeen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public Modelmessage() {
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    @PropertyName("isSeen")
    public boolean isSeen() {
        return isSeen;
    }
    @PropertyName("isSeen")
    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
