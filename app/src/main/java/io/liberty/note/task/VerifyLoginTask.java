package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loginshield.sdk.realm.login.gateway.GatewayClient;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyRealmLoginRequest;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyRealmLoginResponse;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyTokenInfo;
import org.underlake.sdk.http.HttpAgent;
import java.io.IOException;
import io.liberty.note.LibertyNote;
import my.apache.http.client.methods.HttpPost;

public class VerifyLoginTask extends AsyncTask<String, Void, VerifyRealmLoginResponse> {
    private VerifyLoginTaskResultListener listener;
    private LibertyNote mApp;
    private GatewayClient gatewayClient;
    private HttpAgent httpAgent;
    private final static String APPLICATION_JSON = "application/json";
    private ObjectMapper mapper = new ObjectMapper();
    final private String REALM_VERIFY_LOGIN_URL = "https://libertynote.x7.cryptium.tech/service/session/login";

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
        Log.d("LIBERTY.IO", "VerifyLoginTask doInBackground... ");
        try {
            VerifyTokenInfo verifyTokenInfo = gatewayClient.getLoginVerificationToken(); // This calls sdk
            String loginInteractionId = params[0];
            return verifyRealmLogin(verifyTokenInfo, loginInteractionId);
        } catch (IOException e) {
            Log.e("LIBERTY.IO", "VerifyRealmLoginResponse doInBackground Error: ", e);
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
        Log.d("LIBERTY.IO", String.format("verifyRealmLogin jsonString: %s", jsonString));
        HttpPost httpPostRequest = mApp.createHttpPostWithString(REALM_VERIFY_LOGIN_URL, jsonString, APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, APPLICATION_JSON);
        VerifyRealmLoginResponse verifyRealmLoginResponse = mapper.readValue(httpPostResult, VerifyRealmLoginResponse.class);
        Log.d("LIBERTY.IO", String.format("verifyRealmLogin response isAuthenticated %s error %s", verifyRealmLoginResponse.isAuthenticated, verifyRealmLoginResponse.error));
        return verifyRealmLoginResponse;
    }
}
