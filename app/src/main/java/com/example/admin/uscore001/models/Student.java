package com.example.admin.uscore001.models;

import com.google.firebase.Timestamp;

public class Student {
    String email;
    String groupID;
    String image_path;
    String score;
    String id;
    String limitScore;
    String teacherID;
    String firstName;
    String secondName;
    String lastName;
    String statusID;
    Timestamp spendLimitScoreDate;

    public Student(
            String email,
            String username,
            String groupID,
            String image_path,
            String score,
            String id,
            String limitScore,
            String teacherID,
            String firstName,
            String secondName,
            String lastName,
            String statusID) {
        this.email = email;
        this.groupID = groupID;
        this.image_path = image_path;
        this.score = score;
        this.id = id;
        this.limitScore = limitScore;
        this.teacherID = teacherID;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.statusID = statusID;
    }

    {
        score = "";
    }

    public Student(){

    }

    public Student(
            String score,
            String username,
            String image_path,
            String groupID,
            String email,
            String firstName,
            String secondName,
            String lastName,
            String statusID){
        this.score = score;
        this.image_path = image_path;
        this.groupID = groupID;
        this.email = email;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.statusID = statusID;
    }

    public Timestamp getSpendLimitScoreDate() {
        return spendLimitScoreDate;
    }

    public void setSpendLimitScoreDate(Timestamp spendLimitScoreDate) {
        this.spendLimitScoreDate = spendLimitScoreDate;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
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

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getLimitScore() {
        return limitScore;
    }

    public void setLimitScore(String limitScore) {
        this.limitScore = limitScore;
    }
    public String getScore() {
        return score;
    }
    public void setScore(String score) {
        this.score = score;
    }
    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String group) {
        this.groupID = group;
    }
}
