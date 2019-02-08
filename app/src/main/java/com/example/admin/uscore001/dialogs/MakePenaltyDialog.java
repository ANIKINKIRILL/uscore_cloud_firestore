package com.example.admin.uscore001.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MakePenaltyDialog extends DialogFragment implements AdapterView.OnItemSelectedListener{

    private static final String TAG = "MakePenaltyDialog";

    // widgets
    private Spinner groupsPickerSpinner, studentPickerSpinner, optionSpinner;
    private TextView scoreTextView, ok, cancel;

    // vars
    private String selectedGroup;
    private String selectedStudent;
    private String selectedOption;
    private ArrayList<String> allStudentsFromPickedGroup = new ArrayList<>();
    private Context context;

    // Firebase
    DatabaseReference mDatabaseOptionsRef = FirebaseDatabase.getInstance().getReference("DefaultPenalty");
    DatabaseReference mRefStudents = FirebaseDatabase.getInstance().getReference("Students");
    private String selectedEmailStudentFromPickedGroup;
    private int counter = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_penalty, container, false);
        try {
            context = inflater.getContext();
        }catch (Exception e){
            e.getMessage();
        }
        init(view);

        return view;
    }

    private void init(View view){

        getDialog().setTitle("Понизить очки");

        groupsPickerSpinner = view.findViewById(R.id.groupsPickerSpinner);
        studentPickerSpinner = view.findViewById(R.id.studentPickerSpinner);
        optionSpinner = view.findViewById(R.id.optionSpinner);
        scoreTextView = view.findViewById(R.id.scoreTextView);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.groupsWithoutTeachers, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupsPickerSpinner.setAdapter(arrayAdapter);
        groupsPickerSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> pickOptionAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.defaultPenalty, android.R.layout.simple_spinner_item);
        pickOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionSpinner.setAdapter(pickOptionAdapter);
        optionSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.groupsPickerSpinner:{
                allStudentsFromPickedGroup.clear();
                selectedGroup = adapterView.getSelectedItem().toString();
                loadAllGroupStudents(selectedGroup);
                break;
            }
            case R.id.optionSpinner:{
                selectedOption = adapterView.getSelectedItem().toString();
                Toast.makeText(getContext(), selectedOption, Toast.LENGTH_SHORT).show();
                getSelectedOptionScore(selectedOption);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView){}

    public void getSelectedOptionScore(String selectedOption){
        mDatabaseOptionsRef.child(selectedOption).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    int optionScore = dataSnapshot.getValue(Option.class).getScore();
                    Log.d(TAG, "onDataChange: " + optionScore);
                    scoreTextView.setText(Integer.toString(optionScore));
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: " + e.getMessage());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }


    public void loadAllGroupStudents(String pickedGroup){
        mRefStudents.child(pickedGroup)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot student : dataSnapshot.getChildren()){
                        try {
                            allStudentsFromPickedGroup.add(student.getValue(Student.class).getUsername());
                            createStudentsAdapter();
                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: " + e.getMessage());
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError){}
            });
    }

    public void createStudentsAdapter(){
        ArrayAdapter<String> studentsAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, allStudentsFromPickedGroup);
        studentsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentPickerSpinner.setAdapter(studentsAdapter);
        studentPickerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedStudent = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView){}
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPickedStudentEmail(selectedGroup, selectedStudent);
                getDialog().dismiss();
                Toast.makeText(context, "Вы оштрафовали " + selectedStudent + " на " + scoreTextView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void decreaseStudentScore(String points){
        mRefStudents.child(selectedGroup).child(selectedEmailStudentFromPickedGroup.replace(".", ""))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(counter != 0) {
                            String currentScore = dataSnapshot.getValue(Student.class).getScore();
                            int currentScoreInt = Integer.parseInt(currentScore);
                            int pointsInt = Integer.parseInt(points);
                            int result = currentScoreInt - pointsInt;
                            if(result < 0){
                                result = 0;
                            }
                            String resultString = Integer.toString(result);
                            dataSnapshot.getRef().child("score").setValue(resultString);
                            counter = 0;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void loadPickedStudentEmail(String group, String username){
        mRefStudents.child(group)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot student : dataSnapshot.getChildren()) {
                                if (student.getValue(Student.class).getUsername().equals(username)) {
                                    selectedEmailStudentFromPickedGroup = student.getValue(Student.class).getEmail();
                                    decreaseStudentScore(scoreTextView.getText().toString());
                                }
                            }
                        }catch (Exception e){
                            Log.d(TAG, "onDataChange: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


}
