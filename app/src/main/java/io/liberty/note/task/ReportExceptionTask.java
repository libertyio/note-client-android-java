package io.liberty.note.task;

import android.app.Activity;
import android.util.Log;

import org.underlake.sdk.http.HttpJsonRequestBuilder;
import org.underlake.sdk.http.HttpJsonRequestFactory;
import org.underlake.sdk.http.HttpRequestBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.liberty.note.EndpointConfiguration;
import io.liberty.note.LibertyNote;
import io.liberty.note.MapperUtil;
import io.liberty.note.async.BackgroundTask;
import io.liberty.note.protocol.ReportExceptionRequest;
import io.liberty.note.protocol.ReportExceptionResponse;
import my.apache.http.client.methods.HttpUriRequest;
import my.apache.http.client.utils.URIBuilder;

public class ReportExceptionTask extends BackgroundTask<ReportExceptionRequest, ReportExceptionResponse> {
    private final static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ReportExceptionTask.class);
    private LibertyNote app;
    public ReportExceptionTask(LibertyNote app, Activity activity, ReportExceptionRequest reportExceptionRequest) {
        super(activity, reportExceptionRequest);
        this.app = app;
    }

    @Override
    protected ReportExceptionResponse doInBackground(ReportExceptionRequest reportExceptionRequest) throws Exception {
        EndpointConfiguration endpointConfiguration = app.getEndpointConfiguration();
        String jsonString = MapperUtil.CACHE.getWriter(ReportExceptionRequest.class).writeValueAsString(reportExceptionRequest);
        LOG.debug("reportException request: {}", jsonString);
        URI uri;
        try {
            URIBuilder uribuilder = new URIBuilder(endpointConfiguration.serviceEndpointUrl);
            uribuilder.setPath(endpointConfiguration.REPORT_EXCEPTION_PATH);
            uri = uribuilder.build();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        HttpUriRequest httpRequest = HttpJsonRequestFactory.createHttpPostString(uri.toString(), jsonString);
        String httpPostResult = app.getHttpAgent().getStringWithContentType(httpRequest, endpointConfiguration.APPLICATION_JSON);
        ReportExceptionResponse reportExceptionResponse = MapperUtil.CACHE.getReader(ReportExceptionResponse.class).readValue(httpPostResult);
        LOG.debug("reportException response: isCreated: {} id: {} error: {}", reportExceptionResponse.isCreated, reportExceptionResponse.id, reportExceptionResponse.error);
        return reportExceptionResponse;
    }
}
