package com.example.admin.uscore001;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.admin.uscore001.activities.login_activity;
import com.example.admin.uscore001.activities.register_activity;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.StudentRegisterRequestModel;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
    static CollectionReference STUDENT_REGISTER_REQUESTS = firebaseFirestore.collection("STUDENT_REGISTER_REQUESTS");
    static CollectionReference REQEUSTS$DB = firebaseFirestore.collection("REQEUSTS$DB");
    static CollectionReference SUBJECTS$DB = firebaseFirestore.collection("SUBJECTS$DB");
    static CollectionReference POSITIONS$DB = firebaseFirestore.collection("POSITIONS$DB");

    // Переменные
    private static ArrayList<String> studentsByGroupName = new ArrayList<>();
    private static ArrayList<Student> studentsByGroupID = new ArrayList<>();
    private static ArrayList<String> teachers = new ArrayList<>();
    private static ArrayList<Group> groups = new ArrayList<>();
    private static ArrayList<RequestAddingScore> confirmedRequests = new ArrayList<>();
    private static ArrayList<RequestAddingScore> deniedRequests = new ArrayList<>();
    private static LinkedList<Student> allStudents = new LinkedList<>();

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
     * Выгрузка учеников группы по названию
     */

    public static class LoadGroupStudentsByGroupName extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            studentsByGroupName.clear();
            String pickedGroupName = (String)asyncTaskArguments[0].mData.data[0];
            GROUPS$DB.whereEqualTo("name", pickedGroupName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot groups : task.getResult().getDocuments()){
                            Group group = groups.toObject(Group.class);
                            String groupID = group.getId();
                            STUDENTS$DB
                                    .whereEqualTo("groupID", groupID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    int studentsSize = task.getResult().size();
                                    if(studentsSize != 0){
                                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                            Student student = documentSnapshot.toObject(Student.class);
                                            studentsByGroupName.add(student.getFirstName() + " " + student.getSecondName());
                                        }
                                        asyncTaskArguments[0].mCallback.execute(studentsByGroupName, groupID);
                                    }else{
                                        asyncTaskArguments[0].mCallback.execute(studentsByGroupName);
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
     * Выгрузка учеников группы по ID
     */

    public static class LoadGroupStudentsByGroupID extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            studentsByGroupID.clear();
            Callback callback = asyncTaskArguments[0].mCallback;
            String groupID = (String)asyncTaskArguments[0].mData.data[0];
            STUDENTS$DB.whereEqualTo("groupID", groupID).orderBy("score", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    String rateInGroup = "";
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Student student = documentSnapshot.toObject(Student.class);
                            studentsByGroupID.add(student);
                            if(student.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                rateInGroup = Integer.toString(studentsByGroupID.indexOf(student) + 1);
                            }
                        }
                        callback.execute(studentsByGroupID, rateInGroup);
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
            String groupID = (String) dataArgument.data[0];
            String firstName = (String) dataArgument.data[1];
            String secondName = (String) dataArgument.data[2];

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
            String firstName = (String) asyncTaskArguments[0].mData.data[0];
            String lastName = (String) asyncTaskArguments[0].mData.data[1];
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

    /**
     * Выгрузка всех групп школы
     */

    public static class GetAllSchoolGroups extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            groups.clear();
            GROUPS$DB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Group group = documentSnapshot.toObject(Group.class);
                            groups.add(group);
                        }
                        asyncTaskArguments[0].mCallback.execute(groups);
                    }
                }
            });
            return null;
        }
    }

    /**
     * Отправка заявки на регистрацию ученика
     */

    public static class SendRegistrationRequest extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            AsyncTaskDataArgument dataArgument = asyncTaskArguments[0].mData;
            String firstName = (String) dataArgument.data[0];
            String secondName = (String) dataArgument.data[1];
            String lastName = (String) dataArgument.data[2];
            String email = (String) dataArgument.data[3];
            String groupID = (String) dataArgument.data[4];
            String teacherID = (String) dataArgument.data[5];
            boolean confirmed = (boolean) dataArgument.data[6];
            boolean denied = (boolean) dataArgument.data[7];
            StudentRegisterRequestModel model = new StudentRegisterRequestModel(firstName, secondName,
            lastName, email, groupID, teacherID, confirmed, denied);
            STUDENT_REGISTER_REQUESTS.add(model).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    task.getResult().update("id", task.getResult().getId());
                    String message = "Вы успешно отправили запрос на регистрацию";
                    callback.execute(message);
                }
            });
            return null;
        }
    }

    /**
     * Выгрузка данных с учительского аккаунта
     */

    public static class GetTeacherClass extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            String teacherLogin = (String) asyncTaskArguments[0].mData.data[0];
            TEACHERS$DB.whereEqualTo("responsible_email", teacherLogin).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Teacher teacher = documentSnapshot.toObject(Teacher.class);
                            callback.execute(teacher);
                        }
                    }
                }
            });
            return null;
        }
    }

    /**
     * Выгрузка данных с учительского аккаунта
     */

    public static class GetStudentClass extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            String studentLogin = (String)asyncTaskArguments[0].mData.data[0];
            STUDENTS$DB.whereEqualTo("email", studentLogin).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Student student = documentSnapshot.toObject(Student.class);
                            callback.execute(student);
                        }
                    }
                }
            });
            return null;
        }
    }

    /**
     * Получение всех принятых запросов на добавление очков
     */

    public static class GetStudentConfirmedRequests extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            confirmedRequests.clear();
            String studentID = (String)asyncTaskArguments[0].mData.data[0];
            Log.d(TAG, "studentID: " + studentID);
            Callback callback = asyncTaskArguments[0].mCallback;
            REQEUSTS$DB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot teacherRequestID : task.getResult().getDocuments()){
                        teacherRequestID.getReference().collection("STUDENTS").document(studentID).collection("REQUESTS")
                            .whereEqualTo("answered", true)
                            .whereEqualTo("canceled", false)
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot studentRequests : task.getResult().getDocuments()){
                                    RequestAddingScore request = studentRequests.toObject(RequestAddingScore.class);
                                    confirmedRequests.add(request);
                                }
                                callback.execute(confirmedRequests);
                            }
                        });
                    }
                }
            });
            return null;
        }
    }

    /**
     * Получение всех отклоненных запросов на добавление очков
     */

    public static class GetStudentDeniedRequests extends AsyncTask<AsyncTaskArguments, Void, Void>{

        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            deniedRequests.clear();
            String studentID = (String)asyncTaskArguments[0].mData.data[0];
            Callback callback = asyncTaskArguments[0].mCallback;
            REQEUSTS$DB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for(DocumentSnapshot teacherRequestID : task.getResult().getDocuments()){
                        teacherRequestID.getReference().collection("STUDENTS").document(studentID).collection("REQUESTS")
                                .whereEqualTo("answered", false)
                                .whereEqualTo("canceled", true)
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for(DocumentSnapshot studentRequests : task.getResult().getDocuments()){
                                    RequestAddingScore request = studentRequests.toObject(RequestAddingScore.class);
                                    deniedRequests.add(request);
                                }
                                callback.execute(deniedRequests);
                            }
                        });
                    }
                }
            });
            return null;
        }
    }

    /**
     * Выгрузка всех учеников со школы
     */

    public static class LoadAllStudents extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            allStudents.clear();
            Callback callback = asyncTaskArguments[0].mCallback;
            STUDENTS$DB
                .orderBy("score", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    String rateStudentInSchool = "";
                    if(task.isSuccessful()){
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Student student = documentSnapshot.toObject(Student.class);
                            allStudents.add(student);
                            if(student.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                                rateStudentInSchool = Integer.toString(allStudents.indexOf(student) + 1);
                            }
                            callback.execute(allStudents, rateStudentInSchool);
                        }
                    }
                }
            });
            return null;
        }
    }

    /**
     * Добавление очков к ученику
     */

    public static class AddScoreToStudent extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            String score = (String) asyncTaskArguments[0].mData.data[0];
            String studentID = (String) asyncTaskArguments[0].mData.data[1];
            String studentScoreNow = (String) STUDENTS$DB.document(studentID).get().getResult().get("score");
            String resultScore = Integer.toString(Integer.parseInt(studentScoreNow) + Integer.parseInt(score));
            STUDENTS$DB.document(studentID).update("score", resultScore);
            String message = "Вы успешно добавили очки";
            callback.execute(message);
            return null;
        }
    }

    /**
     * Предмет учителя по ID полям
     */

    public static class GetSubjectValueByID extends AsyncTask<AsyncTaskArguments, Void, Void> {
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            String subjectID = (String) asyncTaskArguments[0].mData.data[0];
            SUBJECTS$DB.document(subjectID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    String subject = documentSnapshot.getString("name");
                    callback.execute(subject);
                }
            });
            return null;
        }
    }

    /**
     * Позиция учителя по ID полю
     */

    public static class GetPositionValueByID extends AsyncTask<AsyncTaskArguments, Void, Void> {
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            Callback callback = asyncTaskArguments[0].mCallback;
            String positionID = (String) asyncTaskArguments[0].mData.data[0];
            POSITIONS$DB.document(positionID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    String position = documentSnapshot.getString("name");
                    callback.execute(position);
                }
            });
            return null;
        }
    }

    /**
     * Обновить ФИО учителя
     */

    public static class UpdateTeacherCredentials extends AsyncTask<AsyncTaskArguments, Void, Void>{
        @Override
        protected Void doInBackground(AsyncTaskArguments... asyncTaskArguments) {
            String teacherID = (String) asyncTaskArguments[0].mData.data[0];
            String firstName = (String) asyncTaskArguments[0].mData.data[1];
            String secondName = (String) asyncTaskArguments[0].mData.data[2];
            String lastName = (String) asyncTaskArguments[0].mData.data[3];
            HashMap<String, Object> updateCredentialsMap = new HashMap<>();
            updateCredentialsMap.put("firstName", firstName.trim());
            updateCredentialsMap.put("secondName", secondName.trim());
            updateCredentialsMap.put("lastName", lastName.trim());
            TEACHERS$DB.document(teacherID).update(updateCredentialsMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        asyncTaskArguments[0].mCallback.execute("Вы успешно изменили свой профиль");
                    }else{
                        asyncTaskArguments[0].mCallback.execute("Что-то пошло не так. Изменения не сохранены");
                    }
                }
            });
            return null;
        }
    }


}
