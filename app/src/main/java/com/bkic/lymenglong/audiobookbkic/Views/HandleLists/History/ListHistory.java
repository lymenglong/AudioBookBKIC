package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.History;

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
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.History.Adapter.HistoryAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.Utils.Const;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.History.ShowListHistory.PresenterShowListHistory;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_API;

public class ListHistory extends AppCompatActivity implements ListHistoryImp {
    private static final String TAG = "ListHistory";
    PresenterShowListHistory presenterShowList = new PresenterShowListHistory(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private HistoryAdapter historyAdapter;
    private String menuTitle;
    private Activity activity = ListHistory.this;
    private Session session;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private ArrayList <Book> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        setTitle(menuTitle);
        initView();
        initDatabase();
        initObject();
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
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
        actionBar.eventToolbar(this, menuTitle, true);
        listChapter = findViewById(R.id.listView);
    }
    private void SetUpdateTableData(Book arrayModel) {
        String UPDATE_DATA =
                "UPDATE " +
                        "history " +
                "SET " +
                        "BookTitle = '"+arrayModel.getTitle()+"', " +
                        "BookImage = '"+arrayModel.getUrlImage()+"', " +
                        "BookLength = '"+arrayModel.getLength()+"', " +
                        "BookAuthor = '"+arrayModel.getAuthor()+"' " +
                "WHERE " +
                        "BookId = '"+arrayModel.getId()+"'; ";
        dbHelper.QueryData(UPDATE_DATA);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void GetCursorData() {
        Cursor cursor;
        list.clear();
        cursor = dbHelper.GetData("SELECT * FROM history");
        while (cursor.moveToNext()) {
            int bookId = cursor.getInt(0);
            String bookTitle = cursor.getString(1);
            String bookImage = cursor.getString(2);
            int bookLength = cursor.getInt(3);
            String bookAuthor = cursor.getString(4);

            list.add(new Book(bookId,bookTitle,bookImage,bookLength,bookAuthor));
        }
        cursor.close();
        historyAdapter.notifyDataSetChanged();
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestLoadingData();
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        historyAdapter = new HistoryAdapter(activity, list);
        listChapter.setAdapter(historyAdapter);
        GetCursorData();
        //get data from json parsing
        if(list.isEmpty()) {
            RequestLoadingData();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void RequestLoadingData() {
        int userId =session.getUserIdLoggedIn();
        HashMap<String,String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"getHistory\", " +
                        "\"UserId\":\""+userId+"\"" +
                        "}";
        ResultHash.put(keyPost,valuePost);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }

    private void SetInsertTableData(Book arrayModel) {
        String INSERT_DATA =
                "INSERT INTO history VALUES" +
                        "(" +
                                "'"+arrayModel.getId()+"', " +
                                "'"+arrayModel.getTitle()+"', " +
                                "'"+arrayModel.getUrlImage()+"', " +
                                "'"+arrayModel.getLength()+"', " +
                                "'"+arrayModel.getAuthor()+"', " +
                                "'"+ Const.BOOK_SYNCED_WITH_SERVER+"'" +//BookSync has already store in server
                        ");";
        dbHelper.QueryData(INSERT_DATA);
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {

    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {

        Book tempModel = new Book();
        tempModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
        tempModel.setTitle(jsonObject.getString("BookTitle"));
        tempModel.setUrlImage(jsonObject.getString("BookImage"));
        tempModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
        tempModel.setAuthor(jsonObject.getString("Author"));

        try {
            SetInsertTableData(tempModel);
        } catch (Exception e) {
            SetUpdateTableData(tempModel);
        }
    }

    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ menuTitle);
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
        Log.d(TAG, "LoadListDataFailed: "+ jsonMessage);
    }
}