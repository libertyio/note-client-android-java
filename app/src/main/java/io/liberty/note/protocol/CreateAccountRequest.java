package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class CreateAccountRequest {

    @JsonProperty("name")
    public String name;

    @JsonProperty("email")
    public String email;

    @JsonProperty("agreeToTerms")
    public Boolean agreeToTerms;
}
