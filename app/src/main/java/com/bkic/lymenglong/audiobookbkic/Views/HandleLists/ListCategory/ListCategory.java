package com.bkic.lymenglong.audiobookbkic.Views.HandleLists.ListCategory;

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
import android.widget.TextView;


import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Adapters.CategoryAdapter;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.PresenterShowList;
import com.bkic.lymenglong.audiobookbkic.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListCategory extends AppCompatActivity implements ListCategoryImp{
    PresenterShowList presenterShowList = new PresenterShowList(this);
    private RecyclerView listChapter;
    private ArrayList<Chapter> chapters;
    private CategoryAdapter adapter;
    private CustomActionBar actionBar;
    private String titleChapter;
    private int idChapter;
    private TextView tvStory;
    private ProgressBar progressBar;
    private DBHelper dbHelper;
    private String HttpUrl = "http://20121969.tk/SachNoiBKIC/AllCategoryData.php";
    private static ArrayList<Chapter> list;
    private View imRefresh;

    HttpParse httpParse = new HttpParse();

    // Http Url For Filter Student Data from Id Sent from previous activity.
    String HttpURL = "http://20121969.tk/SachNoiBKIC/FilterCategoryData.php";

    String ParseResult ;
    HashMap<String,String> ResultHash = new HashMap<>();
    String FinalJSonObject ;
    private ProgressDialog pDialog;


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
        listChapter = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);
        imRefresh = findViewById(R.id.imRefresh);
    }

    private void initDatabase() {
        String DB_NAME = "menu.sqlite";
        int DB_VERSION = 1;
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS category(" +
                "Id INTEGER PRIMARY KEY, " +
                "Name VARCHAR(255), " +
                "TypeID INTEGER);";
        dbHelper = new DBHelper(this,DB_NAME ,null,DB_VERSION);
        //create database
        dbHelper.QueryData(CREATE_TABLE);

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
                HttpWebCall(String.valueOf(idChapter));
            } else {
                progressBar.setVisibility(View.GONE);
            }

            imRefresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: check internet connection before be abel to press Button Refresh
                    HttpWebCall(String.valueOf(idChapter));
//                    Toast.makeText(ListCategory.this, "Refresh", Toast.LENGTH_SHORT).show();
                }
            });

    }

    //Method to show current record Current Selected Record
    public void HttpWebCall(final String PreviousListViewClickedItem){

        class HttpWebCallFunction extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pDialog = ProgressDialog.show(ListCategory.this,"Loading Data","Please wait...",true,true);
            }

            @Override
            protected void onPostExecute(String httpResponseMsg) {

                super.onPostExecute(httpResponseMsg);

                pDialog.dismiss();

                //Storing Complete JSon Object into String Variable.
                FinalJSonObject = httpResponseMsg ;

                //Parsing the Stored JSOn String to GetHttpResponse Method.
                new GetHttpResponse(ListCategory.this).execute();

            }

            @Override
            protected String doInBackground(String... params) {

                ResultHash.put("BookTypeID",params[0]);

                ParseResult = httpParse.postRequest(ResultHash, HttpURL);

                return ParseResult;
            }
        }

        HttpWebCallFunction httpWebCallFunction = new HttpWebCallFunction();

        httpWebCallFunction.execute(PreviousListViewClickedItem);
    }


    // Parsing Complete JSON Object.
    private class GetHttpResponse extends AsyncTask<Void, Void, Void>
    {
        public Context context;

        ArrayList<Chapter> categories;

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
                if(FinalJSonObject != null)
                {
                    JSONArray jsonArray = null;

                    try {
                        jsonArray = new JSONArray(FinalJSonObject);

                        JSONObject jsonObject;

                        Chapter tempModel;

                        categories = new ArrayList<>();

                        for(int i=0; i<jsonArray.length(); i++)
                        {
//                                student = new Student();
                            tempModel = new Chapter();

                            jsonObject = jsonArray.getJSONObject(i);

                            // Adding Student Id TO IdList Array.
                            tempModel.setId(Integer.parseInt(jsonObject.getString("Id")));

                            //Adding Student Name.
                            tempModel.setTitle(jsonObject.getString("Name"));
                            categories.add(tempModel);

                            try {
                                String INSERT_DATA = "INSERT INTO category VALUES('"+tempModel.getId()+"','"+tempModel.getTitle()+"','"+idChapter+"')";
                                dbHelper.QueryData(INSERT_DATA);
                            } catch (Exception e) {
                                String UPDATE_DATA = "UPDATE category SET Name = '"+tempModel.getTitle()+"' WHERE Id = '"+tempModel.getId()+"' AND TypeID = '"+idChapter+"'";
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
            progressBar.setVisibility(View.GONE);
            GetCursorData();
            Log.d("MyTagView", "onPostExecute: "+ titleChapter);
        }
    }
}
