package com.bkic.lymenglong.audiobookbkic.Presenters.History.UpdateHistory;

public interface PresenterUpdateHistoryImp {
    //Update Favorite Or History To Server (addHistory, addFavorite)
    void RequestUpdateToServer(String actionRequest, String userId, String bookId, String insertTime);
}
