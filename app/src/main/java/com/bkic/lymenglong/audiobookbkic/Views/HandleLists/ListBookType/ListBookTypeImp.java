package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBookType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by KHAIMINH2 on 3/28/2018.
 */

public interface ListBookTypeImp {

    void SetTableData(JSONObject jsonObject) throws JSONException;

    void ShowProgressDialog();

    void DismissDialog();

    void ShowListBookType();

    void CompareDataPhoneWithServer(JSONArray jsonArray);

    void SetTableSelectedData(JSONObject jsonObject) throws JSONException;

    void ShowListFromSelected();
}
