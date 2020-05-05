package io.liberty.note;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import io.liberty.note.protocol.ReportExceptionRequest;
import io.liberty.note.protocol.ReportExceptionResponse;
import io.liberty.note.task.ReportExceptionTask;

public class ReportExceptionActivity extends AppCompatActivity {
    private final static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ReportExceptionActivity.class);
    public static final String EXCEPTION_FAULT = "io.liberty.note.ReportException.FAULT";
    public static final String EXCEPTION_CLASS = "io.liberty.note.ReportException.CLASS";
    public static final String EXCEPTION_STACKTRACE = "io.liberty.note.ReportException.STACKTRACE";
    public static final String EXCEPTION_INFO = "io.liberty.note.ReportException.INFO";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_exception);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        ImageView imageViewBack = findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent homeIntent = new Intent(NoteActivity.this, HomeActivity.class);
//                startActivity(homeIntent);
                finish();
            }
        });

    }


    public void onShare(View view) {
        progressBar.setVisibility(View.VISIBLE);

        ReportExceptionRequest reportExceptionRequest = new ReportExceptionRequest();
        reportExceptionRequest.deviceManufacturer = Build.MANUFACTURER;
        reportExceptionRequest.deviceModel = Build.MODEL;
        reportExceptionRequest.systemName = "Android";
        reportExceptionRequest.systemVersion = Build.VERSION.RELEASE;
        reportExceptionRequest.applicationId = BuildConfig.APPLICATION_ID;
        reportExceptionRequest.applicationVersion = BuildConfig.VERSION_NAME;

        Intent intent = getIntent();
        reportExceptionRequest.className = intent.getStringExtra(EXCEPTION_CLASS);
        reportExceptionRequest.fault = intent.getStringExtra(EXCEPTION_FAULT);
        reportExceptionRequest.stacktrace = intent.getStringExtra(EXCEPTION_STACKTRACE);
        reportExceptionRequest.info = intent.getStringExtra(EXCEPTION_INFO);

        final ReportExceptionTask reportExceptionTask = new ReportExceptionTask((LibertyNote)getApplication(), this, reportExceptionRequest);
        reportExceptionTask.setOnResult(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                ReportExceptionResponse reportExceptionResponse = reportExceptionTask.getOutput();
                if (reportExceptionResponse != null && reportExceptionResponse.isCreated != null && reportExceptionResponse.isCreated ) {
                    setResult(RESULT_OK);
                    finish();
                }
                else {
                    View v = findViewById(android.R.id.content);
                    showSnackbar(v, getString(R.string.report_exception_failed));
                }
            }
        });
        reportExceptionTask.setOnError(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                View v = findViewById(android.R.id.content);
                showSnackbar(v, getString(R.string.report_exception_failed));
            }
        });

        reportExceptionTask.start();
    }

    public void showSnackbar(View v, String message) {
        CoordinatorLayout coordinatorLayout = findViewById(R.id.reportExceptionCoordinatorLayout);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        v = snackbar.getView();
        CoordinatorLayout.LayoutParams params=(CoordinatorLayout.LayoutParams)v.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.gravity = Gravity.CENTER_HORIZONTAL;
        v.setLayoutParams(params);
        snackbar.show();
    }

    public static class ExtraInfoFault {
        @JsonProperty("fault")
        final public String fault;
        @JsonProperty("className")
        final public String className;

        public  ExtraInfoFault(String className) {
            this.fault = "extra-info-fault";
            this.className = className;
        }
    }

    public static class ExtraInfoNull {
        @JsonProperty("fault")
        final public String fault;

        public  ExtraInfoNull() {
            this.fault = "extra-info-null";
        }
    }

    /**
     *
     * @param context activity reporting the exception
     * @param fault unique (within this app) programming constant describing the location and nature of the problem
     * @param e exception that was caught
     * @param info should be a fault object ready to serialize to JSON; may be null; if present it should have @JsonProperty annotations so the field names won't be lost when building the compressed app
     * @return the intent to start
     */
    public static Intent createIntent(Context context, String fault, Throwable e, Object info) {
        // get the stack trace
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);
        e.printStackTrace(writer);
        writer.close();
        String stacktrace = new String(out.toByteArray()); // first line is "<exception class>: <message>" followed by the trace
        // get the extra info
        String infoJson;
        try {
            if (info == null) {
                infoJson = MapperUtil.CACHE.getWriter(ExtraInfoNull.class).writeValueAsString(new ExtraInfoNull());
            }
            else {
                infoJson = MapperUtil.CACHE.getWriter(info.getClass()).writeValueAsString(info);
            }
        }
        catch(IOException infoException) {
            LOG.error("prepareIntent: cannot write extraInfo", infoException);
            try {
                ExtraInfoFault extraInfoFault = new ExtraInfoFault(info.getClass().getCanonicalName());
                infoJson = MapperUtil.CACHE.getWriter(ExtraInfoFault.class).writeValueAsString(extraInfoFault);
            }
            catch(IOException infoException2) {
                LOG.error("prepareIntent: cannot write extraInfoFault", infoException2);
                infoJson = null;
            }
        }
        // prepare the intent
        Intent intent = new Intent(context, ReportExceptionActivity.class);
        intent.putExtra(ReportExceptionActivity.EXCEPTION_CLASS, context.getClass().getName());
        intent.putExtra(ReportExceptionActivity.EXCEPTION_FAULT, fault);
        intent.putExtra(ReportExceptionActivity.EXCEPTION_STACKTRACE, stacktrace);
        intent.putExtra(ReportExceptionActivity.EXCEPTION_INFO, infoJson);
        return intent; // caller should do startActivity(intent);
    }

}
