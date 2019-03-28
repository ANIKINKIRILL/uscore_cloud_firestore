package com.it_score.admin.uscore001.models;

import com.it_score.admin.uscore001.AsyncTaskArguments;
import com.it_score.admin.uscore001.AsyncTaskDataArgument;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.FirebaseServer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Пользователь
 */

public class User {

    // Firebase and Firestore
    static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    static CollectionReference STUDENTS$DB = firebaseFirestore.collection("STUDENTS$DB");
    static FirebaseAuth auth = FirebaseAuth.getInstance();


    private String login;       // логин/почта при авторизации
    private String password;    // пароль
    private String status;      // статус пользователя (Ученик, Учитель)
    public static boolean isAuthenticated = false;    // Авторизован ли пользователь

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Авторизация пользователя
     * @param login             // Логин
     * @param password          // Пароль
     * @param callback          // Callback, вызываемый после авторизации пользователя
     */

    public static void authenticate(String login, String password, Callback callback){
        FirebaseServer.authenticateUser(login, password, callback);
    }

    public static void exit(){
        auth.signOut();
        User.isAuthenticated = false;
    }

    /**
     * Извлечение статуса пользователя
     * @param login     // Логин/почта пользователя
     */

    public static void getUserStatus(String login, Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(login));
        FirebaseServer.GetUserStatus getUserStatus = new FirebaseServer.GetUserStatus();
        getUserStatus.execute(asyncTaskArguments);
    }

    /**
     * Выгрузка всех групп школы
     * @param callback              // Callback, который вызывиться после асинхронного полуения групп с Сервера
     */

    public static void getAllSchoolGroups(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.GetAllSchoolGroups getAllSchoolGroups = new FirebaseServer.GetAllSchoolGroups();
        getAllSchoolGroups.execute(asyncTaskArguments);
    }

    /**
     * Получить название группы пользователя по ID группы
     * @param callback      callback, который вернется после асинхронного получения данных с Сервера
     * @param groupID       id группы
     */

    public static void getUserGroupName(Callback callback, String groupID){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(groupID));
        FirebaseServer.GetUserGroupName getUserGroupName = new FirebaseServer.GetUserGroupName();
        getUserGroupName.execute(asyncTaskArguments);
    }

    /**
     * Получить данные опции
     * @param option_name       название опции
     */

    public static void getOptionData(Callback callback, String option_name){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback, new AsyncTaskDataArgument(option_name));
        FirebaseServer.GetOptionData getOptionData = new FirebaseServer.GetOptionData();
        getOptionData.execute(asyncTaskArguments);
    }

    /**
     * Получить список наказаний
     * @param callback      callback
     */

    public static void getAllPenaltiesList(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.GetAllPenaltiesList getAllPenaltiesList = new FirebaseServer.GetAllPenaltiesList();
        getAllPenaltiesList.execute(asyncTaskArguments);
    }

    /**
     * Получить список должностей
     * @param callback          callback
     */

    public static void getAllPositionsList(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.GetAllPositionsList getAllPositionsList = new FirebaseServer.GetAllPositionsList();
        getAllPositionsList.execute(asyncTaskArguments);
    }

    /**
     * Получить список предметов учителей
     * @param callback          callback
     */

    public static void getAllSubjectsList(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.GetAllSubjectsList getAllSubjectsList = new FirebaseServer.GetAllSubjectsList();
        getAllSubjectsList.execute(asyncTaskArguments);
    }

    /**
     * Получить список поощрений учителей
     * @param callback          callback
     */

    public static void getAllEncouragementsList(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.GetAllEncouragementsList getAllEncouragementsList = new FirebaseServer.GetAllEncouragementsList();
        getAllEncouragementsList.execute(asyncTaskArguments);
    }

}
