package com.example.admin.uscore001.models;

public class Penalty {
    String id;
    String optionID;
    String groupID;
    String studentID;
    String score;

    public Penalty(String id, String optionID, String groupID, String studentID, String score) {
        this.id = id;
        this.optionID = optionID;
        this.groupID = groupID;
        this.studentID = studentID;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOptionID() {
        return optionID;
    }

    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
