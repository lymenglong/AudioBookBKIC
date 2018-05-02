package com.bkic.lymenglong.audiobookbkic.Views.Account.ShowUserInfo;

import com.bkic.lymenglong.audiobookbkic.Models.Account.Utils.User;

import java.util.List;

public interface UserInfoImp {

    void LogoutFailed();

    void LogoutSuccess();

    void DisplayUserDetail();

    List<User> GetCurrentUserDetail();

    void UpdateDetailSuccess(String message);

    void UpdateDetailFailed(String message);

    void UpdatePasswordSuccess(String message);

    void UpdatePasswordFailed(String message);
}
