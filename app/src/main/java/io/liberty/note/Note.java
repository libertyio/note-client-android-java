package io.liberty.note;

public class Note {
    public String title;
    public String body;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
