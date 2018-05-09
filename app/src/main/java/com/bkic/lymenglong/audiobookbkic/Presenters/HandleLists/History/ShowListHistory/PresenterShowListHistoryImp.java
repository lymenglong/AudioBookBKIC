package com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.History.ShowListHistory;


import android.app.Activity;

import java.util.HashMap;

public interface PresenterShowListHistoryImp {
    void GetSelectedResponse(Activity activity, HashMap<String,String> ResultHash, String httpUrl);
}
