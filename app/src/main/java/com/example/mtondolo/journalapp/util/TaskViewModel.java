package com.example.mtondolo.journalapp.util;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.mtondolo.journalapp.data.TaskDatabase;
import com.example.mtondolo.journalapp.data.TaskEntity;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    // Constant for logging

    private static final String TAG = TaskViewModel.class.getSimpleName();

    // Tasks member variable for a list of TaskEntry objects wrapped in a LiveData
    private LiveData<List<TaskEntity>> tasks;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        // In the constructor use the loadAllTasks of the taskDao to initialize the tasks variable
        TaskDatabase database = TaskDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the tasks from the DataBase");
        tasks = database.taskDao().loadAllTasks();
    }

    // Create a getter for the tasks variable
    public LiveData<List<TaskEntity>> getTasks() {
        return tasks;
    }
}
