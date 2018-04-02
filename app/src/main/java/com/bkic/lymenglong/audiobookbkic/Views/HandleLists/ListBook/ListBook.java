package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook;

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
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.BookAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_FilterBookData;

public class ListBook extends AppCompatActivity implements ListBookImp{
    private static final String TAG = "ListBook";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private BookAdapter bookAdapter;
    private String titleChapter;
    private int idChapter;
    private Activity activity = ListBook.this;
    private DBHelper dbHelper;
    private static ArrayList<Book> list;
    private ProgressBar progressBar;
    private View imRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        getDataFromIntent();
        initView();
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
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleChapter, true);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        imRefresh = findViewById(R.id.imRefresh);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void initObject() {
        //set bookAdapter to list view
        SetAdapterToListView();
        //update list
        GetCursorData();

        //region get data from json parsing
        if(list.isEmpty()){
            HashMap<String, String> ResultHash = new HashMap<>();
            String keyPost = "CategoryID";
            String postValue = String.valueOf(idChapter);
            ResultHash.put(keyPost,postValue);
            presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_FilterBookData);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        //endregion

        //To refresh list when click button refresh
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: check internet connection before be abel to press Button Refresh
                HashMap<String, String> ResultHash = new HashMap<>();
                String keyPost = "CategoryID";
                String postValue = String.valueOf(idChapter);
                ResultHash.put(keyPost,postValue);
                presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_FilterBookData);
            }
        });
    }

    private void SetAdapterToListView() {
        list = new ArrayList<>();
        bookAdapter = new BookAdapter(ListBook.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(bookAdapter);
    }

    //region Method to get data for database
    private void GetCursorData() {
        list.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM book");
        while (cursor.moveToNext()){
            if(cursor.getInt(2)== idChapter){
                String name = cursor.getString(1);
                int id = cursor.getInt(0);
                String fileUrl = cursor.getString(3);
                String textContent = cursor.getString(4);
                list.add(new Book(id,name,textContent,fileUrl));
            }
        }
        cursor.close();
        bookAdapter.notifyDataSetChanged();
        dbHelper.close();
    }
    //endregion

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {

    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {

        Book bookModel = new Book();

        bookModel.setId(Integer.parseInt(jsonObject.getString("Id")));

        bookModel.setTitle(jsonObject.getString("Name"));

        bookModel.setCategoryId(Integer.parseInt(jsonObject.getString("CategoryId")));

        bookModel.setContent(jsonObject.getString("TextContent"));

        bookModel.setFileUrl(jsonObject.getString("FileUrl"));

        int Id = bookModel.getId();
        int CategoryId = bookModel.getCategoryId();
        String Name = bookModel.getTitle();
        String TextContent = bookModel.getContent();
        String FileUrl = bookModel.getFileUrl();
        String INSERT_DATA = null;
        try {
            INSERT_DATA = "INSERT INTO book VALUES('"+Id+"','"+Name+"','"+idChapter+"','"+FileUrl+"','"+TextContent+"')";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            Log.d(TAG, "SetInsertTableData: failed "+INSERT_DATA);
            String UPDATE_DATA = "UPDATE book SET " +
                    "Name = '"+Name+"', " +
                    "CategoryId = '"+CategoryId+"', " +
                    "FileUrl = '"+FileUrl+"' ," +
                    "TextContent = '"+TextContent+"' " +
                    "WHERE Id = '"+Id+"'";
            dbHelper.QueryData(UPDATE_DATA);
        }
    }

    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ titleChapter);
    }
}
