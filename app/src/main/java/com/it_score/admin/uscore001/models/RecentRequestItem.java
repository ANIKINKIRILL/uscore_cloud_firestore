package com.it_score.admin.uscore001.models;

public class RecentRequestItem {
    String answered;
    String body;
    boolean canceled;
    String date;
    String firstName;
    String getter;
    String groupID;
    String id;
    String image_path;
    String option;

    public RecentRequestItem(String answered, String body, boolean canceled, String date, String firstName, String getter, String groupID, String id, String image_path, String option) {
        this.answered = answered;
        this.body = body;
        this.canceled = canceled;
        this.date = date;
        this.firstName = firstName;
        this.getter = getter;
        this.groupID = groupID;
        this.id = id;
        this.image_path = image_path;
        this.option = option;
    }

    public RecentRequestItem(){

    }

    public String getAnswered() {
        return answered;
    }

    public void setAnswered(String answered) {
        this.answered = answered;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getGetter() {
        return getter;
    }

    public void setGetter(String getter) {
        this.getter = getter;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
