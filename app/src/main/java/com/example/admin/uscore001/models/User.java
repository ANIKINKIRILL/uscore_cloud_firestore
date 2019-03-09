package com.example.admin.uscore001.models;

import android.support.annotation.NonNull;

import com.example.admin.uscore001.AsyncTaskArguments;
import com.example.admin.uscore001.AsyncTaskDataArgument;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.FirebaseServer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

}
