package com.bkic.lymenglong.audiobookbkic.Presenters.Favorite;


import android.app.Activity;

import java.util.HashMap;

public interface PresenterShowListFavoriteImp {
    void GetSelectedResponse(Activity activity, HashMap<String,String> ResultHash, String httpUrl);
}
