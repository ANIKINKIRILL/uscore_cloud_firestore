package com.it_score.admin.uscore001.models;

/**
 * Функция Админа
 */

public class AdminFunction {
    String id;
    String name;
    String description;

    public AdminFunction(){

    }

    public AdminFunction(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public void setName(String title) {
        this.name = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
