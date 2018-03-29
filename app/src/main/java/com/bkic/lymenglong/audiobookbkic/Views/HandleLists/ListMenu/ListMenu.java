package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListMenu;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.FavoriteAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.HistoryAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.MenuAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Account.ShowUserInfo.UserInfoActivity;
import com.bkic.lymenglong.audiobookbkic.Views.Help.HelpActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListMenu extends AppCompatActivity implements ListMenuImp{
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private MenuAdapter adapter;
    private HistoryAdapter historyAdapter;
    private FavoriteAdapter favoriteAdapter;
    private String titleHome;
    private int idMenu;
    private Activity activity = ListMenu.this;
    private Session session;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList <Chapter> list = new ArrayList<>();
    private Chapter tempModel;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        setTitle(titleHome);
        initView();
        initDatabase();
        initObject();
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleHome = getIntent().getStringExtra("titleHome");
        idMenu = getIntent().getIntExtra("idHome", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        session = new Session(activity);
        progressBar = findViewById(R.id.progressBar);
        imRefresh = findViewById(R.id.imRefresh);
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleHome, true);
        listChapter = findViewById(R.id.listView);
    }

    private void SetTempModel(Chapter tempModel, JSONObject jsonObject, String tableSwitched) throws JSONException {
        switch (tableSwitched){
            case "history":
                tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));
                tempModel.setTitle(jsonObject.getString("Name"));
                tempModel.setCategoryId(Integer.parseInt(jsonObject.getString("CategoryId")));
                tempModel.setPauseTime(Integer.parseInt(jsonObject.getString("PauseTime")));
                tempModel.setInsertTime(Integer.parseInt(jsonObject.getString("InsertTime")));
                tempModel.setContent(jsonObject.getString("TextContent"));
                tempModel.setFileUrl(jsonObject.getString("FileUrl"));
                break;
            case "favorite":
                tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));
                tempModel.setTitle(jsonObject.getString("Name"));
                tempModel.setCategoryId(Integer.parseInt(jsonObject.getString("CategoryId")));
                tempModel.setStatus(Integer.parseInt(jsonObject.getString("Status")));
                tempModel.setInsertTime(Integer.parseInt(jsonObject.getString("InsertTime")));
                tempModel.setContent(jsonObject.getString("TextContent"));
                tempModel.setFileUrl(jsonObject.getString("FileUrl"));
                break;
        }
    }

    private void SetUpdateTableData(Chapter arrayModel, String tableName) {
        String UPDATE_DATA;
        switch (tableName){
            case "booktype":
                UPDATE_DATA = "UPDATE '"+tableName+"' SET Name = '"+arrayModel.getTitle()+"' WHERE Id = '"+arrayModel.getId()+"';";
                dbHelper.QueryData(UPDATE_DATA);
                break;
            case "history":
                UPDATE_DATA = "UPDATE history SET " +
                        "InsertTime = '"+arrayModel.getInsertTime()+"', " +
                        "PauseTime = '"+arrayModel.getPauseTime()+"' " +
                        "WHERE " +
                        "IdBook = '"+arrayModel.getId()+"' AND IdUser = '"+session.getUserIdLoggedIn()+"';";
                dbHelper.QueryData(UPDATE_DATA);
                break;
            case "favorite":
                try {
                    UPDATE_DATA = "UPDATE favorite SET " +
                            "InsertTime = '"+arrayModel.getInsertTime()+"', " +
                            "Status = '"+arrayModel.getStatus()+"' " +
                            "WHERE " +
                            "IdBook = '"+arrayModel.getId()+"' AND IdUser = '"+session.getUserIdLoggedIn()+"';";
                    dbHelper.QueryData(UPDATE_DATA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    private void initDatabase() {
        String DB_NAME = "menu.sqlite";
        int DB_VERSION = 1;
        String CREATE_TABLE_BOOKTYPE = "CREATE TABLE IF NOT EXISTS booktype(Id INTEGER PRIMARY KEY, Name VARCHAR(255));";
        String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS history(" +
                "IdBook INTEGER PRIMARY KEY, " +
                "IdUser INTEGER, " +
                "InsertTime INTEGER, " +
                "PauseTime INTEGER);";
        String CREATE_TABLE_FAVORITE = "CREATE TABLE IF NOT EXISTS favorite(" +
                "IdBook INTEGER PRIMARY KEY, " +
                "IdUser INTEGER, " +
                "InsertTime INTEGER, " +
                "Status INTEGER);";
        String CREATE_TABLE_BOOK = "CREATE TABLE IF NOT EXISTS book " +
                "(Id INTEGER PRIMARY KEY, " +
                "Name VARCHAR(255), " +
                "CategoryID INTEGER, " +
                "FileUrl VARCHAR(255), " +
                "TextContent LONGTEXT);";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE_BOOKTYPE);
        dbHelper.QueryData(CREATE_TABLE_HISTORY);
        dbHelper.QueryData(CREATE_TABLE_BOOK);
        dbHelper.QueryData(CREATE_TABLE_FAVORITE);

    }

    private void GetCursorData(String tableName) {
        Cursor cursor;
        list.clear();
        switch (tableName){
            case "booktype":
                cursor = dbHelper.GetData("SELECT * FROM '"+tableName+"'");
                while (cursor.moveToNext()){
                    String name = cursor.getString(1);
                    int id = cursor.getInt(0);
                    list.add(new Chapter(id,name));
                }
                cursor.close();
                adapter.notifyDataSetChanged();
                break;
            case "history":
                cursor = dbHelper.GetData("SELECT * FROM history, book WHERE history.IdBook = book.Id");
                while (cursor.moveToNext()){
//                    int userId = cursor.getInt(0);
//                    int historybookId = cursor.getInt(1);
                    int insertTime = cursor.getInt(2);
                    int pauseTime = cursor.getInt(3);
                    int bookId = cursor.getInt(4);
                    String bookName = cursor.getString(5);
                    int categoryId = cursor.getInt(6);
                    String content = cursor.getString(8);
                    String fileURL = cursor.getString(7);
                    list.add(new Chapter(bookId,bookName,content,pauseTime,insertTime,fileURL,categoryId));
                }
                cursor.close();
                historyAdapter.notifyDataSetChanged();
                break;
            case "favorite":
                cursor = dbHelper.GetData("SELECT * FROM favorite, book WHERE favorite.IdBook = book.Id");
                while (cursor.moveToNext()) {
//                    int userId = cursor.getInt(0);
//                    int historybookId = cursor.getInt(1);
                    int insertTime = cursor.getInt(2);
                    int status = cursor.getInt(3);
                    int bookId = cursor.getInt(4);
                    String bookName = cursor.getString(5);
                    int categoryId = cursor.getInt(6);
                    String content = cursor.getString(8);
                    String fileURL = cursor.getString(7);

                    list.add(new Chapter(bookId, bookName, content, 0, insertTime, fileURL, categoryId, status));
                }
                cursor.close();
                favoriteAdapter.notifyDataSetChanged();
                break;

        }
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(activity, "Refresh", Toast.LENGTH_SHORT).show();
                switch (idMenu){
                    case 1:
                        presenterShowList.GetBookTypeResponse(HttpUrlSwitched(idMenu));
                        break;
                    default:
                        presenterShowList.GetSelectedResponse(String.valueOf(session.getUserIdLoggedIn()),HttpUrlSwitched(idMenu));
                        break;
                }
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        Intent intent;
        switch (idMenu){
            case 1 : //book type
                adapter = new MenuAdapter(ListMenu.this, list);
                listChapter.setAdapter(adapter);
                GetCursorData(TableSwitched(idMenu));
                //get data from json parsing
                if(list.isEmpty()){
                    presenterShowList.GetBookTypeResponse(HttpUrlSwitched(idMenu));
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case 2: //history
                historyAdapter = new HistoryAdapter(activity, list);
                listChapter.setAdapter(historyAdapter);
                GetCursorData(TableSwitched(idMenu));
                //get data from json parsing
                if(list.isEmpty()) {
//                    HttpWebCall(String.valueOf(session.getUserIdLoggedIn()));
                    presenterShowList.GetSelectedResponse(String.valueOf(session.getUserIdLoggedIn()),HttpUrlSwitched(idMenu));
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case 3: // favorite
                favoriteAdapter = new FavoriteAdapter(activity, list);
                listChapter.setAdapter(favoriteAdapter);
                GetCursorData(TableSwitched(idMenu));
                //get data from json parsing
                if(list.isEmpty()) {
                    presenterShowList.GetSelectedResponse(String.valueOf(session.getUserIdLoggedIn()),HttpUrlSwitched(idMenu));
                } else {
                    progressBar.setVisibility(View.GONE);
                }
                break;
            case 4: //account info
                intent = new Intent(this, UserInfoActivity.class);
                this.finish();
                this.startActivity(intent);
                break;
            case 5: //help
                intent  = new Intent(this, HelpActivity.class);
                intent.putExtra("idHome", idMenu);
                intent.putExtra("titleHome",getString(R.string.hint_help));
                this.finish();
                this.startActivity(intent);
                break;
            case 6: //exit
                ActivityCompat.finishAffinity(this);
                System.exit(0);
                break;
        }
    }

    //region Switch Https Url
    private static final String HttpUrl_AllBookTypeData = "http://20121969.tk/SachNoiBKIC/AllBookTypeData.php";
    private static final String HttpUrl_FilterHistoryData = "http://20121969.tk/SachNoiBKIC/FilterHistoryData.php";
    private static final String HttpUrl_FilterFavoriteData = "http://20121969.tk/SachNoiBKIC/FilterFavoriteData.php";
    private String HttpUrlSwitched(int id){
        String pathUrl = null;
        switch (id){
            case 1: // list book
                pathUrl = HttpUrl_AllBookTypeData;
                break;
            case 2: // list history
                pathUrl = HttpUrl_FilterHistoryData;
                break;
            case 3: // list favorite
                pathUrl = HttpUrl_FilterFavoriteData;
                break;

        }
        return pathUrl;
    }
    //endregion

    private String TableSwitched(int id){
        String tableName = null;
        switch (id){
            case 1: // list book
                tableName = "booktype";
                break;
            case 2: // list history
                tableName = "history";
                break;
            case 3: // list favorite
                tableName = "favorite";
            break;

        }
        return tableName;
    }

    private void SetInsertTableData(Chapter arrayModel, String tableName) {
        String INSERT_DATA;
        switch (tableName){
            case "booktype":
                INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES('"+arrayModel.getId()+"','"+arrayModel.getTitle()+"')";
                dbHelper.QueryData(INSERT_DATA);
                break;
            case "history":
                    INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES(" +
                            "'"+arrayModel.getId()+"'," +
                            "'"+session.getUserIdLoggedIn()+"'," +
                            "'"+arrayModel.getInsertTime()+"'," +
                            "'"+arrayModel.getPauseTime()+"')";
                    dbHelper.QueryData(INSERT_DATA);
                try {
                    INSERT_DATA = "INSERT INTO book VALUES (" +
                            "'"+arrayModel.getId()+"'," +
                            "'"+arrayModel.getTitle()+"'," +
                            "'"+arrayModel.getCategoryId()+"'," +
                            "'"+arrayModel.getFileUrl()+"'," +
                            "'"+arrayModel.getContent()+"');";
                    dbHelper.QueryData(INSERT_DATA);
                } catch (Exception e) {
                    Log.d("MyTagView", "SetInsertTableData: failed "+INSERT_DATA);
                }
                break;
            case "favorite":
                INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES(" +
                        "'"+arrayModel.getId()+"'," +
                        "'"+session.getUserIdLoggedIn()+"'," +
                        "'"+arrayModel.getInsertTime()+"'," +
                        "'"+arrayModel.getStatus()+"')";
                dbHelper.QueryData(INSERT_DATA);
                try {
                    INSERT_DATA = "INSERT INTO book VALUES (" +
                            "'"+arrayModel.getId()+"'," +
                            "'"+arrayModel.getTitle()+"'," +
                            "'"+arrayModel.getCategoryId()+"'," +
                            "'"+arrayModel.getFileUrl()+"'," +
                            "'"+arrayModel.getContent()+"');";
                    dbHelper.QueryData(INSERT_DATA);
                } catch (Exception e) {
                    Log.d("MyTagView", "SetInsertTableData: failed "+INSERT_DATA);
                }
                break;
        }

    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {
        Cursor cursor;
        if(idMenu == 2) { // if list of database in sqlite on phone we delete all data in history table in sqlite phone
            cursor = dbHelper.GetData("SELECT * FROM history, book WHERE history.IdBook = book.Id");
            ArrayList<Chapter> arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
//                int userId = cursor.getInt(0);
//                int historybookId = cursor.getInt(1);
                int insertTime = cursor.getInt(2);
                int pauseTime = cursor.getInt(3);
                int bookId = cursor.getInt(4);
                String bookName = cursor.getString(5);
                int categoryId = cursor.getInt(6);
                String content = cursor.getString(8);
                String fileURL = cursor.getString(7);
                arrayList.add(new Chapter(bookId, bookName, content, pauseTime, insertTime, fileURL, categoryId));
            }
            if (arrayList.size() > jsonArray.length()) {
                dbHelper.QueryData("DELETE FROM '" + TableSwitched(idMenu) + "'");
            }
        }
        if(idMenu == 3) { // if list of database in sqlite on phone we delete all data in favorite table in sqlite phone
            cursor = dbHelper.GetData("SELECT * FROM favorite, book WHERE favorite.IdBook = book.Id");
            ArrayList<Chapter> arrayList = new ArrayList<>();
            while (cursor.moveToNext()) {
//                int userId = cursor.getInt(0);
//                int historybookId = cursor.getInt(1);
                int insertTime = cursor.getInt(2);
                int status = cursor.getInt(3);
                int bookId = cursor.getInt(4);
                String bookName = cursor.getString(5);
                int categoryId = cursor.getInt(6);
                String content = cursor.getString(8);
                String fileURL = cursor.getString(7);
                arrayList.add(new Chapter(bookId, bookName, content, 0, insertTime, fileURL, categoryId,status));
            }
            if (arrayList.size() > jsonArray.length()) {
                dbHelper.QueryData("DELETE FROM '" + TableSwitched(idMenu) + "'");
            }
        }
    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {

        tempModel = new Chapter();
        //return tempModel value from jsonObject
        SetTempModel(tempModel,jsonObject,TableSwitched(idMenu));

        try {
            SetInsertTableData(tempModel,TableSwitched(idMenu));
        } catch (Exception e) {
            SetUpdateTableData(tempModel, TableSwitched(idMenu));
        }
    }

    @Override
    public void ShowListFromSelected() {
        pDialog.dismiss();
        progressBar.setVisibility(View.GONE);
        GetCursorData(TableSwitched(idMenu));
        Log.d("MyTagView", "onPostExecute: "+ titleHome);
    }

    @Override
    public void SetTableData(JSONObject jsonObject) throws JSONException {
        tempModel = new Chapter();
        tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));
        tempModel.setTitle(jsonObject.getString("Name"));
        try {
            SetInsertTableData(tempModel,TableSwitched(idMenu));
        } catch (Exception e) {
            SetUpdateTableData(tempModel, TableSwitched(idMenu));
        }
    }

    @Override
    public void ShowProgressDialog() {
        pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
    }

    @Override
    public void DismissDialog() {
        pDialog.dismiss();
    }

    @Override
    public void ShowListBookType() {
        progressBar.setVisibility(View.GONE);
        GetCursorData(TableSwitched(idMenu));
        Log.d("MyTagView", "onPostExecute: "+titleHome);
    }

}
