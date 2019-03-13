package com.example.admin.uscore001.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.Settings;
import com.example.admin.uscore001.models.RecentRequestItem;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.RecentRequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Фрагмент с принятыми запросами
 */

public class PositiveRequestsFragment extends Fragment {

    private static final String TAG = "PositiveRequestsFragm";

    // Виджеты
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";

    // Переменные
    private int positiveRequestsAmount = 0;
    private int teacherWentTrough = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.positive_requests_fragment, container, false);
        init(view);

        if(Settings.getStatus().equals(STUDENT_STATUS)){
            getStudentPositiveRequests();
        }

        if(Settings.getStatus().equals(TEACHER_STATUS)){
            getTeacherPositiveRequests();
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
     * Получить приняты запросы ученика
     */

    private void getStudentPositiveRequests(){
        positiveRequestsAmount = 0;
        teacherWentTrough = 0;
        Student.getConfirmedRequests(mGetStudentPositiveRequests, Settings.getUserId());
    }

    /**
     * Callback, который вернется после получения принятых запросов
     */

    Callback mGetStudentPositiveRequests = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<RequestAddingScore> requests = (ArrayList) data;
            positiveRequestsAmount += requests.size();
            teacherWentTrough++;
            RecentRequestsAdapter adapter = new RecentRequestsAdapter(requests);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            if(teacherWentTrough == 3 && positiveRequestsAmount == 0){
                /*
                        Перенаправляем пользвателя на фрагмент что у него нет принятых заросов
                 */

                Toast.makeText(getContext(), "У тебя нет принятых запросов", Toast.LENGTH_SHORT).show();

            }
        }
    };

    /**
     * Получить приняты запросы учителя
     */

    private void getTeacherPositiveRequests(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
        String teacherRequestID = sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "");
        Teacher.getTeacherPositiveRequests(mGetTeacherPositiveRequests, teacherRequestID);
    }

    /**
     * Callback, который вернется после получения принятых запросов
     */

    Callback mGetTeacherPositiveRequests = new Callback() {
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
