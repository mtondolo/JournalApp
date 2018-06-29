package com.example.mtondolo.journalapp.task;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.mtondolo.journalapp.R;

public class AddTaskActivity extends AppCompatActivity {

    // Constant for logging
    private static final String TAG = AddTaskActivity.class.getSimpleName();

    // Fields for views
    EditText mDescriptionEditText, mDetailsEditText;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);

        initViews();
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mDescriptionEditText = findViewById(R.id.editTextTaskDescription);
        mDetailsEditText = findViewById(R.id.editTextTaskDetails);

        mButton = findViewById(R.id.saveButton);
    }
}
