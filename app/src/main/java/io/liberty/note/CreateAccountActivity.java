package io.liberty.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.underlake.sdk.http.HttpAgent;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import my.apache.http.client.methods.HttpPost;
import my.apache.http.client.utils.URIBuilder;

public class CreateAccountActivity extends AppCompatActivity {

    ImageView imageViewBack;
    TextInputLayout textLayoutName;
    TextInputEditText textEditName;
    TextInputLayout textLayoutEmail;
    TextInputEditText textEditEmail;
    CheckBox checkboxAgree;
    Button buttonCreateAccount;
    Intent createAccountIntent;
    LibertyNote mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mApp = (LibertyNote) getApplication();

        imageViewBack = findViewById(R.id.imageViewBack);
        textLayoutName = findViewById(R.id.textLayoutName);
        textEditName = findViewById(R.id.textEditName);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textEditEmail = findViewById(R.id.textEditEmail);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        checkboxAgree = findViewById(R.id.checkboxAgree);

        String checkBoxText = String.format("I agree to the <a href='%s/about/terms'>Terms of Use</a> and <a href='%s/about/privacy'>Privacy Policy</a>", getString(R.string.service_endpoint_url), getString(R.string.service_endpoint_url));
        checkboxAgree.setText(fromHtml(checkBoxText));
        checkboxAgree.setLinkTextColor(getColor(R.color.blue));
        checkboxAgree.setMovementMethod(LinkMovementMethod.getInstance());

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
                Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
