package com.example.admin.uscore001.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.activities.RequestDetailView;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

    // vars
    private ArrayList<RequestAddingScore> requests = new ArrayList<>();
    int counter = 0;

    // Firebase
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Students");
    DatabaseReference mDatabaseRequestRef = FirebaseDatabase.getInstance().getReference("RequestsAddingScore");

    public class RequestsViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView ok, cancel, username, score, group, selectedOption;
        CardView cardViewLayout;
        String teacherName;

        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.image);
            ok = itemView.findViewById(R.id.ok);
            cancel = itemView.findViewById(R.id.cancel);
            username = itemView.findViewById(R.id.username);
            group = itemView.findViewById(R.id.group);
            score = itemView.findViewById(R.id.score);
            selectedOption = itemView.findViewById(R.id.selectedOption);
            cardViewLayout = itemView.findViewById(R.id.cardViewLayout);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            teacherName = sharedPreferences.getString(itemView.getContext().getString(R.string.intentTeacherFullname), "");
        }
    }

    public RequestsAdapter(ArrayList<RequestAddingScore> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.request_item, viewGroup, false);
        RequestsViewHolder holder = new RequestsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestsViewHolder requestsViewHolder, int i) {

        final RequestAddingScore request = requests.get(i);

        String image_path = request.getImage_path();

        if (request.getImage_path().isEmpty()) {
            image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
        }

        ImageLoader.getInstance().displayImage(image_path, requestsViewHolder.circleImageView);
        requestsViewHolder.username.setText(request.getSenderUsername());
        requestsViewHolder.score.setText(Integer.toString(request.getScore()));
        requestsViewHolder.group.setText(request.getGroup());
        requestsViewHolder.selectedOption.setText(request.getOption());

        String email = request.getSenderEmail();

        requestsViewHolder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addScore(Integer.parseInt(requestsViewHolder.score.getText().toString()), email, requestsViewHolder.group.getText().toString(), v);
                requestsViewHolder.cardViewLayout.setVisibility(View.GONE);
                changeAnswerValue(requestsViewHolder.teacherName, email, request.getRequestID());
            }
        });

        requestsViewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestsViewHolder.cardViewLayout.setVisibility(View.GONE);
                cancelScore(requestsViewHolder.teacherName, email, request.getRequestID());
            }
        });

        requestsViewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requestsViewHolder.cardViewLayout.getContext();
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(context.getString(R.string.requestDetailsBody), request.getBody());
                editor.putString(context.getString(R.string.requestDetailsDate), request.getDate());
                editor.putString(context.getString(R.string.requestDetailsGroup), request.getGroup());
                editor.putString(context.getString(R.string.requestDetailsScore), Integer.toString(request.getScore()));
                editor.putString(context.getString(R.string.requestDetailsOption), request.getOption());
                editor.putString(context.getString(R.string.requestDetailsSenderEmail), request.getSenderEmail());
                editor.putString(context.getString(R.string.requestDetailsSenderUsername), request.getSenderUsername());
                editor.putString(context.getString(R.string.requestDetailsUserImage), request.getImage_path());
                editor.apply();
                Intent intent = new Intent(context.getApplicationContext(), RequestDetailView.class);
                context.startActivity(intent);

            }
        });

    }

    public void addScore(final int score, final String studentEmailAddress, final String group, final View view){

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
                        Toast.makeText(view.getContext(), "Successfully added to " + studentEmailAddress, Toast.LENGTH_SHORT).show();
                        counter = 1;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void changeAnswerValue(String teacherName, String studentEmail, String requestID){
        mDatabaseRequestRef.child(teacherName).child(studentEmail.replace(".",""))
                .child(requestID).child("answer").setValue(true);
        requests.clear();
    }

    public void cancelScore(String teacherName, String studentEmail, String requestID){
        mDatabaseRequestRef.child(teacherName).child(studentEmail.replace(".", ""))
                .child(requestID).child("cancel").setValue(true);
        requests.clear();
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
