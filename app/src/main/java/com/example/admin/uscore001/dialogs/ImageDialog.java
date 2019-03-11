package com.example.admin.uscore001.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.uscore001.util.OnImageClickListener;
import com.example.admin.uscore001.R;

/**
 * Окно, которое позволяет пользователю выбрать способ сделать фото (Камера, галерея)
 */

public class ImageDialog extends DialogFragment implements View.OnClickListener{

    // Постоянные переменные
    private static final int CAMERA_REQUEST = 1;
    private static final int MEMORY_REQUEST = 2;

    // Виджеты
    TextView openCamera, chooseFromMemory;

    // Переменные
    OnImageClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogfragment_imagedialog, container, false);
        init(view);
        configureDialog();
        return view;
    }

    /**
     * Инициализация
     * @param view      на чем находяться элементы
     */

    private void init(View view){
        openCamera = view.findViewById(R.id.openCamera);
        chooseFromMemory = view.findViewById(R.id.chooseFromMemory);

        openCamera.setOnClickListener(this);
        chooseFromMemory.setOnClickListener(this);
    }

    /**
     * Настройка Окна
     */

    private void configureDialog(){
        getDialog().setTitle("Моя аватарка");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.openCamera:{
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCameraIntent, CAMERA_REQUEST);
                break;
            }
            case R.id.chooseFromMemory:{
                Intent openGallery = new Intent(Intent.ACTION_GET_CONTENT);
                openGallery.setType("image/*");
                startActivityForResult(openGallery, MEMORY_REQUEST);
                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");
            listener.getBitmapPath(bitmap);
        }

        if(requestCode == MEMORY_REQUEST && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            listener.getUriPath(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            listener = (OnImageClickListener) context;
        }catch (Exception e){
            e.getMessage();
        }
    }
}
