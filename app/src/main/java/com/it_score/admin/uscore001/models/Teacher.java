package com.it_score.admin.uscore001.models;

import com.it_score.admin.uscore001.AsyncTaskArguments;
import com.it_score.admin.uscore001.AsyncTaskDataArgument;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.FirebaseServer;

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
   public static final String GROUP_NAME = "group_name";
   public static final String EMAIL = "email";
   public static final String IMAGE_PATH = "image_path";
   public static final String POSITION_ID = "position_id";
   public static final String SUBJECT_ID = "subject_id";
   public static final String STATUS_ID = "status_id";
   public static final String TEACHER_REQUEST_ID = "teacher_request_id";
   public static final String TEACHER_ID = "teacher_id";
   public static final String FIRST_NAME = "first_name";
   public static final String SECOND_NAME = "second_name";
   public static final String LAST_NAME = "last_name";
   public static final String SUBJECT_DATA = "subject_data";
   public static final String POSITION_DATA = "position_data";

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
        return this.getSecondName() + " " + this.getFirstName() + " " + this.getLastName();
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

    public static void addScoreToStudent(Callback callback, String score, String studentID, int studentScore){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(score, studentID, studentScore));
        FirebaseServer.AddScoreToStudent addScoreToStudent = new FirebaseServer.AddScoreToStudent();
        addScoreToStudent.execute(asyncTaskArguments);
    }

    /**
     * Предмет учителя по ID полям
     *
     * @param callback      callback, который вернётся после асинхронного получения данных с Сервера
     * @param subjectID     id предмета
     */

    public static void getSubjectValueByID(Callback callback, String subjectID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(subjectID));
        FirebaseServer.GetSubjectValueByID getSubjectValueByID = new FirebaseServer.GetSubjectValueByID();
        getSubjectValueByID.execute(asyncTaskArguments);
    }

    /**
     * Позиция учителя по ID полям
     *
     * @param callback      callback, который вернётся после асинхронного получения данных с Сервера
     * @param positionID     id позиции
     */

    public static void getPositionValueByID(Callback callback, String positionID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(positionID));
        FirebaseServer.GetPositionValueByID getPositionValueByID = new FirebaseServer.GetPositionValueByID();
        getPositionValueByID.execute(asyncTaskArguments);
    }

    /**
     * Обновить ФИО учителя
     *
     * @param callback          Callback, который вернётся после обновления
     * @param teacherID         id учителя
     * @param firstName         Имя
     * @param secondName        Фамилия
     * @param lastName          Отчество
     */

    public static void updateCredentials(Callback callback, String teacherID, String firstName, String secondName, String lastName, String subjectID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherID, firstName, secondName, lastName, subjectID));
        FirebaseServer.UpdateTeacherCredentials updateTeacherCredentials = new FirebaseServer.UpdateTeacherCredentials();
        updateTeacherCredentials.execute(asyncTaskArguments);
    }

    /**
     * Получить все запросы учителя
     */

    public static void getTeacherRequests(Callback callback, String teacherRequestID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherRequestID));
        FirebaseServer.GetTeacherRequests getTeacherRequests = new FirebaseServer.GetTeacherRequests();
        getTeacherRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить все непросмотренные запросы учителя
     */

    public static void getTeacherNewRequests(Callback callback, String teacherRequestID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherRequestID));
        FirebaseServer.GetTeacherNewRequests getTeacherNewRequests = new FirebaseServer.GetTeacherNewRequests();
        getTeacherNewRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить все принятые запросы учителя
     */

    public static void getTeacherPositiveRequests(Callback callback, String teacherRequestID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherRequestID));
        FirebaseServer.GetTeacherPositiveRequests getTeacherPositiveRequests = new FirebaseServer.GetTeacherPositiveRequests();
        getTeacherPositiveRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить все отклоненные запросы учителя
     */

    public static void getTeacherNegativeRequests(Callback callback, String teacherRequestID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherRequestID));
        FirebaseServer.GetTeacherNegativeRequests getTeacherNegativeRequests = new FirebaseServer.GetTeacherNegativeRequests();
        getTeacherNegativeRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить все штрафы учителя
     */

    public static void getTeacherPenalties(Callback callback, String teacherRequestID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherRequestID));
        FirebaseServer.GetTeacherPenalties getTeacherPenalties = new FirebaseServer.GetTeacherPenalties();
        getTeacherPenalties.execute(asyncTaskArguments);
    }

    /**
     * Добавить очки студенту
     * @param callback          Callback который вернётся полсе начисления очков
     * @param studentID         id ученика
     */

    public static void addPointsToStudent(Callback callback, String studentID, int requestScore, String requestID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(studentID, requestScore, requestID));
        FirebaseServer.AddScoreToStudentMoreParameters addScoreToStudentMoreParameters = new FirebaseServer.AddScoreToStudentMoreParameters();
        addScoreToStudentMoreParameters.execute(asyncTaskArguments);
    }

    /**
     * Выгрузка всех учителей
     */

    public static void loadAllTeachersClasses(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.LoadAllTeacherClasses loadAllTeacherClasses = new FirebaseServer.LoadAllTeacherClasses();
        loadAllTeacherClasses.execute(asyncTaskArguments);
    }

    /**
     * Понизить очки ученика
     * @param callback
     * @param requestedScoreValue       очки
     * @param studentId                 id ученика
     */

    public static void decreaseStudentLimitScore(Callback callback, int requestedScoreValue, String studentId){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(null, new AsyncTaskDataArgument(requestedScoreValue, studentId));
        FirebaseServer.DecreaseStudentLimitScore decreaseStudentLimitScore = new FirebaseServer.DecreaseStudentLimitScore();
        decreaseStudentLimitScore.execute(asyncTaskArguments);
    }

    /**
     * Оштрафовать ученика
     * @param studentID         id ученика
     * @param scoreToDecrease   очки на которые учитель штрафует учинка
     */

    public static void decreaseStudentScore(String studentID, int scoreToDecrease){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(null, new AsyncTaskDataArgument(studentID, scoreToDecrease));
        FirebaseServer.DecreaseStudentScore decreaseStudentScore = new FirebaseServer.DecreaseStudentScore();
        decreaseStudentScore.execute(asyncTaskArguments);
    }

    /**
     * Получить запросы учителя на регистрацию ученика в системе
     * @param callback      callback
     * @param teacherID     id учителя
     */

    public static void getRegistrationRequests(Callback callback, String teacherID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherID));
        FirebaseServer.GetRegistrationRequests getRegistrationRequests = new FirebaseServer.GetRegistrationRequests();
        getRegistrationRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить принятые запросы учителя на регистрацию ученика в системе
     * @param callback      callback
     * @param teacherID     id учителя
     */

    public static void getConfirmedRegistrationRequests(Callback callback, String teacherID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherID));
        FirebaseServer.GetConfirmedRegistrationRequests getConfirmedRegistrationRequests = new FirebaseServer.GetConfirmedRegistrationRequests();
        getConfirmedRegistrationRequests.execute(asyncTaskArguments);
    }

    /**
     * Получить отклоненные запросы учителя на регистрацию ученика в системе
     * @param callback      callback
     * @param teacherID     id учителя
     */

    public static void getDeniedRegistrationRequests(Callback callback, String teacherID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(teacherID));
        FirebaseServer.GetDeniedRegistrationRequests getDeniedRegistrationRequests = new FirebaseServer.GetDeniedRegistrationRequests();
        getDeniedRegistrationRequests.execute(asyncTaskArguments);
    }
}
