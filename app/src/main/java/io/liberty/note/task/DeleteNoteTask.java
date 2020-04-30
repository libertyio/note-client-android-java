package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import io.liberty.note.protocol.DeleteNoteRequest;
import io.liberty.note.protocol.DeleteNoteResponse;
import io.liberty.note.LibertyNote;

public class DeleteNoteTask extends AsyncTask<DeleteNoteRequest, Void, DeleteNoteResponse> {
    private DeleteNoteTaskResultListener listener;
    private LibertyNote mApp;
    private DeleteNoteRequest deleteNoteRequest;

    public DeleteNoteTask(DeleteNoteTask.DeleteNoteTaskResultListener listener, LibertyNote mApp, DeleteNoteRequest deleteNoteRequest) {
        this.listener = listener;
        this.mApp = mApp;
        this.deleteNoteRequest = deleteNoteRequest;
    }

    public interface DeleteNoteTaskResultListener {
        void onDeleteNoteTaskResult(DeleteNoteResponse deleteNoteResponse);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected DeleteNoteResponse doInBackground(DeleteNoteRequest... params) {
        Log.d("LIBERTY.IO", "DeleteNoteTask doInBackground...");
        try {
            // Delete note from database
            return mApp.deleteNote(params[0]);
        } catch (IOException e) {
            Log.e("LIBERTY.IO", "doInBackground Error: ", e);
            DeleteNoteResponse deleteNoteResponse = new DeleteNoteResponse();
            deleteNoteResponse.isDeleted = false;
            return deleteNoteResponse;
        }
    }

    protected void onPostExecute(DeleteNoteResponse deleteNoteResponse) {
        super.onPostExecute(deleteNoteResponse);
        if( listener != null ) {
            listener.onDeleteNoteTaskResult(deleteNoteResponse);
        }
    }
}



