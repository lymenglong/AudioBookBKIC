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
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.CategoryAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_FilterCategoryData;

public class ListCategory extends AppCompatActivity implements ListCategoryImp{
    private static final String TAG = "ListCategory";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private CategoryAdapter adapter;
    private String titleChapter;
    private int idChapter;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList<Chapter> list;
    private View imRefresh;

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
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
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
                HashMap<String, String> ResultHash = new HashMap<>();
                String keyPost = "BookTypeID";
                String postValue = "String.valueOf(idChapter)";
                ResultHash.put(keyPost,postValue);
                presenterShowList.GetSelectedResponse(this, ResultHash, HttpURL_FilterCategoryData);
            } else {
                progressBar.setVisibility(View.GONE);
            }

            imRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: check internet connection before be abel to press Button Refresh
                    HashMap<String, String> ResultHash = new HashMap<>();
                    String keyPost = "BookTypeID";
                    String postValue = String.valueOf(idChapter);
                    ResultHash.put(keyPost,postValue);
                    presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_FilterCategoryData);
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
        Log.d(TAG, "onPostExecute: "+ titleChapter);
    }
}
