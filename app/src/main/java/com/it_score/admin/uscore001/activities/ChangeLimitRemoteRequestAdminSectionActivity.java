package com.it_score.admin.uscore001.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.it_score.admin.uscore001.R;

/**
 * Актитвити для изменения лимита удаленных запросов (Пользователь -> Админ)
 */

public class ChangeLimitRemoteRequestAdminSectionActivity extends AppCompatActivity implements View.OnClickListener{

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference LIMIT_REMOTE_REQUEST = firebaseFirestore.collection("LIMIT_REMOTE_REQUEST");

    // Виджеты
    private EditText limitEditText;
    private Button releaseButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_limit_remote_request_admin_section_activity);
        init();
        initActionBar();
    }

    /**
     * Инициализация виджетов
     */

    private void init(){
        limitEditText = findViewById(R.id.limit);
        releaseButton = findViewById(R.id.releaseButton);

        releaseButton.setOnClickListener(this);
    }

    /**
     * Настройка ActionBar
     */

    private void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Лимит удаленных запросов");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.releaseButton:{
                String new_limit = limitEditText.getText().toString();
                LIMIT_REMOTE_REQUEST.document(getString(R.string.limit_remote_request_doc)).update("limit", new_limit).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ChangeLimitRemoteRequestAdminSectionActivity.this, "Лимит обновлен успешно", Toast.LENGTH_SHORT).show();
                            limitEditText.setText("");
                        }else{
                            Toast.makeText(ChangeLimitRemoteRequestAdminSectionActivity.this, "ERROR die to :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
