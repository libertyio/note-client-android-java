package io.liberty.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import io.liberty.note.protocol.CreateNoteRequest;
import io.liberty.note.protocol.CreateNoteResponse;
import io.liberty.note.protocol.DeleteNoteRequest;
import io.liberty.note.protocol.DeleteNoteResponse;
import io.liberty.note.protocol.EditNoteRequest;
import io.liberty.note.protocol.EditNoteResponse;
import io.liberty.note.task.CreateNoteTask;
import io.liberty.note.task.DeleteNoteTask;
import io.liberty.note.task.EditNoteTask;

public class NoteActivity extends AppCompatActivity {

    private String id;
    private String title;
    private String body;
    private TextInputEditText textEditTitle;
    private TextInputEditText textEditBody;
    private LibertyNote mApp;
    private boolean isNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mApp = (LibertyNote) getApplication();

        ImageView imageViewBack = findViewById(R.id.imageViewBack);
        ImageView imageViewDelete = findViewById(R.id.imageViewDelete);
        textEditTitle = findViewById(R.id.textEditTitle);
        textEditBody = findViewById(R.id.textEditBody);

        Intent noteIntent = getIntent();
        id = noteIntent.getStringExtra("id");
        title = noteIntent.getStringExtra("title");
        body = noteIntent.getStringExtra("body");
        isNewNote = noteIntent.getBooleanExtra("isNewNote", false);

        textEditTitle.setText(title);
        textEditBody.setText(body);

        if (isNewNote) {
            textEditTitle.requestFocus();
            mApp.showKeyboard(NoteActivity.this);
        }

        textEditTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    // Save title to database, or create new note if flag passed
                    if (textEditTitle.getText() != null && textEditTitle.getText().toString().trim().length() > 0) {
                        if (isNewNote) {
                            CreateNoteRequest createNoteRequest = new CreateNoteRequest();
                            createNoteRequest.info = new CreateNoteRequest.Info();
                            title = textEditTitle.getText().toString();
                            createNoteRequest.info.title = title;
                            createNoteTask(createNoteRequest);
                        } else {
                            EditNoteRequest editNoteRequest = new EditNoteRequest();
                            editNoteRequest.id = id;
                            editNoteRequest.info = new EditNoteRequest.Info();
                            title = textEditTitle.getText().toString();
                            editNoteRequest.info.title = title;
                            editNoteTask(editNoteRequest);
                        }
                    }
                }
            }
        });

        textEditBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                } else {
                    // Save body to database, or create new note if flag passed
                    if (textEditBody.getText() != null && textEditTitle.getText().toString().trim().length() > 0) {
                        if (isNewNote) {
                            CreateNoteRequest createNoteRequest = new CreateNoteRequest();
                            body = textEditBody.getText().toString();
                            createNoteRequest.content = body;
                            createNoteTask(createNoteRequest);
                        } else {
                            EditNoteRequest editNoteRequest = new EditNoteRequest();
                            editNoteRequest.id = id;
                            body = textEditBody.getText().toString();
                            editNoteRequest.content = body;
                            editNoteTask(editNoteRequest);
                        }
                    }
                }
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the user back to home
                saveOnExit();
                Intent homeIntent = new Intent(NoteActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete note from database and send user back to home
                DeleteNoteRequest deleteNoteRequest = new DeleteNoteRequest();
                deleteNoteRequest.id = id;
                deleteNoteTask(deleteNoteRequest);
            }
        });
    }

    public void editNoteTask(EditNoteRequest editNoteRequest) {
        final EditNoteTask.EditNoteTaskResultListener callback = new EditNoteTask.EditNoteTaskResultListener() {
            @Override
            public void onEditNoteTaskResult(EditNoteResponse editNoteResponse) {
                Log.d("LIBERTY.IO", "EditNoteTask finished, isEdited: " + editNoteResponse.isEdited);
//                if (editNoteResponse.isEdited) {
//
//                } else {
//
//                }
            }
        };
        Log.d("LIBERTY.IO", "editNoteTask executed");
        EditNoteTask editNoteTask = new EditNoteTask(callback, mApp, editNoteRequest);
        editNoteTask.execute(editNoteRequest);
    }

    public void createNoteTask(CreateNoteRequest createNoteRequest) {
        final CreateNoteTask.CreateNoteTaskResultListener callback = new CreateNoteTask.CreateNoteTaskResultListener() {
            @Override
            public void onCreateNoteTaskResult(CreateNoteResponse createNoteResponse) {
                // Show snackbar with positive/negative result.
                Log.d("LIBERTY.IO", "CreateNoteTask finished, isCreated: " + createNoteResponse.isCreated);
                if (createNoteResponse.isCreated) {
                    NoteActivity.this.id = createNoteResponse.id;
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.note_created));
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.error_creating_note));
                }
            }
        };
        Log.d("LIBERTY.IO", "createNoteTask executed");
        CreateNoteTask createNoteTask = new CreateNoteTask(callback, mApp, createNoteRequest);
        createNoteTask.execute(createNoteRequest);
    }

    public void deleteNoteTask(final DeleteNoteRequest deleteNoteRequest) {
        final DeleteNoteTask.DeleteNoteTaskResultListener callback = new DeleteNoteTask.DeleteNoteTaskResultListener() {
            @Override
            public void onDeleteNoteTaskResult(DeleteNoteResponse deleteNoteResponse) {
                Log.d("LIBERTY.IO", "DeleteNoteTask finished, isDeleted: " + deleteNoteResponse.isDeleted);
                // If result is positive, return to home. If negative, show snackbar
                if (deleteNoteResponse.isDeleted) {

                    // This will not show because we're returning user to home. Send intent for snackbar when user lands on home page
                    Intent homeIntent = new Intent(NoteActivity.this, HomeActivity.class);
                    homeIntent.putExtra("action", "delete");
                    startActivity(homeIntent);
                    finish();
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.error_deleting_note));
                }
            }
        };
        Log.d("LIBERTY.IO", "DeleteNoteTask executed, id:" + deleteNoteRequest.id);
        DeleteNoteTask deleteNoteTask = new DeleteNoteTask(callback, mApp, deleteNoteRequest);
        deleteNoteTask.execute(deleteNoteRequest);
    }

    public void saveOnExit() {
        EditNoteRequest editNoteRequest = new EditNoteRequest();
        editNoteRequest.id = id;
        editNoteRequest.info = new EditNoteRequest.Info();
        if (textEditTitle.getText() != null) {
            title = textEditTitle.getText().toString();
            editNoteRequest.info.title = title;
        }
        if (textEditBody.getText() != null) {
            body = textEditBody.getText().toString();
            editNoteRequest.content = body;
        }
        editNoteTask(editNoteRequest);
    }

    public void showSnackbar(View v, String message) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.noteCoordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        v = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        v.setLayoutParams(params);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveOnExit();
        Intent homeIntent = new Intent(NoteActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
