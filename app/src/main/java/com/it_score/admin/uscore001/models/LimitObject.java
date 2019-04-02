package com.it_score.admin.uscore001.models;

import com.google.firebase.Timestamp;

import java.util.Date;

/**
 * Класс удаленного запроса на добавление запроса
 */

public class LimitObject {
    String id;
    String limit;
    Timestamp date_of_start;

    public LimitObject(String id, String limit, Timestamp date_of_start) {
        this.id = id;
        this.limit = limit;
        this.date_of_start = date_of_start;
    }

    public LimitObject(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public Timestamp getDate_of_start() {
        return date_of_start;
    }

    public void setDate_of_start(Timestamp date_of_start) {
        this.date_of_start = date_of_start;
    }
}
