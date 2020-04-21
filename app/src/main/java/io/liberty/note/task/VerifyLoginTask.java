package io.liberty.note.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loginshield.sdk.realm.login.gateway.GatewayClient;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyRealmLoginRequest;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyRealmLoginResponse;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyTokenInfo;

import org.underlake.sdk.http.HttpAgent;

import java.io.Closeable;
import java.io.IOException;

import ch.qos.logback.core.net.server.Client;
import io.liberty.note.ClientTokenHelper;
import io.liberty.note.LibertyNote;
import io.liberty.note.R;
import my.apache.http.HttpResponse;
import my.apache.http.client.methods.HttpPost;
import my.apache.http.client.methods.HttpUriRequest;
import my.apache.http.client.protocol.HttpClientContext;
import my.apache.http.impl.client.CloseableHttpClient;

public class VerifyLoginTask extends AsyncTask<String, Void, VerifyRealmLoginResponse> {
    private VerifyLoginTaskResultListener listener;
    private LibertyNote mApp;
    GatewayClient gatewayClient;
    HttpAgent httpAgent;
    private final static String APPLICATION_JSON = "application/json";
    private ObjectMapper mapper = new ObjectMapper();
    final public String REALM_VERIFY_LOGIN_URL = "https://libertynote.x7.cryptium.tech/service/session/login";

    public VerifyLoginTask(VerifyLoginTask.VerifyLoginTaskResultListener listener, LibertyNote mApp, GatewayClient gatewayClient, HttpAgent httpAgent) {
        this.listener = listener;
        this.mApp = mApp;
        this.gatewayClient = gatewayClient;
        this.httpAgent = httpAgent;
    }

    public interface VerifyLoginTaskResultListener {
        void onVerifyLoginTaskResult(VerifyRealmLoginResponse verifyRealmLoginResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected VerifyRealmLoginResponse doInBackground(String... params) {
        Log.d("CRYPTIUM", "VerifyLoginTask doInBackground... ");
        try {
            VerifyTokenInfo verifyTokenInfo = gatewayClient.getLoginVerificationToken(); // This calls sdk
            String loginInteractionId = params[0];
            return verifyRealmLogin(verifyTokenInfo, loginInteractionId);
        } catch (IOException e) {
            Log.e("CRYPTIUM", "VerifyRealmLoginResponse doInBackground Error: ", e);
            return null;
        }
    }

    protected void onPostExecute(VerifyRealmLoginResponse verifyRealmLoginResponse) {
        super.onPostExecute(verifyRealmLoginResponse);
        if( listener != null ) {
            listener.onVerifyLoginTaskResult(verifyRealmLoginResponse);
        }
    }

    private VerifyRealmLoginResponse verifyRealmLogin(VerifyTokenInfo verifyTokenInfo, String loginInteractionId) throws IOException {
        VerifyRealmLoginRequest verifyRealmLoginRequest = new VerifyRealmLoginRequest();
        verifyRealmLoginRequest.interactionId = loginInteractionId;
        verifyRealmLoginRequest.verifyToken = verifyTokenInfo.verifyToken;
        String jsonString = mapper.writeValueAsString(verifyRealmLoginRequest);
        Log.d("CRYPTIUM", String.format("verifyRealmLogin jsonString: %s", jsonString));
        HttpPost httpPostRequest = mApp.createHttpPostWithString(REALM_VERIFY_LOGIN_URL, jsonString, APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, APPLICATION_JSON);
        VerifyRealmLoginResponse verifyRealmLoginResponse = mapper.readValue(httpPostResult, VerifyRealmLoginResponse.class);
        Log.d("CRYPTIUM", String.format("verifyRealmLogin response isAuthenticated %s error %s", verifyRealmLoginResponse.isAuthenticated, verifyRealmLoginResponse.error));
        return verifyRealmLoginResponse;
    }
}
