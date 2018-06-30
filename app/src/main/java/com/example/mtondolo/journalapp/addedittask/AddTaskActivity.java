package com.example.mtondolo.journalapp.addedittask;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mtondolo.journalapp.R;
import com.example.mtondolo.journalapp.addedittask.AddTaskViewModel;
import com.example.mtondolo.journalapp.addedittask.AddTaskViewModelFactory;
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
    EditText mTitleEditText, mDescriptionEditText;
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

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);
            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                // Assign the value of EXTRA_TASK_ID in the intent to mTaskId
                // Use DEFAULT_TASK_ID as the default
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                // Declare a AddTaskViewModelFactory using mDb and mTaskId
                AddTaskViewModelFactory factory = new AddTaskViewModelFactory(mDb, mTaskId);
                // Declare a AddTaskViewModel variable and initialize it by calling ViewModelProviders.of
                // for that use the factory created above AddTaskViewModel
                final AddTaskViewModel viewModel
                        = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);
                // Observe the LiveData object in the ViewModel. Use it also when removing the observer
                viewModel.getTask().observe(this, new Observer<TaskEntity>() {
                    @Override
                    public void onChanged(@Nullable TaskEntity taskEntity) {
                        // Remove the observer as we do not need it any more
                        viewModel.getTask().removeObserver(this);
                        populateUI(taskEntity);
                    }
                });
            }
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
        mTitleEditText = findViewById(R.id.editTextTaskTitle);
        mDescriptionEditText = findViewById(R.id.editTextTaskDescription);
        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntity task) {
        // Return if the task is null
        if (task == null) {
            return;
        }
        // Use the variable task to populate the UI
        mTitleEditText.setText(task.getTitle());
        mDescriptionEditText.setText(task.getDescription());
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        String taskTitle = mTitleEditText.getText().toString();
        String taskDescription = mDescriptionEditText.getText().toString();
        Date date = new Date();

        final TaskEntity task = new TaskEntity(taskTitle, taskDescription, date);
        TaskExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                // Insert the task only if mTaskId matches DEFAULT_TASK_ID
                // Otherwise update it
                // call finish in any case
                if (mTaskId == DEFAULT_TASK_ID) {
                    // insert new task
                    mDb.taskDao().insertTask(task);
                } else {
                    //update task
                    task.setId(mTaskId);
                    mDb.taskDao().updateTask(task);
                }
                finish();
            }
        });
    }


}
