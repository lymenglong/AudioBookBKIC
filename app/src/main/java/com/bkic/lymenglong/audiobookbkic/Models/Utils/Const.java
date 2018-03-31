package com.bkic.lymenglong.audiobookbkic.Models.Utils;

public final class Const {

    //region HTTP URL

    //<editor-fold desc="Old URL">
    public static final String HttpURL_Login = "http://20121969.tk/audiobook/mobile_registration/login.php";
    public static final String HttpURL_GetUser = "http://20121969.tk/audiobook/mobile_registration/get_user.php";
    public static final String HttpUrl_InsertHistory = "http://20121969.tk/audiobook/mobile_registration/history.php";
    public static final String HttpUrl_InsertFavorite = "http://20121969.tk/audiobook/mobile_registration/favorite.php";
    //</editor-fold>

    //<editor-fold desc="New URL">
    public static final String HttpUrl_AllBookTypeData = "http://20121969.tk/SachNoiBKIC/AllBookTypeData.php";
    public static final String HttpUrl_FilterFavoriteData = "http://20121969.tk/SachNoiBKIC/FilterFavoriteData.php";
    public static final String HttpUrl_ALLMenuData = "http://20121969.tk/SachNoiBKIC/AllMenuData.php";
    public static final String HttpURL_FilterBookData = "http://20121969.tk/SachNoiBKIC/FilterBookData.php";
    public static final String HttpURL_FilterCategoryData = "http://20121969.tk/SachNoiBKIC/FilterCategoryData.php";
    public static final String HttpUrl_FilterHistoryData = "http://20121969.tk/SachNoiBKIC/FilterHistoryData.php";
    //</editor-fold>

    //endregion

    //region DBHelper Constants
    public static final String DB_NAME = "audiobook.sqlite";
    public static final int DB_VERSION = 1;
    public static final String CREATE_TABLE_MENU =
            "CREATE TABLE IF NOT EXISTS menu(" +
                    "Id INTEGER PRIMARY KEY, " +
                    "Name VARCHAR(255));";
    public static final String CREATE_TABLE_BOOK_TYPE =
            "CREATE TABLE IF NOT EXISTS booktype(" +
                    "Id INTEGER PRIMARY KEY, " +
                    "Name VARCHAR(255));";
    public static final String CREATE_TABLE_HISTORY =
            "CREATE TABLE IF NOT EXISTS history(" +
                    "IdBook INTEGER PRIMARY KEY, " +
                    "IdUser INTEGER, " +
                    "InsertTime INTEGER, " +
                    "PauseTime INTEGER);";
    public static final String CREATE_TABLE_FAVORITE =
            "CREATE TABLE IF NOT EXISTS favorite(" +
                    "IdBook INTEGER PRIMARY KEY, " +
                    "IdUser INTEGER, " +
                    "InsertTime INTEGER, " +
                    "Status INTEGER);";
    public static final String CREATE_TABLE_BOOK =
            "CREATE TABLE IF NOT EXISTS book " +
                    "(Id INTEGER PRIMARY KEY, " +
                    "Name VARCHAR(255), " +
                    "CategoryID INTEGER, " +
                    "FileUrl VARCHAR(255), " +
                    "TextContent LONGTEXT);";
    public static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS category(" +
                    "Id INTEGER PRIMARY KEY, " +
                    "Name VARCHAR(255), " +
                    "TypeID INTEGER);";
    //endregion
}
