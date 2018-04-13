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
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.CategoryAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Category;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_API;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.SELECT_CATEGORY_BY_PARENT_ID;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.UPDATE_CATEGORY_DATA;

public class ListCategory extends AppCompatActivity implements ListCategoryImp {
    private static final String TAG = "ListCategory";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private View imRefresh;
    private CategoryAdapter adapter;
    private String title;
    private Activity activity = ListCategory.this;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private static ArrayList <Category> list = new ArrayList<>();
    private String menuTitle;
    private String categoryTitle;
    private int categoryId;
    private String categoryDescription;
    private int categoryParent;
    private int numOfChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        SetToolBarTitle();
        initView();
        initDatabase();
        initObject();
    }

    private void SetToolBarTitle() {
        if(menuTitle == null){
            title = categoryTitle;
        } else{
            title = menuTitle;
        }
        setTitle(title);
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
        categoryTitle = getIntent().getStringExtra("CategoryTitle");
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryDescription = getIntent().getStringExtra("CategoryDescription");
        categoryParent = getIntent().getIntExtra("CategoryParent",0);
        numOfChild = getIntent().getIntExtra("NumOfChild",0);

    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        progressBar = findViewById(R.id.progressBar);
        imRefresh = findViewById(R.id.imRefresh);
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, title, true);
        listChapter = findViewById(R.id.listView);
    }

    private void SetUpdateTableData(Category arrayModel) {
        dbHelper.QueryData(
                UPDATE_CATEGORY_DATA(
                        arrayModel.getId(),
                        arrayModel.getTitle(),
                        arrayModel.getDescription(),
                        arrayModel.getParentId(),
                        arrayModel.getNumOfChild()
                )
        );
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void GetCursorData() {
        Cursor cursor;
        list.clear();
        int parentId = categoryId; //getIntent
        String SELECT_DATA = SELECT_CATEGORY_BY_PARENT_ID(parentId);
        cursor = dbHelper.GetData(SELECT_DATA);
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String description = cursor.getString(2);
            int parent = cursor.getInt(3);
            int numOfChild = cursor.getInt(4);
            list.add(new Category(id,title,description,parent,numOfChild));
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        dbHelper.close();
    }

    private void initObject() {
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RefreshLoadingData();
            }
        });
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        adapter = new CategoryAdapter(ListCategory.this, list);
        listChapter.setAdapter(adapter);
        GetCursorData();
        //get data from json parsing
        if(list.isEmpty()){
            RefreshLoadingData();
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void RefreshLoadingData() {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String postValue = "{\"Action\":\"getListCategory\"}";
        ResultHash.put(keyPost, postValue);
        presenterShowList.GetSelectedResponse(activity,ResultHash, HttpURL_API);
    }

    private void SetInsertTableData(Category arrayModel) {
        String INSERT_DATA =
                "INSERT INTO category VALUES" +
                "(" +
                        "'"+arrayModel.getId()+"', " +
                        "'"+arrayModel.getTitle()+"', " +
                        "'"+arrayModel.getDescription()+"', " +
                        "'"+arrayModel.getParentId()+"', " +
                        "'"+arrayModel.getNumOfChild()+"'" +
                ")";
        dbHelper.QueryData(INSERT_DATA);
    }

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {
    }


    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ title);
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
        Toast.makeText(activity, jsonMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {
        Category tempModel;
        JSONArray jsonArrayCategoryChildren;
        do {
            int j = 0;
            do {
                tempModel = new Category();
                tempModel.setId(Integer.parseInt(jsonObject.getString("CategoryId")));
                tempModel.setTitle(jsonObject.getString("CategoryName"));
                tempModel.setDescription(jsonObject.getString("CategoryDescription"));
                tempModel.setParentId(Integer.parseInt(jsonObject.getString("CategoryParent")));
//                tempModel.setNumOfChild(Integer.parseInt(jsonObject.getString("NumOfChild")));
                tempModel.setCategoryChildren(jsonObject.getString("CategoryChildren"));
                jsonArrayCategoryChildren = new JSONArray(tempModel.getCategoryChildren());
                int numOfChild = jsonArrayCategoryChildren.length();
                tempModel.setNumOfChild(numOfChild);
                try {
                    SetInsertTableData(tempModel);
                } catch (Exception e) {
                    SetUpdateTableData(tempModel);
                }
                try {
                    jsonObject = jsonArrayCategoryChildren.getJSONObject(j++);
                } catch (JSONException ignored) {
                    Log.d(TAG, "SetTableSelectedData: "+j);
                }
            } while (j <= jsonArrayCategoryChildren.length());
        } while (jsonArrayCategoryChildren.length()!=0);
    }

}
