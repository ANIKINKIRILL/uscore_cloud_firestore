package com.it_score.admin.uscore001;

/**
 * Сложный обьект для агрумента к AsyncTaskArguments
 */

public class AsyncTaskDataArgument {

    public Object[] data;

    public AsyncTaskDataArgument(Object... data){
        this.data = data;
    }

    public AsyncTaskDataArgument(){

    }

}
