package com.example.mtondolo.journalapp.addedittask;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.mtondolo.journalapp.data.TaskDatabase;

public class AddTaskViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    // Two member variables. One for the database and one for the taskId
    private final TaskDatabase mDb;
    private final int mTaskId;

    // Initialize the member variables in the constructor with the parameters received
    public AddTaskViewModelFactory(TaskDatabase database, int taskId) {
        mDb = database;
        mTaskId = taskId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTaskViewModel(mDb, mTaskId);
    }
}
