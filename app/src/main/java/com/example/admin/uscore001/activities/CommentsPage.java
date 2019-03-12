package com.example.admin.uscore001.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Comment;
import com.example.admin.uscore001.util.CommentListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CommentsPage extends AppCompatActivity implements View.OnClickListener{

    // widgets
    EditText leaveCommentEditText;
    ImageButton sendButton;
    ListView listView;
    ImageView back;
    TextView commentsNumber;

    // Firebase
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Comments");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser mUser = mAuth.getCurrentUser();

    // vars
    String to_whom_sended_email;
    String senderImage;
    String senderUsername;
    ArrayList<Comment> comments = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commnets_activity);

        Bundle bundle = getIntent().getExtras();
        to_whom_sended_email = bundle.getString("to_whom_send_email");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(!mUser.getEmail().contains("teacher")){
            senderImage = sharedPreferences.getString(getString(R.string.intentSenderImage), "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png");
//            senderUsername = sharedPreferences.getString(getString(R.string.currentStudentUsername), "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png");
        }

        leaveCommentEditText = findViewById(R.id.leaveCommentEditText);
        sendButton = findViewById(R.id.sendButton);
        listView = findViewById(R.id.listView);
        commentsNumber = findViewById(R.id.comments_number);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

        leaveCommentEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        if(!mUser.getEmail().contains("teacher")) {
            sendButton.setOnClickListener(this);
            loadSelectedStudentComments(to_whom_sended_email);
        }else{
            Toast.makeText(this, "While you are teacher, you cannot comment (-_-)", Toast.LENGTH_LONG).show();
            loadSelectedStudentComments(to_whom_sended_email);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sendButton:{
                comments.clear();
                commentsNumber.setText("Comments");
                Calendar calendar = Calendar.getInstance();
//                String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
                if(leaveCommentEditText.getText().toString().trim().isEmpty()){
                    leaveCommentEditText.setError("Check it out");
                    leaveCommentEditText.requestFocus();
                }else {
                    Comment comment = new Comment(senderImage, senderUsername, "", leaveCommentEditText.getText().toString(), 0, 0);
                    mDatabaseRef.child(to_whom_sended_email.replace(".", "")).child(mDatabaseRef.push().getKey()).setValue(comment);
                    leaveCommentEditText.setText("");
                }
                break;
            }
            case R.id.back:{
                finish();
                break;
            }
        }
    }

    public void loadSelectedStudentComments(String email){
        comments.clear();
        mDatabaseRef.child(email.replace(".", ""))
        .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Long amountOfComments = dataSnapshot.getChildrenCount();

                String commentNumberText = commentsNumber.getText().toString();
                commentNumberText = commentNumberText + " " + Long.toString(amountOfComments);

                commentsNumber.setText(commentNumberText);

                for(DataSnapshot comment : dataSnapshot.getChildren()){
                    String circleImage = comment.getValue(Comment.class).getSenderImage();
                    String username = comment.getValue(Comment.class).getSenderUsername();
                    String date = comment.getValue(Comment.class).getCurrentDate();
                    String body = comment.getValue(Comment.class).getBody();
                    int likes = comment.getValue(Comment.class).getLikes();
                    int dislikes = 0;
                    if(likes == 0){
                        likes = 0;
                    }
                    if(circleImage.isEmpty()){
                        circleImage = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
                    }
                    Comment new_comment = new Comment(circleImage, username, date, body, likes, dislikes);
                    comments.add(new_comment);
                }
                CommentListAdapter adapter = new CommentListAdapter(comments, CommentsPage.this);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
