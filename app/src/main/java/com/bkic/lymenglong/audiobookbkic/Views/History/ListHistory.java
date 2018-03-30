package com.bkic.lymenglong.audiobookbkic.Views.History;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.History.Adapter.HistoryAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.History.Utils.Index;
import com.bkic.lymenglong.audiobookbkic.Presenters.History.PresenterShowListHistory;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListHistory extends AppCompatActivity implements ListHistoryImp {
    PresenterShowListHistory presenterShowListHistory = new PresenterShowListHistory(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private HistoryAdapter historyAdapter;
    private String titleHome;
    private Activity activity = ListHistory.this;
    private Session session;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList <Index> list = new ArrayList<>();
    private static final String HttpUrl_FilterHistoryData = "http://20121969.tk/SachNoiBKIC/FilterHistoryData.php";
    private String tableDB = "history";

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
//        int idMenu = getIntent().getIntExtra("idHome", -1);
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

    private void SetTempModel(Index tempModel, JSONObject jsonObject, String tableSwitched) throws JSONException {
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

    private void SetUpdateTableData(Index arrayModel, String tableName) {
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

    private void GetCursorData() {
        Cursor cursor;
        list.clear();
        cursor = dbHelper.GetData("SELECT * FROM history, book WHERE history.IdBook = book.Id");
        while (cursor.moveToNext()){
            int insertTime = cursor.getInt(2);
            int pauseTime = cursor.getInt(3);
            int bookId = cursor.getInt(4);
            String bookName = cursor.getString(5);
            int categoryId = cursor.getInt(6);
            String content = cursor.getString(8);
            String fileURL = cursor.getString(7);
            list.add(new Index(bookId,bookName,content,pauseTime,insertTime,fileURL,categoryId));
        }
        cursor.close();
        historyAdapter.notifyDataSetChanged();
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String keyPost = "UserID";
            presenterShowListHistory.GetSelectedResponse(activity, keyPost, String.valueOf(session.getUserIdLoggedIn()),HttpUrl_FilterHistoryData);
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        historyAdapter = new HistoryAdapter(activity, list);
        listChapter.setAdapter(historyAdapter);
        GetCursorData();
        //get data from json parsing
        if(list.isEmpty()) {
            String keyPost = "UserID";
            presenterShowListHistory.GetSelectedResponse(activity, keyPost, String.valueOf(session.getUserIdLoggedIn()), HttpUrl_FilterHistoryData);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void SetInsertTableData(Index arrayModel, String tableName) {
        String INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES(" +
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
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {
        Cursor cursor;
       // if list of database in sqlite on phone we delete all data in history table in sqlite phone
        cursor = dbHelper.GetData("SELECT * FROM history, book WHERE history.IdBook = book.Id");
        ArrayList<Index> arrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int insertTime = cursor.getInt(2);
            int pauseTime = cursor.getInt(3);
            int bookId = cursor.getInt(4);
            String bookName = cursor.getString(5);
            int categoryId = cursor.getInt(6);
            String content = cursor.getString(8);
            String fileURL = cursor.getString(7);
            arrayList.add(new Index(bookId, bookName, content, pauseTime, insertTime, fileURL, categoryId));
        }
        if (arrayList.size() > jsonArray.length()) {
            dbHelper.QueryData("DELETE FROM '" + tableDB + "'");
        }
    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {

        Index tempModel = new Index();
        //return tempModel value from jsonObject
        SetTempModel(tempModel,jsonObject,tableDB);

        try {
            SetInsertTableData(tempModel,tableDB);
        } catch (Exception e) {
            SetUpdateTableData(tempModel, tableDB);
        }
    }

    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d("MyTagView", "onPostExecute: "+ titleHome);
    }
}