package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListOffline;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.BookOfflineAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.R;

import java.util.ArrayList;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;

public class ListOfflineBook extends AppCompatActivity {
//    private static final String TAG = "ListOfflineBook";
    private RecyclerView listChapter;
    private BookOfflineAdapter bookOfflineAdapter;
    private String menuTitle;
    private Activity activity = ListOfflineBook.this;
    private DBHelper dbHelper;
    private ArrayList <Book> list = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        getDataFromIntent();
        setTitle(menuTitle);
        initView();
        initDatabase();
        initObject();
    }

    /**
     * Lấy dữ liệu thông qua intent
     */
    private void getDataFromIntent() {
        menuTitle = getIntent().getStringExtra("MenuTitle");
//        int idMenu = getIntent().getIntExtra("idHome", -1);
    }

    /**
     * Khai báo các view và khởi tạo giá trị
     */
    private void initView() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, menuTitle, false);
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void initDatabase() {
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
    }

    private void GetCursorData() {
        Cursor cursor;
        list.clear();
        cursor = dbHelper.GetData
                (
                        "SELECT BookId, BookTitle, BookImage, BookLength, CategoryId " +
                                "FROM book " +
                                    "WHERE BookStatus = '1';" // It means that some of chapter in this book is downloaded
                );
        while (cursor.moveToNext()) {
            int bookId = cursor.getInt(0);
            String bookTitle = cursor.getString(1);
            String bookImage = cursor.getString(2);
            int bookLength = cursor.getInt(3);
            int categoryId = cursor.getInt(4);

            list.add(new Book(bookId,bookTitle,bookImage,bookLength,categoryId));
        }
        cursor.close();
        bookOfflineAdapter.notifyDataSetChanged();
        dbHelper.close();
        progressBar.setVisibility(View.GONE);
    }

    private void initObject() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        bookOfflineAdapter = new BookOfflineAdapter(activity, list);
        listChapter.setAdapter(bookOfflineAdapter);
        GetCursorData();
    }

}