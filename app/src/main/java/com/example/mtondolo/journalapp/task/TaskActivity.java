package com.example.mtondolo.journalapp.task;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.example.mtondolo.journalapp.R;
import com.example.mtondolo.journalapp.addedittask.AddTaskActivity;
import com.example.mtondolo.journalapp.data.TaskDatabase;
import com.example.mtondolo.journalapp.data.TaskEntity;
import com.example.mtondolo.journalapp.util.TaskExecutors;
import com.example.mtondolo.journalapp.util.TaskViewModel;

import java.util.List;

import static android.widget.GridLayout.VERTICAL;

public class TaskActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener{
    // Constant for logging
    private static final String TAG = TaskActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    private TaskAdapter mAdapter;

    //Create TaskDatabase member variable for the Database
    private TaskDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_act);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerViewTasks);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

         /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Get the diskIO Executor from the instance of AppExecutors and
                // call the diskIO execute method with a new Runnable and implement its run method
                TaskExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        // Get the position from the viewHolder parameter
                        int position = viewHolder.getAdapterPosition();
                        List<TaskEntity> tasks = mAdapter.getTasks();
                        // Call deleteTask in the taskDao with the task at that position
                        mDb.taskDao().deleteTask(tasks.get(position));
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);

         /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        FloatingActionButton fabButton = findViewById(R.id.fab);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(TaskActivity.this, AddTaskActivity.class);
                startActivity(addTaskIntent);
            }
        });

        //Initialize member variable for the data base
        mDb = TaskDatabase.getInstance(getApplicationContext());
        setupViewModel();
    }

    private void setupViewModel() {
        // Declare a ViewModel variable and initialize it by calling ViewModelProviders.of
        TaskViewModel viewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        // Observe the LiveData object in the ViewModel
        viewModel.getTasks().observe(this, new Observer<List<TaskEntity>>() {
                    @Override
                    public void onChanged(@Nullable List<TaskEntity> taskEntities) {
                        Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                        mAdapter.setTasks(taskEntities);
                    }
        });
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        // Launch AddTaskActivity with itemId as extra for the key AddTaskActivity.EXTRA_TASK_ID
        Intent intent = new Intent(TaskActivity.this, AddTaskActivity.class);
        intent.putExtra(AddTaskActivity.EXTRA_TASK_ID, itemId);
        startActivity(intent);
    }
}
