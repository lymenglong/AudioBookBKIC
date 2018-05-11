package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.Search;

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

import com.bkic.lymenglong.audiobookbkic.Models.CheckInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.BookAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Category;
import com.bkic.lymenglong.audiobookbkic.Presenters.Search.PresenterSearchBook;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;

public class ListBookSearch extends AppCompatActivity implements ListBookSearchImp{
    private static final String TAG = "ListBookSearch";
    private PresenterSearchBook presenterSearchBook = new PresenterSearchBook(this);
    private RecyclerView listChapter;
    private BookAdapter bookAdapter;
    private Activity activity = ListBookSearch.this;
    private DBHelper dbHelper;
    private ArrayList<Book> list;
    private ProgressBar progressBar;
    private View imRefresh;
    private Category categoryIntent;
/*    private String categoryTitle;
    private int categoryId;
    private String categoryDescription;
    private int categoryParent;
    private int numOfChild;*/
//    private int mPAGE = 1; //page from server
    private Boolean isFinalPage = false;
    private String keyWord = "Hạ Đỏ";
    private String menuTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        getDataFromIntent();
        initView();
        initDatabase();
        initObject();
    }


    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        categoryIntent = new Category
                (
                        getIntent().getIntExtra("CategoryId", -1),
                        getIntent().getStringExtra("CategoryTitle"),
                        getIntent().getStringExtra("CategoryDescription"),
                        getIntent().getIntExtra("CategoryParent",0),
                        getIntent().getIntExtra("NumOfChild",0)
                );
        menuTitle = getIntent().getStringExtra("MenuTitle");
        /*categoryTitle = getIntent().getStringExtra("CategoryTitle");
        categoryId = getIntent().getIntExtra("CategoryId", -1);
        categoryDescription = getIntent().getStringExtra("CategoryDescription");
        categoryParent = getIntent().getIntExtra("CategoryParent",0);
        numOfChild = getIntent().getIntExtra("NumOfChild",0);*/
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        String titleToolbar = categoryIntent.getTitle()==null? menuTitle :categoryIntent.getTitle();
        setTitle(titleToolbar);
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleToolbar, true);
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
        GetCursorData();
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
//                    RefreshBookTable();
//                    mPAGE = 1;
                    RequestLoadingData();
                } else {
                    Toast.makeText(activity, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*private void RefreshBookTable() {
        String DELETE_DATA =
                "UPDATE book " +
                "SET " +
//                        "BookId = NULL, "+
                        "BookTitle = NULL, " +
                        "BookAuthor = NULL, " +
                        "BookPublishDate= NULL, " +
                        "BookImage = NULL, " +
                        "BookContent = NULL, " +
                        "BookLength = NULL, " +
                        "BookURL = NULL, " +
                        "NumOfChapter = NULL " +
                "WHERE CategoryId = '"+categoryIntent.getId()+"'";
        dbHelper.QueryData(DELETE_DATA);
        dbHelper.close();
    }*/

    private void RequestLoadingData() {
        presenterSearchBook.SearchBook(keyWord);
    }

    private void SetAdapterToListView() {
        list = new ArrayList<>();
        bookAdapter = new BookAdapter(ListBookSearch.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(bookAdapter);
        listChapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == SCROLL_STATE_DRAGGING && !isFinalPage){
//                        mPAGE++;
                        RequestLoadingData();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    //region Method to get data for database
    private void GetCursorData() {
        list.clear();
        String SELECT_DATA = "SELECT * FROM bookSearch WHERE KeyWord = '"+keyWord+"'";
        Cursor cursor = dbHelper.GetData(SELECT_DATA);
        while (cursor.moveToNext()){
            Book bookModel = new Book
                    (
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getInt(4),
                            cursor.getInt(5)
                    );
            list.add(bookModel);
        }
        cursor.close();
        bookAdapter.notifyDataSetChanged();
        dbHelper.close();
        progressBar.setVisibility(View.GONE);
    }
    //endregion

    @Override
    public void CompareDataPhoneWithServer(JSONArray jsonArray) {

    }

    @Override
    public void SetTableSelectedData(JSONObject jsonObject) throws JSONException {
        Book bookModel = new Book();
        bookModel.setId(Integer.parseInt(jsonObject.getString("BookId")));
        bookModel.setTitle(jsonObject.getString("BookTitle"));
        bookModel.setUrlImage(jsonObject.getString("BookImage"));
        bookModel.setLength(Integer.parseInt(jsonObject.getString("BookLength")));
        bookModel.setCategoryId(Integer.parseInt(jsonObject.getString("Category")));
        bookModel.setAuthor(jsonObject.getString("Author"));
        String INSERT_DATA;
        try {
            INSERT_DATA =
                    "INSERT INTO bookSearch VALUES(" +
                            "'"+bookModel.getId()+"', " +
                            "'"+bookModel.getTitle()+"', " +
                            "'"+bookModel.getAuthor()+"', " +
                            "'"+bookModel.getUrlImage() +"', " +
                            "'"+bookModel.getLength()+"', " +
                            "'"+bookModel.getCategoryId()+"', " + //CategoryID
                            "'"+keyWord+"'"+
                            ")";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA =
                    "UPDATE " +
                            "bookSearch " +
                    "SET " +
                            "BookTitle = '"+bookModel.getTitle()+"', " +
                            "BookImage = '"+bookModel.getUrlImage()+"', " +
                            "BookLength = '"+bookModel.getLength()+"' ," +
                            "CategoryId = '"+bookModel.getCategoryId()+"', " + //CategoryId
                            "BookAuthor = '"+bookModel.getAuthor()+"'"+
                    "WHERE " +
                            "BookId = '"+bookModel.getId()+"'";
            dbHelper.QueryData(UPDATE_DATA);
        }
    }

    @Override
    public void ShowListFromSelected() {
        GetCursorData();
        Log.d(TAG, "onPostExecute: "+ categoryIntent.getTitle());
    }

    @Override
    public void LoadListDataFailed(String jsonMessage) {
//        mPAGE--;
        isFinalPage = true;
        Toast.makeText(activity, jsonMessage, Toast.LENGTH_SHORT).show();
    }
}
