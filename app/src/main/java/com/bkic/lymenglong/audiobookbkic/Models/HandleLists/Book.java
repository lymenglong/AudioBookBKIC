package com.bkic.lymenglong.audiobookbkic.Models.HandleLists;


public class Book {
    private int id;
    private String title;
    private String content;
    private Boolean status; //when status is true, it means that you select audio book
    private String FileUrl;

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }

    private int CategoryId;

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public Book(int id, String title, String content, String fileUrl) {

        this.id = id;
        this.title = title;
        this.content = content;
        FileUrl = fileUrl;
    }

    public Book(int id, String title, String content, Boolean status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    public Book(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }
    public Book(int id, String title) {
        this.id = id;
        this.title = title;
    }
    public Book() {
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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
