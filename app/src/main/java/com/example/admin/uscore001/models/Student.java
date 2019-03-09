package com.example.admin.uscore001.models;

import android.os.AsyncTask;

import com.example.admin.uscore001.AsyncTaskArguments;
import com.example.admin.uscore001.AsyncTaskDataArgument;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.FirebaseServer;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

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

    public static final String STUDENT_DATA = "student_data";
    public static final String EMAIL = "email";
    public static final String GROUP_ID = "group_id";
    public static final String IMAGE_PATH = "image_path";
    public static final String SCORE = "score";
    public static final String ID = "id";
    public static final String LIMIT_SCORE = "limitScore";
    public static final String TEACHER_ID = "teacher_id";
    public static final String FIRST_NAME = "firs_name";
    public static final String SECOND_NAME = "second_name";
    public static final String LAST_NAME = "last_name";
    public static final String STATUS_ID = "status_id";
    public static final String CONFIRMED_REQUESTS_AMOUNT = "confirmed_requests_amount";
    public static final String DENIED_REQUESTS_AMOUNT = "denied_requests_amount";
    public static final String RATE_IN_GROUP = "rate_in_group";
    public static final String RATE_IN_SCHOOL = "rate_in_school";

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
     * Выгрузка всех учеников группы
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
     * Выгрузка всех учеников со школы
     * @param callback
     */

    public static void loadAllStudents(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.LoadAllStudents loadAllStudents = new FirebaseServer.LoadAllStudents();
        loadAllStudents.execute(asyncTaskArguments);
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

    /**
     * Выгрузка данных с аккаунта ученика
     * @param callback
     * @param studentLogin
     */

    public static void getStudentClass(Callback callback, String studentLogin){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentLogin));
        FirebaseServer.GetStudentClass getStudentClass = new FirebaseServer.GetStudentClass();
        getStudentClass.execute(asyncTaskArguments);
    }

    /**
     * Получение всех принятых запросов на добавление очков
     * @param callback
     * @param studentID         // ID Ученика
     */

    public static void getConfirmedRequests(Callback callback, String studentID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentID));
        FirebaseServer.GetStudentConfirmedRequests getStudentConfirmedRequests = new FirebaseServer.GetStudentConfirmedRequests();
        getStudentConfirmedRequests.execute(asyncTaskArguments);
    }

    /**
     * Получение всех отклоненных запросов на добавление очков
     * @param callback
     * @param studentID         // ID Ученика
     */

    public static void getDeniedRequests(Callback callback, String studentID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentID));
        FirebaseServer.GetStudentDeniedRequests getStudentDeniedRequests = new FirebaseServer.GetStudentDeniedRequests();
        getStudentDeniedRequests.execute(asyncTaskArguments);
    }

}
