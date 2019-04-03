package com.it_score.admin.uscore001.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.activities.login_activity;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.StudentRegisterRequestModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Адаптер для регистрации ученика в системе
 */

public class StudentRegisterRequestRecyclerViewAdapter extends RecyclerView.Adapter<StudentRegisterRequestRecyclerViewAdapter.StudentRegisterRequestRecyclerViewViewHolder> {

    private static final String TAG = "StudentRegisterRequestR";

    // Переменные
    ArrayList<StudentRegisterRequestModel> requestModels = new ArrayList<>();
    String teacherEmail;
    int counter = 0;
    private String limitRemoteRequests;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference student$db = firebaseFirestore.collection("STUDENTS$DB");
    CollectionReference student_register_requests$db = firebaseFirestore.collection("STUDENT_REGISTER_REQUESTS");
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    public StudentRegisterRequestRecyclerViewAdapter(ArrayList<StudentRegisterRequestModel> requestModels, Context context) {
        this.requestModels = requestModels;
    }

    static class StudentRegisterRequestRecyclerViewViewHolder extends RecyclerView.ViewHolder{
        TextView fullName, email;
        CardView cardView;
        public StudentRegisterRequestRecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            email = itemView.findViewById(R.id.email);
            cardView = itemView.findViewById(R.id.cardViewLayout);
        }
    }

    @NonNull
    @Override
    public StudentRegisterRequestRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_register_request_recyeler_view_item, viewGroup, false);
        StudentRegisterRequestRecyclerViewViewHolder holder = new StudentRegisterRequestRecyclerViewViewHolder(view);
        getLimitRemoteRequests(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentRegisterRequestRecyclerViewViewHolder studentRegisterRequestRecyclerViewViewHolder, int i) {
        StudentRegisterRequestModel model = requestModels.get(i);
        if(model.isConfirmed() && !model.isDenied()){
            studentRegisterRequestRecyclerViewViewHolder.cardView.setCardBackgroundColor(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().getResources().getColor(R.color.addedColor));
        }else if(model.isDenied() && !model.isConfirmed()){
            studentRegisterRequestRecyclerViewViewHolder.cardView.setCardBackgroundColor(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().getResources().getColor(R.color.canceledColor));
        }else if(!model.isConfirmed() && !model.isDenied()){
            studentRegisterRequestRecyclerViewViewHolder.cardView.setCardBackgroundColor(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().getResources().getColor(R.color.inProcessColor));
        }
        studentRegisterRequestRecyclerViewViewHolder.fullName.setText(model.getFirstName()+" "+model.getSecondName());
        studentRegisterRequestRecyclerViewViewHolder.email.setText(model.getEmail());

        studentRegisterRequestRecyclerViewViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = model.getFirstName();
                String secondName = model.getSecondName();
                String lastName = model.getLastName();
                String email = model.getEmail();
                String message = "Имя: " + firstName + "\n" +
                        "Фамилия: " + secondName + "\n" +
                        "Отчество: " + lastName + "\n" +
                        "Почта: " + email;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext());
                alertDialog.create();
                if(!model.isConfirmed() && !model.isDenied()) {
                    // открываем диалог с информациией о запросе
                    alertDialog.setTitle("Хотите добавить ученика в свой класс?");
                    alertDialog.setMessage(message);
                    alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Регистрация пользователя в системе
                            firebaseAuth.createUserWithEmailAndPassword(email, "qwerty").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        // Создание обьекта класса Student
                                        Student student = new Student(email.trim(), model.getGroupID(),
                                               "", 100, "",
                                               limitRemoteRequests, model.getTeacherID(), firstName.trim(),
                                               secondName.trim(), lastName.trim(),
                                               "y1igExymzKFaV3BU8zH8");
                                        // Добавление в базу
                                        if(counter == 0) {
                                           student$db.add(student).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                               @Override
                                               public void onComplete(@NonNull Task<DocumentReference> task) {
                                                   String documentID = task.getResult().getId();
                                                   HashMap<String, String> idMap = new HashMap<>();
                                                   idMap.put("id", documentID);
                                                   task.getResult().set(idMap, SetOptions.merge());

                                                   HashMap<String, Boolean> change_pass_Map = new HashMap<>();
                                                   change_pass_Map.put("change_password", false);
                                                   task.getResult().set(change_pass_Map, SetOptions.merge());
                                               }
                                           });
                                           counter = 1;
                                        }
                                        // Изменение confirmed -> true
                                        student_register_requests$db.document(model.getId()).update("confirmed", true);
                                        dialog.dismiss();
                                        // В данный момент пользователем является тот ученик который был только что зарегистрирован
                                        // чтобы перезайти на учительский аккаунт делаем перезапуск приложения
                                        Intent intent = new Intent(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext(), login_activity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().startActivity(intent);
                                    }else{
                                        Toast.makeText(alertDialog.getContext(), "Возможно, ученик с этим email уже есть в системе", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Изменение denied -> true
                            student_register_requests$db.document(model.getId()).update("denied", true);
                            dialog.dismiss();
                        }
                    });
                }else{
                    alertDialog.setTitle("Подробная информация о запросе");
                    alertDialog.setMessage(message);
                    alertDialog.show();
                }
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestModels.size();
    }

    DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            dialog.dismiss();
        }
    };

    /**
     * Получить лимит на удаленные запросы
     */

    private void getLimitRemoteRequests(StudentRegisterRequestRecyclerViewViewHolder studentRegisterRequestRecyclerViewViewHolder){
        SharedPreferences sharedPreferences = studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
        limitRemoteRequests = sharedPreferences.getString(Student.LIMIT_REMOTE_REQUEST_NUMBER, "3");
    }

}
