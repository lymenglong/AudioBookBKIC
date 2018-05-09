package com.bkic.lymenglong.audiobookbkic.Models.HandleLists.History.Utils;


public class PlaybackHistory {
    private int chapterId;
    private int bookId;
    private int pauseTime;
    private String lastDateTime;

    public PlaybackHistory(int chapterId, int bookId, int pauseTime, String lastDateTime) {
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.pauseTime = pauseTime;
        this.lastDateTime = lastDateTime;
    }

    public int getChapterId() {
        return chapterId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getPauseTime() {
        return pauseTime;
    }

    public String getLastDateTime() {
        return lastDateTime;
    }
}
