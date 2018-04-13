package com.bkic.lymenglong.audiobookbkic.Views.Favorite;

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
import com.bkic.lymenglong.audiobookbkic.Models.Favorite.Adapters.FavoriteAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.Favorite.Utils.Favorite;
import com.bkic.lymenglong.audiobookbkic.Presenters.Favorite.PresenterShowListFavorite;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpUrl_FilterFavoriteData;

public class ListFavorite extends AppCompatActivity implements ListFavoriteImp {
    private static final String TAG = "ListFavorite";
    PresenterShowListFavorite presenterShowList = new PresenterShowListFavorite(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private FavoriteAdapter favoriteAdapter;
    private String menuTitle;
    private Activity activity = ListFavorite.this;
    private Session session;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList <Favorite> list = new ArrayList<>();
    private final String tableDB = "favorite";

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

    private void SetTempModel(Favorite tempModel, JSONObject jsonObject) throws JSONException {
        tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));
        tempModel.setTitle(jsonObject.getString("Name"));
        tempModel.setCategoryId(Integer.parseInt(jsonObject.getString("CategoryId")));
        tempModel.setStatus(Integer.parseInt(jsonObject.getString("Status")));
        tempModel.setInsertTime(Integer.parseInt(jsonObject.getString("InsertTime")));
        tempModel.setContent(jsonObject.getString("TextContent"));
        tempModel.setFileUrl(jsonObject.getString("FileUrl"));
    }

    private void SetUpdateTableData(Favorite arrayModel) {
        String UPDATE_DATA = "UPDATE favorite SET " +
                            "InsertTime = '"+arrayModel.getInsertTime()+"', " +
                            "Status = '"+arrayModel.getStatus()+"' " +
                            "WHERE " +
                            "IdBook = '"+arrayModel.getId()+"' AND IdUser = '"+session.getUserIdLoggedIn()+"';";
        dbHelper.QueryData(UPDATE_DATA);
}

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void GetCursorData() {
        Cursor cursor;
        list.clear();
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

            list.add(new Favorite(bookId, bookName, content, insertTime, fileURL, categoryId, status));
        }
        cursor.close();
        favoriteAdapter.notifyDataSetChanged();
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> ResultHash = new HashMap<>();
                String keyPost = "UserID";
                String valuePost = String.valueOf(session.getUserIdLoggedIn());
                ResultHash.put(keyPost,valuePost);
                presenterShowList.GetSelectedResponse(activity, ResultHash,HttpUrl_FilterFavoriteData);
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        favoriteAdapter = new FavoriteAdapter(activity, list);
        listChapter.setAdapter(favoriteAdapter);
        GetCursorData();
        //get data from json parsing
        if(list.isEmpty()) {
            HashMap<String,String> ResultHash = new HashMap<>();
            String keyPost = "UserID";
            String valuePost = String.valueOf(session.getUserIdLoggedIn());
            ResultHash.put(keyPost,valuePost);
            presenterShowList.GetSelectedResponse(activity, ResultHash,HttpUrl_FilterFavoriteData);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void SetInsertTableData(Favorite arrayModel, String tableName) {
        String INSERT_DATA = "INSERT INTO '"+tableName+"' VALUES(" +
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
            Log.d(TAG, "SetInsertTableData: failed "+INSERT_DATA);
        }
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {
        // if list of database in sqlite on phone we delete all data in favorite table in sqlite phone
        Cursor cursor = dbHelper.GetData("SELECT * FROM favorite, book WHERE favorite.IdBook = book.Id");
        ArrayList<Favorite> arrayList = new ArrayList<>();
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
            arrayList.add(new Favorite(bookId, bookName, content, insertTime, fileURL, categoryId,status));
        }
        if (arrayList.size() > jsonArray.length()) {
            dbHelper.QueryData("DELETE FROM '" +tableDB + "'");
        }
    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {

        Favorite tempModel = new Favorite();
        //return tempModel value from jsonObject
        SetTempModel(tempModel,jsonObject);

        try {
            SetInsertTableData(tempModel,tableDB);
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

}
