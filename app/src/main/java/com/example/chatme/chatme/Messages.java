package com.example.chatme.chatme;

public class Messages {

    private String name;
    private String comment;

    public Messages()
    {

    }

    public Messages(String name, String comment)
    {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
}