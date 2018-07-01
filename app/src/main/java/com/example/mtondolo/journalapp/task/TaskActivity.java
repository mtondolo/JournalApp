package com.example.mtondolo.journalapp.task;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.mtondolo.journalapp.R;
import com.example.mtondolo.journalapp.addedittask.AddTaskActivity;
import com.example.mtondolo.journalapp.data.TaskDatabase;
import com.example.mtondolo.journalapp.data.TaskEntity;
import com.example.mtondolo.journalapp.util.TaskExecutors;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;


import java.util.List;

import static android.widget.GridLayout.VERTICAL;

public class TaskActivity extends AppCompatActivity implements TaskAdapter.ItemClickListener,
View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    private SignInButton signInButton;
    private FloatingActionButton floatingActionButton;
    private  GoogleApiClient googleApiClient;
    private static final int REQ_CODE = 9001;

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

        // Set the views to their corresponding views
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);
        signInButton = (SignInButton) findViewById(R.id.btn_login);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        signInButton.setOnClickListener(this);

        mRecyclerView.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions)
                .build();

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

    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly. We can try and retrieve an
            // authentication code.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Signing in ...");
            progressDialog.show();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    progressDialog.dismiss();
                    handleResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btn_login:
                signIn();
                break;

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, REQ_CODE);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false);
            }
        });

    }

    private void handleResult(GoogleSignInResult result){
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            updateUI(true);
        } else {
            updateUI(false);
        }

    }

    private void updateUI(boolean isLogin){
        if(isLogin){
            mRecyclerView.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);

        } else {
            mRecyclerView.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater ().inflate ( R.menu.menu, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId ()) {
            // Respond to a click on the "Delete all entries" menu option
            case R.id.btn_logout:
                signOut ();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




