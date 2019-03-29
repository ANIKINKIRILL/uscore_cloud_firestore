package com.it_score.admin.uscore001.dialogs;

import android.content.Context;
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
import android.widget.Toast;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Teacher;

/**
 * Окно, где учитель может изменить свои данные (ФИО, ПРЕДМЕТ, ПОЗИЦИЯ)
 */

public class TeacherSettingsDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "TeacherSettingsDialog";

    // Виджеты
    private EditText position, subject, firstName, secondName, lastName;

    // Переменные
    private String teacherID;
    private String teacherLastName;
    private String teacherSecondName;
    private String teacherFirstName;
//    private String subjectData;
//    private String positionData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_settings_dialog, container, false);
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
//        position = view.findViewById(R.id.position);
//        subject = view.findViewById(R.id.subject);
        firstName = view.findViewById(R.id.firstName);
        secondName = view.findViewById(R.id.secondName);
        lastName = view.findViewById(R.id.lastName);
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
//            subjectData = sharedPreferences.getString(Teacher.SUBJECT_DATA, "");
//            positionData = sharedPreferences.getString(Teacher.POSITION_DATA, "");
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
//        position.setText(positionData);
//        subject.setText(subjectData);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:{
                String new_first_name = firstName.getText().toString();
                String new_second_name = secondName.getText().toString();
                String new_last_name = lastName.getText().toString();
                Teacher.updateCredentials(mUpdateCredentialsCallback, teacherID, new_first_name, new_second_name, new_last_name);
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
     * Callback, который вернёться после обновления ФИО учителя
     */

    Callback mUpdateCredentialsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String resultMessage = (String) data;
            Toast.makeText(getContext(), resultMessage, Toast.LENGTH_SHORT).show();
            getDialog().dismiss();
        }
    };

}
