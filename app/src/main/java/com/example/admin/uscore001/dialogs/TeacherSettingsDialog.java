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
import com.rey.material.widget.TextView;

public class TeacherSettingsDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "TeacherSettingsDialog";

    // widgets
    private EditText position, subject, fullName;
    private android.widget.TextView ok, cancel;

    // vars
    String fullnameSharedPrefValue;
    String positionSharedPrefValue;
    String subjectSharedPrefValue;

    // Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance("https://fir-01-ff46b.firebaseio.com/");
    private DatabaseReference teachersRef = database.getReference("Teachers");

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

        position = view.findViewById(R.id.position);
        position.setText(positionSharedPrefValue);
        subject = view.findViewById(R.id.subject);
        subject.setText(subjectSharedPrefValue);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:{
                String new_position = position.getText().toString();
                String new_subject = subject.getText().toString();
                try {
                    teachersRef.child(fullnameSharedPrefValue).child("position").setValue(new_position);
                    teachersRef.child(fullnameSharedPrefValue).child("subject").setValue(new_subject);
                    getDialog().dismiss();
                    Toast.makeText(getContext(), "Вы изменили свой профиль", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "update teacher profile data updated to" + " : " + new_position + " : " + new_subject);
                }catch (Exception e){
                    Log.d(TAG, "update teacher profile data ERROR die to: " + e.getMessage());
                }
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
