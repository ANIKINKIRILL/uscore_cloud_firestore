package com.example.admin.uscore001.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentDetailPage extends AppCompatActivity implements View.OnClickListener{

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

    // Firebase
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Comments");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile2);

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(configuration);

        Bundle intent = getIntent().getExtras();
        String intentImageView = intent.getString(getString(R.string.intentImage));
        String intentUsername = intent.getString(getString(R.string.intentUsername));
        String intentScore = intent.getString(getString(R.string.intentScore));
        String intentGroup = intent.getString(getString(R.string.intentGroup));
        String intentEmail = intent.getString(getString(R.string.intentEmail));

        circleImageView = findViewById(R.id.imageView);
        ratingHint = findViewById(R.id.ratingHint);
        emailAddress = findViewById(R.id.emailAddress);
        score = findViewById(R.id.score);
        group = findViewById(R.id.group);
        username = findViewById(R.id.username);
        rateInGroup = findViewById(R.id.rateInGroup);
        rateInSchool = findViewById(R.id.rateInSchool);
        backArraw = findViewById(R.id.back);
        backArraw.setOnClickListener(this);
        addCommentButton = findViewById(R.id.addCommentButton);
        addCommentButton.setOnClickListener(this);
        status = findViewById(R.id.status);
        showAllComments = findViewById(R.id.showAllComments);
        showAllComments.setOnClickListener(this);

        if(!currentUser.getEmail().contains("teacher")){
            status.setText(R.string.statusStudent);
        }else {
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

        rateStudentInGroup(intentGroup);

        rateStudentInSchool();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:{
                finish();
                break;
            }
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

    public void rateStudentInGroup(final String foundGroup){
        students.clear();
        FirebaseDatabase.getInstance().getReference("Students").child(foundGroup).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot currentGroupMembers : dataSnapshot.getChildren()) {
                        if (currentGroupMembers.getKey().equals(emailAddress.getText().toString().replace(".", ""))) {
                            scoreValue = currentGroupMembers.getValue(Student.class).getScore();
                            currentStudentClass = new Student(scoreValue, "", "", "", "");
                        } else {
                            scoreValue = currentGroupMembers.getValue(Student.class).getScore();
                            Student student = new Student(scoreValue, "", "", "", "");
                            students.add(student);
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                students.add(currentStudentClass);
                bubbleSortStudents(students);
                Collections.reverse(students);
                int currentStudentRateGroup = students.indexOf(currentStudentClass)+1;
                rateInGroup.setText(Integer.toString(currentStudentRateGroup));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void rateStudentInSchool(){
        students2.clear();
        FirebaseDatabase.getInstance().getReference("Students").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot groups : dataSnapshot.getChildren()) {
                        for (DataSnapshot student : groups.getChildren()) {
                            if (student.getKey().equals(emailAddress.getText().toString().replace(".", ""))) {
                                scoreValue = student.getValue(Student.class).getScore();
                                currentStudentClass = new Student(scoreValue, "", "", "", "");
                            } else {
                                scoreValue = student.getValue(Student.class).getScore();
                                Student new_student = new Student(scoreValue, "", "", "", "");
                                students2.add(new_student);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                students2.add(currentStudentClass);
                bubbleSortStudents(students2);
                Collections.reverse(students2);
                int currentStudentRateSchool = students2.indexOf(currentStudentClass)+1;
                rateInSchool.setText(Integer.toString(currentStudentRateSchool));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

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
