package com.example.admin.uscore001.dialogs;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.example.admin.uscore001.models.User;
import com.example.admin.uscore001.util.RequestAddingScoreAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.model.value.FieldValue;
import com.google.firebase.firestore.model.value.FieldValueOptions;

import java.lang.reflect.Executable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


/**
 * Окно для отправления заявки на добваление очков учителю (Пользователь -> ученик)
 */

public class DialogRequestAddingScore extends DialogFragment implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener{

    private static final String TAG = "DialogRequestAddingScor";

    // Виджеты
    Spinner teacherSpinner, options;
    EditText requestBody;
    TextView ok, cancel, score;
    RelativeLayout dialogLayout;

    // Переменные
    String teacherName;
    String selectedOption;
    String addedDate;
    private String optionID;
    private String teacherRequestID;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference students$db = firebaseFirestore.collection("STUDENTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.request_adding_score_dialog, container, false);
        init(view);
        setDateFormat();
        setDialogConfiguration();
        populateTeachersSpinner();
        populateOptionsSpinner(view);
        return view;
    }

    /**
     * Инициализация виджетов
     * @param view      окно диалогового окна
     */

    private void init(View view){
        dialogLayout = view.findViewById(R.id.dialogLayout);
        teacherSpinner = view.findViewById(R.id.teacherSpinner);
        options = view.findViewById(R.id.options);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);
        requestBody = view.findViewById(R.id.requestBody);
        score = view.findViewById(R.id.score);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void setDateFormat(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        addedDate = simpleDateFormat.format(new Date());
    }

    /**
     * Настройка диалогового окна
     */

    private void setDialogConfiguration(){
        getDialog().setTitle(getResources().getString(R.string.make_request));
    }

    /**
     * Выгрузка всех учителей
     */

    private void populateTeachersSpinner(){
        Teacher.loadAllTeachersClasses(new Callback() {
            @Override
            public void execute(Object data, String... params) {
                ArrayList<Teacher> teachers = (ArrayList<Teacher>) data;
                RequestAddingScoreAdapter pickTeacherAdapter = new RequestAddingScoreAdapter(getContext(), android.R.layout.simple_spinner_item, teachers);
                pickTeacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                teacherSpinner.setAdapter(pickTeacherAdapter);
                teacherSpinner.setOnItemSelectedListener(DialogRequestAddingScore.this);
            }
        });
    }

    /**
     * Наполнение опций в спиннер
     */

    private void populateOptionsSpinner(View view){
        ArrayAdapter<CharSequence> pickOptionAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.options, android.R.layout.simple_spinner_item);
        pickOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(pickOptionAdapter);
        options.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Student.STUDENT_DATA, Context.MODE_PRIVATE);
        String currentUserGroupID = sharedPreferences.getString(Student.GROUP_ID, "");
        String currentStudentID = sharedPreferences.getString(Student.ID, "");
        String firstName = sharedPreferences.getString(Student.FIRST_NAME, "");
        String secondName = sharedPreferences.getString(Student.SECOND_NAME, "");
        String senderImage = sharedPreferences.getString(Student.IMAGE_PATH, "");
        switch (v.getId()) {
            case R.id.ok: {
                String id = "";
                String body = requestBody.getText().toString();
                String date = addedDate;
                String getter = teacherName;
                String image_path = senderImage;
                String senderEmail = currentUser.getEmail();
                int scoreValue = Integer.parseInt(score.getText().toString());
                String groupID = currentUserGroupID;
                String requestID = teacherRequestID;
                String option = optionID;
                boolean answered = false;
                boolean canceled = false;
                String senderID = currentStudentID;
                students$db.document(currentStudentID)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            Student student = documentSnapshot.toObject(Student.class);
                            int currentLimitScore = Integer.parseInt(student.getLimitScore());
                            Log.d(TAG, "currentStudentLimitScore: " + currentLimitScore);
                            if(currentLimitScore + 5 < scoreValue) {
                                try {
                                    Toast.makeText(getContext(), "Ваш лимит меньше, чем запрашиваемые очки", Toast.LENGTH_SHORT).show();
                                    YoYo.with(Techniques.Shake).repeat(0).duration(1000).playOn(dialogLayout);
                                }catch (Exception e1){
                                    Log.d(TAG, "onEvent: " + e1.getMessage());
                                }
                            }else if (currentLimitScore + 5 >= scoreValue) {
                                decreaseLimitScore(scoreValue, currentStudentID);
                                sendRequest(
                                        id, body, date, getter, image_path, senderEmail,
                                        firstName, secondName, "", scoreValue, groupID,
                                        requestID, option, answered, canceled, currentStudentID
                                );
                                try {
                                    Toast.makeText(getContext(), "Успешно отправленно " + teacherName, Toast.LENGTH_SHORT).show();
                                    getDialog().dismiss();
                                }catch (Exception e1){
                                    Log.d(TAG, "toast message: " + e1.getMessage());
                                }
                            }
                        }
                    });
                break;
            }
            case R.id.cancel: {
                getDialog().dismiss();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.teacherSpinner:{
                Teacher teacher = (Teacher) parent.getSelectedItem();
                teacherRequestID = teacher.getRequestID();
                Log.d(TAG, teacher.getFirstName()+" "+teacher.getLastName() + " id: " + teacherRequestID);
                teacherName = teacher.getFirstName() + " " + teacher.getLastName();
                break;
            }
            case R.id.options:{
                selectedOption = parent.getItemAtPosition(position).toString();
                getSelectedOptionScoreAndID(selectedOption);
                Toast.makeText(getContext(), selectedOption, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * Получить данные опции
     * @param optionName        название опции
     */

    private void getSelectedOptionScoreAndID(String optionName){
        User.getOptionData(new Callback() {
            @Override
            public void execute(Object data, String... params) {
                optionID = (String) data;
                String optionScore = params[0];
                score.setText(optionScore);
            }
        }, optionName);
    }

    /**
     * Отправить запрос учителю на добавление очков
     *
     * @param id            id запроса
     * @param body          текст запроса
     * @param date          дата запроса
     * @param getter        фио учителя к кому был этот запрос отпрален
     * @param image_path    приклепленная фотография
     * @param senderEmail   email ученика
     * @param firstName     имя
     * @param secondName    фамилия
     * @param lastName      отчество
     * @param score         очки
     * @param groupID       id группы
     * @param requestID     requestID учителя
     * @param optionID      id опции за что ученик хочет чтобы ему начислели быллы
     * @param answered      принят ли запрос
     * @param canceled      отклонен ли запрос
     * @param senderID      id ученика
     */

    public void sendRequest(
            String id, String body, String date, String getter, String image_path, String senderEmail,
            String firstName, String secondName, String lastName, int score, String groupID,
            String requestID, String optionID, boolean answered, boolean canceled, String senderID)
    {
        RequestAddingScore request = new RequestAddingScore(
                id, body, date, getter, image_path, senderEmail,
                firstName, secondName, lastName, score, groupID,
                requestID, optionID, answered, canceled, senderID);
        Student.sendRequest(request);
    }

    /**
     * Вычитание баллов на день за отправку запроса учителю
     * @param requestedScoreValue       запрашиваемые очки
     * @param studentId                 id ученика
     */

    public void decreaseLimitScore(int requestedScoreValue, String studentId){
        Teacher.decreaseStudentLimitScore(null, requestedScoreValue, studentId);
    }

}
