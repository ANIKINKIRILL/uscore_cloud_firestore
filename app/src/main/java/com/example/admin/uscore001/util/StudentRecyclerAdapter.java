package com.example.admin.uscore001.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.activities.StudentDetailPage;
import com.example.admin.uscore001.activities.StudentProfile_activity2;
import com.example.admin.uscore001.models.Group;
import com.example.admin.uscore001.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class StudentRecyclerAdapter extends RecyclerView.Adapter<StudentRecyclerAdapter.StudentRecyclerViewHolder> implements Filterable {

    private static final String TAG = "StudentRecyclerAdapter";

    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

    // Firestore
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference groups$DB = firebaseFirestore.collection("GROUPS$DB");

    // vars
    private String groupName;
    private ArrayList<Student> students = new ArrayList<>();
    private ArrayList<Student> studentsCopy;

    public class StudentRecyclerViewHolder extends RecyclerView.ViewHolder{
        ImageView userAvatar;
        TextView username, score, group;
        RelativeLayout cardViewLayout;
        private StudentRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.userAvatar);
            username = itemView.findViewById(R.id.username);
            score = itemView.findViewById(R.id.userScore);
            group = itemView.findViewById(R.id.userGroup);
            cardViewLayout = itemView.findViewById(R.id.cardViewLayout);
        }
    }

    public StudentRecyclerAdapter(ArrayList<Student> students) {
        this.students = students;
        this.studentsCopy = new ArrayList<>(students);
    }

    @NonNull
    @Override
    public StudentRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_recycler_cardview, viewGroup,false);
        StudentRecyclerViewHolder holder = new StudentRecyclerViewHolder(view);
        return holder;
    }

    public void groupNameByGroupID(String groupID, StudentRecyclerViewHolder studentRecyclerViewHolder){
        groups$DB.document(groupID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                groupName = task.getResult().get("name").toString();
                studentRecyclerViewHolder.group.setText(groupName);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull final StudentRecyclerViewHolder studentRecyclerViewHolder, int i) {
        final Student student = students.get(i);
        groupNameByGroupID(student.getGroupID(), studentRecyclerViewHolder);
        final String image_path = student.getImage_path();
        Glide.with(studentRecyclerViewHolder.cardViewLayout.getContext()).load(image_path).into(studentRecyclerViewHolder.userAvatar);
        studentRecyclerViewHolder.username.setText(student.getFirstName()+" "+student.getSecondName());
        studentRecyclerViewHolder.score.setText(student.getScore());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(studentRecyclerViewHolder.cardViewLayout.getContext());
        final String currentStudentUsername = sharedPreferences.getString(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.currentStudentUsername), "");
        String currentStudentGroupID = sharedPreferences.getString(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.currentStudentGroupID), "");


        if((student.getFirstName()+" "+student.getSecondName()).equals(currentStudentUsername) && student.getGroupID().equals(currentStudentGroupID) && !mUser.getEmail().contains("teacher")){
            studentRecyclerViewHolder.cardViewLayout.setBackgroundColor(
                    studentRecyclerViewHolder.cardViewLayout.getResources().getColor(R.color.currentStudentCardViewColor)
            );
        }

        studentRecyclerViewHolder.cardViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studentRecyclerViewHolder.username.getText().toString().equals(currentStudentUsername)){
                    Intent intent = new Intent(studentRecyclerViewHolder.cardViewLayout.getContext(), StudentProfile_activity2.class);
                    studentRecyclerViewHolder.cardViewLayout.getContext().startActivity(intent);
                }else {
                    Intent intent = new Intent(studentRecyclerViewHolder.cardViewLayout.getContext(), StudentDetailPage.class);
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentScore), studentRecyclerViewHolder.score.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentGroup), studentRecyclerViewHolder.group.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentUsername), studentRecyclerViewHolder.username.getText().toString());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentImage), image_path);
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentEmail), student.getEmail());
                    intent.putExtra(studentRecyclerViewHolder.cardViewLayout.getContext().getString(R.string.intentGroupID), student.getGroupID());
                    studentRecyclerViewHolder.cardViewLayout.getContext().startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Student> filteredStudents = new ArrayList<>();
            if(constraint.length() == 0 || constraint == null){
                filteredStudents.addAll(studentsCopy);
            }else{
                String studentName = constraint.toString().toLowerCase().trim();
                for(Student student : studentsCopy){
                    if(student.getFirstName().toLowerCase().trim().contains(studentName) || student.getSecondName().toLowerCase().trim().contains(studentName)){
                        filteredStudents.add(student);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredStudents;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            students.clear();
            students.addAll((ArrayList<Student>)results.values);
            notifyDataSetChanged();
        }
    };

}
