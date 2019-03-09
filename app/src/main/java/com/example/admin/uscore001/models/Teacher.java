package com.example.admin.uscore001.models;

import com.example.admin.uscore001.AsyncTaskArguments;
import com.example.admin.uscore001.AsyncTaskDataArgument;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.FirebaseServer;

/**
 * Учитель
 */

public class Teacher {
   private String responsible_email;
   private String image_path;
   private String positionID;
   private String subjectID;
   private String firstName;
   private String secondName;
   private String lastName;
   private String groupID;
   private String id;
   private String requestID;
   private String statusID;

   public static final String TEACHER_DATA = "teacher_data";
   public static final String GROUP_ID = "group_id";
   public static final String EMAIL = "email";
   public static final String IMAGE_PATH = "image_path";
   public static final String POSITION_ID = "position_id";
   public static final String SUBJECT_ID = "subject_id";
   public static final String REQUEST_ID = "request_id";
   public static final String STATUS_ID = "status_id";
   public static final String TEACHER_REQUEST_ID = "teacher_request_id";
   public static final String TEACHER_ID = "teacher_id";
   public static final String FIRST_NAME = "first_name";
   public static final String SECOND_NAME = "second_name";
   public static final String LAST_NAME = "last_name";

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

    /*
                        METHODS SECTION
     */

    /**
     * Выгрузка всех учителей
     * @param callback
     */

    public static void loadAllTeachers(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.LoadAllTeachers loadAllTeachers = new FirebaseServer.LoadAllTeachers();
        loadAllTeachers.execute(asyncTaskArguments);
    }

    /**
     * Извлечение почты учителя
     * @param firstName
     * @param lastName
     * @param callback
     */

    public static void getTeacherEmail(String firstName, String lastName, Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(firstName, lastName));
        FirebaseServer.GetTeacherEmail getTeacherEmail = new FirebaseServer.GetTeacherEmail();
        getTeacherEmail.execute(asyncTaskArguments);
    }

    /**
     * Выгрузка данных с учительского аккаунта
     * @param callback
     * @param login
     */

    public static void getTeacherClass(Callback callback, String login){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(login));
        FirebaseServer.GetTeacherClass getTeacherClass = new FirebaseServer.GetTeacherClass();
        getTeacherClass.execute(asyncTaskArguments);
    }

    /**
     * Добавление очков к ученику
     * @param score     Очки (Запрашиваемы)
     * @param studentID ID ученика
     */

    public static void addScoreToStudent(Callback callback, String score, String studentID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(score, studentID));
        FirebaseServer.AddScoreToStudent addScoreToStudent = new FirebaseServer.AddScoreToStudent();
        addScoreToStudent.execute(asyncTaskArguments);
    }

}
