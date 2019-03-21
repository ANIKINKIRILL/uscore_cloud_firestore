package com.example.admin.uscore001.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.uscore001.App;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Адаптер для отображения запросов учителя
 */

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestsViewHolder> {

    private static final String TAG = "RequestsAdapter";

    // Переменные
    private ArrayList<RequestAddingScore> requests = new ArrayList<>();
    private String group;
    private String option;
    private DialogFragment dialog;

    // Firestore
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    private CollectionReference options$DB = firebaseFirestore.collection("OPTIONS$DB");
    private CollectionReference reqeusts$DB = firebaseFirestore.collection("REQEUSTS$DB");


    public class RequestsViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView ok, cancel, username, score, group, selectedOption;
        CardView cardViewLayout;
        private final String teacherRequestID;
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

            SharedPreferences sharedPreferences = cardViewLayout.getContext().getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
            teacherRequestID = sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "");
        }
    }

    public RequestsAdapter(ArrayList<RequestAddingScore> requests, DialogFragment dialog) {
        this.requests = requests;
        this.dialog = dialog;
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
                Teacher.addPointsToStudent(
                        new Callback() {
                            @Override
                            public void execute(Object data, String... params) {
                                Log.d(TAG, "execute: was modified");
                                Toast.makeText(App.context, (String) data, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }, request.getSenderID(),
                        request.getScore(),
                        request.getId()
                );
            }
        });

        requestsViewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelScore(
                    requestsViewHolder.teacherRequestID,
                    requestsViewHolder.senderID.getText().toString(),
                    requestsViewHolder.requestID.getText().toString(), v, requestsViewHolder
                );
            }
        });

        requestsViewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = requestsViewHolder.cardViewLayout.getContext();
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
                alertDialog.setPositiveButton("Хорошо", positiveButtonOnClickListener);
                alertDialog.show();
            }
        });
    }

    private DialogInterface.OnClickListener positiveButtonOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    /**
     * Отклонить запрос на добавление очков к ученику
     * @param teacherRequestID  requestId учителя
     * @param studentID         id ученика
     * @param id                id запроса
     * @param view              view
     */

    private void cancelScore(String teacherRequestID, String studentID, String id, View view, RequestsViewHolder requestsViewHolder){
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
                        dialog.dismiss();
                    }else{
                        Log.d(TAG, "request canceled field han not been changed: false die to" + task.getException().getMessage());
                    }
                }
            });
    }

    /**
     * Найти название группы и название опции
     * @param groupID       id группы
     * @param optionID      id опции
     * @param requestsViewHolder    viewholder
     */

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

    /**
     * Callback, который вернется после добавления очков к ученику
     */

    private Callback mAddPointsToStudent = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            Log.d(TAG, "execute: was modified");
            Toast.makeText(App.context, (String) data, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public int getItemCount() {
        return requests.size();
    }
}
