package com.example.admin.uscore001.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Option;
import com.example.admin.uscore001.models.Penalty;
import com.example.admin.uscore001.models.Student;
import com.example.admin.uscore001.models.Teacher;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class PenaltyRecyclerViewAdapter extends RecyclerView.Adapter<PenaltyRecyclerViewAdapter.PenaltyRecyclerViewHolder> {

    // vars
    private ArrayList<Penalty> penaltyArrayList = new ArrayList<>();
    private boolean isTeacher;
    private boolean isStudent;

    // Firebase
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference options$db = firebaseFirestore.collection("OPTIONS$DB");
    private CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");
    private CollectionReference students$DB = firebaseFirestore.collection("STUDENTS$DB");
    private CollectionReference teachers$DB = firebaseFirestore.collection("TEACHERS$DB");

    public class PenaltyRecyclerViewHolder extends RecyclerView.ViewHolder{
        TextView date, score, username, option,group;
        CardView cardViewlayout;
        View  dividerLine;
        public PenaltyRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            score = itemView.findViewById(R.id.score);
            username = itemView.findViewById(R.id.teacherName);
            cardViewlayout = itemView.findViewById(R.id.cardViewLayout);
            dividerLine = itemView.findViewById(R.id.dividerLine);
            option = itemView.findViewById(R.id.option);
            group = itemView.findViewById(R.id.group);
        }
    }

    public PenaltyRecyclerViewAdapter(ArrayList<Penalty> penaltyArrayList, boolean isTeacher, boolean isStudent) {
        this.penaltyArrayList = penaltyArrayList;
        this.isTeacher = isTeacher;
        this.isStudent = isStudent;
    }

    @NonNull
    @Override
    public PenaltyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recent_request_item, viewGroup, false);
        PenaltyRecyclerViewHolder viewHolder = new PenaltyRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PenaltyRecyclerViewHolder penaltyRecyclerViewHolder, int i) {
        Penalty penalty = penaltyArrayList.get(i);
        String date = penalty.getDate();
        String groupID = penalty.getGroupID();
        String optionID = penalty.getOptionID();
        String score = penalty.getScore();
        String studentID = penalty.getStudentID();
        String teacherID = penalty.getTeacherID();

        // Установка divider
        if(penaltyArrayList.indexOf(penalty)+1!=penaltyArrayList.size()) {
            penaltyRecyclerViewHolder.dividerLine.setBackgroundColor(penaltyRecyclerViewHolder.cardViewlayout.
                    getResources().getColor(R.color.penaltyColor));
        }

        if(isTeacher){
            setData(groupID, date, optionID, score, studentID, teacherID, penaltyRecyclerViewHolder, true, false);
        }

        if(isStudent){
            setData(groupID, date, optionID, score, studentID, teacherID, penaltyRecyclerViewHolder, false, true);
        }

        /*
         * Нажатие на view
         */

        penaltyRecyclerViewHolder.cardViewlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = penalty.getDate();
                String option = penaltyRecyclerViewHolder.option.getText().toString();
                String score = penalty.getScore();
                String userName = penaltyRecyclerViewHolder.username.getText().toString();
                String group = penaltyRecyclerViewHolder.group.getText().toString();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(penaltyRecyclerViewHolder.cardViewlayout.getContext());
                alertDialog.setTitle("Подробная информация о штрафе").setPositiveButton("Хорошо", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                if(isStudent) {
                    alertDialog.setMessage("Учитель: " + userName + "\n" +
                            "Причина штрафа: " + option + "\n" +
                            "Штраф: " + score + "\n" +
                            "Дата: " + date);
                }
                if(isTeacher) {
                    alertDialog.setMessage("Ученик: " + userName + "\n" +
                            "Группа: " + group + "\n" +
                            "Причина штрафа: " + option + "\n" +
                            "Штраф: " + score + "\n" +
                            "Дата: " + date);
                }
                alertDialog.show();
            }
        });




    }

    @Override
    public int getItemCount() {
        return penaltyArrayList.size();
    }

    private void setData(String groupID, String date, String optionID, String score, String studentID,
                                       String teacherID, PenaltyRecyclerViewHolder penaltyRecyclerViewHolder,
                                       boolean isTeacher, boolean isStudent){
        options$db.document("6oemB2Fxo1hyrWrrNQ07").collection("options").document(optionID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Option option = documentSnapshot.toObject(Option.class);
                penaltyRecyclerViewHolder.option.setText(option.getName());
            }
        });
        penaltyRecyclerViewHolder.score.setText("Очки: " + score);
        penaltyRecyclerViewHolder.date.setText("Дата: " + date);

        if(isTeacher) {
            students$DB.document(studentID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    Student student = documentSnapshot.toObject(Student.class);
                    penaltyRecyclerViewHolder.username.setText(student.getFirstName() + " " + student.getSecondName());
                }
            });
            groups$DB.document(groupID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    penaltyRecyclerViewHolder.group.setText(documentSnapshot.toObject(Group.class).getName());
                }
            });
        }

        if(isStudent){
            teachers$DB.document(teacherID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    Teacher teacher = documentSnapshot.toObject(Teacher.class);
                    penaltyRecyclerViewHolder.username.setText(teacher.getFirstName() + " " + teacher.getLastName());
                }
            });
        }

    }

}
