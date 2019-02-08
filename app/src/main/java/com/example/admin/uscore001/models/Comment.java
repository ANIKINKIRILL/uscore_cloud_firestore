package com.example.admin.uscore001.models;

public class Comment {
    String senderImage;
    String senderUsername;
    String currentDate;
    String body;
    int likes;
    int dislikes;

    public Comment(String senderImage, String senderUsername, String currentDate, String body, int likes, int dislikes) {
        this.senderImage = senderImage;
        this.senderUsername = senderUsername;
        this.currentDate = currentDate;
        this.body = body;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public Comment(){

    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }
}
