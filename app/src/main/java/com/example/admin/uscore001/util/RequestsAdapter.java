package com.example.admin.uscore001.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.activities.RequestDetailView;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

    private static final String TAG = "RequestsAdapter";

    // vars
    private ArrayList<RequestAddingScore> requests = new ArrayList<>();
    private String group;
    private String option;
    private int counter = 0;

    // Firebase
    private DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Students");
    private DatabaseReference mDatabaseRequestRef = FirebaseDatabase.getInstance().getReference("RequestsAddingScore");

    // Firestore
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    private CollectionReference options$DB = firebaseFirestore.collection("OPTIONS$DB");
    private CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    private CollectionReference reqeusts$DB = firebaseFirestore.collection("REQEUSTS$DB");


    public class RequestsViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView ok, cancel, username, score, group, selectedOption;
        CardView cardViewLayout;
        String teacherName;
        private final String teacherRequestID;
//        String senderID = "";
//        String requestID = "";
        TextView senderID, requestID;

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
            senderID = itemView.findViewById(R.id.senderID);
            requestID = itemView.findViewById(R.id.requestID);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
//            teacherName = sharedPreferences.getString(itemView.getContext().getString(R.string.intentTeacherFullname), "");
            teacherRequestID = sharedPreferences.getString("intentTeacherRequestID", "");
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

    private void findGroupOptionByID(String groupID, String optionID, RequestsViewHolder requestsViewHolder){
        groups$DB.document(groupID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    group = task.getResult().get("name").toString();
                    Log.d(TAG, "onComplete: " + group);
                    requestsViewHolder.group.setText(group);

                }
            }
        });
        options$DB
            .document("a31J0nT0lYTRmvyp7T8F")
            .collection("options")
            .document(optionID)
            .get()
            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    option = task.getResult().get("name").toString();
                    Log.d(TAG, "onComplete: " + option);
                    requestsViewHolder.selectedOption.setText(option);
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestsViewHolder requestsViewHolder, int i) {

        final RequestAddingScore request = requests.get(i);

        String image_path = request.getImage_path();

        if (request.getImage_path().isEmpty()) {
            image_path = "https://cdn2.iconfinder.com/data/icons/male-users-2/512/2-512.png";
        }
        findGroupOptionByID(request.getGroupID(), request.getOptionID(), requestsViewHolder);
        GlideApp.with(requestsViewHolder.cardViewLayout.getContext()).load(image_path).centerCrop().into(requestsViewHolder.circleImageView);
        requestsViewHolder.username.setText(request.getFirstName() + " " + request.getSecondName());
        requestsViewHolder.score.setText(Integer.toString(request.getScore()));
        requestsViewHolder.group.setText(group);
        requestsViewHolder.selectedOption.setText(option);

        requestsViewHolder.requestID.setText(request.getId());
        requestsViewHolder.senderID.setText(request.getSenderID());

        requestsViewHolder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counter = 0;
                addScore(Integer.parseInt(requestsViewHolder.score.getText().toString()),
                        requestsViewHolder.senderID.getText().toString(),
                        v,
                        requestsViewHolder.teacherRequestID,
                        requestsViewHolder.requestID.getText().toString());
                requestsViewHolder.cardViewLayout.setVisibility(View.GONE);
                requestsViewHolder.cardViewLayout.setVisibility(View.GONE);
            }
        });

        requestsViewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelScore(requestsViewHolder.teacherRequestID,
                        requestsViewHolder.senderID.getText().toString(),
                        requestsViewHolder.requestID.getText().toString(), v);
                requestsViewHolder.cardViewLayout.setVisibility(View.GONE);
                requestsViewHolder.cardViewLayout.setVisibility(View.GONE);
            }
        });

        requestsViewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requestsViewHolder.cardViewLayout.getContext();
                /*
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(context.getString(R.string.requestDetailsBody), request.getBody());
                editor.putString(context.getString(R.string.requestDetailsDate), request.getDate());
                editor.putString(context.getString(R.string.requestDetailsGroup), group);
                editor.putString(context.getString(R.string.requestDetailsScore), Integer.toString(request.getScore()));
                editor.putString(context.getString(R.string.requestDetailsOption), option);
                editor.putString(context.getString(R.string.requestDetailsSenderEmail), request.getSenderEmail());
                editor.putString(context.getString(R.string.requestDetailsSenderUsername), request.getFirstName() + " " + request.getSecondName());
                editor.putString(context.getString(R.string.requestDetailsUserImage), request.getImage_path());
                editor.apply();
                Intent intent = new Intent(context.getApplicationContext(), RequestDetailView.class);
                context.startActivity(intent);
                */

                String requestStatus = "";
                if(request.isAnswered()){
                    requestStatus = "Принята";
                }else if(request.isCanceled()){
                    requestStatus = "Отклонена";
                }else if(!request.isCanceled() && !request.isAnswered()){
                    requestStatus = "В процессе";
                }

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Подробная информация о запросе");
                String requestBodyText = request.getBody();
                if(requestBodyText.trim().isEmpty()){
                    requestBodyText = "доп. текст отсутствует";
                }
                alertDialog.setMessage("Ученик: " + request.getFirstName() + " " + request.getSecondName() + "\n" +
                                        "Класс: " + group + "\n" +
                                        "Запрошиваемые очки: " + request.getScore() + "\n" +
                                        "Причина: " + option + "\n" +
                                        "Сообщение ученика: " + requestBodyText + "\n" +
                                        "Дата: " + request.getDate() + "\n" +
                                        "Статус заявки: " + requestStatus);
                alertDialog.setPositiveButton("Хорошо", posititveButtonOnClickListener);
                alertDialog.show();
            }
        });
    }

    DialogInterface.OnClickListener posititveButtonOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    public void addScore(final int score, final String studentID,final View view, String teacherRequestID, String requestID){
        students$DB.document(studentID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(counter != 1){
                    Student selectedStudent = task.getResult().toObject(Student.class);
                    String old_score = selectedStudent.getScore();
                    int old_score_int = Integer.parseInt(old_score);
                    int result = old_score_int + score;
                    String result_str = Integer.toString(result);
                    students$DB.document(studentID).update("score", result_str);
                    reqeusts$DB.document(teacherRequestID).collection("STUDENTS").document(studentID).collection("REQUESTS")
                            .document(requestID).update("answered", true);
                    Toast.makeText(view.getContext(),
                            "Успешно добавленно к " + selectedStudent.getFirstName() + " " + selectedStudent.getSecondName(),
                            Toast.LENGTH_SHORT).show();
                    counter = 1;
                }
            }
        });

    }

    public void cancelScore(String teacherRequestID, String studentID, String id, View view){
        reqeusts$DB
                .document(teacherRequestID)
                .collection("STUDENTS")
                .document(studentID)
                .collection("REQUESTS")
                .document(id)
                .update("canceled", true)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "request canceled field just has been changed: true");
                            Toast.makeText(view.getContext(), "Вы отклонили запрос", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d(TAG, "request canceled field han not been changed: false die to" + task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
