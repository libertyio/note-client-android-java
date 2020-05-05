package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class Note {
    @JsonProperty("info")
    public Info info;
    @JsonProperty("content")
    public String content;
    @JsonProperty("id")
    public String id;

    public static Note createNote(Info info, String content, String id) {
        Note note = new Note();
        note.info = info;
        note.content = content;
        note.id = id;
        return note;
    }

    public static class Info{
        @JsonProperty("title")
        public String title;
        @JsonProperty("author")
        public String author;
    }
}
