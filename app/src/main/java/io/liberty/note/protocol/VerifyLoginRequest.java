package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author jbuhacoff
 */

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class VerifyLoginRequest {

    @JsonProperty("interactionId")
    public String interactionId;

    @JsonProperty("verifyToken")
    public String verifyToken;
}