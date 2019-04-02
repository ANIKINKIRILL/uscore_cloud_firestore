package com.it_score.admin.uscore001.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.util.StudentRecyclerAdapter;

import java.util.ArrayList;

/**
 * Фрагмент с рейтингом учеников группы
 */

public class MyGroupTopScoreFragment extends Fragment {

    private static final String TAG = "MyGroupTopScoreFragment";

    // Переменные
    public static StudentRecyclerAdapter adapter;
    private String teacherGroupID;

    // Виджеты
    TextView title;
    RecyclerView recyclerView;
    TextView currentStudentRate;
    ProgressDialog progressDialog;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_mygroup_topscore, container, false);
        init(view);
        setTitle();
        if(getUserStatus().equals(TEACHER_STATUS)){        // Пользователь = Учитель
            if(doesTeacherHasGroup()){                    // У учителя есть класс
                currentStudentRate.setVisibility(View.GONE);
                getGroupRatingByTeacher();
            }
        }

        if(getUserStatus().equals(STUDENT_STATUS)){        // Пользователь = Ученик
            getGroupRatingByStudent();
        }

        return view;
    }

    /**
     * Инициализация виджетов
     */

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        title = view.findViewById(R.id.title);
        currentStudentRate = view.findViewById(R.id.currentStudentRate);
    }

    /**
     * Есть ли у учителя класс
     * @return  Если есть класс -> true      Если нет класса -> false
     */

    private boolean doesTeacherHasGroup(){
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
            teacherGroupID = sharedPreferences.getString(Teacher.GROUP_ID, "");
        }catch (Exception e){
            Log.d(TAG, "doesTeacherHasGroup: " + e.getMessage());
        }
        if (teacherGroupID.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Получить статус пользователя
     * @return      статус пользователя
     */

    private String getUserStatus(){
        return Settings.getStatus();
    }

    /**
     * Получить рейтинг учеников группы от Ученика
     */

    private void getGroupRatingByStudent(){
        try {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Загрузка рейтинга твоей группы...");
            progressDialog.show();
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
            String groupID = sharedPreferences.getString(Student.GROUP_ID, "");
            Student.loadGroupStudentsByGroupID(groupID, Settings.getUserId(), mGetGroupRatingCallbackForStudent);
        }catch (Exception e){
            Log.d(TAG, "getGroupRatingByStudent: " + e.getMessage());
        }
    }

    /**
     * Получить рейтинг учеников группы от Учителя
     */

    private void getGroupRatingByTeacher(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Загрузка рейтинга вашей группы...");
        progressDialog.show();
        Student.loadGroupStudentsByGroupID(teacherGroupID, Settings.getUserId(), mGetGroupRatingCallbackForTeacher);
    }

    /**
     * Callback, который вернётся после асинхронного получия данных с Сервера для Ученика
     */

    Callback mGetGroupRatingCallbackForStudent = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            /*
                    Оборачиваем в try catch блок для того чтобы не было краша приложения когда context = null
             */
            try {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
                int studentScore = sharedPreferences.getInt(Student.SCORE, 0);
                ArrayList<Student> ratedStudentsList = (ArrayList) data;
                String currentStudentRateInGroup = params[0];
                adapter = new StudentRecyclerAdapter(ratedStudentsList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                currentStudentRate.setText(String.format("Ты на %s месте с %s очками", currentStudentRateInGroup, Integer.toString(studentScore)));
                progressDialog.dismiss();
            }catch (Exception e){
                Log.d(TAG, "execute: " + e.getMessage());
            }
        }
    };

    /**
     * Callback, который вернётся после асинхронного получия данных с Сервера для Учителя
     */

    Callback mGetGroupRatingCallbackForTeacher = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Student> ratedStudentsList = (ArrayList) data;
            adapter = new StudentRecyclerAdapter(ratedStudentsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
        }
    };

    /**
     * Установка названия фрагмента
     */

    private void setTitle(){
        try {
            title.setText("Рейтинг учеников по Вашей группе");
        }catch (Exception e){
            Log.d(TAG, "setTitle: " + e.getMessage());
        }
    }

}
