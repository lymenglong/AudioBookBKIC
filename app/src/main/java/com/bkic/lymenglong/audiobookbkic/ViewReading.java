package com.bkic.lymenglong.audiobookbkic;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ViewReading extends AppCompatActivity{
    private int idChapter;
    private TextView tvReading;
    private String titleChapter, detailReadingHtml, detailReading;
    private CustomActionBar actionBar;
    private ScrollView scrollView;
    private final DisplayMetrics dm = new DisplayMetrics();
    int offset;
    private Button btnFavorite;

    private RequestQueue requestQueue;
    private static final String URL = "http://20121969.tk/audiobook/mobile_registration/history.php";
    private StringRequest request;
    private static final String favoriteURL = "http://20121969.tk/audiobook/mobile_registration/favorite.php";
    private StringRequest requestFavorite;
    private RequestQueue requestQueueFavorite;
    private Session session;
    private Activity activity = ViewReading.this;
    private boolean existedContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reading);
        getDataFromIntent();
        setTitle(titleChapter);
        ViewCompat.setImportantForAccessibility(getWindow().findViewById(R.id.tvToolbar), ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);
        initView();
        initObject();
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postFavoriteDataToServer();
            }
        });
    }

    private void postFavoriteDataToServer() {

        requestFavorite = new StringRequest(Request.Method.POST, favoriteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
                        Toast.makeText(getApplicationContext(),"Thành công, "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Lỗi, " +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Lỗi mạng, không được thêm vào yêu thích", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("IdBook", String.valueOf(idChapter));
                hashMap.put("IdUser", session.getUserIdLoggedIn());
                return hashMap;
            }
        };

        requestQueueFavorite.add(requestFavorite);
    }

    private void initObject() {
        detailReading = String.valueOf(Html.fromHtml(detailReadingHtml));
        tvReading = (TextView) findViewById(R.id.tvDetailReading);
        requestQueueFavorite = Volley.newRequestQueue(activity);
        if (!detailReadingHtml.trim().isEmpty()) {
            tvReading.setText(detailReading);
            existedContent = true;
        } else {
            tvReading.setText("Chưa cấp nhật dữ liệu, Vui lòng chọn sách khác");
            existedContent = false;
        }
        Log.d("scroll", String.valueOf(scrollView.getChildAt(0).getHeight()));
        Log.d("heightText", String.valueOf(getTextHeight(tvReading)) + " and "
                + String.valueOf(getScreenHeight())+ " num " + String.valueOf(numPage
                    (getTextHeight(tvReading), getScreenHeight() - 480)));
    }

    /**
     * Lấy dữ liệu từ intent
     */
    private void getDataFromIntent() {
        idChapter = getIntent().getIntExtra("idChapter",-1);
        titleChapter = getIntent().getStringExtra("titleChapter");
        detailReadingHtml = getIntent().getStringExtra("content");
        offset = getIntent().getIntExtra("offset", -1);
    }

    private void postDataToServer() {

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.names().get(0).equals("success")){
//                        Toast.makeText(getApplicationContext(),"SUCCESS "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(),"Thành công, "+jsonObject.getString("success"),Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Lỗi, " +jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getApplicationContext(), "Add To History Failed", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Lỗi mạng, không thể ghim vào lịch sử", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("IdBook", String.valueOf(idChapter));
                hashMap.put("IdUser", session.getUserIdLoggedIn());
//                hashMap.put("InsertTime","1234");
//                hashMap.put("PauseTime ","3210");
                return hashMap;
            }
        };

        requestQueue.add(request);
    }



    private void initView() {
        requestQueue = Volley.newRequestQueue(activity);
        session = new Session(this);
        actionBar = new CustomActionBar();
        detailReading = String.valueOf(Html.fromHtml(detailReadingHtml));
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        actionBar.eventToolbar(this, titleChapter, false);
        btnFavorite = findViewById(R.id.btn_add_favorite);

    }

    /**
     * Lấy độ cao của textview
     *
     * @param text
     * @return
     */
    private int getTextHeight(TextView text) {
        text.measure(0, 0);
        return text.getMeasuredHeight();
    }

    /**
     * Lấy độ cao của màn hình thiết bị
     *
     * @return
     */
    private int getScreenHeight() {
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * Lấy chiều rộng của thiết bị
     *
     * @return
     */
    private int getScreenWidth() {
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * Số trang của văn bản
     *
     * @param textHeight   chiều cao textview
     * @param screenHeight chiều cao màn hình
     * @return mỗi màn hình là 1 trang, dựa vào height xác định số trang của văn bản
     */
    private int numPage(int textHeight, int screenHeight) {
        int num = 0;
        num = textHeight / screenHeight;
        if (textHeight > screenHeight * num) {
            num += 1;
        }
        return num;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (existedContent) {
            postDataToServer();
        }
    }
}
