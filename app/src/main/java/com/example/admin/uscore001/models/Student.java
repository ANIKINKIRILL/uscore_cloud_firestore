package com.example.admin.uscore001.models;

public class Student {
    String email;
    String username;
    String group;
    String image_path;
    String score;
    String uID;
    String limitScore;
    String teacherID;

    public Student(String email, String username, String group, String image_path, String score, String uID, String limitScore, String teacherID) {
        this.email = email;
        this.username = username;
        this.group = group;
        this.image_path = image_path;
        this.score = score;
        this.uID = uID;
        this.limitScore = limitScore;
        this.teacherID = teacherID;
    }

    {
        score = "";
    }

    public Student(){

    }

    public Student(String score, String username, String image_path, String group, String email){
        this.score = score;
        this.username = username;
        this.image_path = image_path;
        this.group = group;
        this.email = email;
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

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
