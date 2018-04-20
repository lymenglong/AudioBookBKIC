package com.bkic.lymenglong.audiobookbkic.Models.Player;

import java.io.FileInputStream;

public interface OnCacheCallBack {

    void onSuccess(FileInputStream stream);

    void onError();
}