package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.Favorite.UpdateFavorite;

public interface PresenterUpdateFavoriteImp {
    //Update Favorite Or History To Server (addHistory, addFavorite)
    void RequestUpdateToServer(String actionRequest, String userId, String bookId, String insertTime);
}