package com.example.projectnotes.model;

public class Note {

    private String title;
    private String date;
    private String content;

    public Note(String title, String date, String content) {
        this.title = title;
        this.date = date;
        this.content = content;
    }

    public static Note fromStorage(String line) {
        String[] parts = line.split("\\|\\|\\|");
        if (parts.length < 3) return null;
        return new Note(parts[0], parts[1], parts[2]);
    }

    public String toStorage() {
        return title + "|||" + date + "|||" + content;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getContent() { return content; }
}
