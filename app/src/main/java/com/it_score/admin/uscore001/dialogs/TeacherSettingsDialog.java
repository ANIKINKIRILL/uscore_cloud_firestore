package com.it_score.admin.uscore001.dialogs;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.it_score.admin.uscore001.App;
import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Subject;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.SubjectArrayAdapter;

import java.util.ArrayList;

/**
 * Окно, где учитель может изменить свои данные (ФИО, ПРЕДМЕТ, ПОЗИЦИЯ)
 */

public class TeacherSettingsDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "TeacherSettingsDialog";

    // Виджеты
    private EditText firstName, secondName, lastName, email, roomNumber;
    private Spinner subjectSpinner;
    private TextView subjectNow;
    private ProgressDialog progressDialog;

    // Переменные
    private String teacherID;
    private String teacherLastName;
    private String teacherSecondName;
    private String teacherFirstName;
    private String subjectID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_settings_dialog, container, false);
        init(view);
        configureDialog();
        getTeacherData();
        getSubjectNameBySubjectID(subjectID);
        setTeacherData();
        populateSubjectSpinner();
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
        subjectNow = view.findViewById(R.id.subjectNow);
        subjectSpinner = view.findViewById(R.id.subjectSpinner);
        email = view.findViewById(R.id.emailAddress);
        roomNumber = view.findViewById(R.id.roomNumber);
        android.widget.TextView ok = view.findViewById(R.id.ok);
        android.widget.TextView cancel = view.findViewById(R.id.cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /**
     * Данные учителя
     */

    private void getTeacherData(){
        // Оборачиваем в try catch блок для того чтобы не было ошибки когда Context = null
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
            teacherFirstName = sharedPreferences.getString(Teacher.FIRST_NAME, "");
            teacherSecondName = sharedPreferences.getString(Teacher.SECOND_NAME, "");
            teacherLastName = sharedPreferences.getString(Teacher.LAST_NAME, "");
            teacherID = sharedPreferences.getString(Teacher.TEACHER_ID, "");
            subjectID = sharedPreferences.getString(Teacher.SUBJECT_ID, "");
        }catch (Exception e){
            Log.d(TAG, "getTeacherData: " + e.getMessage());
        }
    }

    /**
     * Устанавливаем данные учителя в виджеты
     */

    private void setTeacherData(){
        firstName.setText(teacherFirstName);
        secondName.setText(teacherSecondName);
        lastName.setText(teacherLastName);
    }

    /**
     * Получить предмет учителя по id предмета
     * @param subjectID     id предмета учителя
     */

    private void getSubjectNameBySubjectID(String subjectID){
        Teacher.getSubjectValueByID(mGetSubjectByIdCallback, subjectID);
    }

    private Callback mGetSubjectByIdCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String subjectName = (String) data;
            subjectNow.setText(subjectName);
        }
    };

    /**
     * Наполнить спиннер с выбором предмета
     */

    private void populateSubjectSpinner(){
        User.getAllSubjectsList(mGetAllSubjectsListCallback);
    }

    private Callback mGetAllSubjectsListCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Subject> subjects = (ArrayList) data;
            SubjectArrayAdapter adapter = new SubjectArrayAdapter(getContext(), subjects);
            subjectSpinner.setAdapter(adapter);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:{
                Log.d(TAG, "onClick: called");
                String new_first_name = firstName.getText().toString();
                String new_second_name = secondName.getText().toString();
                String new_last_name = lastName.getText().toString();
                String realEmail = email.getText().toString();
                String roomNumberValue = roomNumber.getText().toString();
                Log.d(TAG, "teachersSettingsDialog: realEmail:" + realEmail + " roomNumber:" + roomNumberValue);
                // Получить выбраный предмет
                Subject subject = (Subject) subjectSpinner.getSelectedItem();
                String subjectID = subject.getId();
                progressDialog = new ProgressDialog(getContext());
                progressDialog.setTitle("Обновление");
                progressDialog.setMessage("Мы изменяем Ваш профиль...");
                progressDialog.show();
                Teacher.updateCredentials(mUpdateCredentialsCallback, teacherID, new_first_name, new_second_name, new_last_name, subjectID, realEmail, roomNumberValue);
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
