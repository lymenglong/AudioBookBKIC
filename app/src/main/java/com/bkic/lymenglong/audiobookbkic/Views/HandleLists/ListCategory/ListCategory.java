package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory;

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


import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.CategoryAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListCategory extends AppCompatActivity implements ListCategoryImp{
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private CategoryAdapter adapter;
    private String titleChapter;
    private int idChapter;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList<Chapter> list;
    private View imRefresh;

    // Http Url For Filter Student Data from Id Sent from previous activity.
    String HttpURL = "http://20121969.tk/SachNoiBKIC/FilterCategoryData.php";

    private Activity activity = ListCategory.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        getDataFromIntent();
        init();
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        setTitle(titleChapter);
        initDatabase();
        initObject();
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        titleChapter = getIntent().getStringExtra("titleChapter");
        idChapter = getIntent().getIntExtra("idChapter", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void init() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleChapter, true);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        imRefresh = findViewById(R.id.imRefresh);
    }

    private void initDatabase() {
        String DB_NAME = "menu.sqlite";
        int DB_VERSION = 1;
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS category(" +
                "Id INTEGER PRIMARY KEY, " +
                "Name VARCHAR(255), " +
                "TypeID INTEGER);";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE);

    }

    private void GetCursorData() {
        list.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM category");
        while (cursor.moveToNext()){
            if(cursor.getInt(2)== idChapter){
                String name = cursor.getString(1);
                int id = cursor.getInt(0);
                list.add(new Chapter(id,name));
            }
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        dbHelper.close();
    }

    private void initObject() {
            list = new ArrayList<>();
            adapter = new CategoryAdapter(ListCategory.this, list);
            LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
            listChapter.setLayoutManager(mLinearLayoutManager);
            listChapter.setAdapter(adapter);
            // update list
            GetCursorData();

            if(list.isEmpty()){
                String keyPost = "BookTypeID";
                presenterShowList.GetSelectedResponse(this, keyPost, String.valueOf(idChapter), HttpURL);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            imRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: check internet connection before be abel to press Button Refresh
                String keyPost = "BookTypeID";
                presenterShowList.GetSelectedResponse(activity, keyPost, String.valueOf(idChapter), HttpURL);
                }
            });

    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {

        Chapter tempModel = new Chapter();

        tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));

        tempModel.setTitle(jsonObject.getString("Name"));

        try {
            String INSERT_DATA = "INSERT INTO category VALUES('"+ tempModel.getId()+"','"+ tempModel.getTitle()+"','"+idChapter+"')";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA = "UPDATE category SET Name = '"+ tempModel.getTitle()+"' WHERE Id = '"+ tempModel.getId()+"' AND TypeID = '"+idChapter+"'";
            dbHelper.QueryData(UPDATE_DATA);
        }
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {
        //todo: CompareDataPhoneWithServer
    }

    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d("MyTagView", "onPostExecute: "+ titleChapter);
    }
}
