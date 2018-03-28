package com.bkic.lymenglong.audiobookbkic.Models.HandleLists;


public class Home {
    private int id;
    private String title;

    public Home() {
    }

    public Home(int id, String title) {
        this.id = id; this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
