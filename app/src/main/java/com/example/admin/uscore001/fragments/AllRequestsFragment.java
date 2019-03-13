package com.example.admin.uscore001.fragments;

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

import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.Settings;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.util.RecentRequestsAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Фрагмент со всеми запросами
 */

public class AllRequestsFragment extends Fragment {

    private static final String TAG = "AllRequestsFragment";

    // Виджеты
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_requests_fragment, container, false);
        init(view);
        if(Settings.getStatus().equals(STUDENT_STATUS)){
            getStudentRequests();
        }

        if(Settings.getStatus().equals(TEACHER_STATUS)){

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
     * Получить запросы ученика
     */

    private void getStudentRequests(){
        Student.getStudentRequests(mGetStudentRequests, Settings.getUserId());
    }

    /**
     * Callback, который вернется после получения запросов
     */

    Callback mGetStudentRequests = new Callback() {
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

