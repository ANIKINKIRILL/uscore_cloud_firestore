package com.it_score.admin.uscore001.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.activities.AddGroupFunctionAdminSectionActivity;
import com.it_score.admin.uscore001.activities.AddOptionActivity;
import com.it_score.admin.uscore001.activities.AddSubjectFunctionAdminSectionActivity;
import com.it_score.admin.uscore001.activities.EditTeacherAdminSectionActivity;
import com.it_score.admin.uscore001.models.AdminFunction;

import java.util.ArrayList;

/**
 * Адаптер для отобрпажения функций админа
 */

public class AdminFunctionsRecyclerViewAdapter extends RecyclerView.Adapter<AdminFunctionsRecyclerViewAdapter.ViewHolder> {

    // Переменные
    private ArrayList<AdminFunction> adminFunctions = new ArrayList<>();
    private Context context;

    // Функции
    public static final String CHANGE_PROMOTIONS_PENALTIES_FUN_ID = "3wFLAY0LCsTwdjLzvS4y";
    public static final String ADD_GROUP_FUN_ID = "MHv4mwDwlxw2BDpI7iAv";
    public static final String CHANGE_PASSWORD_FUN_ID = "NxD88xLfgyWmHpZuiKhy";
    public static final String EDIT_TEACHER_FUN_ID = "YeGNUFEcNqD8JuY3a9U7";

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView position, title, description;
        CardView cardView;
        View dividerLine;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.position);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            cardView = itemView.findViewById(R.id.cardView);
            dividerLine = itemView.findViewById(R.id.dividerLine);
        }
    }

    public AdminFunctionsRecyclerViewAdapter(ArrayList<AdminFunction> adminFunctions) {
        this.adminFunctions = adminFunctions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.admin_function_recycler_view_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = viewHolder.cardView.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        AdminFunction adminFunction = adminFunctions.get(i);
        String positionNumber = Integer.toString(i+1);
        String functionTitle = adminFunction.getName();
        String functionDescription = adminFunction.getDescription();
        viewHolder.position.setText(positionNumber);
        viewHolder.title.setText(functionTitle);
        viewHolder.description.setText(functionDescription);

        if(i+1 == adminFunctions.size()){
            viewHolder.dividerLine.setVisibility(View.GONE);
        }

        /*
                    НАЖАТИЕ НА ITEM
         */

        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (adminFunction.getId()){
                    case ADD_GROUP_FUN_ID:{
                        Intent intent = new Intent(context, AddGroupFunctionAdminSectionActivity.class);
                        context.startActivity(intent);
                        break;
                    }
                    case CHANGE_PROMOTIONS_PENALTIES_FUN_ID:{
                        Intent intent = new Intent(context, AddOptionActivity.class);
                        context.startActivity(intent);
                        break;
                    }
                    case EDIT_TEACHER_FUN_ID:{
                        Intent intent = new Intent(context, EditTeacherAdminSectionActivity.class);
                        context.startActivity(intent);
                        break;
                    }
                    case CHANGE_PASSWORD_FUN_ID:{
                        Toast.makeText(context, "На данный момент не доступно", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return adminFunctions.size();
    }
}
