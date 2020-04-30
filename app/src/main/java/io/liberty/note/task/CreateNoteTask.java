package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import io.liberty.note.protocol.CreateNoteRequest;
import io.liberty.note.protocol.CreateNoteResponse;
import io.liberty.note.LibertyNote;

public class CreateNoteTask extends AsyncTask<CreateNoteRequest, Void, CreateNoteResponse> {
    private CreateNoteTaskResultListener listener;
    private LibertyNote mApp;
    private CreateNoteRequest createNoteRequest;

    public CreateNoteTask(CreateNoteTask.CreateNoteTaskResultListener listener, LibertyNote mApp, CreateNoteRequest createNoteRequest) {
        this.listener = listener;
        this.mApp = mApp;
        this.createNoteRequest = createNoteRequest;
    }

    public interface CreateNoteTaskResultListener {
        void onCreateNoteTaskResult(CreateNoteResponse createNoteResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected CreateNoteResponse doInBackground(CreateNoteRequest... params) {
        Log.d("LIBERTY.IO", "CreateNoteTask doInBackground...");
        try {
            // Create note in database
            return mApp.createNote(params[0]);
        } catch (IOException e) {
            Log.e("LIBERTY.IO", "doInBackground Error: ", e);
            CreateNoteResponse createNoteResponse = new CreateNoteResponse();
            createNoteResponse.isCreated = false;
            return createNoteResponse;
        }
    }

    protected void onPostExecute(CreateNoteResponse createNoteResponse) {
        super.onPostExecute(createNoteResponse);
        if( listener != null ) {
            listener.onCreateNoteTaskResult(createNoteResponse);
        }
    }
}


