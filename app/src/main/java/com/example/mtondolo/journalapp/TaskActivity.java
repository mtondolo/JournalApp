package com.example.mtondolo.journalapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mtondolo.journalapp.room.TaskDatabase;
import com.example.mtondolo.journalapp.room.TaskDatabase_Impl;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        
        TaskDatabase mDb = TaskDatabase.getInstance(getApplicationContext());
        mDb.taskDao();

    }
}
