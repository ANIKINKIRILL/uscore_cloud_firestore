package com.example.admin.uscore001.fragments;

import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AskForScoreDialogFragment extends DialogFragment implements View.OnClickListener{

    // widgets
    EditText teacherPassword, teacherEmail;
    EditText score, group;
    TextView ok, cancel;

    // vars
    String teacherEmailText, teacherPasswordText;
    int scoreValue;
    int counter = 0;

    // Firebase
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mRef = mDatabase.getReference("Students");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_askforscore, container, false);

        getDialog().setTitle(getResources().getString(R.string.addScoreDialogTitle));

        teacherPassword = view.findViewById(R.id.teacherPassword);
        teacherEmail = view.findViewById(R.id.teacherEmailAddress);
        score = view.findViewById(R.id.score);
        group = view.findViewById(R.id.group);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:{

                teacherEmailText = teacherEmail.getText().toString();
                teacherPasswordText = teacherPassword.getText().toString();

                if(teacherEmailText.contains("teacher") && teacherPasswordText.equals("qwerty")) {
                    addScore(Integer.parseInt(score.getText().toString()), group.getText().toString().trim());
                    getDialog().dismiss();
                }else{
                    teacherPassword.setError(getResources().getString(R.string.notGoodError));
                    teacherPassword.requestFocus();
                    teacherEmail.setError(getResources().getString(R.string.MaybeyougottacheckError));
                    teacherEmail.requestFocus();
                }
                break;
            }
            case R.id.cancel:{
                getDialog().dismiss();
                break;
            }
        }
    }


    public void addScore(final int score, String group){
        Query query = mRef.child(group).orderByChild("email").equalTo(currentUser.getEmail());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot selectedStudent : dataSnapshot.getChildren()) {
                    String old_score = selectedStudent.getValue(Student.class).getScore();
                    int old_score_int = Integer.parseInt(old_score);
                    int result = old_score_int + score;
                    String result_str = Integer.toString(result);
                    selectedStudent.getRef().child("score").setValue(result_str);
                    counter = 1;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
