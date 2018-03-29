package com.bkic.lymenglong.audiobookbkic.Presenters.Account.ShowUserInfo;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import com.bkic.lymenglong.audiobookbkic.Views.Account.ShowUserInfo.UserInfoActivity;


public class PresenterUserInfo implements PresenterUserInfoImp {
    private UserInfoActivity userInfoActivity;

    public PresenterUserInfo(UserInfoActivity userInfoActivity) {
        this.userInfoActivity = userInfoActivity;
    }

    @Override
    public void ShowAlertLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(userInfoActivity);
//        builder.setTitle("Đãng Xuất Tài Khoản");
        builder.setMessage("Bạn có muốn đăng xuất không?");
        builder.setCancelable(false);
        builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userInfoActivity.LogoutFailed();
            }
        });
        builder.setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                userInfoActivity.LogoutSuccess();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
