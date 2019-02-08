package com.example.admin.uscore001.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.util.RequestsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class ShowRequestDialog extends DialogFragment implements View.OnClickListener{

    // widgets
    RecyclerView recyclerView;
    ImageView closeImageView;
    // vars
    ArrayList<RequestAddingScore> requests = new ArrayList<>();

    // Firebase
    DatabaseReference mDatabaseRequestRef = FirebaseDatabase.getInstance().getReference("RequestsAddingScore");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_request_dialog, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        closeImageView = view.findViewById(R.id.close);

        closeImageView.setOnClickListener(this);

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(getContext()).build();
        ImageLoader.getInstance().init(configuration);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String teacher = sharedPreferences.getString(getString(R.string.intentTeacherFullname), "");

        loadRequest(teacher);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close:{
                getDialog().dismiss();
                break;
            }
        }
    }

    public void loadRequest(final String teacher){
        requests.clear();
        mDatabaseRequestRef.child(teacher).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot students : dataSnapshot.getChildren()){
                    for(DataSnapshot request : students.getChildren()){
                        if( !request.getValue(RequestAddingScore.class).isAnswer() // is not answered
                                &&
                            !request.getValue(RequestAddingScore.class).isCancel() // is not canceled
                            ){
                                String senderUsername = request.getValue(RequestAddingScore.class).getSenderUsername();
                                int score = request.getValue(RequestAddingScore.class).getScore();
                                String group = request.getValue(RequestAddingScore.class).getGroup();
                                String body = request.getValue(RequestAddingScore.class).getBody();
                                String date = request.getValue(RequestAddingScore.class).getDate();
                                String senderEmail = request.getValue(RequestAddingScore.class).getSenderEmail();
                                String requestID = request.getValue(RequestAddingScore.class).getRequestID();
                                String senderImage = request.getValue(RequestAddingScore.class).getImage_path();
                                String option = request.getValue(RequestAddingScore.class).getOption();
                                RequestAddingScore requestClass = new RequestAddingScore(false, body, date, teacher, senderImage, senderEmail,
                                        senderUsername, score, group, requestID, false, option);
                                requests.add(requestClass);
                            }
                    }
                }

                RequestsAdapter adapter = new RequestsAdapter(requests);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(adapter);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
