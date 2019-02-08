package com.example.admin.uscore001.models;

import java.util.Date;

public class RequestAddingScore {
    boolean answer;
    boolean cancel;
    String body;
    String date;
    String getter;
    String  image_path;
    String senderEmail;
    String senderUsername;
    String group;
    int score;
    String requestID;
    String option;

    public RequestAddingScore(boolean answer, String body, String date, String getter, String image_path,
                              String senderEmail, String senderUsername, int score, String group, String requestID,
                                boolean cancel, String option) {
        this.answer = answer;
        this.body = body;
        this.date = date;
        this.getter = getter;
        this.image_path = image_path;
        this.senderEmail = senderEmail;
        this.senderUsername = senderUsername;
        this.score = score;
        this.group = group;
        this.requestID = requestID;
        this.cancel = cancel;
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public RequestAddingScore(){}

    public boolean isCancel() {
        return cancel;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public int getScore() {
        return score;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
}
