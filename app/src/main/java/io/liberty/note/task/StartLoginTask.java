package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loginshield.sdk.realm.login.gateway.GatewayClient;
import org.underlake.sdk.http.HttpAgent;
import java.io.IOException;
import io.liberty.note.LibertyNote;
import io.liberty.note.protocol.StartLoginRequest;
import io.liberty.note.protocol.StartLoginResponse;
import my.apache.http.client.methods.HttpPost;

public class StartLoginTask extends AsyncTask<StartLoginRequest, Void, StartLoginTask.StartLoginTaskResult> {
    private final static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StartLoginTask.class);
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
        void onStartLoginTaskResult(StartLoginTaskResult startLoginResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected StartLoginTaskResult doInBackground(StartLoginRequest... params) {
        LOG.debug("StartLoginTask doInBackground... username: {}", params[0].username);
        try {
            StartLoginRequest startLoginRequest = new StartLoginRequest();
            startLoginRequest.username = params[0].username;
            StartLoginResponse startLoginResponse = startRealmLogin(startLoginRequest);
            LOG.debug("StartLoginTask doInBackground got startLoginResponse, calling startRealmLoginWithForwardUrl");
            String appLinkUrl = gatewayClient.startRealmLoginWithForwardUrl(startLoginResponse.forward);
            StartLoginTaskResult startLoginTaskResult = new StartLoginTaskResult();
            startLoginTaskResult.appLinkUrl = appLinkUrl;
            startLoginTaskResult.interactionId = startLoginResponse.interactionId;
            return startLoginTaskResult;
        } catch (IOException e) {
            LOG.error("StartLoginTask doInBackground error", e);
            return null;
        }
    }

    protected void onPostExecute(StartLoginTaskResult startLoginResponse) {
        super.onPostExecute(startLoginResponse);
        if( listener != null ) {
            listener.onStartLoginTaskResult(startLoginResponse);
        }
    }

    public StartLoginResponse startRealmLogin(StartLoginRequest startRealmLoginRequest) throws IOException {
        String jsonString = mapper.writeValueAsString(startRealmLoginRequest);
        String realmStartLoginUrl = mApp.getEndpointConfiguration().serviceEndpointUrl + mApp.getEndpointConfiguration().REALM_START_LOGIN_PATH;
        LOG.debug("startRealmLogin realmStartLoginUrl: {}", realmStartLoginUrl);
        HttpPost httpPostRequest = mApp.createHttpPostWithString(realmStartLoginUrl, jsonString, mApp.getEndpointConfiguration().APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, mApp.getEndpointConfiguration().APPLICATION_JSON);
        StartLoginResponse startLoginResponse = mapper.readValue(httpPostResult, StartLoginResponse.class);
        LOG.debug("startRealmLogin response isAuthenticated {} forward url {} interactionId {}", startLoginResponse.isAuthenticated, startLoginResponse.forward, startLoginResponse.interactionId);
        return startLoginResponse;
    }

    public static class StartLoginTaskResult {
        /**
         * Received from liberty.io server
         */
        public String interactionId;

        /**
         * Received from loginshield.com server
         */
        public String appLinkUrl;
    }
}
