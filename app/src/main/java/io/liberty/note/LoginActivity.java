package io.liberty.note;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loginshield.sdk.realm.login.gateway.GatewayClient;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyRealmLoginResponse;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.underlake.sdk.http.HttpAgent;
import io.liberty.note.task.StartLoginTask;
import io.liberty.note.task.VerifyLoginTask;
import my.apache.http.client.config.CookieSpecs;
import my.apache.http.client.config.RequestConfig;
import my.apache.http.client.protocol.HttpClientContext;
import my.apache.http.impl.client.BasicCookieStore;
import my.apache.http.impl.client.CloseableHttpClient;
import my.apache.http.impl.client.HttpClients;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout textLayoutEmail;
    TextInputEditText textEditEmail;
    Button buttonLogin;
    Button buttonCreateAccount;
    LibertyNote mApp;
    GatewayClient gatewayClient;
    boolean isOpeningAppStore = false;
    String loginDataUri;
    ProgressBar progressBar;
    private ObjectMapper mapper = new ObjectMapper();
    HttpAgent serviceHttpAgent;
    ClientTokenHelper clientTokenHelper;
    final private static int LOGIN_REQUEST_CODE = 1;
    final private static int MARKET_REQUEST_CODE = 2;
    String loginInteractionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mApp = (LibertyNote) getApplication();

        progressBar = findViewById(R.id.progressBar);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        textLayoutEmail = findViewById(R.id.textLayoutEmail);
        textEditEmail = findViewById(R.id.textEditEmail);

        progressBar.setVisibility(View.INVISIBLE);

        initGatewayClient();
        serviceHttpAgent = mApp.getHttpAgent();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startLoginshieldActivity(LOGINSHIELD_PACKAGE_NAME, "https://tigercomet.x7.cryptium.tech/login/android#mx=123&key=321");
