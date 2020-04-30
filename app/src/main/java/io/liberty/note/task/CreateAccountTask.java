package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import io.liberty.note.protocol.CreateAccountRequest;
import io.liberty.note.protocol.CreateAccountResponse;
import io.liberty.note.LibertyNote;

public class CreateAccountTask extends AsyncTask<CreateAccountRequest, Void, CreateAccountResponse> {
    private CreateAccountTaskResultListener listener;
    private LibertyNote mApp;
    private CreateAccountRequest createAccountRequest;

    public CreateAccountTask(CreateAccountTask.CreateAccountTaskResultListener listener, LibertyNote mApp, CreateAccountRequest createAccountRequest) {
        this.listener = listener;
        this.mApp = mApp;
        this.createAccountRequest = createAccountRequest;
    }

    public interface CreateAccountTaskResultListener {
        void onCreateAccountTaskResult(CreateAccountResponse createAccountResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected CreateAccountResponse doInBackground(CreateAccountRequest... params) {
        Log.d("LIBERTY.IO", "CreateAccountTask doInBackground...");
        try {
            // Create account in database
            return mApp.createAccount(params[0]);
        } catch (IOException e) {
            Log.e("LIBERTY.IO", "doInBackground Error: ", e);
            CreateAccountResponse createAccountResponse = new CreateAccountResponse();
            createAccountResponse.isSent = false;
            return createAccountResponse;
        }
    }

    protected void onPostExecute(CreateAccountResponse createAccountResponse) {
        super.onPostExecute(createAccountResponse);
        if( listener != null ) {
            listener.onCreateAccountTaskResult(createAccountResponse);
        }
    }
}


