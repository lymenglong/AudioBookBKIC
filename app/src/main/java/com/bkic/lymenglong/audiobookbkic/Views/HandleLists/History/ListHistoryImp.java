package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListHistoryImp {

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);
}