package io.liberty.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class NoteActivity extends AppCompatActivity {

    Intent noteIntent;
    ImageView imageViewBack;
    ImageView imageViewDelete;
    TextInputEditText textEditTitle;
    TextInputEditText textEditBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        imageViewBack = findViewById(R.id.imageViewBack);
        imageViewDelete = findViewById(R.id.imageViewDelete);
        textEditTitle = findViewById(R.id.textEditTitle);
        textEditBody = findViewById(R.id.textEditBody);

        noteIntent = getIntent();
        String title = noteIntent.getStringExtra("title");
        String body = noteIntent.getStringExtra("body");

        textEditTitle.setText(title);
        textEditBody.setText(body);

        textEditTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("CRYPTIUM", "Title focused");
                } else {
                    // TODO: Save textEditTitle.getText().toString() to database
                }
            }
        });

        textEditBody.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    Log.d("CRYPTIUM", "Body focused");
                } else {
                    // TODO: Save textEditBody.getText().toString() to database
                }
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(NoteActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Delete note from database and send user back to home (possibly after dialog confirmation)

                Intent homeIntent = new Intent(NoteActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(NoteActivity.this, HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }
}
