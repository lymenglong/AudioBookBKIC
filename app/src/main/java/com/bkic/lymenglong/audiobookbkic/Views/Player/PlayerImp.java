package com.bkic.lymenglong.audiobookbkic.Views.Player;

public interface PlayerImp {

    Boolean initCheckChapterDownloadStatus();

    void PrepareChapter();

    void AddReviewBookToServer();

    void UpdateHistoryData();

    void UpdateHistorySuccess(String message);

    void UpdateHistoryFailed(String message);

    void UpdateFavoriteFailed(String message);

    void UpdateFavoriteSuccess(String message);

    void UpdateReviewSuccess(String message);

    void UpdateReviewFailed(String message);
}
