package io.liberty.note;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
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

        String checkBoxText = "I agree to the <a href='https://liberty.io/about/terms'>Terms of Use</a> and <a href='https://liberty.io/about/privacy'>Privacy Policy</a>";
        checkboxAgree.setText(Html.fromHtml(checkBoxText));
        checkboxAgree.setLinkTextColor(getResources().getColor(R.color.blue));
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
                // TODO: Login with Loginshield and create account in database
//                createAccount();
                Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }

//    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest) throws IOException {
//        String jsonString = mApp.mapper.writeValueAsString(createAccountRequest);
//        Log.d("CRYPTIUM", "createAccount jsonString: " + jsonString);
//        URI uri;
//        try {
//            URIBuilder uribuilder = new URIBuilder(LibertyNote.HOST_URI);
//            uribuilder.setPath(LibertyNote.CREATE_ACCOUNT_PATH);
//            uri = uribuilder.build();
//        } catch (URISyntaxException e) {
//            throw new IOException(e);
//        }
//        HttpPost httpPostRequest = mApp.createHttpPostWithString(uri.toString(), jsonString, LibertyNote.CONTENT_TYPE);
//        HttpAgent httpAgent = new HttpAgent(mApp.httpClient, mApp.httpClientContext);
//        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, LibertyNote.CONTENT_TYPE);
//        CreateAccountResponse createAccountResponse = mApp.mapper.readValue(httpPostResult, createAccountResponse.class);
//        Log.d("CRYPTIUM", "createAccount response: " + createAccountResponse.isCreated);
//        return createAccountResponse;
//    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent loginIntent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
