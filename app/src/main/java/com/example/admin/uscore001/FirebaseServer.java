package com.example.admin.uscore001;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.admin.uscore001.activities.login_activity;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Класс для асинхронной работы с Firebase
 */

public class FirebaseServer {

    private static final String TAG = "FirebaseServer";

    // Firebase and Firestore
    static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    static CollectionReference STUDENTS$DB = firebaseFirestore.collection("STUDENTS$DB");
    static CollectionReference TEACHERS$DB = firebaseFirestore.collection("TEACHERS$DB");
    static CollectionReference GROUPS$DB = firebaseFirestore.collection("GROUPS$DB");

    // Переменные
    private static ArrayList<String> students = new ArrayList<>();
    private static ArrayList<String> teachers = new ArrayList<>();

    /**
     * Авторизация пользователя
     * @param login             // Логин
     * @param password          // Пароль
     * @param callback          // Callback, вызываемый после авторизации пользователя
     */

    public static void authenticateUser(String login, String password, Callback callback){
        mAuth.signInWithEmailAndPassword(login, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        callback.execute(true, login, password);
                    }else{
                        callback.execute(false, task.getException().getMessage());
                    }
                }
            });
    }

    /**
     * Извлечение статуса пользователя
     */

    public static class GetUserStatus extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            STUDENTS$DB
                .whereEqualTo("email",asyncTaskArguments[0].mData.data[0])
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Student student = documentSnapshot.toObject(Student.class);
                            String statusID = student.getStatusID();
                            asyncTaskArguments[0].mCallback.execute(statusID);
                        }
                    }
                }
            });
            TEACHERS$DB.whereEqualTo("responsible_email", asyncTaskArguments[0].mData.data[0])
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                Teacher teacher = documentSnapshot.toObject(Teacher.class);
                                String statusID = teacher.getStatusID();
                                asyncTaskArguments[0].mCallback.execute(statusID);
                            }
                        }
                    }
                });
            return null;
        }
    }

    /**
     * Выгрузка одноклассников ученика
     */

    public static class LoadGroupStudents extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            students.clear();
            String pickedGroupName = asyncTaskArguments[0].mData.data[0];
            GROUPS$DB.whereEqualTo("name", pickedGroupName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot groups : task.getResult().getDocuments()){
                            Group group = groups.toObject(Group.class);
                            String groupID = group.getId();
                            STUDENTS$DB.whereEqualTo("groupID", groupID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int studentsSize = task.getResult().size();
                                    if(studentsSize != 0){
                                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                            Student student = documentSnapshot.toObject(Student.class);
                                            students.add(student.getFirstName() + " " + student.getSecondName());
                                        }
                                        asyncTaskArguments[0].mCallback.execute(students, groupID);
                                    }else{
                                        asyncTaskArguments[0].mCallback.execute(students);
                                    }
                                }
                            });
                        }
                    }
                }
            });
            return null;
        }
    }

    /**
     * Изввлечение email ученика
     */

    public static class GetStudentEmail extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            AsyncTaskDataArgument dataArgument = asyncTaskArguments[0].mData;
            String groupID = dataArgument.data[0];
            String firstName = dataArgument.data[1];
            String secondName = dataArgument.data[2];

            STUDENTS$DB
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("secondName", secondName)
                .whereEqualTo("groupID", groupID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentSnapshot studentsSnapshots : queryDocumentSnapshots.getDocuments()) {
                            Student student = studentsSnapshots.toObject(Student.class);
                            String email = student.getEmail();
                            Log.d(TAG, "picked student email: " + email);
                            Log.d(TAG, "firstName: " + firstName + " secondName: " + secondName + "groupID: " + groupID);
                            callback.execute(email);
                        }
                    }
                });
            return null;
        }
    }

    /**
     * Выгрузка всех учитителей
     */

    public static class LoadAllTeachers extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            teachers.clear();
            Callback callback = asyncTaskArguments[0].mCallback;
            TEACHERS$DB.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        Teacher teacherObj = documentSnapshot.toObject(Teacher.class);
                        teachers.add(teacherObj.getFirstName() + " " + teacherObj.getLastName());
                    }
                    callback.execute(teachers);
                }
            });
            return null;
        }
    }

    /**
     * Изввлечение email учителя
     */

    public static class GetTeacherEmail extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            String firstName = asyncTaskArguments[0].mData.data[0];
            String lastName = asyncTaskArguments[0].mData.data[1];
            TEACHERS$DB
                .whereEqualTo("firstName", firstName)
                .whereEqualTo("lastName", lastName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        try {
                            List<DocumentSnapshot> teachersSnapshot = queryDocumentSnapshots.getDocuments();
                            Teacher teacher = teachersSnapshot.get(0).toObject(Teacher.class);
                            String email = teacher.getResponsible_email();
                            Log.d(TAG, "selectedTeacherEmail: " + email);
                            callback.execute(email);
                        }catch (Exception e1){
                            Log.d(TAG, "GetTeacherEmail: " + e1.getMessage());
                        }
                    }
                });
            return null;
        }
    }

}
