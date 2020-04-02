package io.liberty.note;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    RecyclerView recyclerViewNotes;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<Note> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Temporary notes array for testing
        // TODO: load notes from database
        notes = new ArrayList<>();
        notes.add(new Note("Note1 Title", "Note1 Body"));
        notes.add(new Note("Note2 Title", "Note2 Body"));

        // Setup Recycler View
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        recyclerViewNotes.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewNotes.setLayoutManager(layoutManager);
        mAdapter = new NoteAdapter(notes);
        recyclerViewNotes.setAdapter(mAdapter);

        recyclerViewNotes.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), recyclerViewNotes, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent noteIntent = new Intent(HomeActivity.this, NoteActivity.class);
                        noteIntent.putExtra("title", notes.get(position).title);
                        noteIntent.putExtra("body", notes.get(position).body);
                        startActivity(noteIntent);
                        finish();
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Snackbar.make(view, "Replace with delete dialog", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();                    }
                })
        );

        FloatingActionButton fab = findViewById(R.id.addNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: route user to NoteActivity with new note flag
                Snackbar.make(view, "Replace with adding a note", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: reload all notes from database to get recent changes since last onCreate()
    }

    // This is for getting back to Login view for testing, may want to remove this functionality or add a logout button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
