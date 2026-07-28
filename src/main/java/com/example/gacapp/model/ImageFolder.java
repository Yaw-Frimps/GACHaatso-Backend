package com.example.gacapp.model;

public enum ImageFolder {

    MEMBERS("gacapp/members"),

    EVENTS("gacapp/events"),

    ANNOUNCEMENTS("gacapp/announcements"),

    USERS("gacapp/users");

    private final String folder;

    ImageFolder(String folder){
        this.folder = folder;
    }

    public String getFolder(){
        return folder;
    }

}