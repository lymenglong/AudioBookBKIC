package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.IntentFilter;
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
import com.bkic.lymenglong.audiobookbkic.Models.CheckInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.Download.DownloadReceiver;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.BookAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Category;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_API;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.SELECT_ALL_BOOK_BY_CATEGORY_ID;

public class ListBook
        extends AppCompatActivity
        implements
                ListBookImp,
                ConnectivityReceiver.ConnectivityReceiverListener,
                DownloadReceiver.DownloadReceiverListener{

    private static final String TAG = "ListBook";
    private PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private BookAdapter bookAdapter;
    private Activity activity = ListBook.this;
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
    private int mPAGE = 1; //page from server
    private Boolean isFinalPage = false;
    private boolean isLoadingData = false;
    private Toast mToast;
    private boolean isShowingToast = false;
    private ProgressBar pBarBottom;
    private int mLastPAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        initIntentFilter();
        getDataFromIntent();
        initView();
        setTitle(categoryIntent.getTitle());
        initDatabase();
        initObject();
    }

    //region BroadCasting
    //connectionReceiver
    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;
    //downloadReceiver
    private IntentFilter filter;
    private DownloadReceiver downloadReceiver;

    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
        //set filter to only when download is complete and register broadcast receiver
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DownloadReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        registerReceiver(receiver, intentFilter);
        registerReceiver(downloadReceiver, filter);
        // register status listener
        MyApplication.getInstance().setConnectivityListener(this);
        MyApplication.getInstance().setDownloadListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister receiver
        unregisterReceiver(receiver);
        unregisterReceiver(downloadReceiver);
        //Cancel Toast Notification
        if(isShowingToast) mToast.cancel();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onDownloadCompleted(long downloadId) {

    }
    //endregion

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
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, categoryIntent.getTitle(), true);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        pBarBottom = findViewById(R.id.pb_bottom);
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
        if(list.isEmpty()) RequestLoadingData();
        //endregion
        //To refresh list when click button refresh
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityReceiver.isConnected()) {
                    RefreshBookTable();
                    isFinalPage = false;
                    mPAGE = 1;
                    RequestLoadingData();
                } else {
                    Toast.makeText(activity, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RefreshBookTable() {
        String DELETE_DATA =
                "DELETE FROM book "+
                "WHERE CategoryId = '"+categoryIntent.getId()+"'";
        dbHelper.QueryData(DELETE_DATA);
        dbHelper.close();
    }

    private void RequestLoadingData() {
        isLoadingData = true;
        pBarBottom.setVisibility(View.VISIBLE);
        HashMap<String, String> ResultHash = new HashMap<>();
        int CategoryId = categoryIntent.getId();
        String keyPost = "json";
        String postValue =
                "{" +
                        "\"Action\":\"getBooksByCategory\", " +
                        "\"CategoryId\":\""+CategoryId+"\", " +
                        "\"Page\":\""+mPAGE+"\"" +
                        "}";
        ResultHash.put(keyPost,postValue);
        presenterShowList.GetSelectedResponse(activity, ResultHash, HttpURL_API);
    }

    private void SetAdapterToListView() {
        list = new ArrayList<>();
        bookAdapter = new BookAdapter(ListBook.this, list);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(bookAdapter);
        listChapter.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!isLoadingData)
                    if(newState == SCROLL_STATE_DRAGGING && !isFinalPage){
                        mPAGE++;
                        Cursor cursor = dbHelper.GetData
                                (
                                        "SELECT MAX(Page) AS LastPage " +
                                                "FROM book " +
                                                "WHERE CategoryId = '"+categoryIntent.getId()+"';"
                                );
                        if(cursor.moveToFirst()) mLastPAGE = cursor.getInt(0);
                        if(mPAGE < mLastPAGE) mPAGE = mLastPAGE;
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
        String SELECT_DATA = SELECT_ALL_BOOK_BY_CATEGORY_ID(categoryIntent.getId());
        Cursor cursor = dbHelper.GetData(SELECT_DATA);
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
        progressBar.setVisibility(View.GONE);
        isLoadingData = false;
        pBarBottom.setVisibility(View.GONE);
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
        bookModel.setCategoryId(categoryIntent.getId());
        String INSERT_DATA;
        try {
            INSERT_DATA =
                    "INSERT INTO book VALUES(" +
                            "'"+bookModel.getId()+"', " +
                            "'"+bookModel.getTitle()+"', " +
                            "'"+bookModel.getAuthor()+"', " +
                            "'"+bookModel.getPublishDate()+"', " +
                            "'"+bookModel.getUrlImage() +"', " +
                            "'"+bookModel.getContent() +"', " +
                            "'"+bookModel.getLength()+"', " +
                            "'"+bookModel.getFileUrl() +"', " +
                            "'"+bookModel.getCategoryId()+"', " + //CategoryID
                            "'"+bookModel.getNumOfChapter()+"', " +
                            "'"+0+"', " +
                            "'"+mPAGE+"'" +
                            ")";
            dbHelper.QueryData(INSERT_DATA);
        } catch (Exception e) {
            String UPDATE_DATA =
                    "UPDATE " +
                            "book " +
                    "SET " +
                            "BookTitle = '"+bookModel.getTitle()+"', " +
                            "BookImage = '"+bookModel.getUrlImage()+"', " +
                            "BookLength = '"+bookModel.getLength()+"' ," +
                            "CategoryId = '"+bookModel.getCategoryId()+"', " + //CategoryId
                            "Page = '"+mPAGE+"'"+
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
        mPAGE--;
        isFinalPage = true;
        isShowingToast = isShowingToastNotification(jsonMessage);
        pBarBottom.setVisibility(View.GONE);
    }
    private boolean isShowingToastNotification(String jsonMessage){
        String mMessage = "DONE";
        mToast = Toast.makeText(activity, jsonMessage.isEmpty()?mMessage:jsonMessage, Toast.LENGTH_SHORT);
        mToast.show();
        return true;
    }
}
