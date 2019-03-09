package com.example.admin.uscore001.models;

import com.example.admin.uscore001.AsyncTaskArguments;
import com.example.admin.uscore001.AsyncTaskDataArgument;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.FirebaseServer;
import com.google.firebase.Timestamp;

/**
 * Ученик
 */

public class Student {
    private String email;
    private String groupID;
    private String image_path;
    private String score;
    private String id;
    private String limitScore;
    private String teacherID;
    private String firstName;
    private String secondName;
    private String lastName;
    private String statusID;
    private Timestamp spendLimitScoreDate;

    public Student(
            String email,
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

    public Student(){

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

    /*
                METHODS SECTION
     */

    /**
     * Выгрузка всех студентов
     *
     * @param callback
     * @param group_name
     */

    public static void loadGroupStudents(String group_name, Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(group_name));
        FirebaseServer.LoadGroupStudents loadStudentClassmates = new FirebaseServer.LoadGroupStudents();
        loadStudentClassmates.execute(asyncTaskArguments);
    }

    /**
     * Извлечение почты ученика
     *
     * @param firstName
     * @param secondName
     * @param callback
     */

    public static void getStudentEmail(String groupID, String firstName, String secondName, Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(groupID, firstName, secondName));
        FirebaseServer.GetStudentEmail getStudentEmail = new FirebaseServer.GetStudentEmail();
        getStudentEmail.execute(asyncTaskArguments);
    }

    /**
     * Отправка заявки на регистрацию ученика
     *
     * @param callback                              // Вызываемый callback полсе отпарвки заяки
     * @param firstName                             // Имя ученика
     * @param secondName                            // Фамилия ученика
     * @param lastName                              // Отчество ученика
     * @param email                                 // Email ученика
     * @param groupID                               // Id группы ученика
     * @param teacherID                             // Id учителя ученика
     * @param confirmed                             // Принята ли заявка
     * @param denied                                // Отклонена ли заявка
     */
    
    public static void sendRegistrationRequest(Callback callback, String firstName, String secondName, String lastName, String email, String groupID, String teacherID, boolean confirmed, boolean denied){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(firstName, secondName, lastName, email, groupID, teacherID, confirmed, denied));
        FirebaseServer.SendRegistrationRequest sendRegistrationRequest = new FirebaseServer.SendRegistrationRequest();
        sendRegistrationRequest.execute(asyncTaskArguments);
    }

}
