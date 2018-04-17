package com.bkic.lymenglong.audiobookbkic.Views.Player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Presenters.Favorite.UpdateFavorite.PresenterUpdateFavorite;
import com.bkic.lymenglong.audiobookbkic.Presenters.History.UpdateHistory.PresenterUpdateHistory;
import com.bkic.lymenglong.audiobookbkic.Presenters.Player.PresenterPlayer;
import com.bkic.lymenglong.audiobookbkic.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_Audio;

public class PlayControl extends AppCompatActivity implements PlayerImp, View.OnClickListener{
    PresenterPlayer presenterPlayer = new PresenterPlayer(this);
    PresenterUpdateHistory presenterUpdateHistory = new PresenterUpdateHistory(this);
    PresenterUpdateFavorite presenterUpdateFavorite = new PresenterUpdateFavorite(this);
    private static final String TAG = "PlayControl";
    private Button btnPlay, btnStop, btnPause, btnForward, btnBackward, btnNext, btnPrev, btnFavorite;
    private Activity playControlActivity = PlayControl.this;
    private SeekBar songProgressBar;
    private int lastPlayDuration = 0;
//    private RequestQueue requestQueueHistory, requestQueueFavorite;
    private Session session;
    private String ChapterUrl;
    private String ChapterTitle;
    private int ChapterId;
    private int ChapterLength;
    public int PauseTime;
    private int BookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_control);
        initDataFromIntent();
        setTitle(ChapterTitle);
        initToolbar();
        initView();
        initObject();
        initCheckAudioUrl();
        intListener();
    }

    @Override
    public void initCheckAudioUrl() {
        if (ChapterUrl.isEmpty()) {
            Toast.makeText(playControlActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            playControlActivity.finish();
        } else{
            //prepareMediaData
            //todo check internet connection
            String AudioUrl = HttpURL_Audio + ChapterUrl;
            presenterPlayer.PrepareMediaPlayer(AudioUrl);
        }
    }

    private void initObject() {
//        requestQueueHistory = Volley.newRequestQueue(playControlActivity);
//        requestQueueFavorite = Volley.newRequestQueue(playControlActivity);
        session = new Session(playControlActivity);
    }

    private void initDataFromIntent() {
        ChapterId = getIntent().getIntExtra("ChapterId",-1);
        ChapterTitle = getIntent().getStringExtra("ChapterTitle");
        ChapterUrl = getIntent().getStringExtra("ChapterUrl");
        ChapterLength = getIntent().getIntExtra("ChapterLength",0);
        BookId = getIntent().getIntExtra("BookId",-1);
/*        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String InsertTimeHolder = simpledateformat.format(calendar.getTime());*/
    }

    private void initToolbar() {
        CustomActionBar actionBar = new CustomActionBar();
        actionBar.eventToolbar(this, ChapterTitle, false);
    }

    private void initView() {
        btnFavorite = findViewById(R.id.btn_add_favorite_book);
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
            case R.id.btn_add_favorite_book:
                //region Update to favorite with httpWebCall
                    String jsonAction = "addFavourite";
                    String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
                    String IdBookHolder = String.valueOf(BookId);
                    Calendar calendar = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                            new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String InsertTimeHolder = simpledateformat.format(calendar.getTime());
                    presenterUpdateFavorite.RequestUpdateToServer(jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);
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
        if (!ChapterUrl.isEmpty()) {
            lastPlayDuration = presenterPlayer.GetLastMediaData();

            //todo Rating Book

            String jsonAction = "addHistory";
            String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
            String IdBookHolder = String.valueOf(BookId);
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String InsertTimeHolder = simpledateformat.format(calendar.getTime());
            //todo check internet connection
            presenterUpdateHistory.RequestUpdateToServer(jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);
            //endregion
//            }
        }
        super.onDestroy();
    }

    @Override
    public void UpdateHistorySuccess(String message) {
        Log.d(TAG, "UpdateHistorySuccess: "+message);
    }

    @Override
    public void UpdateHistoryFailed(String message) {
        Log.d(TAG, "UpdateHistoryFailed: "+message);
    }

    @Override
    public void UpdateFavoriteSuccess(String message) {
        Log.d(TAG, "UpdateFavoriteSuccess: "+message);
    }
    @Override
    public void UpdateFavoriteFailed(String message) {
        Log.d(TAG, "UpdateFavoriteSuccess: "+message);
    }
}
