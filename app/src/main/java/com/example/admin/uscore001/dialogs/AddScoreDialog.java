package com.example.admin.uscore001.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddScoreDialog extends DialogFragment implements View.OnClickListener{

    // widgets
    TextView cancel, ok;
    EditText emailView, scoreView, groupView;

    // vars
    String studentEmailAddress;
    int score;
    String groupValue;
    int counter = 0;
    Context context = getContext();

    // Firebase
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("Students");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_addscore, container, false);

        emailView = view.findViewById(R.id.studentEmailAddress);
        scoreView = view.findViewById(R.id.score);
        groupView = view.findViewById(R.id.group);

        cancel = view.findViewById(R.id.cancel);
        ok = view.findViewById(R.id.ok);

        cancel.setOnClickListener(this);
        ok.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:{
                getDialog().dismiss();
                break;
            }
            case R.id.ok:{
                studentEmailAddress = emailView.getText().toString();
                score = Integer.parseInt(scoreView.getText().toString());
                groupValue = groupView.getText().toString();
                addScore(score, studentEmailAddress, groupValue);
                getDialog().dismiss();
                break;
            }
        }
    }

    public void addScore(final int score, final String studentEmailAddress, final String group){

        // SELECT * FROM Students/group WHERE 'email' EQUALS studentEmailAddress

        Query query = mDatabaseRef.child(group).orderByChild("email").equalTo(studentEmailAddress);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(counter != 1) {
                    for (DataSnapshot selectedStudent : dataSnapshot.getChildren()) {
                        String old_score = selectedStudent.getValue(Student.class).getScore();
                        int old_score_int = Integer.parseInt(old_score);
                        int result = old_score_int + score;
                        String result_str = Integer.toString(result);
                        selectedStudent.getRef().child("score").setValue(result_str);
                        counter = 1;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
