package com.it_score.admin.uscore001.models;

import com.it_score.admin.uscore001.AsyncTaskArguments;
import com.it_score.admin.uscore001.AsyncTaskDataArgument;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.FirebaseServer;
import com.google.firebase.Timestamp;

/**
 * Ученик
 */

public class Student {
    private String email;
    private String groupID;
    private String image_path;
    private int score;
    private String id;
    private String limitScore;
    private String teacherID;
    private String firstName;
    private String secondName;
    private String lastName;
    private String statusID;
    private Timestamp spendLimitScoreDate;
    private boolean change_password;

    public static final String STUDENT_DATA = "student_data";
    public static final String EMAIL = "email";
    public static final String GROUP_ID = "group_id";
    public static final String GROUP_NAME = "group_name";
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
    public static final String CHANGE_PASSWORD = "change_password";
    public static final String LIMIT_REMOTE_REQUEST_NUMBER = "limit_remote_request_number";

    public Student(
            String email,
            String groupID,
            String image_path,
            int score,
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

    public boolean isChange_password() {
        return change_password;
    }

    public void setChange_password(boolean change_password) {
        this.change_password = change_password;
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
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
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
     * Выгрузка всех учеников группы по названию группы
     *
     * @param callback
     * @param group_name        Название группы
     */

    public static void loadGroupStudentsByGroupName(String group_name, Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(group_name));
        FirebaseServer.LoadGroupStudentsByGroupName loadGroupStudentsByGroupName = new FirebaseServer.LoadGroupStudentsByGroupName();
        loadGroupStudentsByGroupName.execute(asyncTaskArguments);
    }

    /**
     * Выгрузка всех учеников группы по id группы
     *
     * @param callback
     * @param groupID        id группы
     * @param studentID      id ученика
     */

    public static void loadGroupStudentsByGroupID(String groupID,String studentID, Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(groupID, studentID));
        FirebaseServer.LoadGroupStudentsByGroupID loadGroupStudentsByGroupID = new FirebaseServer.LoadGroupStudentsByGroupID();
        loadGroupStudentsByGroupID.execute(asyncTaskArguments);
    }

    /**
     * Выгрузка всех учеников со школы
     * @param callback
     * @param studentID         id ученика
     */

    public static void loadAllStudents(Callback callback, String studentID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentID));
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

    /**
     * Получить запросы ученика
     *
     * @param callback      Вызываемый callback полсе получения запросов
     * @param studentID     id ученика
     */

    public static void getStudentRequests(Callback callback, String studentID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentID));
        FirebaseServer.GetStudentRequests getStudentRequests = new FirebaseServer.GetStudentRequests();
        getStudentRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить непросмотренные запросы ученика
     *
     * @param callback      Вызываемый callback полсе получения запросов
     * @param studentID     id ученика
     */

    public static void getStudentNewRequests(Callback callback, String studentID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentID));
        FirebaseServer.GetStudentNewRequests getStudentNewRequests = new FirebaseServer.GetStudentNewRequests();
        getStudentNewRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить штрафы студента
     *
     * @param callback  Вызываемый callback полсе получения штрафов
     * @param studentID id ученика
     */

    public static void getStudentPenalties(Callback callback, String studentID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentID));
        FirebaseServer.GetStudentPenalties getStudentPenalties = new FirebaseServer.GetStudentPenalties();
        getStudentPenalties.execute(asyncTaskArguments);
    }

    /*
     * Отправить запрос учителю на добавление очков
     *
     * @param callback      callback который вернется после добавления очков
     * @param id            id запроса
     * @param body          текст запроса
     * @param date          дата запроса
     * @param getter        фио учителя к кому был этот запрос отпрален
     * @param image_path    приклепленная фотография
     * @param senderEmail   email ученика
     * @param firstName     имя
     * @param secondName    фамилия
     * @param lastName      отчество
     * @param score         очки
     * @param groupID       id группы
     * @param requestID     requestID учителя
     * @param optionID      id опции за что ученик хочет чтобы ему начислели быллы
     * @param answered      принят ли запрос
     * @param canceled      отклонен ли запрос
     * @param senderID      id ученика

    public static void sendRequest(
        Callback callback,
        String id,
        String body,
        String date,
        String getter,
        String image_path,
        String senderEmail,
        String firstName,
        String secondName,
        String lastName,
        int score,
        String groupID,
        String requestID,
        String optionID,
        boolean answered,
        boolean canceled,
        String senderID)
    {
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(
                id, body, date, getter, image_path, senderEmail, firstName,
                secondName, lastName, score, groupID, requestID, optionID,
                answered, canceled, senderID));
        FirebaseServer.SendRequest sendRequest = new FirebaseServer.SendRequest();
        sendRequest.execute(asyncTaskArguments);
    }
    */

    public static void sendRequest(RequestAddingScore request, Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(request));
        FirebaseServer.SendRequest sendRequest = new FirebaseServer.SendRequest();
        sendRequest.execute(asyncTaskArguments);
    }

}
