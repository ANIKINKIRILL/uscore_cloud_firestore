package com.it_score.admin.uscore001.models;

/**
 * Предмет, который ведет учитель
 */

public class Subject {
    String id;
    String name;

    public Subject(String id, String name){
        this.id = id;
        this.name = name;
    }

    public Subject(){

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
}
