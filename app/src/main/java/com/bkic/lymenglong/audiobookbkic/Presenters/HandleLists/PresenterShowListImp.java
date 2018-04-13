package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists;


import android.app.Activity;

import java.util.HashMap;

public interface PresenterShowListImp {
    void GetSelectedResponse(Activity activity, HashMap<String,String> ResultHash, String httpUrl);
}
