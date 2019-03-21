package com.example.admin.uscore001.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.Settings;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.RecentRequestsAdapter;

import java.util.ArrayList;

/**
 * Фрагмент с непросмотренными заявками
 */

public class NewRequestsFragment extends Fragment {

    private static final String TAG = "NewRequestsFragment";

    // Виджеты
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";

    // Переменные
    private int newRequestsAmount = 0;
    private int teacherWentTrough = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.positive_requests_fragment, container, false);
        init(view);

        if(Settings.getStatus().equals(STUDENT_STATUS)){
            getStudentNewRequests();
        }

        if(Settings.getStatus().equals(TEACHER_STATUS)){
            getTeacherNewRequests();
        }

        return view;
    }

    /**
     * Инициализация виджетов
     * @param view  на чем находятся виджеты
     */

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
    }

    /**
     * Получить непросмотренные запросы ученика
     */

    private void getStudentNewRequests(){
        newRequestsAmount = 0;
        teacherWentTrough = 0;
        Student.getStudentNewRequests(mGetStudentNewRequests, Settings.getUserId());
    }
    /**
     * Callback, который вернется после получения непросмотренных запросов
     */

    Callback mGetStudentNewRequests = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<RequestAddingScore> requests = (ArrayList) data;
            newRequestsAmount += requests.size();
            teacherWentTrough++;
            RecentRequestsAdapter adapter = new RecentRequestsAdapter(requests);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            if(teacherWentTrough == 3 && newRequestsAmount == 0){
                /*
                        Перенаправляем пользвателя на фрагмент что у него нет новых заросов
                 */
                Toast.makeText(getContext(), "У тебя нет новых запросов", Toast.LENGTH_SHORT).show();

            }
        }
    };

    /**
     * Получить непросмотренные запросы учителя
     */

    private void getTeacherNewRequests(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
        String teacherRequestID = sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "");
        Teacher.getTeacherNewRequests(mGetTeacherNewRequests, teacherRequestID);
    }
    /**
     * Callback, который вернется после получения непросмотренных запросов
     */

    Callback mGetTeacherNewRequests = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<RequestAddingScore> requests = (ArrayList) data;
            RecentRequestsAdapter adapter = new RecentRequestsAdapter(requests);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
        }
    };

}

