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
import com.example.admin.uscore001.models.StudentRegisterRequestModel;

import java.util.ArrayList;

public class StudentRegisterRequestRecyclerViewAdapter extends RecyclerView.Adapter<StudentRegisterRequestRecyclerViewAdapter.StudentRegisterRequestRecyclerViewViewHolder> {
    ArrayList<StudentRegisterRequestModel> requestModels = new ArrayList<>();

    public StudentRegisterRequestRecyclerViewAdapter(ArrayList<StudentRegisterRequestModel> requestModels) {
        this.requestModels = requestModels;
    }

    static class StudentRegisterRequestRecyclerViewViewHolder extends RecyclerView.ViewHolder{
        TextView fullName, email;
        CardView cardView;
        public StudentRegisterRequestRecyclerViewViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.fullName);
            email = itemView.findViewById(R.id.email);
            cardView = itemView.findViewById(R.id.cardViewLayout);
        }
    }

    @NonNull
    @Override
    public StudentRegisterRequestRecyclerViewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_register_request_recyeler_view_item, null);
        StudentRegisterRequestRecyclerViewViewHolder holder = new StudentRegisterRequestRecyclerViewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentRegisterRequestRecyclerViewViewHolder studentRegisterRequestRecyclerViewViewHolder, int i) {
        StudentRegisterRequestModel model = requestModels.get(i);
        if(model.isConfirmed() && !model.isDenied()){
            studentRegisterRequestRecyclerViewViewHolder.cardView.setCardBackgroundColor(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().getResources().getColor(R.color.addedColor));
        }else if(model.isDenied() && !model.isConfirmed()){
            studentRegisterRequestRecyclerViewViewHolder.cardView.setCardBackgroundColor(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().getResources().getColor(R.color.canceledColor));
        }else if(!model.isConfirmed() && !model.isDenied()){
            studentRegisterRequestRecyclerViewViewHolder.cardView.setCardBackgroundColor(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext().getResources().getColor(R.color.inProcessColor));
        }
        studentRegisterRequestRecyclerViewViewHolder.fullName.setText(model.getFirstName()+" "+model.getSecondName());
        studentRegisterRequestRecyclerViewViewHolder.email.setText(model.getEmail());

        studentRegisterRequestRecyclerViewViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = model.getFirstName();
                String secondName = model.getSecondName();
                String lastName = model.getLastName();
                String email = model.getEmail();
                String message = "Имя: " + firstName + "\n" +
                        "Фамилия: " + secondName + "\n" +
                        "Отчество: " + lastName + "\n" +
                        "Почта: " + email;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(studentRegisterRequestRecyclerViewViewHolder.cardView.getContext());
                alertDialog.create();
                if(!model.isConfirmed() && !model.isDenied()) {
                    // открываем диалог с информациией о запросе
                    alertDialog.setTitle("Хотите добавить ученика в свой класс?");
                    alertDialog.setMessage(message);
                    alertDialog.setPositiveButton("Да", positiveOnClickListener).setNegativeButton("Нет", negativeOnClickListener);
                }else{
                    alertDialog.setTitle("Подробная информация о запросе");
                    alertDialog.setMessage(message);
                    alertDialog.setOnDismissListener(onDismissListener);
                    alertDialog.show();
                }
                alertDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestModels.size();
    }

    DialogInterface.OnClickListener positiveOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    };

    DialogInterface.OnClickListener negativeOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

        }
    };

    DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() {
        @Override
        public void onDismiss(DialogInterface dialog) {
            dialog.dismiss();
        }
    };

}
