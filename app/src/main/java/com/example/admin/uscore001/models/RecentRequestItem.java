package com.example.admin.uscore001.models;

public class RecentRequestItem {
    String score;
    String date;
    String result;
    String teacher;

    public RecentRequestItem(String score, String date, String result, String teacher) {
        this.score = score;
        this.date = date;
        this.result = result;
        this.teacher = teacher;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
