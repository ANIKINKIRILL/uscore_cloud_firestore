package com.it_score.admin.uscore001.fragments;

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

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.StudentRegisterRequestModel;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.util.StudentRegisterRequestRecyclerViewAdapter;

import java.util.ArrayList;


/**
 * Фрагмент со принятыми запросами на регистрацию
 */

public class ConfirmedRegisterRequests extends Fragment {

    // Виджеты
    RecyclerView recyclerView;

    // Переменные
    String teacherID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_register_requests, container, false);
        getTeacherData();
        init(view);
        getRegistrationRequests();
        return view;
    }

    /**
     * Инициализация виджетов
     */

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    /**
     * Получение данных учителя
     */

    private void getTeacherData(){
        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
            teacherID = sharedPreferences.getString(Teacher.TEACHER_ID, "");
        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * Получить принятые запросы учителя на регистрацию ученика
     */

    private void getRegistrationRequests(){
        Teacher.getConfirmedRegistrationRequests(mGetRegistrationRequestsCallback, teacherID);
    }

    private Callback mGetRegistrationRequestsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<StudentRegisterRequestModel> requests = (ArrayList) data;
            StudentRegisterRequestRecyclerViewAdapter adapter = new StudentRegisterRequestRecyclerViewAdapter(requests, getContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    };
}
