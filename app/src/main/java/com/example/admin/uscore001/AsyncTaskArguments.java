package com.example.admin.uscore001;

/**
 * Аргументы для асинхронного класса
 */

public class AsyncTaskArguments {

    public Callback mCallback;

    public AsyncTaskDataArgument mData;

    /**
     * Конструктор
     * @param callback      Callback после получения данных с Сервера
     * @param data          Сложный обьект (логин, пароль, image_url и т.д)
     */

    public AsyncTaskArguments(Callback callback, AsyncTaskDataArgument data){
        mCallback = callback;
        mData = data;
    }

    /**
     * Конструктор
     * @param callback      Callback после получения данных с Сервера
     */

    public AsyncTaskArguments(Callback callback){
        mCallback = callback;
    }

    public AsyncTaskArguments(){

    }

}
