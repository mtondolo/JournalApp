package com.example.mtondolo.journalapp.task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.mtondolo.journalapp.R;
import com.example.mtondolo.journalapp.data.TaskDatabase;
import com.example.mtondolo.journalapp.data.TaskEntity;
import com.example.mtondolo.journalapp.util.TaskExecutors;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";
    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";

    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;

    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();

    // Fields for views
    EditText mDescriptionEditText, mDetailsEditText;
    Button mButton;

    // Member variable for the Database
    private TaskDatabase mDb;

    private int mTaskId = DEFAULT_TASK_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        initViews();

        //Initialize member variable for the data base
        mDb = TaskDatabase.getInstance(getApplicationContext());

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mDescriptionEditText = findViewById(R.id.editTextTaskDescription);
        mDetailsEditText = findViewById(R.id.editTextTaskDetails);

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String taskDescription = mDescriptionEditText.getText().toString();
        String taskDetails = mDetailsEditText.getText().toString();
        Date date = new Date();

        final TaskEntity taskEntity = new TaskEntity(taskDescription, taskDetails, date);

        // Get the diskIO Executor from the instance of TaskExecutors and
        // call the diskIO execute method with a new Runnable and implement its run method
        TaskExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // Move the remaining logic inside the run method
                mDb.taskDao().insertTask(taskEntity);
                finish();
            }
        });
    }


}
