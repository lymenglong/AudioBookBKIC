package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBookType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public interface ListBookTypeImp {

    void SetTableData(JSONObject jsonObject) throws JSONException;

    void ShowListBookType();

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void ShowListFromSelected();
}
