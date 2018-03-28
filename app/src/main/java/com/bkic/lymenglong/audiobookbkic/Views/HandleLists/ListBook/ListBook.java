package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListBook;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.BookAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Book;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListBook extends AppCompatActivity implements ListBookImp{
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private ArrayList<Chapter> chapters;
    private Book bookModel;
    private BookAdapter bookAdapter;
    private CustomActionBar actionBar;
    private String titleChapter;
    private int idChapter;
    private Activity activity = ListBook.this;
    private static final String URL = "http://20121969.tk/audiobook/books/getAllBooks.php";
    private StringRequest stringRequest;
    private RequestQueue requestQueue;
    private DBHelper dbHelper;
    private static ArrayList<Book> list;
    private ProgressBar progressBar;
    private View imRefresh;


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
        actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, titleChapter, true);
        listChapter = (RecyclerView) findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        imRefresh = (View) findViewById(R.id.imRefresh);

        //region Get list Book Old Code
    /*    requestQueue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        getJSON(URL);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(activity, "Not Response", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(stringRequest);
*/
/*
        databaseHelper = new DatabaseHelper(this);
        chapters = databaseHelper.getListAllChapter(idChapter);
        bookAdapter = new BookAdapter(ListBook.this, chapters);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        listChapter.setLayoutManager(mLinearLayoutManager);
        listChapter.setAdapter(bookAdapter);
*/
        //endregion

    }

    private void initDatabase() {
        String DB_NAME = "menu.sqlite";
        int DB_VERSION = 1;
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS book " +
                "(Id INTEGER PRIMARY KEY, " +
                "Name VARCHAR(255), " +
                "CategoryID INTEGER, " +
                "FileUrl VARCHAR(255), " +
                "TextContent LONGTEXT);";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE);

    }

    private void initObject() {
        //set bookAdapter to list view
        SetAdapterToListView();
        //update list
        GetCursorData();

        //region get data from json parsing
        if(list.isEmpty()){
            HttpWebCall(String.valueOf(idChapter));
        } else {
            progressBar.setVisibility(View.GONE);
        }
        //endregion

        //To refresh list when click button refresh
        imRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: check internet connection before be abel to press Button Refresh
                HttpWebCall(String.valueOf(idChapter));
//                Toast.makeText(activity, "Refresh", Toast.LENGTH_SHORT).show();
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


    //region Method to show current record Current Selected Record
    private ProgressDialog pDialog;
    private String FinalJSonObject;
    private HashMap<String, String> ResultHash = new HashMap<>();
    private String ParseResult;
    private HttpParse httpParse = new HttpParse();
    private String HttpURL = "http://20121969.tk/SachNoiBKIC/FilterBookData.php";
    public void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(activity,"Loading Data","Please wait",true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponse(ListBook.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("CategoryID",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, HttpURL);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }
    //endregion

    //region Parsing Complete JSON Object.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        ArrayList<Book> books;

        public GetHttpResponse(Context context)
        {
            this.context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0)
        {
            try
            {
                if(FinalJSonObject != null && !FinalJSonObject.equals("No Results Found.")) //When no data, it will return "No Results Found." Value to String JSONObject.
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        Book bookModel;
//                            studentList = new ArrayList<Student>();
                        books = new ArrayList<>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
//                                student = new Student();
                            bookModel = new Book();

                            jsonObject = jsonArray.getJSONObject(i);

                            bookModel.setId(Integer.parseInt(jsonObject.getString("Id")));

                            bookModel.setTitle(jsonObject.getString("Name").toString());

                            bookModel.setCategoryId(Integer.parseInt(jsonObject.getString("CategoryId")));

                            bookModel.setContent(jsonObject.getString("TextContent"));

                            bookModel.setFileUrl(jsonObject.getString("FileUrl"));

                            books.add(bookModel);

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
                                Log.d("MyTagView", "SetInsertTableData: failed "+INSERT_DATA);
                                String UPDATE_DATA = "UPDATE book SET " +
                                        "Name = '"+Name+"', " +
                                        "CategoryId = '"+CategoryId+"', " +
                                        "FileUrl = '"+FileUrl+"' ," +
                                        "TextContent = '"+TextContent+"' " +
                                        "WHERE Id = '"+Id+"'";
                                dbHelper.QueryData(UPDATE_DATA);
                            }
                        }
                    }
                    catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            pDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            GetCursorData();
            Log.d("MyTagView", "onPostExecute: "+ titleChapter);

        }
    }
    //endregion
}
