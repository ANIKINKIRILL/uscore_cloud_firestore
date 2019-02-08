package com.example.admin.uscore001.dialogs;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RecentRequestItem;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.util.RecentRequestsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.Spinner;

import java.util.ArrayList;

public class FilterRequestsDialog extends DialogFragment implements Spinner.OnItemSelectedListener {

    // vars
    String selectedTeacher;
//    ArrayList<RecentRequestItem> filteredRequestItems = new ArrayList<>();

    // widgets
    com.rey.material.widget.Spinner spinner;

    // Firebase
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseRef = mDatabase.getReference("RequestsAddingScore");
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter_reqeust, container, false);

        spinner = view.findViewById(R.id.spinner);

        getDialog().setTitle("Pick a teacher");

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.teachersWithAllOption, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(Spinner parent, View view, int position, long id) {
        selectedTeacher = parent.getSelectedItem().toString();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.selectedTeacher), selectedTeacher);
        editor.apply();
        Toast.makeText(getContext(), "Selected: "+selectedTeacher, Toast.LENGTH_SHORT).show();
        getDialog().dismiss();
        getDialog().getOwnerActivity().recreate();
    }



//    public void filterSelectedTeacherRequests(final String selectedTeacherName){
//        mDatabaseRef.child(selectedTeacherName).child(currentUser.getEmail().replace(".",""))
//        .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot request : dataSnapshot.getChildren()){
//                    boolean answer = request.getValue(RequestAddingScore.class).isAnswer();
//                    boolean cancel = request.getValue(RequestAddingScore.class).isCancel();
//                    String date = request.getValue(RequestAddingScore.class).getDate();
//                    String score = Integer.toString(request.getValue(RequestAddingScore.class).getScore());
//                    String result = "";
//                    if(answer && !cancel){
//                        result = "Added";
//                    }else if(!answer && cancel){
//                        result = "Canceled";
//                    }else{
//                        result = "In Process...";
//                    }
//                    RecentRequestItem recentRequestItem = new RecentRequestItem(score, date, result, selectedTeacherName);
//                    filteredRequestItems.add(recentRequestItem);
//                }
//
//                RecentRequestsAdapter adapter = new RecentRequestsAdapter(filteredRequestItems);
//                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

}
