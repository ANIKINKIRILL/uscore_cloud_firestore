package com.it_score.admin.uscore001.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.it_score.admin.uscore001.App;
import com.it_score.admin.uscore001.AsyncTaskArguments;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.FirebaseServer;

/**
 * Администратор
 */

public class Admin {
    private String id;
    private String firstName;
    private String secondName;
    private String lastName;
    private String positionID;
    private String responsible_email;
    private String image_path;
    private String statusID;
    private int roomNumber;

    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    public static final String ADMIN_DATA = "admin_data";
    public static final String ADMIN_ID = "id";
    public static final String ADMIN_FIRST_NAME = "first_name";
    public static final String ADMIN_SECOND_NAME = "second_name";
    public static final String ADMIN_LAST_NAME = "last_name";
    public static final String ADMIN_EMAIL = "responsible_email";
    public static final String ADMIN_IMAGE_PATH = "image_path";
    public static final String ADMIN_POSITION_ID = "position_id";
    public static final String ADMIN_STATUS_ID = "status_id";
    public static final String ADMIN_ROOM_NUMBER = "room_number";

    public Admin(){}

    public Admin(
            String id,
            String firstName,
            String secondName,
            String lastName,
            String positionID,
            String responsible_email,
            String image_path,
            String statusID,
            int roomNumber
    ){
        this.id = id;
        this.firstName = firstName;
        this.secondName = secondName;
        this.lastName = lastName;
        this.positionID = positionID;
        this.responsible_email = responsible_email;
        this.image_path = image_path;
        this.statusID = statusID;
        this.roomNumber = roomNumber;
    }

    static {
        Context context = App.context;
        sharedPreferences = context.getSharedPreferences(ADMIN_DATA, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public static void setAdminRoomNumber(int roomNumber){
        editor.putInt(ADMIN_ROOM_NUMBER, roomNumber);
        editor.apply();
    }

    public static int getAdminRoomNumber(){
        return sharedPreferences.getInt(ADMIN_ROOM_NUMBER, 0);
    }

    public static void setAdminIdSharedPreference(String id){
        editor.putString(ADMIN_ID, id);
        editor.apply();
    }


    public static String getIdSharedPreference() {
        return sharedPreferences.getString(ADMIN_ID, "");
    }

    public static String getFirstNameSharedPreference() {
        return sharedPreferences.getString(ADMIN_FIRST_NAME, "");
    }

    public static void setFirstNameSharedPreference(String firstName) {
        editor.putString(ADMIN_FIRST_NAME, firstName);
        editor.apply();
    }

    public static String getSecondNameSharedPreference() {
        return sharedPreferences.getString(ADMIN_SECOND_NAME, "");
    }

    public static void setSecondNameSharedPreference(String secondName) {
        editor.putString(ADMIN_SECOND_NAME, secondName);
        editor.apply();
    }

    public static String getLastNameSharedPreference() {
        return sharedPreferences.getString(ADMIN_LAST_NAME, "");
    }

    public static void setLastNameSharedPreference(String lastName) {
        editor.putString(ADMIN_LAST_NAME, lastName);
        editor.apply();
    }

    public static String getPositionIDSharedPreference() {
        return sharedPreferences.getString(ADMIN_POSITION_ID, "");
    }

    public static void setPositionIDSharedPreference(String positionID) {
        editor.putString(ADMIN_POSITION_ID, positionID);
        editor.apply();
    }

    public static String getResponsible_emailSharedPreference() {
        return sharedPreferences.getString(ADMIN_EMAIL, "");
    }

    public static void setResponsible_emailSharedPreference(String responsible_email) {
        editor.putString(ADMIN_EMAIL, responsible_email);
        editor.apply();
    }

    public static String getImage_pathSharedPreference() {
        return sharedPreferences.getString(ADMIN_IMAGE_PATH, "");
    }

    public static void setImage_pathSharedPreference(String image_path) {
        editor.putString(ADMIN_IMAGE_PATH, image_path);
        editor.apply();
    }

    public static String getStatusIDSharedPreference() {
        return sharedPreferences.getString(ADMIN_STATUS_ID, "");
    }

    public static void setStatusIDSharedPreference(String statusID) {
        editor.putString(ADMIN_STATUS_ID, statusID);
        editor.apply();
    }

    public String getStatusID() {
        return statusID;
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

    public String getPositionID() {
        return positionID;
    }

    public void setPositionID(String positionID) {
        this.positionID = positionID;
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

    public void setStatusID(String statusID) {
        this.statusID = statusID;
    }

    /**
     * Получить список функций админа
     * @param callback      callback после асиннхроного получения списка функций админа с Сервера
     */

    public static void getAdminFunctionsList(Callback callback){
        AsyncTaskArguments asyncTaskArguments = new AsyncTaskArguments(callback);
        FirebaseServer.GetAdminFunctions getAdminFunctions = new FirebaseServer.GetAdminFunctions();
        getAdminFunctions.execute(asyncTaskArguments);
    }


}
