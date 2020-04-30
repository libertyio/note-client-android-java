package io.liberty.note.task;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import io.liberty.note.LibertyNote;

public class GetNoteTask extends AsyncTask<Void, Void, GetNoteTask.GetNoteTaskResult> {
    private GetNoteTaskResultListener listener;
    private LibertyNote mApp;

    public GetNoteTask(GetNoteTask.GetNoteTaskResultListener listener, LibertyNote mApp) {
        this.listener = listener;
        this.mApp = mApp;
    }

    public interface GetNoteTaskResultListener {
        void onGetNoteTaskResult(GetNoteTaskResult result);
    }

    public static class GetNoteTaskResult {
        private boolean passed;
        public GetNoteTaskResult(boolean passed) {
            this.passed = passed;
        }
        public boolean isPassed() {
            return passed;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected GetNoteTaskResult doInBackground(Void... params) {
        Log.d("LIBERTY.IO", "GetNoteTask doInBackground...");
        try {
            mApp.getNoteList();
        } catch (IOException e) {
            Log.d("LIBERTY.IO", "getNoteTask doInBackground ERROR");
            e.printStackTrace();
        }
        return new GetNoteTaskResult(true);
    }

    protected void onPostExecute(GetNoteTaskResult getNoteTaskResult) {
        super.onPostExecute(getNoteTaskResult);
        if( listener != null ) {
            listener.onGetNoteTaskResult(getNoteTaskResult);
        }
    }
}

