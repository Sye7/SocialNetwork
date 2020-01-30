package com.example.socialnetwork.model;

public class Profile {

    private String id;
    private String userName;
    private String occupation;
    private int posts;
    private int followers;
    private int following;
    private String dp;

    public Profile(String id, String userName, String dp) {
        this.id = id;
        this.userName = userName;
        this.dp = dp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public int getFollowing() {
        return following;
    }

    public void setFollowing(int following) {
        this.following = following;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public Profile() {
    }

    public Profile(String id, String userName, String occupation, int posts, int followers, int following, String dp) {
        this.id = id;
        this.userName = userName;
        this.occupation = occupation;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
        this.dp = dp;
    }
}
