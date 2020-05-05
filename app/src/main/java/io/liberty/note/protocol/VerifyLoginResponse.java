package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author jbuhacoff
 */
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class VerifyLoginResponse {

    @JsonProperty("isAuthenticated")
    public Boolean isAuthenticated;

    @JsonProperty("error")
    public String error;

}