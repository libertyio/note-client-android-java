package io.liberty.note.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import io.liberty.note.protocol.Note;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NoteList {
    @JsonProperty("list")
    public ArrayList<Note> list;

    public static NoteList createNoteList(ArrayList<Note> list) {
        NoteList noteList = new NoteList();
        noteList.list = list;
        return noteList;
    }
}
