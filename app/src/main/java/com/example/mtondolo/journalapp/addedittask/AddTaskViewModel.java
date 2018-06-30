package com.example.mtondolo.journalapp.addedittask;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.mtondolo.journalapp.data.TaskDatabase;
import com.example.mtondolo.journalapp.data.TaskEntity;

public class AddTaskViewModel extends ViewModel {

    // Task member variable for the TaskEntry object wrapped in a LiveData
    private LiveData<TaskEntity> task;

    // Constructor where you call loadTaskById of the taskDao to initialize the tasks variable
    // Note: The constructor receiveS the database and the taskId
    public AddTaskViewModel(TaskDatabase database, int taskId) {
        task = database.taskDao().loadTaskById(taskId);
    }

    // Create a getter for the task variable
    public LiveData<TaskEntity> getTask() {
        return task;
    }
}
