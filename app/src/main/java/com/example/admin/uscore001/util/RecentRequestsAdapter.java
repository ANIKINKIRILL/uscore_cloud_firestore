package com.example.admin.uscore001.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import com.example.admin.uscore001.App;
import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.Settings;
import com.example.admin.uscore001.models.RecentRequestItem;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Адаптер для запросов
 */

public class RecentRequestsAdapter extends RecyclerView.Adapter<RecentRequestsAdapter.RequestsViewHolder> {

    private static final String TAG = "RecentRequestsAdapter";
    // Переменные
    private ArrayList<RequestAddingScore> requests = new ArrayList<>();
    private String group;
    private String option;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    private CollectionReference options$DB = firebaseFirestore.collection("OPTIONS$DB");
    private CollectionReference reqeusts$DB = firebaseFirestore.collection("REQEUSTS$DB");

    // Постоянные переменные
    public static final String STUDENT_STATUS = "y1igExymzKFaV3BU8zH8";
    public static final String TEACHER_STATUS = "PGIg1vm8SrHN6YLeN0TD";

    public class RequestsViewHolder extends RecyclerView.ViewHolder{
        TextView date, score, result, teacherName;
        CardView cardViewlayout;
        public RequestsViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            score = itemView.findViewById(R.id.score);
            result = itemView.findViewById(R.id.result);
            teacherName = itemView.findViewById(R.id.teacherName);
            cardViewlayout = itemView.findViewById(R.id.cardViewLayout);
        }
    }

    public RecentRequestsAdapter(ArrayList<RequestAddingScore> requests) {
        this.requests = requests;
    }

    @NonNull
    @Override
    public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_request_item, viewGroup, false);
        RequestsViewHolder holder = new RequestsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestsViewHolder requestsViewHolder, int i) {

        Context context = requestsViewHolder.cardViewlayout.getContext();

        final RequestAddingScore request = requests.get(i);

        findGroupOptionByID(request.getGroupID(), request.getOptionID(), requestsViewHolder);

        requestsViewHolder.date.setText(request.getDate());
        requestsViewHolder.score.setText(Integer.toString(request.getScore()));
        if(request.isAnswered() && !request.isCanceled()){
           requestsViewHolder.result.setText("Принята");
        }else if(request.isCanceled() && !request.isAnswered()){
           requestsViewHolder.result.setText("Отклонена");
        }else if(!request.isCanceled() && !request.isAnswered()){
            requestsViewHolder.result.setText("В процессе...");
        }

        if(Settings.getStatus().equals(STUDENT_STATUS)){                // Ученик
            if(request.getGetter().length() > 15){
                requestsViewHolder.teacherName.setText(request.getGetter().substring(0,15)+"...");
            }else {
                requestsViewHolder.teacherName.setText(request.getGetter());
            }
        }else if(Settings.getStatus().equals(TEACHER_STATUS)){         // Учитель
            if((request.getFirstName()+request.getSecondName()).length() > 15){
                requestsViewHolder.teacherName.setText((request.getFirstName()+ " " + request.getSecondName()).substring(0,15)+"...");
            }else {
                requestsViewHolder.teacherName.setText(request.getFirstName() + " " + request.getSecondName());
            }
        }


       if(request.isAnswered()){
           requestsViewHolder.cardViewlayout.setBackgroundColor(requestsViewHolder.cardViewlayout.
                                                getResources().getColor(R.color.addedColor));
       }

       if(request.isCanceled()){
           requestsViewHolder.cardViewlayout.setBackgroundColor(requestsViewHolder.cardViewlayout.
                   getResources().getColor(R.color.canceledColor));
       }

       if(!request.isCanceled() && !request.isAnswered()){
           requestsViewHolder.cardViewlayout.setBackgroundColor(requestsViewHolder.cardViewlayout.
                   getResources().getColor(R.color.inProcessColor));
       }

       if(Settings.getStatus().equals(TEACHER_STATUS)) {        // Учитель
           requestsViewHolder.cardViewlayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String requestStatus = "";
                   if(request.isAnswered()){
                       requestStatus = "Принята";
                   }else if(request.isCanceled()){
                       requestStatus = "Отклонена";
                   }else if(!request.isCanceled() && !request.isAnswered()){
                       requestStatus = "В процессе";
                   }
                   AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                   alertDialog.setMessage("Ученик: " + request.getFirstName() + request.getLastName() + "\n" +
                           "Группа:" + group + "\n" +
                           "Запрашиваемые очки: " + request.getScore() + "\n" +
                           "Дата: " + request.getDate() + "\n" +
                           "Сообщение ученика: " + request.getBody() + "\n" +
                           "Причина: " + option + "\n" +
                            "Статус заявки: " + requestStatus);
                   if (!request.isAnswered() && !request.isCanceled()) {
                       alertDialog.setTitle("Хотите добавить ученику очки?");
                       alertDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               /*
                               addScore(request.getScore(),
                                       request.getSenderID(),
                                       request.getId()
                               );
                               dialog.dismiss();
                               */
                               Toast.makeText(context, "В скором времени эта функция будет доступна, пока можете принять запрос на главном экране", Toast.LENGTH_SHORT).show();
                               dialog.dismiss();
                           }
                       }).setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               /*
                               cancelScore(
                                       request.getRequestID(),
                                       request.getSenderID(),
                                       request.getId(),
                                       requestsViewHolder.cardViewlayout.getContext(),
                                       requestsViewHolder
                               );
                               dialog.dismiss();
                               */
                               Toast.makeText(context, "В скором времени эта функция будет доступна, пока можете отклонить запрос на главном экране", Toast.LENGTH_SHORT).show();
                               dialog.dismiss();
                           }
                       });
                   } else {
                       alertDialog.setTitle("Подробная информация о запросе").setPositiveButton("Хорошо", onPositiveButtonOnClickListener);
                   }
                   alertDialog.show();
               }
           });
       }else if(Settings.getStatus().equals(STUDENT_STATUS)){       // Ученик
           requestsViewHolder.cardViewlayout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String requestStatus = "";
                   if(request.isAnswered()){
                       requestStatus = "Принята";
                   }else if(request.isCanceled()){
                       requestStatus = "Отклонена";
                   }else if(!request.isCanceled() && !request.isAnswered()){
                       requestStatus = "В процессе";
                   }
                   AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                   alertDialog.setMessage("Ученик: " + request.getFirstName() + request.getLastName() + "\n" +
                           "Группа: " + group + "\n" +
                           "Запрашиваемые очки: " + request.getScore() + "\n" +
                           "Дата: " + request.getDate() + "\n" +
                           "Сообщение ученика: " + request.getBody() + "\n" +
                           "Причина: " + option + "\n" +
                            "Статус заяки: " + requestStatus);

                   alertDialog.setTitle("Подробная информация о запросе");
                   alertDialog.setPositiveButton("Хорошо", onPositiveButtonOnClickListener);
                   alertDialog.show();
               }
           });
       }
    }

    DialogInterface.OnClickListener onPositiveButtonOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    };

    /**
     * Добавить очки
     * @param score
     * @param studentID
     * @param requestID
     */

    private void addScore(final int score, final String studentID, String requestID) {
        Teacher.addPointsToStudent(mAddPointsToStudentCallback, studentID, score, requestID);
    }

    private Callback mAddPointsToStudentCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            String message = (String) data;
            Toast.makeText(App.context, message, Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * Отменить добавление очков/Отменить запрос
     */

    private void cancelScore(String teacherRequestID, String studentID, String id, Context context, RequestsViewHolder requestsViewHolder){
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
                        Toast.makeText(context, "Вы отклонили запрос", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    /**
     * Найти группу и опцию поощрения
     * @param groupID   id группы
     * @param optionID  id опции
     * @param requestsViewHolder    viewholder
     */

    private void findGroupOptionByID(String groupID, String optionID, RequestsViewHolder requestsViewHolder){
        groups$DB.document(groupID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    group = task.getResult().get("name").toString();
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
                    }
                }
            });
    }


}
