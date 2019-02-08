package com.example.admin.uscore001.dialogs;

import android.content.SharedPreferences;
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
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Executable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DialogRequestAddingScore extends DialogFragment implements AdapterView.OnItemSelectedListener,
                                                                View.OnClickListener, View.OnFocusChangeListener{

    private static final String TAG = "DialogRequestAddingScor";

    // widgets
    Spinner teacherSpinner, options;
    EditText requestBody;
    TextView ok, cancel, score;
    RelativeLayout dialogLayout;

    // vars
    String optionValue;
    String teacherName;
    String selectedOption;
    boolean isValid = true;
    String senderImage;
    String currentUserGroup;
    String currentUserUsername;
    String addedDate;
    int optionScore;
    boolean isDone = false;
    private int counter = 1;

    // Firebase
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("RequestsAddingScore");
    DatabaseReference mDatabaseOptionsRef = mDatabase.getReference("Options");
    DatabaseReference mDatabaseStudentsRef = mDatabase.getReference("Students");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.request_adding_score_dialog, container, false);

        dialogLayout = view.findViewById(R.id.dialogLayout);

        getDialog().setTitle(getResources().getString(R.string.make_request));

        teacherSpinner = view.findViewById(R.id.teacherSpinner);
        options = view.findViewById(R.id.options);
        ok = view.findViewById(R.id.ok);
        cancel = view.findViewById(R.id.cancel);
        requestBody = view.findViewById(R.id.requestBody);
        score = view.findViewById(R.id.score);

        ArrayAdapter<CharSequence> pickTeacheradapter = ArrayAdapter.createFromResource(view.getContext(), R.array.teachers, android.R.layout.simple_spinner_item);
        pickTeacheradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(pickTeacheradapter);
        teacherSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> pickOptionAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.options, android.R.layout.simple_spinner_item);
        pickOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(pickOptionAdapter);
        options.setOnItemSelectedListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        senderImage = sharedPreferences.getString(getString(R.string.intentSenderImage), "");
        currentUserGroup = sharedPreferences.getString(getString(R.string.currentStudentGroup), "");
        currentUserUsername = sharedPreferences.getString(getString(R.string.currentStudentUsername), "");

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

        requestBody.setOnFocusChangeListener(this);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        addedDate = simpleDateFormat.format(new Date());

        return view;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {}

    public void sendRequest(String body, String date, String getter, String image_path,
                        String senderEmail, String senderUsername, int score, String group, String requestID, String option)
    {
        RequestAddingScore request = new RequestAddingScore(false, body, date, getter, image_path, senderEmail,
                                                            senderUsername, score, group, requestID, false, option);
        mDatabaseRef.child(getter).child(currentUser.getEmail().replace(".", "")).child(requestID).setValue(request);
    }

    public void validateFields(){
        isValid = true;
        if(score.getText().toString().trim().isEmpty()){
            score.setError("Field is required");
            score.requestFocus();
            isValid = false;
            YoYo.with(Techniques.Shake).duration(1000).repeat(0).playOn(score);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ok:{
                if(!score.getText().toString().trim().isEmpty()) {
                    String body = requestBody.getText().toString();
                    String date = addedDate;
                    String getter = teacherName;
                    String image_path = senderImage;
                    String senderEmail = currentUser.getEmail();
                    String senderUsername = currentUserUsername;
                    String groupValue = currentUserGroup;
                    int scoreValue = Integer.parseInt(score.getText().toString());
                    String requestID = mDatabaseRef.push().getKey();
                    validateFields();
                    if (isValid) {
                        try {
                            mDatabaseStudentsRef.child(groupValue).child(senderEmail.replace(".", ""))
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(counter == 1) {
                                            try {
                                                int currentLimitScore = Integer.parseInt(dataSnapshot.getValue(Student.class).getLimitScore());
                                                if (currentLimitScore < scoreValue) {
                                                    Toast.makeText(getContext(), "Ваш лимит меньше, чем запрашиваемые очки", Toast.LENGTH_SHORT).show();
                                                    YoYo.with(Techniques.Shake).repeat(0).duration(1000).playOn(dialogLayout);
                                                    counter = 0;
                                                } else if (currentLimitScore >= scoreValue) {
                                                    decreaseLimitScore(groupValue, senderEmail, scoreValue);
                                                    sendRequest(body, date, getter, image_path, senderEmail,
                                                            senderUsername, scoreValue, groupValue, requestID, selectedOption);
                                                    Toast.makeText(getContext(), "Успешно отправленно " + teacherName, Toast.LENGTH_SHORT).show();
                                                    getDialog().dismiss();
                                                    counter = 0;
                                                }
                                            } catch (Exception e) {
                                                Log.d(TAG, "onDataChange: " + e.getMessage());
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError){}
                                });
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), "Check errors", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }else{
                    Toast.makeText(getContext(), "Please insert score", Toast.LENGTH_SHORT).show();
                }

            }
            case R.id.cancel:{
                getDialog().dismiss();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.teacherSpinner:{
                teacherName = parent.getItemAtPosition(position).toString();
                break;
            }
            case R.id.options:{
                selectedOption = parent.getItemAtPosition(position).toString();
                getSelectedOptionScore(selectedOption);
                Toast.makeText(getContext(), selectedOption, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public void getSelectedOptionScore(String selectedOption){
        mDatabaseOptionsRef.child(selectedOption).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    optionValue = dataSnapshot.getValue(Option.class).getOption();
                    optionScore = dataSnapshot.getValue(Option.class).getScore();
                    Log.d(TAG, "onDataChange: " + optionValue + "/" + optionScore);
                    score.setText(Integer.toString(optionScore));
                }catch (Exception e){
                    Log.d(TAG, "onDataChange: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void decreaseLimitScore(String group, String email, int requestedScoreValue){
        mDatabaseStudentsRef.child(group).child(email.replace(".", ""))
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!isDone) {
                    String limitScore = dataSnapshot.getValue(Student.class).getLimitScore();
                    int limitScoreInteger = Integer.parseInt(limitScore);
                    int result = limitScoreInteger - requestedScoreValue;
                    if(result <= 0){
                        dataSnapshot.getRef().child("limitScore").setValue("0");
                    }else{
                        String resultString = Integer.toString(result);
                        dataSnapshot.getRef().child("limitScore").setValue(resultString);
                        Log.d(TAG, "decreaseLimitScore: " + resultString);
                    }
                    isDone = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError){}
        });
    }

}
