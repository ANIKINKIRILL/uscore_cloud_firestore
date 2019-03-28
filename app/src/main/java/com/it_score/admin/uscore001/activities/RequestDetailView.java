package com.it_score.admin.uscore001.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.it_score.admin.uscore001.R;

public class RequestDetailView extends AppCompatActivity implements View.OnClickListener{

    // vars
    String body, date, userImage, senderUsername, group, score, option, senderEmail;

    // widgets
    TextView usernameView, groupView, bodyView, optionView, dateView, scoreView, emailView;
    ImageView backArrow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        usernameView = findViewById(R.id.username);
        groupView = findViewById(R.id.group);
        bodyView = findViewById(R.id.body);
        optionView = findViewById(R.id.option);
        dateView = findViewById(R.id.date);
        scoreView = findViewById(R.id.score);
        emailView = findViewById(R.id.email);
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(this);

        getSharedPreferences();

        setDetailValues();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backArrow:{
                finish();
                break;
            }
        }
    }

    public void getSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        body = sharedPreferences.getString(getString(R.string.requestDetailsBody),"");
        date = sharedPreferences.getString(getString(R.string.requestDetailsDate),"");
        userImage = sharedPreferences.getString(getString(R.string.requestDetailsUserImage),"");
        senderUsername = sharedPreferences.getString(getString(R.string.requestDetailsSenderUsername),"");
        group = sharedPreferences.getString(getString(R.string.requestDetailsGroup),"");
        score = sharedPreferences.getString(getString(R.string.requestDetailsScore),"");
        option = sharedPreferences.getString(getString(R.string.requestDetailsOption),"");
        option = sharedPreferences.getString(getString(R.string.requestDetailsOption),"");
        senderEmail = sharedPreferences.getString(getString(R.string.requestDetailsSenderEmail),"");

    }

    public void setDetailValues(){
        usernameView.setText(senderUsername);
        groupView.setText(group);
        bodyView.setText(body);
        optionView.setText(option);
        dateView.setText(date);
        scoreView.setText(score);
        emailView.setText(senderEmail);
    }

}
