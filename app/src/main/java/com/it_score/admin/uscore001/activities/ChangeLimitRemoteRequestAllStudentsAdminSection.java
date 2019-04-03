package com.it_score.admin.uscore001.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.it_score.admin.uscore001.R;

/**
 * Активити с изменением лимита на удаленных запросов у всех студентов (Пользователь -> Админ)
 */

public class ChangeLimitRemoteRequestAllStudentsAdminSection extends AppCompatActivity implements View.OnClickListener{

    // Виджеты
    private EditText limit;
    private Button releaseButton;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference STUDENTS$DB = firebaseFirestore.collection("STUDENTS$DB");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_limit_remote_request_all_students_admin_section_activity);
        init();
        initActionBar();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        limit = findViewById(R.id.limit);
        releaseButton = findViewById(R.id.releaseButton);

        releaseButton.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Изменить лимит на удал.запросы");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.releaseButton:{
                STUDENTS$DB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot student : task.getResult().getDocuments()){
                                student.getReference().update("limitScore", "3");
                            }
                        }
                    }
                });
                break;
            }
        }
    }
}
