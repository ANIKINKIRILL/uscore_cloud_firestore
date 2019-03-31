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
import android.widget.TextView;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.util.StudentRecyclerAdapter;

import java.util.ArrayList;

/**
 * Фрагмент с рейтингом по всей школе
 */

public class EntireSchoolTopScoreFragment extends Fragment {

    private static final String TAG = "EntireSchoolTopScoreFra";

    // Переменные
    public static StudentRecyclerAdapter adapter;

    // Виджеты
    RecyclerView recyclerView;
    TextView currentStudentRate;
    ProgressDialog progressDialog;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";
    public static final String ADMIN_STATUS = "26gmBm7N0oUVupLktAg6";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragement_entireschool_topscore, container, false);
        init(view);

        if(Settings.getStatus().equals(STUDENT_STATUS)){
            getSchoolRatingByStudent();
        }

        if(Settings.getStatus().equals(TEACHER_STATUS) || Settings.getStatus().equals(ADMIN_STATUS)){
            currentStudentRate.setVisibility(View.GONE);
            getSchoolRatingByTeacher();
        }
        return view;
    }

    /**
     * Инициализация виджетов
     * @param view              на чем находяться виджеты
     */

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        currentStudentRate = view.findViewById(R.id.currentStudentRate);
    }

    /**
     * Получить рейтинг учеников школы от Ученика
     */

    private void getSchoolRatingByStudent(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Загрузка");
        progressDialog.setMessage("Загрузка рейтинга школы...");
        progressDialog.show();
        Student.loadAllStudents(mGetSchoolRatingByStudent, Settings.getUserId());
    }

    /**
     * Callback, который вернётся после асинхронного получия рейтинга с Сервера для Ученика
     */

    Callback mGetSchoolRatingByStudent = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            try {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
                int studentScore = sharedPreferences.getInt(Student.SCORE, 0);
                ArrayList<Student> ratedStudentsList = (ArrayList) data;
                String currentStudentSchoolRate = params[0];
                adapter = new StudentRecyclerAdapter(ratedStudentsList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);
                currentStudentRate.setText(String.format("Ты на %s месте с %s очками", currentStudentSchoolRate, Integer.toString(studentScore)));
                progressDialog.dismiss();
            }catch (Exception e){
                Log.d(TAG, "execute: " + e.getMessage());
            }
        }
    };

    /**
     * Получить рейтинг учеников школы от Учителя
     */

    private void getSchoolRatingByTeacher(){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Загрузка");
        progressDialog.setMessage("Загрузка рейтинга школы...");
        progressDialog.show();
        Student.loadAllStudents(mGetSchoolRatingByTeacher, Settings.getUserId());
    }

    /**
     * Callback, который вернётся после асинхронного получия рейтинга с Сервера для Учителя
     */

    Callback mGetSchoolRatingByTeacher = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Student> ratedStudentsList = (ArrayList) data;
            adapter = new StudentRecyclerAdapter(ratedStudentsList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            progressDialog.dismiss();
        }
    };


}
