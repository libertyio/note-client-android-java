package io.liberty.note;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import io.liberty.note.task.GetNoteTask;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerViewNotes;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton addNote;
    NoteList noteList;
    LibertyNote mApp;
    Intent homeIntent;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mApp = (LibertyNote) getApplication();

        progressBar = findViewById(R.id.progressBar);
        addNote = findViewById(R.id.addNote);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);

        // Setup Recycler View; use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        recyclerViewNotes.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewNotes.setLayoutManager(layoutManager);

        recyclerViewNotes.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerViewNotes, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent noteIntent = new Intent(HomeActivity.this, NoteActivity.class);
                        noteIntent.putExtra("id", noteList.list.get(position).id);
                        noteIntent.putExtra("title", noteList.list.get(position).info.title);
                        noteIntent.putExtra("body", noteList.list.get(position).content);
                        startActivity(noteIntent);
                        finish();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // TODO: actually delete note on long item click
                        showSnackbar(view, "Replace with delete dialog");
                    }
                })
        );

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Route user to NoteActivity with new note flag
                Intent noteIntent = new Intent(HomeActivity.this, NoteActivity.class);
                noteIntent.putExtra("isNewNote", true);
                startActivity(noteIntent);
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNoteTask();
//                mAdapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getNoteTask() {
        final GetNoteTask.GetNoteTaskResultListener callback = new GetNoteTask.GetNoteTaskResultListener() {
            @Override
            public void onGetNoteTaskResult(GetNoteTask.GetNoteTaskResult result) {
                // Once GetNoteTask is finished, update recycler view
                Log.d("CRYPTIUM", "GetNoteTask finished, updating recycler view adapter");
                progressBar.setVisibility(View.INVISIBLE);
                noteList = mApp.noteList;
                swipeRefreshLayout.setRefreshing(false);
                if (noteList != null && noteList.list != null && noteList.list.size() > 0) {
                    mAdapter = new NoteAdapter(noteList);
                    recyclerViewNotes.setAdapter(mAdapter);
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getResources().getString(R.string.create_first_note));
                }
            }
        };
        Log.d("CRYPTIUM", "getNoteTask executed");
        progressBar.setVisibility(View.VISIBLE);
        GetNoteTask getNoteTask = new GetNoteTask(callback, mApp);
        getNoteTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load all notes from database in async task
        Log.d("CRYPTIUM", "HomeActivity onResume: ");
        getNoteTask();
        mApp.hideKeyboard(HomeActivity.this);

        // Check intent to see which snackbar to display
        if (getIntent() != null) {
            homeIntent = getIntent();
            if (homeIntent != null) {
                String action = homeIntent.getStringExtra("action");
                if (action != null) {
                    if (action.equals("delete")) {
                        View v = findViewById(android.R.id.content);
                        showSnackbar(v, getResources().getString(R.string.note_deleted));                    }
                }
            }
        }
    }

    public void showSnackbar(View v, String message) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.homeCoordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        v = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        v.setLayoutParams(params);
        snackbar.show();
    }

    // This is for getting back to Login view for testing, may want to remove this functionality or add a logout button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
