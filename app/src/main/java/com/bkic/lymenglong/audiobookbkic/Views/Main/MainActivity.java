package com.bkic.lymenglong.audiobookbkic.Views.Main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.Main.Adapters.MainAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.Main.Utils.Menu;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Account.Login.ViewLoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;

public class MainActivity extends AppCompatActivity implements MainImp{
//    PresenterMain presenterMain = new PresenterMain(this);
    private static final String TAG = "MainActivity";
    private RecyclerView homeList;
    private MainAdapter mainAdapter;
    DBHelper dbHelper;
    private static ArrayList<Menu> menuList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getDataFromIntent();
        initView();
        initDatabase();
        initObject();
        GetCursorData();
        //get data from json parsing
//        presenterMain.GetHttpResponse(HttpUrl_ALLMenuData);
    }

    // to make application remember pass LoginActivity in to MainActivity
    private void getDataFromIntent() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            Intent intent = new Intent(this, ViewLoginActivity.class);
            startActivity(intent);
        }
    }

    private void initView() {
        homeList = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.label_name),
                ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this, DB_NAME, null, DB_VERSION);
    }

    private void initObject() {
//        menus = databaseHelper.getHomeList();
        menuList = new ArrayList<>();
        mainAdapter = new MainAdapter(MainActivity.this, menuList);
//        mainAdapter = new MainAdapter(MainActivity.this, menus);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        homeList.setLayoutManager(mLinearLayoutManager);
        homeList.setAdapter(mainAdapter);
    }

    private void GetCursorData() {
        menuList.clear();
        String SELECT_MENU_DATA = "SELECT * FROM menu";
        Cursor cursor = dbHelper.GetData(SELECT_MENU_DATA);
        while (cursor.moveToNext()) {
            String title = cursor.getString(1);
            int id = cursor.getInt(0);
            menuList.add(new Menu(id, title));
        }
        cursor.close();
        mainAdapter.notifyDataSetChanged();
        dbHelper.close();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void ShowListMenu() {
//        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: " + getTitle());
    }

    @Override
    public void SetMenuData(JSONObject jsonObject) throws JSONException {
        Menu menuModel = new Menu();
        menuModel.setId(Integer.parseInt(jsonObject.getString("Id")));
        menuModel.setTitle(jsonObject.getString("Name"));
        int Id = menuModel.getId();
        String Name = menuModel.getTitle();
        try {
            String INSERT_DATA = "INSERT INTO menu VALUES('"+Id+"','"+Name+"')";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA = "UPDATE menu SET Name = '" + Name + "' WHERE Id = '" + Id + "'";
            dbHelper.QueryData(UPDATE_DATA);
        }
        Log.d(TAG, "SetMenuData");
    }
}