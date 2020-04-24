package io.liberty.note;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.lifecycle.LifecycleObserver;
import com.fasterxml.jackson.databind.ObjectMapper;

import my.apache.http.client.config.CookieSpecs;
import my.apache.http.client.config.RequestConfig;
import my.apache.http.client.methods.HttpGet;
import my.apache.http.client.methods.HttpPost;
import my.apache.http.client.protocol.HttpClientContext;
import my.apache.http.client.utils.URIBuilder;
import my.apache.http.entity.StringEntity;
import my.apache.http.impl.client.BasicCookieStore;
import my.apache.http.impl.client.CloseableHttpClient;
import my.apache.http.impl.client.HttpClients;
import org.underlake.sdk.http.HttpAgent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class LibertyNote extends Application implements LifecycleObserver {

    NoteList noteList;
    ObjectMapper mapper;
    private EndpointConfiguration endpointConfiguration;
    final public static String PREFS_FILE = "login_preferences";
    private HttpAgent httpAgent;

    public LibertyNote() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mapper = new ObjectMapper();

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.serviceEndpointUrl = getResources().getString(R.string.service_endpoint_url);
        endpointConfiguration.loginshieldEndpointUrl = getResources().getString(R.string.loginshield_endpoint_url);
        endpointConfiguration.loginshieldPackageName = getResources().getString(R.string.loginshield_package_name);

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        HttpClientContext httpClientContext = HttpClientContext.create();
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        httpClientContext.setCookieStore(basicCookieStore);
        httpAgent = new HttpAgent(httpClient, httpClientContext);


//        try {
//            serviceEndpoint = new URL(getString(R.string.service_endpoint_url));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        httpAgent = new HttpAgent(serviceEndpoint, httpClient, httpClientContext);
//        HttpAuthorization httpAuthorization = new HttpAuthorization() {
//            @Override
//            public void onUnauthorized() throws IOException {
//                AsymmetricKey identityKey = initClientIdentity();
//                login(identityKey);
//            }
//
//            @Override
//            public void authorize(HttpRequest request) throws IOException {
//                if (clientAuthorizationToken != null && clientAuthorizationToken.token != null) {
//                    request.setHeader("Authorization", String.format("Token %s", clientAuthorizationToken.token));
//                }
//            }
//        };
//        httpAgentWithAuthorization = new HttpAgentWithAuthorization(httpAgent, httpAuthorization);
    }

    public EndpointConfiguration getEndpointConfiguration() {
        return endpointConfiguration;
    }

    public HttpAgent getHttpAgent() {
        return httpAgent;
    }

    public void getNoteList() throws IOException {
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.GET_LIST_PATH);
            uri = uribuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpGet httpGetRequest = new HttpGet(uri.toString());
        String httpGetResult = httpAgent.getStringWithContentType(httpGetRequest, endpointConfiguration.CONTENT_TYPE);
        noteList = mapper.readValue(httpGetResult, NoteList.class);
        Log.d("CRYPTIUM", String.format("getLoginStatus response status %s", httpGetResult));
    }

    public EditNoteResponse editNote(EditNoteRequest editNoteRequest) throws IOException {
        String jsonString = mapper.writeValueAsString(editNoteRequest);
        Log.d("CRYPTIUM", "editNote jsonString: " + jsonString);
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.EDIT_PATH);
            uribuilder.addParameter("id", editNoteRequest.id);
            uri = uribuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpPost httpPostRequest = createHttpPostWithString(uri.toString(), jsonString, endpointConfiguration.CONTENT_TYPE);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, endpointConfiguration.CONTENT_TYPE);
        EditNoteResponse editNoteResponse = mapper.readValue(httpPostResult, EditNoteResponse.class);
        Log.d("CRYPTIUM", "editNote response: " + editNoteResponse.isEdited);
        return editNoteResponse;
    }

    public CreateNoteResponse createNote(CreateNoteRequest createNoteRequest) throws IOException {
        String jsonString = mapper.writeValueAsString(createNoteRequest);
        Log.d("CRYPTIUM", "createNote jsonString: " + jsonString);
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.CREATE_PATH);
            uri = uribuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpPost httpPostRequest = createHttpPostWithString(uri.toString(), jsonString, endpointConfiguration.CONTENT_TYPE);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, endpointConfiguration.CONTENT_TYPE);
        CreateNoteResponse createNoteResponse = mapper.readValue(httpPostResult, CreateNoteResponse.class);
        Log.d("CRYPTIUM", "createNote response: " + createNoteResponse.isCreated);
        return createNoteResponse;

    }

    public DeleteNoteResponse deleteNote(DeleteNoteRequest deleteNoteRequest) throws IOException {
        String jsonString = mapper.writeValueAsString(deleteNoteRequest);
        Log.d("CRYPTIUM", "deleteNote jsonString: " + jsonString);
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.DELETE_PATH);
            uribuilder.addParameter("id", deleteNoteRequest.id);
            uri = uribuilder.build();
            Log.d("CRYPTIUM", "setPath: " + uri);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpPost httpPostRequest = createHttpPostWithString(uri.toString(), jsonString, endpointConfiguration.CONTENT_TYPE);
        // TODO: This is not returning an unexpected result, check server and check that substring(1) is actually required
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, endpointConfiguration.CONTENT_TYPE);
        DeleteNoteResponse deleteNoteResponse = mapper.readValue(httpPostResult, DeleteNoteResponse.class);
        Log.d("CRYPTIUM", "deleteNote response: " + deleteNoteResponse.isDeleted);
        return deleteNoteResponse;
    }

    public HttpPost createHttpPostWithString(String uri, String content, String contentType) throws IOException {
        HttpPost requestWithEntityAndContentType = new HttpPost(uri);
        requestWithEntityAndContentType.setHeader("Content-Type", contentType);
        requestWithEntityAndContentType.setEntity(new StringEntity(content));
        return requestWithEntityAndContentType;
    }

    public void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        }
    }

    public void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
