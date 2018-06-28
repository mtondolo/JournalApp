package com.example.mtondolo.journalapp.task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mtondolo.journalapp.R;
import com.example.mtondolo.journalapp.data.TaskDatabase;

public class TaskActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_act);
        
        TaskDatabase mDb = TaskDatabase.getInstance(getApplicationContext());
        mDb.taskDao();

    }
}
