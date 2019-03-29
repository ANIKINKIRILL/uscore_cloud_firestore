package com.it_score.admin.uscore001.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Group;
import com.it_score.admin.uscore001.models.Teacher;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * Адаптер для отоюражения списка групп
 */

public class GroupListRecyclerViewAdapter extends RecyclerView.Adapter<GroupListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "GroupListRecyclerViewAd";

    // Переменные
    private ArrayList<Group> groups = new ArrayList<>();

    // Firebase
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    CollectionReference TEACHERS$DB = firebaseFirestore.collection("TEACHERS$DB");
    CollectionReference GROUPS$DB = firebaseFirestore.collection("GROUPS$DB");

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView teacherName, groupName;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherName = itemView.findViewById(R.id.teacherName);
            groupName = itemView.findViewById(R.id.groupName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public GroupListRecyclerViewAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Group group = groups.get(i);
        String groupID = group.getId();
        String groupName = group.getName();
        String groupTeacherId = group.getTeacherID();
        if(!groupTeacherId.trim().isEmpty()) {
            getTeacherData(groupTeacherId, viewHolder);
        }else{
            viewHolder.teacherName.setText("Нет");
        }
        viewHolder.groupName.setText(groupName);

        /*
                    ОБРАБОТКА ДОЛГОГО НАЖАТИЯ НА ITEM, ЧТОБЫ УДАЛИТЬ ГРУППУ
         */

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                GROUPS$DB.document(groupID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(viewHolder.cardView.getContext(), "Группа " + groupName + " удалена", Toast.LENGTH_SHORT).show();
                            notifyDataSetChanged();
                        }
                    }
                });
                return true;
            }
        });


    }

    /**
     * Получить данные классного руководителя
     */

    public void getTeacherData(String teacherID, ViewHolder viewHolder) {
        TEACHERS$DB.document(teacherID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                try {
                    Teacher teacher = documentSnapshot.toObject(Teacher.class);
                    String teacherFirstName = teacher.getFirstName();
                    String teacherSecondName = teacher.getSecondName();
                    String teacherLastName = teacher.getLastName();
                    String resultFullName = String.format("%s.%s.%s", teacherSecondName, teacherFirstName.substring(0,1), teacherLastName.substring(0,1));
                    viewHolder.teacherName.setText(resultFullName);
                }catch (Exception e1){
                    Log.d(TAG, "getTeacherData: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
}
