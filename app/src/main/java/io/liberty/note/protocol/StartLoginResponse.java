package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class StartLoginResponse {

    /**
     * Received from liberty.io server
     */
    @JsonProperty("isAuthenticated")
    public Boolean isAuthenticated;

    /**
     * Received from liberty.io server
     */
    @JsonProperty("forward")
    public String forward;

    /**
     * Received from liberty.io server
     */
    @JsonProperty("interactionId")
    public String interactionId;

    /**
     * Received from liberty.io server
     */
    @JsonProperty("error")
    public String error;
}
