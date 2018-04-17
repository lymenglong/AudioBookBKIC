package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListChapter;

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
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.ChapterAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
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
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_API;

public class ListChapter extends AppCompatActivity implements ListChapterImp{
    private static final String TAG = "ListChapter";
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private ChapterAdapter chapterAdapter;
    private Activity activity = ListChapter.this;
    private DBHelper dbHelper;
    private static ArrayList<Chapter> list;
    private ProgressBar progressBar;
    private View imRefresh;
    private int bookId;
    private String bookTitle;
    private int categoryId;
    private String bookUrlImage;
    private int bookLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initDataFromIntent();
        initView();
        setTitle(bookTitle);
        initDatabase();
        initObject();
    }


    /**
     * Lấy dữ liệu thông qua intent
     */
    private void initDataFromIntent() {
        bookId = getIntent().getIntExtra("BookId", -1);
        bookTitle = getIntent().getStringExtra("BookTitle");
        bookUrlImage = getIntent().getStringExtra("BookImage");
        bookLength = getIntent().getIntExtra("BookLength", 0);
        categoryId = getIntent().getIntExtra("CategoryId", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, bookTitle, true);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        imRefresh = findViewById(R.id.imRefresh);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void initObject() {
        //Update BookDetail
        //set chapterAdapter to list view
        SetAdapterToListView();
        //update list
        GetCursorData();
        //region get data from json parsing
        if(list.isEmpty()){
            SetRequestUpdateBookDetail();
            RequestLoadList();
        } else {
            progressBar.setVisibility(View.GONE);
        }
        //endregion

        //To refresh list when click button refresh
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetRequestUpdateBookDetail();
                RequestLoadList();
            }
        });
    }

    private void SetRequestUpdateBookDetail() {
        HashMap<String, String> ResultHash = new HashMap<>();
        String keyPost = "json";
        String valuePost =
                "{" +
                        "\"Action\":\"getBookDetail\", " +
                        "\"BookId\":"+ bookId +"" +
                "}";
        ResultHash.put(keyPost,valuePost);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }

    private void RequestLoadList() {
        // todo: check internet connection before be abel to press Button Refresh
        HashMap<String, String> ResultHash = new HashMap<>();
        int BookId = bookId;
        int Page = 1;
        String keyPost = "json";
        String postValue =
                "{" +
                        "\"Action\":\"getChapterList\", " +
                        "\"BookId\":\""+BookId+"\", " +
                        "\"Page\":\""+Page+"\"" +
                        "}";
        ResultHash.put(keyPost,postValue);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }


    private void SetAdapterToListView() {
        list = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(ListChapter.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(chapterAdapter);
    }

    //region Method to get data for database
    private void GetCursorData() {
        //todo: for new api
        list.clear();
        Cursor cursor = dbHelper.GetData("SELECT * FROM chapter WHERE BookId = '"+ bookId +"'");
        while (cursor.moveToNext()){
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            list.add(chapterModel);
        }
        cursor.close();
        chapterAdapter.notifyDataSetChanged();
        dbHelper.close();
    }
    //endregion

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {

    }

    @Override
    public void SetUpdateBookDetail(JSONObject jsonObject) throws JSONException {
        Book bookModel = new Book();
        bookModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
        bookModel.setTitle(jsonObject.getString("BookTitle"));
        bookModel.setAuthor(jsonObject.getString("Author"));
        bookModel.setPublishDate(jsonObject.getString("PublishDate"));
        bookModel.setUrlImage(jsonObject.getString("BookImage"));
        bookModel.setContent(jsonObject.getString("BookContent"));
        bookModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
        bookModel.setFileUrl(jsonObject.getString("BookURL"));
        bookModel.setCategoryList(jsonObject.getString("CategoryList"));
        bookModel.setNumOfChapter(Integer.parseInt(jsonObject.getString("NumOfChapter")));
        String UPDATE_DATA = null;
        try {
            UPDATE_DATA =
                    "UPDATE book SET " +
                            "BookTitle = '"+bookModel.getTitle()+"', " +
                            "BookAuthor = '"+bookModel.getAuthor()+"', " +
                            "BookPublishDate = '"+bookModel.getPublishDate()+"', " +
                            "BookImage = '"+bookModel.getUrlImage()+"', " +
                            "BookContent = '"+bookModel.getContent()+"', " +
                            "BookLength = '"+bookModel.getLength()+"', " +
                            "BookURL = '"+bookModel.getFileUrl()+"', " +
//                            "CategoryId = '"+bookModel.getCategoryId()+"', " +
                            "NumOfChapter = '"+bookModel.getNumOfChapter()+"' " +
                                    "WHERE " +
                                            "BookId = '"+bookModel.getId()+"'" +
                    ";";
            dbHelper.QueryData(UPDATE_DATA);
        } catch (Exception ignored) {
            Log.d(TAG, "SetUpdateBookDetail: " + UPDATE_DATA);
        }
    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {
        //todo: for new api
        Chapter chapterModel = new Chapter();
        chapterModel.setId(Integer.parseInt(jsonObject.getString("ChapterId")));
        chapterModel.setTitle(jsonObject.getString("ChapterTitle"));
        chapterModel.setFileUrl(jsonObject.getString("ChapterURL"));
        chapterModel.setLength(Integer.parseInt(jsonObject.getString("ChapterLength")));
        int BookId = bookId;
        String INSERT_DATA;
        try {
            INSERT_DATA =
                    //todo: create new book table for sqlite
                    "INSERT INTO chapter VALUES" +
                            "(" +
                            "'"+chapterModel.getId()+"', " +
                            "'"+chapterModel.getTitle()+"', " +
                            "'"+chapterModel.getFileUrl() +"', " +
                            "'"+chapterModel.getLength() +"', " +
                            "'"+BookId+"'" + //BookId
                            ")";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA = "UPDATE chapter SET " +
                    "ChapterTitle = '"+chapterModel.getTitle()+"', " +
                    "ChapterUrl = '"+chapterModel.getFileUrl()+"', " +
                    "ChapterLength = '"+chapterModel.getLength()+"', " +
                    "BookId = '"+BookId+"' " + //BookId
                    "WHERE ChapterId = '"+chapterModel.getId()+"'";
            dbHelper.QueryData(UPDATE_DATA);
        }
    }

    @Override
    public void ShowListFromSelected() {
        progressBar.setVisibility(View.GONE);
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ bookTitle);
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
        Toast.makeText(activity, jsonMessage, Toast.LENGTH_SHORT).show();
    }
}
