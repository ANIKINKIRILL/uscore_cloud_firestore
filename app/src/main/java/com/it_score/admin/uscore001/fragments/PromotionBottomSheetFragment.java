package com.it_score.admin.uscore001.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Option;
import com.it_score.admin.uscore001.util.RulesListViewAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PromotionBottomSheetFragment extends Fragment {

    private static final String TAG = "PromotionBottomSheetFra";

    // widgets
    private ListView listView;

    // vars
    private RulesListViewAdapter adapter;
    private ArrayList<Option> options = new ArrayList<>();

    // Firebase
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference optionsRef = db.getReference("Options");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.promotion_fragment, container, false);
        init(view);
        loadAllOptions();
        return view;
    }

    private void init(View view){
        listView = view.findViewById(R.id.listView);
    }

    private void loadAllOptions(){
        options.clear();
        try {
            optionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange: loading options");
                    for (DataSnapshot option : dataSnapshot.getChildren()) {
//                        String optionValue = option.getValue(Option.class).getOption();
//                        int scoreValue = option.getValue(Option.class).getScore();
//                        Option optionClass = new Option(scoreValue, optionValue);
//                        options.add(optionClass);
                    }
                    adapter = new RulesListViewAdapter(options, getContext());
                    listView.setAdapter(adapter);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "onCancelled: loading proccess is canceled");
                }
            });
        }catch (Exception e){
            Log.d(TAG, "loadAllOptions: " + e.getMessage());
        }
    }


}
