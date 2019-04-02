package com.it_score.admin.uscore001.dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Admin;
import com.it_score.admin.uscore001.models.Subject;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.SubjectArrayAdapter;

import java.util.ArrayList;

/**
 * Окно, где admin может изменить свои данные (ФИО, КАБИНЕТ, ПОЧТА)
 */

public class AdminSettingsDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "TeacherSettingsDialog";

    // Виджеты
    private EditText firstName, secondName, lastName, email, roomNumber;
    private ProgressDialog progressDialog;

    // Переменные
    private String adminID;
    private String adminLastName;
    private String adminSecondName;
    private String adminFirstName;
    private int adminRoomNumber;
    private String adminRealEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_settings_dialog, container, false);
        init(view);
        configureDialog();
        getTeacherData();
        setTeacherData();
        return view;
    }

    /**
     * Настройка диалогового окна
     */

    private void configureDialog(){
        getDialog().setTitle("Изменить свой профиль");
    }

    /**
     * Инициализация
     * @param view      на чём находяться виджеты
     */

    private void init(View view){
        firstName = view.findViewById(R.id.firstName);
        secondName = view.findViewById(R.id.secondName);
        lastName = view.findViewById(R.id.lastName);
        email = view.findViewById(R.id.emailAddress);
        roomNumber = view.findViewById(R.id.roomNumber);
        TextView ok = view.findViewById(R.id.ok);
        TextView cancel = view.findViewById(R.id.cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /**
     * Данные учителя
     */

    private void getTeacherData(){
        // Оборачиваем в try catch блок для того чтобы не было ошибки когда Context = null
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Admin.ADMIN_DATA, Context.MODE_PRIVATE);
            adminFirstName = sharedPreferences.getString(Admin.ADMIN_FIRST_NAME, "");
            adminSecondName = sharedPreferences.getString(Admin.ADMIN_SECOND_NAME, "");
            adminLastName = sharedPreferences.getString(Admin.ADMIN_LAST_NAME, "");
            adminRoomNumber = sharedPreferences.getInt(Admin.ADMIN_ROOM_NUMBER, 0);
            adminID = sharedPreferences.getString(Admin.ADMIN_ID, "");
            adminRealEmail = sharedPreferences.getString(Admin.ADMIN_REAL_EMAIL, "");
        }catch (Exception e){
            Log.d(TAG, "getTeacherData: " + e.getMessage());
        }
    }

    /**
     * Устанавливаем данные учителя в виджеты
     */

    private void setTeacherData(){
        firstName.setText(adminFirstName);
        secondName.setText(adminSecondName);
        lastName.setText(adminLastName);
        email.setText(adminRealEmail);
        roomNumber.setText(Integer.toString(adminRoomNumber));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:{
                Log.d(TAG, "onClick: called");
                String new_first_name = firstName.getText().toString();
                String new_second_name = secondName.getText().toString();
                String new_last_name = lastName.getText().toString();
                String realEmail = email.getText().toString();
                int roomNumberValue = Integer.parseInt(roomNumber.getText().toString());
                Log.d(TAG, "teachersSettingsDialog: realEmail:" + realEmail + " roomNumber:" + roomNumberValue);
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Обновление");
                progressDialog.setMessage("Мы изменяем Ваш профиль...");
                progressDialog.show();
                Admin.updateCredentials(mUpdateCredentialsCallback, adminID, new_first_name, new_second_name, new_last_name, realEmail, roomNumberValue);
                break;
            }
            case R.id.cancel:{
                getDialog().dismiss();
                Toast.makeText(getContext(), "Изменения отменены", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    /**
     * Callback, который вернёться после обновления данных учителя
     */

    Callback mUpdateCredentialsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            progressDialog.dismiss();
            getDialog().dismiss();
            Toast.makeText(getContext(), "При след. запуске приложения данные обновяться", Toast.LENGTH_SHORT).show();
        }
    };
}
