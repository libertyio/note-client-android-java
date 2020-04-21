package io.liberty.note;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class EditNoteRequest {
    @JsonIgnore
    public String id;

    @JsonProperty("info")
    public Info info;

    @JsonProperty("content")
    public String content;

    public static class Info{
        @JsonProperty("title")
        public String title;

        @JsonProperty("author")
        public String author;
    }
}
