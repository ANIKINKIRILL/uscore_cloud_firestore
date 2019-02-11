package com.example.admin.uscore001.models;

public class Teacher {
    String responsible_email;
    String image_path;
    String positionID;
    String subjectID;
    String firstName;
    String secondName;
    String lastName;
    String groupID;
    String id;
    String requestID;
    String statusID;

    public Teacher(
            String responsible_email,
            String image_path,
            String positionID,
            String subjectID,
            String firstName,
            String secondName,
            String lastName,
            String groupID,
            String id,
            String requestID,
            String statusID) {
        this.responsible_email = responsible_email;
        this.image_path = image_path;
        this.positionID = positionID;
        this.subjectID = subjectID;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.groupID = groupID;
        this.id = id;
        this.requestID = requestID;
        this.statusID = statusID;
    }

    public Teacher(){}

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

    public String getResponsible_email() {
        return responsible_email;
    }

    public void setResponsible_email(String responsible_email) {
        this.responsible_email = responsible_email;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getPositionID() {
        return positionID;
    }

    public void setPositionID(String positionID) {
        this.positionID = positionID;
    }

    public String getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(String subjectID) {
        this.subjectID = subjectID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getStatusID() {
        return statusID;
    }

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    @Override
    public String toString() {
        return this.getFirstName() + " " + this.getLastName();
    }
}
