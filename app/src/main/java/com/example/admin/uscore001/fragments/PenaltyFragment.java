package com.example.admin.uscore001.fragments;

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
import com.example.admin.uscore001.models.Penalty;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.util.PenaltyRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Фрагмент со штрафами ученика
 */

public class PenaltyFragment extends Fragment {

    // Виджиты
    RecyclerView recyclerView;
    ProgressBar progressBar;

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";

    // Переменные
    private int penaltiesAmount = 0;
    private int teacherWentTrough = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.penalty_fragment, container, false);
        init(view);

        if(Settings.getStatus().equals(STUDENT_STATUS)){
            penaltiesAmount = 0;
            teacherWentTrough = 0;
            Student.getStudentPenalties(mGetStudentPenalties, Settings.getUserId());
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
     * Callback, который вернется после получения штрафов
     */

    Callback mGetStudentPenalties = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Penalty> penalties = (ArrayList) data;
            penaltiesAmount += penalties.size();
            teacherWentTrough++;
            PenaltyRecyclerViewAdapter adapter = new PenaltyRecyclerViewAdapter(penalties, false, true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility(View.GONE);
            if(teacherWentTrough == 3 && penaltiesAmount == 0){
                /*
                        Перенаправляем пользвателя на фрагмент что у него нет штрафов
                 */

                Toast.makeText(getContext(), "У тебя нет штрафов", Toast.LENGTH_SHORT).show();

            }
        }
    };
}
