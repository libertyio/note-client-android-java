package io.liberty.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class StartLoginResponse {

    @JsonProperty("appLinkUrl")
    public String appLinkUrl;

    @JsonProperty("interactionId")
    public String interactionId;

    @JsonProperty("error")
    public Boolean error;
}
