package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory;

import org.json.JSONArray;
import org.json.JSONException;

public interface ListCategoryImp {

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void ShowListFromSelected();

    void LoadListDataFailed(String jsonMessage);

    void SetTableSelectedData(JSONArray jsonArray) throws JSONException;
}
