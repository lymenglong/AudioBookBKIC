package com.bkic.lymenglong.audiobookbkic.Presenters.Reading;

public interface PresenterViewReadingImp {
    void postDataToServer(String HttpUrl, int idChapter);

    void postFavoriteDataToServer(String favoriteURL, int idChapter);
}
