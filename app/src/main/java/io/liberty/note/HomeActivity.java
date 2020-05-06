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
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;

import io.liberty.note.protocol.NoteList;
import io.liberty.note.task.GetNoteTask;

public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private RecyclerView recyclerViewNotes;
    private RecyclerView.Adapter mAdapter;
    private NoteList noteList;
    private LibertyNote mApp;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mApp = (LibertyNote) getApplication();

        progressBar = findViewById(R.id.progressBar);
        FloatingActionButton addNote = findViewById(R.id.addNote);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);

        // Setup Recycler View; use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        recyclerViewNotes.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewNotes.setLayoutManager(layoutManager);

        recyclerViewNotes.addOnItemTouchListener(
                new RecyclerViewItemClickListener(getApplicationContext(), recyclerViewNotes, new RecyclerViewItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent noteIntent = new Intent(HomeActivity.this, NoteActivity.class);
                        noteIntent.putExtra("id", noteList.list.get(position).id);
                        noteIntent.putExtra("title", noteList.list.get(position).info.title);
                        noteIntent.putExtra("body", noteList.list.get(position).content);
                        startActivity(noteIntent);
                        finish();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // Can add delete note on long item click
                       Log.d("LIBERTY.IO", "Long press on note");
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
                Log.d("LIBERTY.IO", "GetNoteTask finished, updating recycler view adapter");
                progressBar.setVisibility(View.INVISIBLE);
                noteList = mApp.noteList;
                swipeRefreshLayout.setRefreshing(false);
                if (noteList != null && noteList.list != null && noteList.list.size() > 0) {
                    mAdapter = new NoteAdapter(noteList);
                    recyclerViewNotes.setAdapter(mAdapter);
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.create_first_note));
                }
            }
        };
        Log.d("LIBERTY.IO", "getNoteTask executed");
        progressBar.setVisibility(View.VISIBLE);
        GetNoteTask getNoteTask = new GetNoteTask(callback, mApp);
        getNoteTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Load all notes from database in async task
        Log.d("LIBERTY.IO", "HomeActivity onResume: ");
        getNoteTask();
        mApp.hideKeyboard(HomeActivity.this);

        // Check intent to see which snackbar to display
        if (getIntent() != null) {
            Intent homeIntent = getIntent();
            if (homeIntent != null) {
                String action = homeIntent.getStringExtra("action");
                if (action != null) {
                    if (action.equals("delete")) {
                        View v = findViewById(android.R.id.content);
                        showSnackbar(v, getString(R.string.note_deleted));                    }
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

    public void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_home);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                return true;
            default:
                return false;
        }
    }
}
