package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBookType;

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
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.BookTypeAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpUrl_AllBookTypeData;

public class ListBookType extends AppCompatActivity implements ListBookTypeImp {
    private static final String TAG = "ListBookType";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private BookTypeAdapter adapter;
    private String titleHome;
    private Activity activity = ListBookType.this;
    private Session session;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList <Chapter> list = new ArrayList<>();

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

    private void SetUpdateTableData(Chapter arrayModel) {
        String UPDATE_DATA = "UPDATE booktype SET Name = '"+arrayModel.getTitle()+"' WHERE Id = '"+arrayModel.getId()+"';";
        dbHelper.QueryData(UPDATE_DATA);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void GetCursorData() {
        Cursor cursor;
        list.clear();
        cursor = dbHelper.GetData("SELECT * FROM booktype");
        while (cursor.moveToNext()){
            String name = cursor.getString(1);
            int id = cursor.getInt(0);
            list.add(new Chapter(id,name));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String keyPost = "UserID";
            presenterShowList.GetSelectedResponse(activity, keyPost, String.valueOf(session.getUserIdLoggedIn()),HttpUrl_AllBookTypeData);
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        adapter = new BookTypeAdapter(ListBookType.this, list);
        listChapter.setAdapter(adapter);
        GetCursorData();
        //get data from json parsing
        if(list.isEmpty()){
            presenterShowList.GetDataResponse(HttpUrl_AllBookTypeData);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void SetInsertTableData(Chapter arrayModel) {
        String INSERT_DATA = "INSERT INTO booktype VALUES('"+arrayModel.getId()+"','"+arrayModel.getTitle()+"')";
        dbHelper.QueryData(INSERT_DATA);
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {
    }


    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ titleHome);
    }

    @Override
    public void SetTableData(JSONObject jsonObject) throws JSONException {
        Chapter tempModel = new Chapter();
        tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));
        tempModel.setTitle(jsonObject.getString("Name"));
        try {
            SetInsertTableData(tempModel);
        } catch (Exception e) {
            SetUpdateTableData(tempModel);
        }
    }

    @Override
    public void ShowListBookType() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+titleHome);
    }

}
