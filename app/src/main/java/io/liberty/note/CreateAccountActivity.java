package io.liberty.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreateAccountActivity extends AppCompatActivity {

    ImageView imageViewBack;
    TextInputLayout textLayoutName;
    TextInputEditText textEditName;
    TextInputLayout textLayoutEmail;
    TextInputEditText textEditEmail;
    Button buttonCreateAccount;
    Intent createAccountIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        imageViewBack = findViewById(R.id.imageViewBack);
        textLayoutName = findViewById(R.id.textLayoutName);
        textEditName = findViewById(R.id.textEditName);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textEditEmail = findViewById(R.id.textEditEmail);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);

        createAccountIntent = getIntent();
        String email = createAccountIntent.getStringExtra("email");
        Log.d("CRYPTIUM", "email: " + email);
        if (email != null) {
            textEditEmail.setText(email);
        }

        textEditName.requestFocus();

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Login with Loginshield and create account in database
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
