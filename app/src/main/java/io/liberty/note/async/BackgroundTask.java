package io.liberty.note.async;

import android.app.Activity;

import androidx.annotation.WorkerThread;

import io.liberty.note.LibertyNote;

public abstract class BackgroundTask<Input,Output> {
    final private Activity activity;
    final private Input input;
    private Output output;
    private Runnable onResult;
    private Runnable onError;

    public BackgroundTask(Activity activity, Input input) {
        this.activity = activity;
        this.input = input;
    }

    public void setOnResult(Runnable runnable) {
        onResult = runnable;
    }

    public void setOnError(Runnable runnable) {
        onError = runnable;
    }

    public Input getInput() {
        return input;
    }

    public Output getOutput() {
        return output;
    }

    public void start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    output = doInBackground(input);
                    if (onResult != null) {
                        activity.runOnUiThread(onResult);
                    }
                }
                catch (Exception e) {
                    if (onError != null) {
                        activity.runOnUiThread(onError);
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Override this method to do the work
     * @param input
     * @return
     */
    @WorkerThread
    protected abstract Output doInBackground(Input input) throws Exception;

}
