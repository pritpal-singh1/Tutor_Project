package com.example.tutor_project.Model;

public class ModelChat {
    String message, reciever,sender, timestamp;
    boolean isSeen;

    public ModelChat() {
    }


    public ModelChat(String message, String reciever, String sender, String timestamp, boolean isSeen) {
        this.message = message;
        this.reciever = reciever;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }



    public boolean isIsSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

}
