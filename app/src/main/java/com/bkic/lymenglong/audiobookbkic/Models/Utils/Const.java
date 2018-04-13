package com.bkic.lymenglong.audiobookbkic.Models.Utils;

public final class Const {

    //region HTTP URL

    //<editor-fold desc="Old URL">
//    public static final String HttpURL_Login = "http://20121969.tk/audiobook/mobile_registration/login.php";
//    public static final String HttpURL_GetUser = "http://20121969.tk/audiobook/mobile_registration/get_user.php";
    public static final String HttpUrl_InsertHistory = "http://20121969.tk/audiobook/mobile_registration/history.php";
    public static final String HttpUrl_InsertFavorite = "http://20121969.tk/audiobook/mobile_registration/favorite.php";
    //</editor-fold>

    //<editor-fold desc="New URL">
//    public static final String HttpUrl_AllBookTypeData = "http://20121969.tk/SachNoiBKIC/AllBookTypeData.php";
    public static final String HttpUrl_FilterFavoriteData = "http://20121969.tk/SachNoiBKIC/FilterFavoriteData.php";
//    public static final String HttpUrl_ALLMenuData = "http://20121969.tk/SachNoiBKIC/AllMenuData.php";
//    public static final String HttpURL_FilterBookData = "http://20121969.tk/SachNoiBKIC/FilterBookData.php";
//    public static final String HttpURL_FilterCategoryData = "http://20121969.tk/SachNoiBKIC/FilterCategoryData.php";
    public static final String HttpUrl_FilterHistoryData = "http://20121969.tk/SachNoiBKIC/FilterHistoryData.php";
    //</editor-fold>

    //<editor-fold desc="New API URL">
//    public static final String HttpURL_API = "http://jsontest123.000webhostapp.com/api/index.php";
    public static final String HttpURL_API = "http://audiobook.aseantech.org/api/";
    public static final String HttpURL_Audio = "http://audiobook.aseantech.org/wp-content/uploads/";
    //</editor-fold>

    //endregion

    //region DBHelper Constants
    public static final String DB_NAME = "audiobook.sqlite";
    public static final int DB_VERSION = 1;

    public static final String CREATE_TABLE_MENU =
            "CREATE TABLE IF NOT EXISTS menu(" +
                    "Id INTEGER PRIMARY KEY, " +
                    "Title VARCHAR(255));";

    public static final String INSERT_MENU_VALUES =
            "INSERT INTO menu VALUES" +
                    "('1', 'Thể Loại Sách'), " +
                    "('2', 'Lịch Sử'), " +
                    "('3', 'Yêu Thích'), " +
                    "('4', 'Tài Khoản'), " +
                    "('5', 'Hướng Dẫn'), " +
                    "('100', 'Thoát')" +
                    ";";

    public static final String CREATE_TABLE_CATEGORY =
            "CREATE TABLE IF NOT EXISTS category" +
                    "(" +
                    "CategoryId INTEGER PRIMARY KEY, " +
                    "CategoryTitle VARCHAR(255), " +
                    "CategoryDescription VARCHAR(255), " +
                    "CategoryParent INTEGER, " +
                    "NumOfChild INTEGER" +
                    ");";

    public static final String CREATE_TABLE_BOOK =
            "CREATE TABLE IF NOT EXISTS books " +
                    "(" +
                    "BookId INTEGER PRIMARY KEY, " +
                    "BookTitle VARCHAR(255), " +
                    "BookAuthor VARCHAR(255), " +
                    "BookPublishDate VARCHAR(255), " +
                    "BookImage VARCHAR(255), " +
                    "BookContent VARCHAR(255), " +
                    "BookLength INTEGER, " +
                    "BookURL VARCHAR(255), " +
                    "CategoryId INTEGER, " +
                    "NumOfChapter INTEGER " +
                    ");";
    public static final String CREATE_TABLE_CHAPTER =
            "CREATE TABLE IF NOT EXISTS chapter " +
                    "(" +
                    "ChapterId INTEGER PRIMARY KEY, " +
                    "ChapterTitle VARCHAR(255), " +
                    "ChapterUrl VARCHAR(255), " +
                    "ChapterLength INTEGER, " +
                    "BookId INTEGER " +
                    ");";

    public static final String CREATE_TABLE_HISTORY =
            "CREATE TABLE IF NOT EXISTS history" +
                    "(" +
                    "IdBook INTEGER PRIMARY KEY, " +
                    "IdUser INTEGER, " +
                    "InsertTime INTEGER, " +
                    "PauseTime INTEGER" +
                    ");";

    public static final String CREATE_TABLE_FAVORITE =
            "CREATE TABLE IF NOT EXISTS favorite" +
                    "(" +
                    "IdBook INTEGER PRIMARY KEY, " +
                    "IdUser INTEGER, " +
                    "InsertTime INTEGER, " +
                    "Status INTEGER" +
                    ");";

    public static final String CREATE_TABLE_REVIEW =
            "CREATE TABLE IF NOT EXISTS favorite" +
                    "(" +
                    "IdUser INTEGER PRIMARY KEY, " +
                    "IdUser INTEGER, " +
                    "InsertTime INTEGER, " +
                    "Status INTEGER" +
                    ");";

    public static String SELECT_CATEGORY_BY_PARENT_ID (int parentId){
        return "SELECT * FROM category WHERE CategoryParent = '"+parentId+"'";
    }

    public static String UPDATE_CATEGORY_DATA (
            int categoryIdValue,
            String categoryTitleValue,
            String categoryDescription,
            int categoryParent,
            int numOfChild){
        return "UPDATE category SET " +
                    "CategoryTitle = '"+categoryTitleValue+"', " +
                    "CategoryDescription = '"+categoryDescription+"', " +
                    "CategoryParent = '"+categoryParent+"', " +
                    "NumOfChild = '"+numOfChild+"' " +
                        "WHERE " +
                            "CategoryId = '"+categoryIdValue+"';";

    }

    public static String SELECT_ALL_BOOK_BY_CATEGORY_ID(int categoryId){
        return "SELECT * FROM books WHERE CategoryId = '"+categoryId+"'";
    }

    //endregion
}
