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
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_API;

public class ListBook extends AppCompatActivity implements ListBookImp{
    private static final String TAG = "ListBook";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private BookAdapter bookAdapter;
    private String title;
    private int id;
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
        setTitle(title);
        initDatabase();
        initObject();
    }


    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        title = getIntent().getStringExtra("titleChapter");
        id = getIntent().getIntExtra("idChapter", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, title, true);
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
            //todo: for new api
            HashMap<String, String> ResultHash = new HashMap<>();
            int Page = 1;
            int CategoryId = 33;
            String keyPost = "json";
            String postValue =
                    "{" +
                            "\"Action\":\"getBooksByCategory\", " +
                            "\"CategoryId\":\""+CategoryId+"\", " +
                            "\"Page\":\""+Page+"\"" +
                    "}";
            ResultHash.put(keyPost,postValue);
            /*HashMap<String, String> ResultHash = new HashMap<>();
            String keyPost = "CategoryId";
            String postValue =String.valueOf(id);
            ResultHash.put(keyPost,postValue);*/
            presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
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
                int Page = 1;
//                int CategoryId = id;
                int CategoryId = 33;
                String keyPost = "json";
                String postValue =
                        "{" +
                                "\"Action\":\"getBooksByCategory\", " +
                                "\"CategoryId\":\""+CategoryId+"\", " +
                                "\"Page\":\""+Page+"\"" +
                        "}";
                ResultHash.put(keyPost,postValue);

/*                HashMap<String, String> ResultHash = new HashMap<>();
                String keyPost = "CategoryID";
                String postValue = String.valueOf(id);
                ResultHash.put(keyPost,postValue);*/
                presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
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
        /*list.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM book");
        while (cursor.moveToNext()){
            if(cursor.getInt(2)== id){
                String name = cursor.getString(1);
                int id = cursor.getInt(0);
                String fileUrl = cursor.getString(3);
                String textContent = cursor.getString(4);
                list.add(new Book(id,name,textContent,fileUrl));
            }
        }
        cursor.close();
        bookAdapter.notifyDataSetChanged();
        dbHelper.close();*/
        //todo: for new api
        list.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM books");
        while (cursor.moveToNext()){
            Book bookModel = new Book();
            bookModel.setId(cursor.getInt(0));
            bookModel.setTitle(cursor.getString(1));
            bookModel.setUrlImage(cursor.getString(2));
            bookModel.setLength(cursor.getInt(3));
            bookModel.setCategoryId(cursor.getInt(4));
            list.add(bookModel);
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
        //todo: for new api
        Book bookModel = new Book();
        bookModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
        bookModel.setTitle(jsonObject.getString("BookTitle"));
        bookModel.setUrlImage(jsonObject.getString("BookImage"));
        bookModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
        int CategoryId = id;
        String INSERT_DATA;
        try {
            INSERT_DATA =
                    //todo: create new book table for sqlite
                    "INSERT INTO books VALUES(" +
                            "'"+bookModel.getId()+"', " +
                            "'"+bookModel.getTitle()+"', " +
                            "'"+bookModel.getUrlImage() +"', " +
                            "'"+bookModel.getLength()+"', " +
                            "'"+CategoryId+"'" + //CategoryID
                            ")";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA = "UPDATE books SET " +
                    "BookTitle = '"+bookModel.getTitle()+"', " +
                    "BookImage = '"+bookModel.getUrlImage()+"', " +
                    "BookLength = '"+bookModel.getLength()+"' ," +
                    "CategoryId = '"+CategoryId+"' " + //CategoryId
                    "WHERE BookId = '"+bookModel.getId()+"'";
            dbHelper.QueryData(UPDATE_DATA);
        }

/*        Book bookModel = new Book();
        bookModel.setId(Integer.parseInt(jsonObject.getString("Id")));
        bookModel.setTitle(jsonObject.getString("Name"));
        bookModel.setCategoryId(Integer.parseInt(jsonObject.getString("CategoryId")));
        bookModel.setContent(jsonObject.getString("TextContent"));
        bookModel.setFileUrl(jsonObject.getString("FileUrl"));

*//*        int Id = bookModel.getId();
        int CategoryId = bookModel.getCategoryId();
        String Name = bookModel.getTitle();
        String TextContent = bookModel.getContent();
        String FileUrl = bookModel.getFileUrl();
        String INSERT_DATA = null;
        try {
            INSERT_DATA = "INSERT INTO book VALUES('"+Id+"','"+Name+"','"+ id +"','"+FileUrl+"','"+TextContent+"')";
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
        }*/
    }

    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ title);
    }
}
