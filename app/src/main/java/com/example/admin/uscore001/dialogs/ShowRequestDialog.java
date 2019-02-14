package com.example.admin.uscore001.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.util.RequestsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

public class ShowRequestDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "ShowRequestDialog";

    // widgets
    RecyclerView recyclerView;
    ImageView closeImageView;

    // vars
    ArrayList<RequestAddingScore> requests = new ArrayList<>();

    // Firestore
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference requests$DB = firebaseFirestore.collection("REQEUSTS$DB");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_request_dialog, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        closeImageView = view.findViewById(R.id.close);

        closeImageView.setOnClickListener(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String teacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");
        Log.d(TAG, "onCreateView: " + teacherRequestID);

        requests.clear();

        loadRequest(teacherRequestID);

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

    public void loadRequest(final String teacherRequestID){
            requests$DB
                .document(teacherRequestID)
                .collection("STUDENTS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                documentSnapshot
                                        .getReference()
                                        .collection("REQUESTS")
                                        .whereEqualTo("answered", false)
                                        .whereEqualTo("canceled", false)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for(DocumentSnapshot documents : task.getResult().getDocuments()){
                                                    RequestAddingScore request = documents.toObject(RequestAddingScore.class);
                                                    String id = request.getId();
                                                    String body = request.getBody();
                                                    String date = request.getDate();
                                                    String getter = request.getGetter();
                                                    String image_path = request.getImage_path();
                                                    String senderEmail = request.getSenderEmail();
                                                    String firstName = request.getFirstName();
                                                    String secondName = request.getSecondName();
                                                    String lastName = request.getLastName();
                                                    int score = request.getScore();
                                                    String groupID = request.getGroupID();
                                                    String requestID = request.getRequestID();
                                                    String optionID = request.getOptionID();
                                                    boolean answered = request.isAnswered();
                                                    boolean canceled = request.isCanceled();
                                                    String senderID = request.getSenderID();
                                                    RequestAddingScore requestClass = new RequestAddingScore(
                                                            id,
                                                            body,
                                                            date,
                                                            getter,
                                                            image_path,
                                                            senderEmail,
                                                            firstName,
                                                            secondName,
                                                            lastName,
                                                            score,
                                                            groupID,
                                                            requestID,
                                                            optionID,
                                                            answered,
                                                            canceled,
                                                            senderID
                                                    );
                                                    requests.add(requestClass);
                                                }
                                                Log.d(TAG, "all requests that in in process: " + requests.size());
                                                RequestsAdapter adapter = new RequestsAdapter(requests);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                                recyclerView.setAdapter(adapter);
                                            }
                                        });
                            }
                        }
                    }
                });



//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
//                            documentSnapshot
//                                .getReference()
//                                .collection("REQUESTS")
//                                .whereEqualTo("answered", false)
//                                .whereEqualTo("canceled", false)
//                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                                        for(DocumentSnapshot documents : queryDocumentSnapshots.getDocuments()){
//                                            RequestAddingScore request = documents.toObject(RequestAddingScore.class);
//                                            String id = request.getId();
//                                            String body = request.getBody();
//                                            String date = request.getDate();
//                                            String getter = request.getGetter();
//                                            String image_path = request.getImage_path();
//                                            String senderEmail = request.getSenderEmail();
//                                            String firstName = request.getFirstName();
//                                            String secondName = request.getSecondName();
//                                            String lastName = request.getLastName();
//                                            int score = request.getScore();
//                                            String groupID = request.getGroupID();
//                                            String requestID = request.getRequestID();
//                                            String optionID = request.getOptionID();
//                                            boolean answered = request.isAnswered();
//                                            boolean canceled = request.isCanceled();
//                                            String senderID = request.getSenderID();
//                                            RequestAddingScore requestClass = new RequestAddingScore(
//                                                    id,
//                                                    body,
//                                                    date,
//                                                    getter,
//                                                    image_path,
//                                                    senderEmail,
//                                                    firstName,
//                                                    secondName,
//                                                    lastName,
//                                                    score,
//                                                    groupID,
//                                                    requestID,
//                                                    optionID,
//                                                    answered,
//                                                    canceled,
//                                                    senderID
//                                            );
//                                            requests.add(requestClass);
//                                        }
//                                        RequestsAdapter adapter = new RequestsAdapter(requests);
//                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                                        recyclerView.setAdapter(adapter);
//                                    }
//                                });
//                        }
//                    }
//                });

    }

}
