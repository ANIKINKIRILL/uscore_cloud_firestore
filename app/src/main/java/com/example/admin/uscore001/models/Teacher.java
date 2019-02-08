package com.example.admin.uscore001.models;

public class Teacher {
    String email;
    String fullname;
    String image_path;
    String position;
    String subject;

    public Teacher(String email, String fullname, String image_path, String position, String subject) {
        this.email = email;
        this.fullname = fullname;
        this.image_path = image_path;
        this.position = position;
        this.subject = subject;
    }

    public Teacher(){}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
