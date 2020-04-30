package io.liberty.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import io.liberty.note.protocol.CreateAccountRequest;
import io.liberty.note.protocol.CreateAccountResponse;
import io.liberty.note.protocol.CreateNoteRequest;
import io.liberty.note.protocol.CreateNoteResponse;
import io.liberty.note.task.CreateAccountTask;
import io.liberty.note.task.CreateNoteTask;

public class CreateAccountActivity extends AppCompatActivity {

    private ObjectMapper mapper = new ObjectMapper();
    private LibertyNote mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mApp = (LibertyNote) getApplication();

        ImageView imageViewBack = findViewById(R.id.imageViewBack);
        final TextInputEditText textEditName = findViewById(R.id.textEditName);
        final TextInputEditText textEditEmail = findViewById(R.id.textEditEmail);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        final CheckBox checkboxAgree = findViewById(R.id.checkboxAgree);

        String checkBoxText = String.format("I agree to the <a href='%s/about/terms'>Terms of Use</a> and <a href='%s/about/privacy'>Privacy Policy</a>", getString(R.string.service_endpoint_url), getString(R.string.service_endpoint_url));
        checkboxAgree.setText(fromHtml(checkBoxText));
        checkboxAgree.setLinkTextColor(getColor(R.color.blue));
        checkboxAgree.setMovementMethod(LinkMovementMethod.getInstance());

        Intent createAccountIntent = getIntent();
        String email = createAccountIntent.getStringExtra("email");
        Log.d("LIBERTY.IO", "email: " + email);
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
                if (textEditName.getText() != null && textEditName.getText().toString().trim().length() > 0) {
                    if (textEditEmail.getText() != null && textEditEmail.getText().toString().trim().length() > 0) {
                        if (checkboxAgree.isChecked()) {
                            CreateAccountRequest createAccountRequest = new CreateAccountRequest();
                            String name = textEditName.getText().toString();
                            String email = textEditEmail.getText().toString();
                            createAccountRequest.name = name;
                            createAccountRequest.email = email;
                            createAccountRequest.agreeToTerms = true;
                            createAccountTask(createAccountRequest);
                        } else {
                            showSnackbar(v, getString(R.string.terms_unchecked));
                        }
                    } else {
                        showSnackbar(v, getString(R.string.email_empty));
                    }
                } else {
                    showSnackbar(v, getString(R.string.name_empty));
                }
            }
        });
    }

    public void createAccountTask(CreateAccountRequest createAccountRequest) {
        final CreateAccountTask.CreateAccountTaskResultListener callback = new CreateAccountTask.CreateAccountTaskResultListener() {
            @Override
            public void onCreateAccountTaskResult(CreateAccountResponse createAccountResponse) {
                // Show snackbar with positive/negative result.
                Log.d("LIBERTY.IO", "CreateAccountTask finished, isSent: " + createAccountResponse.isSent);
                if (createAccountResponse.isSent) {
                    // TODO: Login
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.account_created));
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.error_creating_account));
                }
            }
        };
        Log.d("LIBERTY.IO", "createAccountTask executed");
        CreateAccountTask createAccountTask = new CreateAccountTask(callback, mApp, createAccountRequest);
        createAccountTask.execute(createAccountRequest);
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public void showSnackbar(View v, String message) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.createAccountCoordinatorLayout);
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
        Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
