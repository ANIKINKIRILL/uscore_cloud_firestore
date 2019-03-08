package com.example.admin.uscore001.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDetailPage extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "StudentDetailPage";

    // widgets
    CircleImageView circleImageView;
    TextView emailAddress, score, group, username;
    TextView rateInGroup, rateInSchool, status;
    ImageView backArraw;
    Button addCommentButton;
    TextView showAllComments, ratingHint;

    TextView countAddedCanceledScore;

    // vars
    ArrayList<Student> students = new ArrayList<>();
    ArrayList<Student> students2 = new ArrayList<>();
    String scoreValue;
    Student currentStudentClass;
    int currentStudentRateSchool;
    String intentEmail;

    // Firebase
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference student$db = firebaseFirestore.collection("STUDENTS$DB");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Мой профиль");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.startblue_transparent)));

        Bundle intent = getIntent().getExtras();
        String intentImageView = intent.getString(getString(R.string.intentImage));
        String intentUsername = intent.getString(getString(R.string.intentUsername));
        String intentScore = intent.getString(getString(R.string.intentScore));
        String intentGroup = intent.getString(getString(R.string.intentGroup));
        intentEmail = intent.getString(getString(R.string.intentEmail));
        String intentGroupID = intent.getString(getString(R.string.intentGroupID));
        Log.d(TAG, "onCreate: " + intentGroupID);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String studentStatusID = sharedPreferences.getString(getString(R.string.studentStatusID), "");
        String teacherStatusID = sharedPreferences.getString(getString(R.string.teacherStatusID), "");

        circleImageView = findViewById(R.id.imageView);
        ratingHint = findViewById(R.id.ratingHint);
        emailAddress = findViewById(R.id.emailAddress);
        score = findViewById(R.id.score);
        group = findViewById(R.id.group);
        username = findViewById(R.id.username);
        rateInGroup = findViewById(R.id.rateInGroup);
        rateInSchool = findViewById(R.id.rateInSchool);
        addCommentButton = findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(this);
        status = findViewById(R.id.status);
        showAllComments = findViewById(R.id.showAllComments);
        showAllComments.setOnClickListener(this);

        if(!studentStatusID.isEmpty() && teacherStatusID.isEmpty()){
            status.setText(R.string.statusStudent);
        }else if(studentStatusID.isEmpty() && !teacherStatusID.isEmpty()){
            status.setText(R.string.statusTeacher);
        }

        countAddedCanceledScore = findViewById(R.id.countAddedCanceledScore);
        countAddedCanceledScore.setText("-");

        ratingHint.setText(getResources().getString(R.string.notYoursRatingScoreHint));

        if(intentImageView.isEmpty()) {
            Glide.with(getApplicationContext()).load("https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png").into(circleImageView);
        }else {
            Glide.with(getApplicationContext()).load(intentImageView).into(circleImageView);
        }
        emailAddress.setText(intentEmail);
        username.setText(intentUsername);
        score.setText(intentScore);
        group.setText(intentGroup);

//        rateStudentInGroup(intentGroupID);

//        rateStudentInSchool();

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addCommentButton:{
                Intent intent = new Intent(StudentDetailPage.this, CommentsPage.class);
                intent.putExtra("to_whom_send_email", emailAddress.getText().toString());
                startActivity(intent);
                break;
            }
            case R.id.showAllComments:{
                Intent intent = new Intent(StudentDetailPage.this, CommentsPage.class);
                intent.putExtra("to_whom_send_email", emailAddress.getText().toString());
                startActivity(intent);
                break;
            }
        }
    }

//    public void rateStudentInGroup(final String foundGroup){
//        Log.d(TAG, "rateStudentInGroup: " + intentEmail);
//        students.clear();
//        student$db.whereEqualTo("groupID", foundGroup).addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                    Student student = documentSnapshot.toObject(Student.class);
//                    if(student.getEmail().equals(intentEmail)){
//                        scoreValue = student.getScore();
//                        currentStudentClass = new Student(
//                                "",
//                                student.getFirstName() + " " + student.getSecondName(),
//                                "",
//                                "",
//                                scoreValue,
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                ""
//                        );
//                    }else{
//                        scoreValue = student.getScore();
//                        Student studentClass = new Student(
//                                "",
//                                student.getFirstName() + " " + student.getSecondName(),
//                                "",
//                                "",
//                                scoreValue,
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                ""
//                        );
//                        students.add(studentClass);
//                    }
//                }
//                students.add(currentStudentClass);
//                bubbleSortStudents(students);
//                Collections.reverse(students);
//                int currentStudentRateGroup = students.indexOf(currentStudentClass)+1;
//                rateInGroup.setText(Integer.toString(currentStudentRateGroup));
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StudentDetailPage.this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(getString(R.string.currentStudentRateInGroup), Integer.toString(currentStudentRateGroup));
//                editor.apply();
//            }
//        });
//    }

//    public void rateStudentInSchool(){
//        student$db.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                for(DocumentSnapshot studentSnapshot : queryDocumentSnapshots.getDocuments()){
//                    Student student = studentSnapshot.toObject(Student.class);
//                    if(student.getEmail().equals(intentEmail)){
//                        scoreValue = student.getScore();
//                        currentStudentClass = new Student(
//                                "",
//                                "",
//                                "",
//                                "",
//                                scoreValue,
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                ""
//                        );
//                    }else{
//                        scoreValue = student.getScore();
//                        Student new_student = new Student(
//                                "",
//                                "",
//                                "",
//                                "",
//                                scoreValue,
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                "",
//                                ""
//                        );
//                        students2.add(new_student);
//                    }
//                }
//                students2.add(currentStudentClass);
//                bubbleSortStudents(students2);
//                Collections.reverse(students2);
//                currentStudentRateSchool = students2.indexOf(currentStudentClass)+1;
//                rateInSchool.setText(Integer.toString(currentStudentRateSchool));
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(StudentDetailPage.this);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString(getString(R.string.currentStudentRateInSchool), Integer.toString(currentStudentRateSchool));
//                editor.apply();
//            }
//        });
//    }

    public void bubbleSortStudents(ArrayList<Student> students){
        int size = students.size();
        Student temp;
        for(int i = 0; i < size; i++){
            for(int j = 1; j < size; j++){
                if(Integer.parseInt(students.get(j-1).getScore()) > Integer.parseInt(students.get(j).getScore())) {
                    temp = students.get(j-1);
                    students.set(j-1, students.get(j));
                    students.set(j, temp);
                }
            }
        }
    }

}
