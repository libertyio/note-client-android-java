package io.liberty.note;

import org.underlake.sdk.http.HttpAgentHelper;
import my.apache.http.Header;
import my.apache.http.HttpResponse;
import my.apache.http.client.methods.HttpUriRequest;

/**
 *
 * @author jbuhacoff
 */

public class ClientTokenHelper implements HttpAgentHelper {
    private String clientToken;

    public ClientTokenHelper(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    @Override
    public void beforeRequest(HttpUriRequest httpRequest) {
        if (clientToken != null) {
            httpRequest.addHeader("Client-Token", clientToken);
        }
    }

    @Override
    public void afterResponse(HttpResponse httpResponse) {
        Header responseClientTokenHeader = httpResponse.getFirstHeader("Client-Token");
        if(responseClientTokenHeader != null) {
            String value = responseClientTokenHeader.getValue();
            if(value != null && !value.equals(clientToken)) {
                clientToken = value;
            }
        }
    }

}



