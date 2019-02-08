package com.example.admin.uscore001.util;

import android.content.SharedPreferences;
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
import com.example.admin.uscore001.models.RecentRequestItem;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecentRequestsAdapter extends RecyclerView.Adapter<RecentRequestsAdapter.RequestsViewHolder> {

    // vars
    private ArrayList<RecentRequestItem> requests = new ArrayList<>();
    int counter = 0;

    // Firebase
    DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("Students");
    DatabaseReference mDatabaseRequestRef = FirebaseDatabase.getInstance().getReference("RequestsAddingScore");

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

    public RecentRequestsAdapter(ArrayList<RecentRequestItem> requests) {
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

        final RecentRequestItem request = requests.get(i);

       requestsViewHolder.date.setText(request.getDate());
       requestsViewHolder.score.setText(request.getScore());
       requestsViewHolder.result.setText(request.getResult());


       if(request.getTeacher().length() > 15){
           requestsViewHolder.teacherName.setText(request.getTeacher().substring(0,15)+"...");
       }else {
           requestsViewHolder.teacherName.setText(request.getTeacher());
       }

       if(requestsViewHolder.result.getText().equals("Added")){
           requestsViewHolder.cardViewlayout.setBackgroundColor(requestsViewHolder.cardViewlayout.
                                                getResources().getColor(R.color.addedColor));
       }

       if(requestsViewHolder.result.getText().equals("Canceled")){
           requestsViewHolder.cardViewlayout.setBackgroundColor(requestsViewHolder.cardViewlayout.
                   getResources().getColor(R.color.canceledColor));
       }

       if(requestsViewHolder.result.getText().equals("In Process...")){
           requestsViewHolder.cardViewlayout.setBackgroundColor(requestsViewHolder.cardViewlayout.
                   getResources().getColor(R.color.inProcessColor));
       }

    }


    @Override
    public int getItemCount() {
        return requests.size();
    }
}
