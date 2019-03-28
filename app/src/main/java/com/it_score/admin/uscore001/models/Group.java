package com.it_score.admin.uscore001.models;

public class Group {
    String id;
    String name;
    String teacherID;

    public Group(String id, String name, String teacherID) {
        this.id = id;
        this.name = name;
        this.teacherID = teacherID;
    }

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Group(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }
}
