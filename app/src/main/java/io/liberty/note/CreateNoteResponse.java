package io.liberty.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class CreateNoteResponse {

    @JsonProperty("isCreated")
    public Boolean isCreated;

    @JsonProperty("id")
    public String id;

    @JsonProperty("error")
    public Boolean error;
}
