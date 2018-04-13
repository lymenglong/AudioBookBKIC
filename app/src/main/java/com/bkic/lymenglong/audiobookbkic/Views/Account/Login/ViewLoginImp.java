package com.bkic.lymenglong.audiobookbkic.Views.Account.Login;

import org.json.JSONException;

public interface ViewLoginImp {
    void LoginSuccess(String message) throws JSONException;
    void LoginFailed(String message);
}
