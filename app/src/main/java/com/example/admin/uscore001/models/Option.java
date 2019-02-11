package com.example.admin.uscore001.models;

public class Option {

    private String id;
    private String points;
    private String name;

    public Option(String id, String points, String name) {
        this.id = id;
        this.points = points;
        this.name = name;
    }

    Option(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
