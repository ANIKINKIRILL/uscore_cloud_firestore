package com.example.admin.uscore001.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.rey.material.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class TeacherSettingsDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "TeacherSettingsDialog";

    // widgets
    private EditText position, subject, firstName, secondName, lastName;
    private android.widget.TextView ok, cancel;

    // vars
    String fullnameSharedPrefValue;
    String positionSharedPrefValue;
    String subjectSharedPrefValue;
    private String teacherID;
    private String teacherLastName;
    private String teacherSecondName;
    private String teacherFirstName;

    // Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://fir-01-ff46b.firebaseio.com/");
    private DatabaseReference teachersRef = database.getReference("Teachers");


    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference subjects$DB = firebaseFirestore.collection("SUBJECTS$DB");
    CollectionReference positions$DB = firebaseFirestore.collection("POSITIONS$DB");
    CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_settings_dialog, container, false);
        init(view);
        getDialog().setTitle("Изменить свой профиль");
        return view;
    }

    private void init(View view){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        fullnameSharedPrefValue = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");
        positionSharedPrefValue = sharedPreferences.getString(getString(R.string.intentTeacherPosition), "");
        subjectSharedPrefValue = sharedPreferences.getString(getString(R.string.intentTeacherSubject), "");
        teacherLastName = sharedPreferences.getString("teacherLastName", "");
        teacherSecondName = sharedPreferences.getString("teacherSecondName", "");
        teacherFirstName = sharedPreferences.getString("teacherFirstName", "");

        teacherID = sharedPreferences.getString("teacherID", "");

        position = view.findViewById(R.id.position);
        subject = view.findViewById(R.id.subject);
        firstName = view.findViewById(R.id.firstName);
        firstName.setText(teacherFirstName);
        secondName = view.findViewById(R.id.secondName);
        secondName.setText(teacherSecondName);
        lastName = view.findViewById(R.id.lastName);
        lastName.setText(teacherLastName);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);

        getSubjectPositionByID(subjectSharedPrefValue, positionSharedPrefValue);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    private void getSubjectPositionByID(String subjectID, String positionID){
        subjects$DB.document(subjectID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                String subjectValue = documentSnapshot.getString("name");
                subject.setText(subjectValue);
            }
        });
        positions$DB.document(positionID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                String positionValue = documentSnapshot.getString("name");
                position.setText(positionValue);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:{
                String new_first_name = firstName.getText().toString();
                String new_second_name = secondName.getText().toString();
                String new_last_name = lastName.getText().toString();
                String new_position = position.getText().toString();
                String new_subject = subject.getText().toString();
                Map<String, Object> updates = new HashMap<>();
                updates.put("firstName", new_first_name.trim());
                updates.put("secondName", new_second_name.trim());
                updates.put("lastName", new_last_name.trim());
                teachers$DB.document(teacherID)
                        .update(updates);
                getDialog().dismiss();
                break;
            }
            case R.id.cancel:{
                getDialog().dismiss();
                Toast.makeText(getContext(), "Изменения отменены", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "close dialog");
                break;
            }
        }
    }
}
