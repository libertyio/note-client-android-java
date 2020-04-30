package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class DeleteNoteRequest {
    @JsonIgnore
    public String id;
}
