package com.bkic.lymenglong.audiobookbkic.Models.History.Utils;


public class History {
    private int id;
    private String title;
    private String content;
    private int pauseTime;
    private int insertTime;
    private String fileUrl;
    private int categoryId;


    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public int getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(int insertTime) {
        this.insertTime = insertTime;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(int pauseTime) {
        this.pauseTime = pauseTime;
    }

    public History(int id, String title, String content, int pauseTime, int insertTime, String fileUrl, int categoryId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.pauseTime = pauseTime;
        this.insertTime = insertTime;
        this.fileUrl = fileUrl;
        this.categoryId = categoryId;
    }

    public History(int id, String title, String content, String fileUrl) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
    }

    public History(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public History(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public History() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
