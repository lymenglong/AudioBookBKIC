package com.bkic.lymenglong.audiobookbkic.Views.Player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Https.HttpParse;
import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Presenters.Player.PresenterPlayer;
import com.bkic.lymenglong.audiobookbkic.R;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PlayControl extends AppCompatActivity implements PlayerImp, View.OnClickListener{
    PresenterPlayer presenterPlayer = new PresenterPlayer(this);
    private Button btnPlay, btnStop, btnPause, btnForward, btnBackward, btnNext, btnPrev, btnFavorite;
    private Activity playControlActivity = PlayControl.this;
    private SeekBar songProgressBar;
    public int lastPlayDuration = 0;
    private RequestQueue requestQueueHistory, requestQueueFavorite;
    private static final String historyURL = "http://20121969.tk/audiobook/mobile_registration/history.php";
    private static final String favoriteURL = "http://20121969.tk/audiobook/mobile_registration/favorite.php";
    private Session session;
    private String getFileUrlChapter;
    private String getTitleChapter;
    private int getIdChapter;
    public int getPauseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_control);
        initDataFromIntent();
        setTitle(getTitleChapter);
        initToolbar();
        initView();
        initObject();
        initCheckAudioUrl();
        intListener();
    }
    //region Method to Update History
    String HttpUrlUpdateHistory = "http://20121969.tk/SachNoiBKIC/UpdateHistory.php";
    String HttpUrlUpdateFavorite = "http://20121969.tk/SachNoiBKIC/UpdateFavorite.php";
    String finalResult ;
    HashMap<String,String> hashMap = new HashMap<>();
    HttpParse httpParse = new HttpParse();
    public void UpdateRecordData(final String S_IdUser,
                                 final String S_IdBook,
                                 final String S_InsertTime,
                                 final String S_PauseTime,
                                 final String S_HttpURL,
                                 final String S_Status){

        @SuppressLint("StaticFieldLeak")
        class UpdateRecordDataClass extends AsyncTask<String,Void,String> {
            @Override
            protected void onPostExecute(String httpResponseMsg) {
                super.onPostExecute(httpResponseMsg);
            }

            @Override
            protected String doInBackground(String... params) {

                PutHashMapToServer(params, hashMap);

//                finalResult = httpParse.postRequest(hashMap, HttpUrlUpdateHistory);
                finalResult = httpParse.postRequest(hashMap, params[4]);

                return finalResult;
            }
        }

        UpdateRecordDataClass updateRecordDataClass = new UpdateRecordDataClass();

        updateRecordDataClass.execute(S_IdUser,S_IdBook,S_InsertTime,S_PauseTime,S_HttpURL,S_Status);
    }

    private void PutHashMapToServer(String [] params, HashMap<String,String> hashMap) {

        if(params[4].equals(HttpUrlUpdateHistory)){

            hashMap.put("IdUser",params[0]);

            hashMap.put("IdBook",params[1]);

            hashMap.put("InsertTime",params[2]);

            hashMap.put("PauseTime",params[3]);
        }
        if(params[4].equals(HttpUrlUpdateFavorite)){

            hashMap.put("IdUser",params[0]);

            hashMap.put("IdBook",params[1]);

            hashMap.put("InsertTime",params[2]);

            hashMap.put("Status",params[5]);
        }
    }
    //endregion

    //region Insert history data to server OLD CODE
    private void postHistoryDataToServer() {

        StringRequest requestHistory = new StringRequest(Request.Method.POST, historyURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(playControlActivity, "Thành công, " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(playControlActivity, "Lỗi, " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Thêm vào lịch sử thất bại", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("IdBook", String.valueOf(getIdChapter));
                hashMap.put("IdUser", session.getUserIdLoggedIn());
                hashMap.put("InsertTime", "30032018");
                hashMap.put("PauseTime ", String.valueOf(lastPlayDuration));
                return hashMap;
            }
        };

        requestQueueHistory.add(requestHistory);
    }
    //endregion

    private void postFavoriteDataToServer() {

        StringRequest requestFavorite = new StringRequest(Request.Method.POST, favoriteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), "Thành công, " + jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Lỗi, " + jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("IdBook", String.valueOf(getIdChapter));
                hashMap.put("IdUser", session.getUserIdLoggedIn());
//                hashMap.put("InsertTime","1234");
//                hashMap.put("Status","3210");
                return hashMap;
            }
        };

        requestQueueFavorite.add(requestFavorite);
    }

    @Override
    public void initCheckAudioUrl() {
        if (getFileUrlChapter.isEmpty()) {
            Toast.makeText(playControlActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            playControlActivity.finish();
        } else{
            //prepareMediaData
            presenterPlayer.PrepareMediaPlayer(getFileUrlChapter);
        }
    }

    private void initObject() {
        requestQueueHistory = Volley.newRequestQueue(playControlActivity);
        requestQueueFavorite = Volley.newRequestQueue(playControlActivity);
        session = new Session(playControlActivity);
    }

    private void initDataFromIntent() {
        getFileUrlChapter = getIntent().getStringExtra("fileUrl");
        getIdChapter = getIntent().getIntExtra("idChapter",-1);
        getTitleChapter = getIntent().getStringExtra("titleChapter");
        getPauseTime = getIntent().getIntExtra("pauseTime", 0);
    }

    private void initToolbar() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, getTitleChapter, false);
    }

    private void initView() {
        btnFavorite = findViewById(R.id.btn_add_favorite);
        btnPlay = findViewById(R.id.btn_play);
        btnPause = findViewById(R.id.btn_pause);
        btnForward = findViewById(R.id.btn_ffw);
        btnBackward = findViewById(R.id.btn_backward);
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_previous);
        btnStop = findViewById(R.id.btn_stop);
        songProgressBar = findViewById(R.id.seekBar);
    }

    private void intListener() {
        btnFavorite.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnForward.setOnClickListener(this);
        btnBackward.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        songProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                if(b){
//                    mediaPlayer.seekTo(i);
//                    seekBar.setProgress(i);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        });
    }


    @Override
    public void onClick(View v) {
        //region Switch Button
        switch (v.getId()){
            case R.id.btn_add_favorite:
                postFavoriteDataToServer();
                //region Update to favorite with httpWebCall
//                    String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
//                    String IdBookHolder = String.valueOf(getIdChapter);
//                    String InsertTimeHolder = String.valueOf(12345); //todo get current date when post to server
//                    String PauseTimeHolder = String.valueOf(lastPlayDuration);
//                    String HttpUrlHolder = String.valueOf(HttpUrlUpdateFavorite);
//                    String Status = String.valueOf(5);
//                    UpdateRecordData(IdUserHolder,IdBookHolder,InsertTimeHolder,PauseTimeHolder,HttpUrlHolder,Status);
                //endregion
                break;
            case R.id.btn_play :
                presenterPlayer.PlayMedia();
                break;
            case R.id.btn_pause:
                presenterPlayer.PauseMedia();
                break;
            case R.id.btn_stop:
                presenterPlayer.ReplayMedia();
                break;
            case R.id.btn_next:
                presenterPlayer.NextMedia();
                break;
            case R.id.btn_previous:
                presenterPlayer.PreviousMedia();
                break;
            case R.id.btn_ffw:
                presenterPlayer.ForwardMedia();
                break;
            case R.id.btn_backward:
                presenterPlayer.RewindMedia();
                break;
        }
        //endregion
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!getFileUrlChapter.isEmpty()) {
            presenterPlayer.GetLastMediaData();
            try {
                postHistoryDataToServer();
            } catch (Exception e) {
                //TODO update history when destroy activity
                //region UpdateHistoryRecordData
                String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
                String IdBookHolder = String.valueOf(getIdChapter);
                String InsertTimeHolder = String.valueOf(12345); //todo get current date when post to server
                String PauseTimeHolder = String.valueOf(lastPlayDuration);
                String HttpUrlHolder = String.valueOf(HttpUrlUpdateHistory);
                UpdateRecordData(IdUserHolder,IdBookHolder,InsertTimeHolder,PauseTimeHolder,HttpUrlHolder,"0");
                //endregion
            }
        }
    }
}
