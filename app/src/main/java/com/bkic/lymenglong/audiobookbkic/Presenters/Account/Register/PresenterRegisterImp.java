package com.bkic.lymenglong.audiobookbkic.Presenters.Account.Register;

import android.app.Activity;

import java.util.HashMap;

public interface PresenterRegisterImp {

    void Register(Activity activity, HashMap<String, String> ResultHash, String HttpUrl_API);
}
