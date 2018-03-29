package com.bkic.lymenglong.audiobookbkic.Views.Main;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KHAIMINH2 on 3/28/2018.
 */

public interface MainImp {
    void ShowListMenu();
    void SetMenuData(JSONObject jsonObject) throws JSONException;
    void ShowProgressDialog();
}
