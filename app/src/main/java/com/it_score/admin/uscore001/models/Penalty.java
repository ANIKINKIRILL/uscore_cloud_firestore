package com.it_score.admin.uscore001.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Penalty {

    String id;
    String optionID;
    String groupID;
    String studentID;
    String score;
    String teacherID;
    String date;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String addedDate = simpleDateFormat.format(new Date());

    public Penalty(String id, String optionID, String groupID, String studentID, String score, String teacherID) {
        this.id = id;
        this.optionID = optionID;
        this.groupID = groupID;
        this.studentID = studentID;
        this.score = score;
        this.teacherID = teacherID;
        this.date = addedDate;
    }

    public Penalty(){

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
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
