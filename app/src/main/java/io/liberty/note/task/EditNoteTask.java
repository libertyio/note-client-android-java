package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.util.Arrays;

import io.liberty.note.EditNoteRequest;
import io.liberty.note.EditNoteResponse;
import io.liberty.note.LibertyNote;

public class EditNoteTask extends AsyncTask<EditNoteRequest, Void, EditNoteResponse> {
    private EditNoteTaskResultListener listener;
    private LibertyNote mApp;
    private EditNoteRequest editNoteRequest;

    public EditNoteTask(EditNoteTask.EditNoteTaskResultListener listener, LibertyNote mApp, EditNoteRequest editNoteRequest) {
        this.listener = listener;
        this.mApp = mApp;
        this.editNoteRequest = editNoteRequest;
    }

    public interface EditNoteTaskResultListener {
        void onEditNoteTaskResult(EditNoteResponse editNoteResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected EditNoteResponse doInBackground(EditNoteRequest... params) {
        Log.d("CRYPTIUM", "EditNoteTask doInBackground...");
        try {
            // Edit note in database
            return mApp.editNote(params[0]);
        } catch (IOException e) {
            Log.e("CRYPTIUM", "doInBackground Error: ", e);
            EditNoteResponse editNoteResponse = new EditNoteResponse();
            editNoteResponse.isEdited = false;
            return editNoteResponse;
        }
    }

    protected void onPostExecute(EditNoteResponse editNoteResponse) {
        super.onPostExecute(editNoteResponse);
        if( listener != null ) {
            listener.onEditNoteTaskResult(editNoteResponse);
        }
    }
}


