package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListCategoryImp {

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);
}
