package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class ReportExceptionResponse {
    /**
     * Server indication that the exception report was created
     */
    @JsonProperty("isCreated")
    public Boolean isCreated;

    /**
     * Server-assigned report id
     */
    @JsonProperty("id")
    public String id;

    /**
     * Server message when the exception report is not accepted
     */
    @JsonProperty("error")
    public String error;
}
