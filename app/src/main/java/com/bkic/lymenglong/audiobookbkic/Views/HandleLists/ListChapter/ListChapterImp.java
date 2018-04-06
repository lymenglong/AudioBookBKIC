package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListChapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListChapterImp {
    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();
}
