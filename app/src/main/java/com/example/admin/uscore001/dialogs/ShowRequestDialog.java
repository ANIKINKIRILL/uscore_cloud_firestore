package com.example.admin.uscore001.dialogs;

import android.content.Context;
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

import com.example.admin.uscore001.Callback;
import com.example.admin.uscore001.R;
import com.example.admin.uscore001.models.RequestAddingScore;
import com.example.admin.uscore001.models.Teacher;
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

/**
 * Диалоговое окно с запросами у учителя (Пользователь -> учитель)
 */

public class ShowRequestDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "ShowRequestDialog";

    // Виджеты
    RecyclerView recyclerView;
    ImageView closeImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_request_dialog, container, true);
        init(view);
        getRequests();
        return view;
    }

    /**
     * Инициализация виджетов
     */

    private void init(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        closeImageView = view.findViewById(R.id.close);
        closeImageView.setOnClickListener(this);
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

    /**
     * Выгрузка запросов учителя
     */

    private void getRequests(){
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(Teacher.TEACHER_DATA, Context.MODE_PRIVATE);
        String teacherRequestID = sharedPreferences.getString(Teacher.TEACHER_REQUEST_ID, "");
        Teacher.getTeacherNewRequests(mGetRequestsCallback, teacherRequestID);
    }

    private Callback mGetRequestsCallback = new Callback() {
        @Override
        public void execute(Object data, String... params) {
            ArrayList<RequestAddingScore> requests = (ArrayList<RequestAddingScore>) data;
            RequestsAdapter adapter = new RequestsAdapter(requests);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
    };

}
