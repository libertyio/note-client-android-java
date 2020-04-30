package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loginshield.sdk.realm.login.gateway.GatewayClient;
import com.loginshield.sdk.realm.login.gateway.protocol.StartRealmLoginRequest;
import com.loginshield.sdk.realm.login.gateway.protocol.StartRealmLoginResponse;
import org.underlake.sdk.http.HttpAgent;
import java.io.IOException;
import io.liberty.note.LibertyNote;
import io.liberty.note.protocol.StartLoginRequest;
import io.liberty.note.protocol.StartLoginResponse;
import my.apache.http.client.methods.HttpPost;

public class StartLoginTask extends AsyncTask<StartLoginRequest, Void, StartLoginResponse> {
    private StartLoginTaskResultListener listener;
    private LibertyNote mApp;
    private GatewayClient gatewayClient;
    private ObjectMapper mapper = new ObjectMapper();
    private HttpAgent httpAgent;

    public StartLoginTask(StartLoginTask.StartLoginTaskResultListener listener, LibertyNote mApp, GatewayClient gatewayClient, HttpAgent httpAgent) {
        this.listener = listener;
        this.mApp = mApp;
        this.gatewayClient = gatewayClient;
        this.httpAgent = httpAgent;
    }

    public interface StartLoginTaskResultListener {
        void onStartLoginTaskResult(StartLoginResponse startLoginResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected StartLoginResponse doInBackground(StartLoginRequest... params) {
        Log.d("LIBERTY.IO", "StartLoginTask doInBackground... params[0]: " + params[0].username);
        try {
            StartRealmLoginRequest startRealmLoginRequest = new StartRealmLoginRequest();
            startRealmLoginRequest.username = params[0].username;
            StartRealmLoginResponse startRealmLoginResponse = startRealmLogin(startRealmLoginRequest);
            String appLinkUrl = gatewayClient.startRealmLoginWithForwardUrl(startRealmLoginResponse.forward);
            StartLoginResponse startLoginResponse = new StartLoginResponse();
            startLoginResponse.appLinkUrl = appLinkUrl;
            startLoginResponse.interactionId = startRealmLoginResponse.interactionId;
            return startLoginResponse;
        } catch (IOException e) {
            Log.e("LIBERTY.IO", "StartLoginResponse doInBackground Error: " + e);
            return null;
        }
    }

    protected void onPostExecute(StartLoginResponse startLoginResponse) {
        super.onPostExecute(startLoginResponse);
        if( listener != null ) {
            listener.onStartLoginTaskResult(startLoginResponse);
        }
    }

    public StartRealmLoginResponse startRealmLogin(StartRealmLoginRequest startRealmLoginRequest) throws IOException {
        String jsonString = mapper.writeValueAsString(startRealmLoginRequest);
        String realmStartLoginUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().REALM_START_LOGIN_PATH;
        Log.d("CRYPTPIUM", "startRealmLogin realmStartLoginUrl: " + realmStartLoginUrl);
        HttpPost httpPostRequest = mApp.createHttpPostWithString(realmStartLoginUrl, jsonString, mApp.getEndpointConfiguration().APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, mApp.getEndpointConfiguration().APPLICATION_JSON);
        StartRealmLoginResponse startRealmLoginResponse = mapper.readValue(httpPostResult, StartRealmLoginResponse.class);
        Log.d("LIBERTY.IO", String.format("startRealmLogin response isAuthenticated %s forward url %s interactionId %s", startRealmLoginResponse.isAuthenticated, startRealmLoginResponse.forward, startRealmLoginResponse.interactionId));
        return startRealmLoginResponse;
    }
}
