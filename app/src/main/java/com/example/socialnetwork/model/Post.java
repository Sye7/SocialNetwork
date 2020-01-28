package com.example.socialnetwork.model;

public class Post {
    private String id;
    private String photo;
    private String caption;
    private int likes;
    private String dp;
    private String userName;

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Post(String id, String photo, String caption, int likes, String dp, String userName) {
        this.id = id;
        this.photo = photo;
        this.caption = caption;
        this.likes = likes;
        this.dp = dp;
        this.userName = userName;
    }

    public Post() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

}
