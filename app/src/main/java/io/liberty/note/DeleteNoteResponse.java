package io.liberty.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class DeleteNoteResponse {

    @JsonProperty("isDeleted")
    public Boolean isDeleted;

    @JsonProperty("error")
    public Boolean error;
}
