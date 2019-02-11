package com.example.admin.uscore001.models;

import java.util.Date;

public class RequestAddingScore {
    String id;
    String body;
    String date;
    String getter;
    String image_path;
    String senderEmail;
    String firstName;
    String secondName;
    String lastName;
    int score;
    String groupID;
    String requestID;
    String optionID;
    boolean answered;
    boolean canceled;
    String senderID;

    public RequestAddingScore(
            String id,
            String body,
            String date,
            String getter,
            String image_path,
            String senderEmail,
            String firstName,
            String secondName,
            String lastName,
            int score,
            String groupID,
            String requestID,
            String optionID,
            boolean answered,
            boolean canceled,
            String senderID
    ) {
        this.id = id;
        this.body = body;
        this.date = date;
        this.getter = getter;
        this.image_path = image_path;
        this.senderEmail = senderEmail;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.score = score;
        this.groupID = groupID;
        this.requestID = requestID;
        this.optionID = optionID;
        this.answered = answered;
        this.canceled = canceled;
        this.senderID = senderID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getOption() {
        return optionID;
    }

    public void setOption(String option) {
        this.optionID = option;
    }

    public RequestAddingScore() {
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

    public void setScore(int score) {
        this.score = score;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getOptionID() {
        return optionID;
    }

    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}