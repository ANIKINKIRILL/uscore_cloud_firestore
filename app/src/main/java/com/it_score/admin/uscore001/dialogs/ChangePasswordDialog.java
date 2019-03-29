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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firestore.v1.UpdateDocumentRequest;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.Settings;
import com.it_score.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Set;

/**
 * Диалоговое окно со сменой пароля
 */

public class ChangePasswordDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "ChangePasswordDialog";

    // Виджеты
    private Button commitButton, laterButton;
    private EditText new_pass_edit_text, repeat_pass_edit_text;
    private RelativeLayout dialogLayout;

    // Firebase
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference STUDENTS$DB = firebaseFirestore.collection("STUDENTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_password_dialog_layout, container, false);
        init(view);
        configureDialog();
        if(didStudentChangePasswordValue()){
            laterButton.setText("ОТМЕНА");
        }
        initCurrentUser();
        return view;
    }

    /**
     * Инициализация виджетов
     * @param view      view диалогового окна
     */

    private void init(View view){
        commitButton = view.findViewById(R.id.change_pass_button);
        laterButton = view.findViewById(R.id.later_button);
        new_pass_edit_text = view.findViewById(R.id.new_password);
        repeat_pass_edit_text = view.findViewById(R.id.new_password_repeat);
        dialogLayout = view.findViewById(R.id.dialogLayout);
        commitButton.setOnClickListener(this);
        laterButton.setOnClickListener(this);
    }

    /**
     * Настройка дилогового окна
     */

    private void configureDialog(){
        getDialog().setTitle("Смена пароля");
    }

    /**
     * Менял ли пользователь пароль
     */

    private boolean didStudentChangePasswordValue(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(Student.CHANGE_PASSWORD, false)){
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_pass_button:{
                // Пользователь = Ученик
                String new_pass_value = new_pass_edit_text.getText().toString();
                String repeat_pass_value = repeat_pass_edit_text.getText().toString();
                if (new_pass_value.trim().equals(repeat_pass_value.trim()) && !new_pass_value.trim().isEmpty() && !repeat_pass_value.trim().isEmpty()) {
                    currentUser.updatePassword(new_pass_value.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            try {
                                if (task.isSuccessful()) {
                                    if(Settings.getStatus().equals(getString(R.string.studentStatusValue))) {
                                        YoYo.with(Techniques.RollOut).duration(1000).repeat(0).playOn(dialogLayout);
                                        Log.d(TAG, "onComplete: change password successfully");
                                        Toast.makeText(getContext(), "Вы успешно поменяли пароль", Toast.LENGTH_SHORT).show();
                                        getDialog().cancel();
                                        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
                                        STUDENTS$DB.document(sharedPreferences.getString(Student.ID, "")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                task.getResult().getReference().update("change_password", true);
                                            }
                                        });
                                    }
                                    if(Settings.getStatus().equals(getString(R.string.teacherStatusValue))){
                                        YoYo.with(Techniques.RollOut).duration(1000).repeat(0).playOn(dialogLayout);
                                        Log.d(TAG, "onComplete: change password successfully");
                                        Toast.makeText(getContext(), "Вы успешно поменяли пароль", Toast.LENGTH_SHORT).show();
                                        getDialog().cancel();
                                    }
                                } else {
                                    Log.d(TAG, "onComplete: change password failed die to : " + task.getException().getMessage());
                                    Toast.makeText(getContext(), "Неправильный пароль, попробуйте еще раз. Минимальное количество символов: 6, перезайдите в свой профиль и попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                    new_pass_edit_text.setText("");
                                    repeat_pass_edit_text.setText("");
                                    YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(dialogLayout);
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Пароли не совпадают, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                    new_pass_edit_text.setText("");
                    repeat_pass_edit_text.setText("");
                    YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(dialogLayout);
                }
                break;
            }
            case R.id.later_button:{
                getDialog().cancel();
                break;
            }
        }
    }

    /**
     * Инициализация пользователя
     */

    private void initCurrentUser(){
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

}
