package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loginshield.sdk.realm.login.gateway.GatewayClient;
import com.loginshield.sdk.realm.login.gateway.protocol.VerifyTokenInfo;
import org.underlake.sdk.http.HttpAgent;
import java.io.IOException;
import io.liberty.note.LibertyNote;
import io.liberty.note.protocol.VerifyLoginRequest;
import io.liberty.note.protocol.VerifyLoginResponse;
import my.apache.http.client.methods.HttpPost;

public class VerifyLoginTask extends AsyncTask<String, Void, VerifyLoginResponse> {
    private VerifyLoginTaskResultListener listener;
    private LibertyNote mApp;
    private GatewayClient gatewayClient;
    private HttpAgent httpAgent;
    private final static String APPLICATION_JSON = "application/json";
    private ObjectMapper mapper = new ObjectMapper();

    public VerifyLoginTask(VerifyLoginTask.VerifyLoginTaskResultListener listener, LibertyNote mApp, GatewayClient gatewayClient, HttpAgent httpAgent) {
        this.listener = listener;
        this.mApp = mApp;
        this.gatewayClient = gatewayClient;
        this.httpAgent = httpAgent;
    }

    public interface VerifyLoginTaskResultListener {
        void onVerifyLoginTaskResult(VerifyLoginResponse verifyLoginResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected VerifyLoginResponse doInBackground(String... params) {
        Log.d("LIBERTY.IO", "VerifyLoginTask doInBackground... ");
        try {
            VerifyTokenInfo verifyTokenInfo = gatewayClient.getLoginVerificationToken(); // This calls sdk
            String loginInteractionId = params[0];
            return verifyRealmLogin(verifyTokenInfo, loginInteractionId);
        } catch (IOException e) {
            Log.e("LIBERTY.IO", "VerifyLoginResponse doInBackground Error: ", e);
            return null;
        }
    }

    protected void onPostExecute(VerifyLoginResponse verifyLoginResponse) {
        super.onPostExecute(verifyLoginResponse);
        if( listener != null ) {
            listener.onVerifyLoginTaskResult(verifyLoginResponse);
        }
    }

    private VerifyLoginResponse verifyRealmLogin(VerifyTokenInfo verifyTokenInfo, String loginInteractionId) throws IOException {
        VerifyLoginRequest verifyRealmLoginRequest = new VerifyLoginRequest();
        verifyRealmLoginRequest.interactionId = loginInteractionId;
        verifyRealmLoginRequest.verifyToken = verifyTokenInfo.verifyToken;
        String jsonString = mapper.writeValueAsString(verifyRealmLoginRequest);
        Log.d("LIBERTY.IO", String.format("verifyRealmLogin jsonString: %s", jsonString));
        String verifyRealmLoginUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().REALM_VERIFY_LOGIN_PATH;
        HttpPost httpPostRequest = mApp.createHttpPostWithString(verifyRealmLoginUrl, jsonString, APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, APPLICATION_JSON);
        VerifyLoginResponse verifyLoginResponse = mapper.readValue(httpPostResult, VerifyLoginResponse.class);
        Log.d("LIBERTY.IO", String.format("verifyRealmLogin response isAuthenticated %s error %s", verifyLoginResponse.isAuthenticated, verifyLoginResponse.error));
        return verifyLoginResponse;
    }
}
