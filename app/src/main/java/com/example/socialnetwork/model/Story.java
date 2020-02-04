package com.example.socialnetwork.model;

public class Story {

    String id;
    String name;
    String dp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public Story() {

    }

    public Story(String id, String name, String dp) {
        this.id = id;
        this.name = name;
        this.dp = dp;
    }
}
