package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists;


import android.app.Activity;

public interface PresenterShowListImp {
    void GetDataResponse(String httpUrl);
    void GetSelectedResponse(Activity activity, String keyPost, String idPost , String httpUrl);
}
