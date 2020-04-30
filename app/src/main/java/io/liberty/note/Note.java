package io.liberty.note;

public class Note {
    public Info info;
    public String content;
    public String id;

    public Note(Info info, String content, String id) {
        this.info = info;
        this.content = content;
        this.id = id;
    }

    public Note() {

    }

    public static class Info{
        public String title;
        public String author;

//        public Info(String title, String author) {
//            this.title = title;
//            this.author = author;
//        }
    }
}
