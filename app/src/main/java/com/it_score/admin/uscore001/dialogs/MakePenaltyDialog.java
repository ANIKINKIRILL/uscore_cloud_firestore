package com.it_score.admin.uscore001.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.it_score.admin.uscore001.Callback;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.activities.MakePenaltyActivity;
import com.it_score.admin.uscore001.models.Group;
import com.it_score.admin.uscore001.models.Option;
import com.it_score.admin.uscore001.models.Penalty;
import com.it_score.admin.uscore001.models.Student;
import com.it_score.admin.uscore001.models.Teacher;
import com.it_score.admin.uscore001.models.User;
import com.it_score.admin.uscore001.util.RegisterActivityGroupAdapter;
import com.it_score.admin.uscore001.util.StudentNameArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Активити 'Оштрафовать ученика' (Пользователь -> Учитель)
 */

public class MakePenaltyDialog extends DialogFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private static final String TAG = "MakePenaltyDialog";

    // Виджеты
    private Spinner groupsPickerSpinner, studentPickerSpinner, optionSpinner;
    private TextView scoreTextView, ok, cancel, optionID;

    // Переменные
    private String selectedOption;
    private Context context;
    private String currentTeacherRequestID;
    private String currentTeacherID;
    private String studentID;
    private String groupId;

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference options$DB = firebaseFirestore.collection("OPTIONS$DB");
    CollectionReference reqeusts$DB = firebaseFirestore.collection("REQEUSTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_penalty, container, false);
        try {
            // try catch блок из-за возникновения проблемы с context`ом, так как после закрытия активити context = null
            context = inflater.getContext();
        }catch (Exception e){
            e.getMessage();
        }

        init(view);
        dialogConfiguration();
        getTeacherData();
        getSchoolGroups();
        populatePenaltySpinner();

        return view;
    }

    /**
     * Инициализация виджетов
     * @param view      на чем находяться виджеты
     */
    private void init(View view){
        groupsPickerSpinner = view.findViewById(R.id.groupsPickerSpinner);
        studentPickerSpinner = view.findViewById(R.id.studentPickerSpinner);
        optionSpinner = view.findViewById(R.id.optionSpinner);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        optionID = view.findViewById(R.id.optionID);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:{
                getDialog().dismiss();
                break;
            }
            case R.id.ok:{
                if(!scoreTextView.getText().toString().trim().isEmpty()){
                    // Внесение данных/наказания в БД
                    addPenaltyToHistory(optionID.getText().toString(), groupId, studentID, scoreTextView.getText().toString());
                    // Понижения баллов
                    decreaseStudentScore(studentID, Integer.parseInt(scoreTextView.getText().toString()));
                }
                break;
            }
        }
    }

    /**
     * Настройки дилогового окна
     */

    private void dialogConfiguration(){
        getDialog().setTitle("Штраф ученика");
    }

    /**
     * Получение requestID учителя
     * id учителя
     */

    private void getTeacherData(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
        currentTeacherRequestID = sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "");
        currentTeacherID = sharedPreferences.getString(Teacher.TEACHER_ID, "");
    }

    /**
     * Получить список всех групп со школы
     */

    private void getSchoolGroups(){
        User.getAllSchoolGroups(mGetAllSchoolGroupsCallback);
    }

    private Callback mGetAllSchoolGroupsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            populateGroupSpinner((ArrayList<Group>) data);
        }
    };

    /**
     * Заполнение адаптера с выбором групп
     */

    private void populateGroupSpinner(ArrayList<Group> groups){
        // try catch блок из-за возникновения проблемы с context`ом, так как после закрытия активити context = null
        try {
            RegisterActivityGroupAdapter groupAdapter = new RegisterActivityGroupAdapter(context, groups);
            groupsPickerSpinner.setAdapter(groupAdapter);
            groupsPickerSpinner.setOnItemSelectedListener(this);
        }catch (Exception e){
            e.getMessage();
        }
    }

    /**
     * Заполнение адаптера с выбором наказаний
     */

    private void populatePenaltySpinner(){
        // try catch блок из-за возникновения проблемы с context`ом, так как после закрытия активити context = null
        try {
            ArrayAdapter<CharSequence> pickOptionAdapter = ArrayAdapter.createFromResource(context, R.array.defaultPenalty, android.R.layout.simple_spinner_item);
            pickOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            optionSpinner.setAdapter(pickOptionAdapter);
            optionSpinner.setOnItemSelectedListener(this);
        }catch (Exception e){
            e.getMessage();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            // пользователь нажал на спиннер с выбором групп
            case R.id.groupsPickerSpinner:{
                Group group = (Group) adapterView.getSelectedItem();
                groupId = group.getId();
                Student.loadGroupStudentsByGroupID(groupId, null, mGetAllGroupStudents);
                break;
            }
            // пользователь нажал на спиннер с выбором наказаний
            case R.id.optionSpinner:{
                selectedOption = adapterView.getSelectedItem().toString();
                Toast.makeText(getContext(), selectedOption, Toast.LENGTH_SHORT).show();
                getSelectedOptionScore(selectedOption);
                break;
            }
            // пользователь нажал на спиннер с выбором ученика
            case R.id.studentPickerSpinner:{
                Student student = (Student) adapterView.getSelectedItem();
                studentID = student.getId();
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){}

    private Callback mGetAllGroupStudents = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<Student> selectedGroupStudentsList = (ArrayList) data;
            StudentNameArrayAdapter adapter = new StudentNameArrayAdapter(context, selectedGroupStudentsList);
            studentPickerSpinner.setAdapter(adapter);
            studentPickerSpinner.setOnItemSelectedListener(MakePenaltyDialog.this);
        }
    };

    /**
     * Получить очки наказания
     * @param selectedOption        название наказания
     */

    public void getSelectedOptionScore(String selectedOption){
        options$DB
            .document(getString(R.string.penaltiesID))
            .collection("options")
            .whereEqualTo("name", selectedOption)
            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                        Option option = documentSnapshot.toObject(Option.class);
                        String optionScore = option.getPoints();
                        String optionIDValue = option.getId();
                        scoreTextView.setText(optionScore);
                        optionID.setText(optionIDValue);
                        Log.d(TAG, "optionScore: " + scoreTextView.getText().toString());
                        Log.d(TAG, "optionID: " + optionID.getText().toString());
                    }
                }
            });
    }

    /**
     * Внесение данных/наказания в БД
     * @param optionID      id наказания
     * @param groupID       id группы ученика
     * @param studentID     id ученика
     * @param score         очки наказания
     */

    private void addPenaltyToHistory(String optionID, String groupID, String studentID, String score){
        Log.d(TAG, "addPenaltyToHistory: optionId: " + optionID + "groupID: " + groupID + "studentID: " + studentID + "scoreID: " + score);
        Penalty penalty = new Penalty("", optionID, groupID, studentID, score, currentTeacherID);
        reqeusts$DB.document(currentTeacherRequestID).collection("STUDENTS").document(studentID).collection("PENALTY").add(penalty)
            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        String penaltyDocumentID = task.getResult().getId();
                        task.getResult().update("id", penaltyDocumentID);
                        Map<String, String> idField = new HashMap<>();
                        idField.put("id", penaltyDocumentID);
                        reqeusts$DB
                            .document(currentTeacherRequestID)
                            .collection("STUDENTS")
                            .document(studentID)
                            .collection("PENALTY")
                            .document(penaltyDocumentID)
                            .set(idField, SetOptions.merge());
                        HashMap<String, String> idMap = new HashMap<>();
                        idMap.put("id", studentID);
                        reqeusts$DB.document(currentTeacherRequestID).collection("STUDENTS").document(studentID).set(idMap,  SetOptions.merge());
                    }
                }
            });
    }

    /**
     * Оштрафовать ученика
     * @param studentID         id ученика
     * @param scoreToDecrease   очки на которые учитель штрафует учинка
     */

    private void decreaseStudentScore(String studentID, int scoreToDecrease){
        Teacher.decreaseStudentScore(studentID, scoreToDecrease, mDecreaseStudentScoreCallback);
        getDialog().dismiss();
        Toast.makeText(context, "Вы оштрафовали ученика. Для более детальной инфармации смотреть в своей истории", Toast.LENGTH_SHORT).show();
    }

    private Callback mDecreaseStudentScoreCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            /*
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
            alertDialog.setTitle("Штраф ученика");
            alertDialog.setMessage("Вы оштрафовали ученика. Для более детальной инфармации смотреть в 'Недавниe'");
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();
            */
        }
    };

}
