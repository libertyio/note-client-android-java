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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.loginshield.sdk.realm.login.gateway.GatewayClient;
import java.io.IOException;
import java.util.List;
import org.underlake.sdk.http.HttpAgent;
import io.liberty.note.protocol.StartLoginRequest;
import io.liberty.note.protocol.StartLoginResponse;
import io.liberty.note.protocol.VerifyLoginResponse;
import io.liberty.note.task.StartLoginTask;
import io.liberty.note.task.VerifyLoginTask;
import my.apache.http.client.config.CookieSpecs;
import my.apache.http.client.config.RequestConfig;
import my.apache.http.client.protocol.HttpClientContext;
import my.apache.http.impl.client.BasicCookieStore;
import my.apache.http.impl.client.CloseableHttpClient;
import my.apache.http.impl.client.HttpClients;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText textEditEmail;
    private LibertyNote mApp;
    private GatewayClient gatewayClient;
    private boolean isOpenedAppStore = false;
    private String loginDataUri;
    private ProgressBar progressBar;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpAgent serviceHttpAgent;
    private ClientTokenHelper clientTokenHelper;
    private String loginInteractionId;
    final private static int LOGIN_REQUEST_CODE = 1;
    final private static int MARKET_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mApp = (LibertyNote) getApplication();

        progressBar = findViewById(R.id.progressBar);
        Button buttonLogin = findViewById(R.id.buttonLogin);
        Button buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        textEditEmail = findViewById(R.id.textEditEmail);

        progressBar.setVisibility(View.INVISIBLE);

        initGatewayClient();
        serviceHttpAgent = mApp.getHttpAgent();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login();
                } catch (IOException e) {
                    Intent intent = ReportExceptionActivity.createIntent(LoginActivity.this, "login-button-click", e, null);
                    startActivity(intent);
                }
            }
        });

        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Take user to create account on website
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                String websiteCreateAccountUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().WEBSITE_CREATE_ACCOUNT_PATH;
//                intent.setData(Uri.parse(websiteCreateAccountUrl));
//                startActivity(intent);

                // TODO: enable create account functionality in-app, so user doesn't have to have another device and can do everything on their phone with 2 apps (LibertyNote and LoginShield)
                Intent createAccountIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                if (textEditEmail.getText() != null) {
                    createAccountIntent.putExtra("email",
                    textEditEmail.getText().toString());
                }
                startActivity(createAccountIntent);
                finish();
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
//        String realmStartLoginUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().REALM_START_LOGIN_PATH;
//        gatewayClient.setRealmStartLoginURL(realmStartLoginUrl);
//        String realmVerifyLoginUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().REALM_VERIFY_LOGIN_PATH;
//        gatewayClient.setRealmVerifyLoginURL(realmVerifyLoginUrl);
    }

    public void login() throws IOException {
        mApp.hideKeyboard(LoginActivity.this);
        progressBar.setVisibility(View.VISIBLE);
        if (textEditEmail.getText() != null) {
            if (textEditEmail.getText().toString().trim().length() > 0) {
                String username = textEditEmail.getText().toString();
                StartLoginRequest startLoginRequest = new StartLoginRequest();
                startLoginRequest.username = username;
                startLoginTask(startLoginRequest);
            } else {
                View v = findViewById(android.R.id.content);
                showSnackbar(v, getString(R.string.enter_valid_username));
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void startLoginTask(StartLoginRequest startLoginRequest) {
        final StartLoginTask.StartLoginTaskResultListener callback = new StartLoginTask.StartLoginTaskResultListener() {
            @Override
            public void onStartLoginTaskResult(StartLoginTask.StartLoginTaskResult startLoginTaskResult) {
                progressBar.setVisibility(View.INVISIBLE);
                if (startLoginTaskResult != null) {
                    Log.d("LIBERTY.IO", String.format("StartLoginResponse finished, appLinkUrl: %s interactionId: %s", startLoginTaskResult.appLinkUrl, startLoginTaskResult.interactionId));
                    loginInteractionId = startLoginTaskResult.interactionId;
                    startLoginshieldActivity(startLoginTaskResult.appLinkUrl);
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.login_failed));
                }
            }
        };
        Log.d("LIBERTY.IO", "startLoginTask executed");
        StartLoginTask startLoginTask = new StartLoginTask(callback, mApp, gatewayClient, serviceHttpAgent);
        startLoginTask.execute(startLoginRequest);
    }

    // launch the LoginShield app for login to the Liberty.io service
    public void startLoginshieldActivity(String dataUrl) {
        Log.d("LIBERTY.IO", "startLoginshieldActivity");
        // Build the intent
        Uri loginshieldUrl = Uri.parse(dataUrl);
        Intent loginshieldIntent = new Intent("tech.cryptium.tigercomet.intent.action.LOGIN", loginshieldUrl);

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(loginshieldIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            Log.d("LIBERTY.IO", "startLoginshieldActivity intent safe ");
            startActivityForResult(loginshieldIntent, LOGIN_REQUEST_CODE);
        } else if (!isOpenedAppStore) {
            Log.d("LIBERTY.IO", "startLoginshieldActivity intent not safe ");
            // Bring user to the market
            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
            marketIntent.setData(Uri.parse("market://details?id=tech.cryptium.tigercomet.loginshield"));
            loginDataUri = dataUrl;
            isOpenedAppStore = true;
            startActivityForResult(marketIntent, MARKET_REQUEST_CODE);
        } else {
            View v = findViewById(android.R.id.content);
            showSnackbar(v, "LoginShield is required");
        }
    }

    public void onLoginShieldResultOk() throws IOException {
        final VerifyLoginTask.VerifyLoginTaskResultListener callback = new VerifyLoginTask.VerifyLoginTaskResultListener() {
            @Override
            public void onVerifyLoginTaskResult(VerifyLoginResponse verifyLoginResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                String loginshieldGatewayProxyClientToken = clientTokenHelper.getClientToken();
                if (loginshieldGatewayProxyClientToken != null) {
                    SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("loginshieldGatewayProxyClientToken", loginshieldGatewayProxyClientToken);
                    editor.apply();
                }
                if (verifyLoginResponse != null) {
                    Log.d("LIBERTY.IO", "VerifyLoginResponse finished, isAuthenticated: " + verifyLoginResponse.isAuthenticated);
                    if (verifyLoginResponse.isAuthenticated != null && verifyLoginResponse.isAuthenticated) {
                        Log.d("LIBERTY.IO", "onLoginShieldResultOk: RESULT_OK");
                        mApp.setAuthenticated(true);
                        Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                    } else {
                        Log.d("LIBERTY.IO", "onLoginShieldResultOk: login failed");
                        View v = findViewById(android.R.id.content);
                        showSnackbar(v, getString(R.string.login_failed));
                    }
                } else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.login_failed));
                }
            }
        };
        Log.d("LIBERTY.IO", "onLoginShieldResultOk, starting verifyLoginTask with interaction Id "+loginInteractionId);
        VerifyLoginTask verifyLoginTask = new VerifyLoginTask(callback, mApp, gatewayClient, serviceHttpAgent);
        verifyLoginTask.execute(loginInteractionId);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        super.onActivityResult(requestCode, resultCode, resultIntent);
        Log.d("LIBERTY.IO", "onActivityResult requestCode: " + requestCode + " resultCode:  " + resultCode);
        switch (requestCode) {
            case LOGIN_REQUEST_CODE:
                onActivityResultFromLogin(resultCode, resultIntent);
                break;
            case MARKET_REQUEST_CODE:
                onActivityResultFromMarket(resultCode, resultIntent);
                break;
            default:
                Log.e("LIBERTY.IO LOGIN", String.format("unrecognized request code: %d", requestCode));
        }
    }

    public void onActivityResultFromLogin(int resultCode, Intent resultIntent) {
        try {
            Log.d("LIBERTY.IO", "onActivityResultFromLogin resultCode: " + resultCode);
            if (resultCode == Activity.RESULT_OK) {
                Log.d("LIBERTY.IO", String.format("onActivityResultFromLogin: onActivityResultFromLogin: RESULT_OK"));
                onLoginShieldResultOk();
            } else {
                Log.d("LIBERTY.IO", "onActivityResultFromLogin: login cancelled");
                View v = findViewById(android.R.id.content);
                showSnackbar(v, getString(R.string.login_failed));
            }
        } catch (Exception e) {
            Log.d("LIBERTY.IO", "onActivityResultFromLogin error: ", e);
            Intent intent = ReportExceptionActivity.createIntent(LoginActivity.this, "onActivityResultFromLogin", e, null);
            startActivity(intent);
        }

    }

    public void onActivityResultFromMarket(int resultCode, Intent resultIntent) {
        Log.d("LIBERTY.IO", "onActivityResultFromMarket resultCode: " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            String action = resultIntent.getStringExtra("action");
            // When user comes back to Note app from marketplace, check action for result and either login or display error
            if (action != null) {
                Log.d("LIBERTY.IO", String.format("LoginActivity: onActivityResultFromMarket: action = %s", action));
                try {
                    login();
                } catch (IOException e) {
                    Intent intent = ReportExceptionActivity.createIntent(LoginActivity.this, "onActivityResultFromMarket", e, null);
                    startActivity(intent);
                }
            }
        } else {
            // unlock was cancelled, so we can't do anything
            View v = findViewById(android.R.id.content);
            showSnackbar(v, getString(R.string.try_again));
        }
    }

    public class InstallListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent marketIntent = new Intent(Intent.ACTION_VIEW);
            marketIntent.setData(Uri.parse("market://details?id=tech.cryptium.tigercomet.loginshield"));
            startActivityForResult(marketIntent, MARKET_REQUEST_CODE);
        }
    }

    public void showSnackbar(View v, String message) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.loginCoordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        v = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        v.setLayoutParams(params);
        if (isOpenedAppStore) {
            snackbar.setAction("Install", new InstallListener());
        }
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isOpenedAppStore) {
            startLoginshieldActivity(loginDataUri);
        }
    }
}