//                startLoginshieldActivity(LOGINSHIELD_PACKAGE_NAME, "login://tigercomet.x7.cryptium.tech/#mx=123&key=321");

                try {
                    login();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String websiteCreateAccountUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().WEBSITE_CREATE_ACCOUNT_PATH;
                intent.setData(Uri.parse(websiteCreateAccountUrl));
                startActivity(intent);
                // TODO: enable create account functionality in-app, so user doesn't have to have another device and can do everything on their phone with 2 apps (LibertyNote and LoginShield)
//                Intent createAccountIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
//                if (textEditEmail.getText() != null) {
//                    createAccountIntent.putExtra("email", textEditEmail.getText().toString());
//                }
//                startActivity(createAccountIntent);
//                finish();
            }
        });
    }

    private void initGatewayClient() {
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        HttpClientContext httpClientContext = HttpClientContext.create();
        httpClientContext.setCookieStore(new BasicCookieStore());
        HttpAgent httpAgent = new HttpAgent(httpClient, httpClientContext);
        SharedPreferences preferences = getSharedPreferences(LibertyNote.PREFS_FILE, Context.MODE_PRIVATE);
        String loginshieldGatewayProxyClientToken = preferences.getString("loginshieldGatewayProxyClientToken", null);
        clientTokenHelper = new ClientTokenHelper(loginshieldGatewayProxyClientToken);
        httpAgent.addHelper(clientTokenHelper);
        gatewayClient = new GatewayClient(httpAgent);
        gatewayClient.setEndpointURL(mApp.getEndpointConfiguration().loginshieldEndpointUrl);
        String realmStartLoginUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().REALM_START_LOGIN_PATH;
        gatewayClient.setRealmStartLoginURL(realmStartLoginUrl);
        String realmVerifyLoginUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().REALM_VERIFY_LOGIN_PATH;
        gatewayClient.setRealmVerifyLoginURL(realmVerifyLoginUrl);
    }

    public void login() throws IOException {
        progressBar.setVisibility(View.VISIBLE);
        if (textEditEmail.getText() != null) {
            if (textEditEmail.getText().toString().trim().length() > 0) {
                String username = textEditEmail.getText().toString();
                StartLoginRequest startLoginRequest = new StartLoginRequest();
                startLoginRequest.username = username;
//                Logger log = LoggerFactory.getLogger(LoginActivity.class);
//                log.info("logback before startLoginTAsk");
                startLoginTask(startLoginRequest);
            } else {
                View v = findViewById(android.R.id.content);
                showSnackbar(v, getResources().getString(R.string.enter_valid_username));
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void startLoginTask(StartLoginRequest startLoginRequest) {
        final StartLoginTask.StartLoginTaskResultListener callback = new StartLoginTask.StartLoginTaskResultListener() {
            @Override
            public void onStartLoginTaskResult(StartLoginResponse startLoginResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                if (startLoginResponse != null) {
                    Log.d("CRYPTIUM", String.format("StartLoginResponse finished, appLinkUrl: %s interactionId: %s", startLoginResponse.appLinkUrl, startLoginResponse.interactionId));
                    loginInteractionId = startLoginResponse.interactionId;
                    startLoginshieldActivity(mApp.getEndpointConfiguration().loginshieldPackageName, startLoginResponse.appLinkUrl);
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getResources().getString(R.string.login_failed));
                }
            }
        };
        Log.d("CRYPTIUM", "startLoginTask executed");
        StartLoginTask startLoginTask = new StartLoginTask(callback, mApp, gatewayClient, serviceHttpAgent);
        startLoginTask.execute(startLoginRequest);
    }

    // Use this to launch another app: startLoginshieldActivity(getApplicationContext(), "tech.cryptium.tigercomet.loginshield");
    public void startLoginshieldActivity(String packageName, String dataUrl) {
        Log.d("CRYPTIUM", "startLoginshieldActivity");
        // Build the intent
        Uri loginshieldUrl = Uri.parse(dataUrl);
//        Intent loginshieldIntent = new Intent(Intent.ACTION_VIEW, loginshieldUrl);
        Intent loginshieldIntent = new Intent("tech.cryptium.tigercomet.intent.action.LOGIN", loginshieldUrl);

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(loginshieldIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            Log.d("CRYPTIUM", "startLoginshieldActivity intent safe ");
            startActivityForResult(loginshieldIntent, LOGIN_REQUEST_CODE);
        } else {
            Log.d("CRYPTIUM", "startLoginshieldActivity intent not safe ");
            // Bring user to the market or let them choose an app?
            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
            marketIntent.setData(Uri.parse("market://details?id=" + mApp.getEndpointConfiguration().loginshieldPackageName));
            loginDataUri = dataUrl;
            isOpeningAppStore = true;
            startActivityForResult(marketIntent, MARKET_REQUEST_CODE);
        }
    }

    public void onLoginShieldResultOk() throws IOException {
        final VerifyLoginTask.VerifyLoginTaskResultListener callback = new VerifyLoginTask.VerifyLoginTaskResultListener() {
            @Override
            public void onVerifyLoginTaskResult(VerifyRealmLoginResponse verifyRealmLoginResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                String loginshieldGatewayProxyClientToken = clientTokenHelper.getClientToken();
                if (loginshieldGatewayProxyClientToken != null) {
                    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("loginshieldGatewayProxyClientToken", loginshieldGatewayProxyClientToken);
                    editor.apply();
                }
                if (verifyRealmLoginResponse != null) {
                    Log.d("CRYPTIUM", "VerifyRealmLoginResponse finished, isAuthenticated: " + verifyRealmLoginResponse.isAuthenticated);
                    if (verifyRealmLoginResponse.isAuthenticated != null && verifyRealmLoginResponse.isAuthenticated) {
                        Log.d("CRYPTIUM", "onLoginShieldResultOk: RESULT_OK");
                        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        // TODO: login failed
                        Log.d("CRYPTIUM", "onLoginShieldResultOk: login failed");
                    }
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getResources().getString(R.string.login_failed));
                }
            }
        };
        Log.d("CRYPTIUM", "startLoginTask executed");
        VerifyLoginTask verifyLoginTask = new VerifyLoginTask(callback, mApp, gatewayClient, serviceHttpAgent);
        verifyLoginTask.execute(loginInteractionId);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        Log.d("CRYPTIUM", "onActivityResult requestCode: " + requestCode + " resultCode:  " + resultCode);
        switch (requestCode) {
            case LOGIN_REQUEST_CODE:
                onActivityResultFromLogin(resultCode, resultIntent);
                break;
            case MARKET_REQUEST_CODE:
                onActivityResultFromMarket(resultCode, resultIntent);
                break;
            default:
                Log.e("CRYPTIUM LOGIN", String.format("unrecognized request code: %d", requestCode));
        }
    }

    public void onActivityResultFromLogin(int resultCode, Intent resultIntent) {
        try {
            Log.d("CRYPTIUM", "onActivityResultFromLogin resultCode: " + resultCode);
            if (resultCode == Activity.RESULT_OK) {
                Log.d("CRYPTIUM", String.format("onActivityResultFromLogin: onActivityResultFromLogin: RESULT_OK"));
                onLoginShieldResultOk();
            } else {
                Log.d("CRYPTIUM", "onActivityResultFromLogin: login cancelled");
                View v = findViewById(android.R.id.content);
                showSnackbar(v, getResources().getString(R.string.login_failed));
            }
        } catch (Exception e) {
            Log.d("CRYPTIUM", "onActivityResultFromLogin error: ", e);
        }

    }

    public void onActivityResultFromMarket(int resultCode, Intent resultIntent) {
        Log.d("CRYPTIUM", "onActivityResultFromLogin resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            String action = resultIntent.getStringExtra("action");
            // When user comes back to Note app from marketplace, check action for result and either login or display error
            if (action != null) {
                Log.d("CRYPTIUM", String.format("LoginActivity: onActivityResultFromLogin: action = %s", action));
                startLoginshieldActivity(mApp.getEndpointConfiguration().loginshieldPackageName, "login://tigercomet.x7.cryptium.tech/#mx=123&key=321");
            }
        } else {
            // unlock was cancelled, so we can't do anything
//            Intent loginResult = new Intent();
//            loginResult.putExtra("action", "login");
//            setResult(Activity.RESULT_CANCELED, loginResult);
//            finish();
        }
    }

    private void setupFloatingLabelError() {
        final TextInputLayout floatingUsernameLabel = (TextInputLayout) findViewById(R.id.textLayoutEmail);
        floatingUsernameLabel.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {
                if (text.length() > 0 && text.length() <= 4) {
                    floatingUsernameLabel.setError(getString(R.string.email_required));
                    floatingUsernameLabel.setErrorEnabled(true);
                } else {
                    floatingUsernameLabel.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void showSnackbar(View v, String message) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.loginCoordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        v = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        v.setLayoutParams(params);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isOpeningAppStore) {
            isOpeningAppStore = false;
            startLoginshieldActivity(mApp.getEndpointConfiguration().loginshieldPackageName, loginDataUri);
        }
    }
}
