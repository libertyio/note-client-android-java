package io.liberty.note;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.lifecycle.LifecycleObserver;

import io.liberty.note.protocol.CreateAccountRequest;
import io.liberty.note.protocol.CreateAccountResponse;
import io.liberty.note.protocol.CreateNoteRequest;
import io.liberty.note.protocol.CreateNoteResponse;
import io.liberty.note.protocol.DeleteNoteRequest;
import io.liberty.note.protocol.DeleteNoteResponse;
import io.liberty.note.protocol.EditNoteRequest;
import io.liberty.note.protocol.EditNoteResponse;
import io.liberty.note.protocol.NoteList;
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

    public NoteList noteList;
    public EndpointConfiguration endpointConfiguration;
    final public static String PREFS_FILE = "login_preferences";
    private HttpAgent httpAgent;

    public LibertyNote() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        endpointConfiguration = new EndpointConfiguration();
        endpointConfiguration.serviceEndpointUrl = getString(R.string.service_endpoint_url);
        endpointConfiguration.loginshieldEndpointUrl = getString(R.string.loginshield_endpoint_url);
        endpointConfiguration.loginshieldPackageName = getString(R.string.loginshield_package_name);

        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
        HttpClientContext httpClientContext = HttpClientContext.create();
        BasicCookieStore basicCookieStore = new BasicCookieStore();
        httpClientContext.setCookieStore(basicCookieStore);
        httpAgent = new HttpAgent(httpClient, httpClientContext);
    }

    public EndpointConfiguration getEndpointConfiguration() {
        return endpointConfiguration;
    }

    public HttpAgent getHttpAgent() {
        return httpAgent;
    }

    public CreateAccountResponse createAccount(CreateAccountRequest createAccountRequest) throws IOException {
        String jsonString = MapperUtil.CACHE.getWriter(CreateAccountRequest.class).writeValueAsString(createAccountRequest);
        Log.d("LIBERTY.IO", "createAccount jsonString: " + jsonString);
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.CREATE_ACCOUNT_PATH);
            uri = uribuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpPost httpPostRequest = createHttpPostWithString(uri.toString(), jsonString, endpointConfiguration.APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, endpointConfiguration.APPLICATION_JSON);
        CreateAccountResponse createAccountResponse = MapperUtil.CACHE.getReader(CreateAccountResponse.class).readValue(httpPostResult);
        Log.d("LIBERTY.IO", "createAccount response: " + createAccountResponse.isSent);
        return createAccountResponse;

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
        String httpGetResult = httpAgent.getStringWithContentType(httpGetRequest, endpointConfiguration.APPLICATION_JSON);
        noteList = MapperUtil.CACHE.getReader(NoteList.class).readValue(httpGetResult);
        Log.d("LIBERTY.IO", String.format("getLoginStatus response status %s", httpGetResult));
    }

    public EditNoteResponse editNote(EditNoteRequest editNoteRequest) throws IOException {
        String jsonString = MapperUtil.CACHE.getWriter(EditNoteRequest.class).writeValueAsString(editNoteRequest);
        Log.d("LIBERTY.IO", String.format("editNote id %s jsonString %s", editNoteRequest.id,  jsonString));
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.EDIT_PATH);
            uribuilder.addParameter("id", editNoteRequest.id);
            uri = uribuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpPost httpPostRequest = createHttpPostWithString(uri.toString(), jsonString, endpointConfiguration.APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, endpointConfiguration.APPLICATION_JSON);
        EditNoteResponse editNoteResponse = MapperUtil.CACHE.getReader(EditNoteResponse.class).readValue(httpPostResult);
        Log.d("LIBERTY.IO", "editNote response: " + editNoteResponse.isEdited);
        return editNoteResponse;
    }

    public CreateNoteResponse createNote(CreateNoteRequest createNoteRequest) throws IOException {
        String jsonString = MapperUtil.CACHE.getWriter(CreateNoteRequest.class).writeValueAsString(createNoteRequest);
        Log.d("LIBERTY.IO", "createNote jsonString: " + jsonString);
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.CREATE_PATH);
            uri = uribuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpPost httpPostRequest = createHttpPostWithString(uri.toString(), jsonString, endpointConfiguration.APPLICATION_JSON);
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, endpointConfiguration.APPLICATION_JSON);
        CreateNoteResponse createNoteResponse = MapperUtil.CACHE.getReader(CreateNoteResponse.class).readValue(httpPostResult);
        Log.d("LIBERTY.IO", "createNote response: " + createNoteResponse.isCreated);
        return createNoteResponse;
    }

    public DeleteNoteResponse deleteNote(DeleteNoteRequest deleteNoteRequest) throws IOException {
        String jsonString = MapperUtil.CACHE.getWriter(DeleteNoteRequest.class).writeValueAsString(deleteNoteRequest);
        Log.d("LIBERTY.IO", "deleteNote jsonString: " + jsonString);
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.DELETE_PATH);
            uribuilder.addParameter("id", deleteNoteRequest.id);
            uri = uribuilder.build();
            Log.d("LIBERTY.IO", "setPath: " + uri);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
        HttpPost httpPostRequest = createHttpPostWithString(uri.toString(), jsonString, endpointConfiguration.APPLICATION_JSON);
        // This is not returning an unexpected result, check server and check that substring(1) is actually required
        String httpPostResult = httpAgent.getStringWithContentType(httpPostRequest, endpointConfiguration.APPLICATION_JSON);
        DeleteNoteResponse deleteNoteResponse = MapperUtil.CACHE.getReader(DeleteNoteResponse.class).readValue(httpPostResult);
        Log.d("LIBERTY.IO", "deleteNote response: " + deleteNoteResponse.isDeleted);
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
