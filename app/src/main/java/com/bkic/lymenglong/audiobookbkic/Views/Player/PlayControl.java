package com.bkic.lymenglong.audiobookbkic.Views.Player;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.History.Utils.PlaybackHistory;
import com.bkic.lymenglong.audiobookbkic.Presenters.Favorite.UpdateFavorite.PresenterUpdateFavorite;
import com.bkic.lymenglong.audiobookbkic.Presenters.History.UpdateHistory.PresenterUpdateHistory;
import com.bkic.lymenglong.audiobookbkic.Presenters.Player.PresenterPlayer;
import com.bkic.lymenglong.audiobookbkic.Presenters.Review.PresenterReview;
import com.bkic.lymenglong.audiobookbkic.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_Audio;

public class PlayControl extends AppCompatActivity implements PlayerImp, View.OnClickListener{
    private PresenterPlayer presenterPlayer = new PresenterPlayer(this);
    private PresenterUpdateHistory presenterUpdateHistory = new PresenterUpdateHistory(this);
    private PresenterUpdateFavorite presenterUpdateFavorite = new PresenterUpdateFavorite(this);
    private PresenterReview presenterReview = new PresenterReview(this);
    private static final String TAG = "PlayControl";
    private Activity playControlActivity = PlayControl.this;
    private Session session;
    private int ResumeTime;
    private DBHelper dbHelper;
    private Button btnPlay, btnStop, btnPause, btnForward, btnBackward, btnNext, btnPrev, btnFavorite;
    private SeekBar seekBar;
    private TextView txtSongTotal;
    private TextView txtCurrentDuration;
    private int RateNumber;
    private String Review;
    private Chapter chapterFromIntent;

    public int getResumeTime() {
        return ResumeTime;
    }

    public void setResumeTime(int resumeTime) {
        ResumeTime = resumeTime;
    }

    public void setRateNumber(int rateNumber) {
        RateNumber = rateNumber;
    }

    public int getRateNumber() {
        return RateNumber;
    }

    public void setReview(String review) {
        Review = review;
    }

    public String getReview() {
        return Review;
    }

    //    DiskLruCache diskLruCache = DiskLruCache.open(getCacheDir(), 1, 1, 50 * 1024 * 1024);
//
//    public PlayControl() throws IOException {
//    }

    public TextView getTxtSongTotal() {
        return txtSongTotal;
    }
    public TextView getTxtCurrentDuration() {
        return txtCurrentDuration;
    }
    public SeekBar getSeekBar() {
        return seekBar;
    }
    private int indexChapterAudio = -1;
    private HashMap<String, Chapter> hashMapChapter = new HashMap<>();
    private Boolean InitialState = true;
    private HashMap<String, PlaybackHistory> historyHashMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_control);
        initDataFromIntent();
        initToolbar(chapterFromIntent.getTitle());
        initView();
        initObject();
        initCollectChapterUrl();
        initHistoryState();
        initCheckAudioUrl();
        intListener();

    }

    private void initHistoryState() {
        String SELECT_PLAY_BACK_HISTORY =
                "SELECT * FROM playHistory " +
                    "WHERE " +
                        "BookId = '"+chapterFromIntent.getBookId()+"' " +
                        "AND " +
                        "ChapterId = '"+chapterFromIntent.getId()+"'" +
                ";";
        Cursor cursor = dbHelper.GetData(SELECT_PLAY_BACK_HISTORY);
        while (cursor.moveToNext()){
            PlaybackHistory history = new PlaybackHistory
                    (
                            cursor.getInt(0),
                            cursor.getInt(1),
                            cursor.getInt(2),
                            cursor.getString(3)
                    );
            historyHashMap.put(String.valueOf(cursor.getInt(0)),history); //key = ChapterId
        }
    }

    private void initCollectChapterUrl(){
        /*JSONArray jsonArray = new JSONArray();*/
        String SELECT_FROM_CHAPTER = "SELECT * FROM CHAPTER WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_FROM_CHAPTER);
        while(cursor.moveToNext()){
            /*JSONObject jsonObject = new JSONObject();
            jsonObject.put("ChapterId",cursor.getInt(0));
            jsonObject.put("ChapterTitle", cursor.getString(1));
            jsonObject.put("ChapterUrl", cursor.getString(2));
            jsonObject.put("ChapterLength", cursor.getString(3));
            jsonObject.put("BookId", cursor.getString(4));
            jsonArray.put(cursor.getPosition(),jsonObject);*/
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            hashMapChapter.put(String.valueOf(cursor.getPosition()), chapterModel);
            if(chapterModel.getId() == chapterFromIntent.getId()){
                indexChapterAudio = cursor.getPosition();
            }
        }
    }

    /*private static final String TALKBACK_SERVICE_NAME = "com.google.android.marvin.talkback/.TalkBackService";

    private void updateTalkBackState(boolean enableTalkBack) {
        if (enableTalkBack) {
            enableAccessibilityService(TALKBACK_SERVICE_NAME);
        } else {
            disableAccessibilityServices();
        }
    }

    private void enableAccessibilityService(String name) {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, name);
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, VALUE_ENABLED);
    }

    private void disableAccessibilityServices() {
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, "");
        Settings.Secure.putString(getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, VALUE_DISABLED);
    }*/

    @Override
    public void initCheckAudioUrl() {
        if (chapterFromIntent.getFileUrl().isEmpty()) {
            Toast.makeText(playControlActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            playControlActivity.finish();
        } else{
            PrepareChapter();
        }
    }

    private void PrepareChapter() {
        if (0 <= indexChapterAudio && indexChapterAudio < hashMapChapter.size()) {
            if (!InitialState) presenterPlayer.StopMedia();
            initToolbar(hashMapChapter.get(String.valueOf(indexChapterAudio)).getTitle());
            //prepareMediaData
            //todo check internet connection

            try {
                int ChapterIdFromIndex = hashMapChapter.get(String.valueOf(indexChapterAudio)).getId();
                int ResumePosition = historyHashMap.get(String.valueOf(ChapterIdFromIndex)).getPauseTime();
                setResumeTime(ResumePosition);
            } catch (Exception ignored) {
                setResumeTime(0);
            }
            String ChapterUrlFromIndex = hashMapChapter.get(String.valueOf(indexChapterAudio)).getFileUrl();
            String AudioUrl = HttpURL_Audio + ChapterUrlFromIndex;
            presenterPlayer.PrepareMediaPlayer(AudioUrl);
            InitialState = false;
        } else if(indexChapterAudio < 0 ) {
            indexChapterAudio = 0;
            String message = "Chương bạn đã yêu cầu không tồn tại";
            Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
        } else {
            indexChapterAudio = hashMapChapter.size()-1;
            String message = "Chương bạn đã yêu cầu không tồn tại";
            Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void initObject() {
        session = new Session(playControlActivity);
        dbHelper = new DBHelper(this,DB_NAME,null,DB_VERSION);
        /*@SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");
        String ChapterLengthConverted = timeFormat.format(ChapterLength*1000); //ChapterLength is second unit so we convert to millisecond
        txtSongTotal.setText(ChapterLengthConverted);*/

    }

    private void initDataFromIntent() {
        chapterFromIntent = new Chapter
                (
                        getIntent().getIntExtra("ChapterId",-1),
                        getIntent().getStringExtra("ChapterTitle"),
                        getIntent().getStringExtra("ChapterUrl"),
                        getIntent().getIntExtra("ChapterLength", 0),
                        getIntent().getIntExtra("BookId",-1)
                );

    }

    private void initToolbar(String ChapterTitle) {
        setTitle(ChapterTitle);
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
        btnStop = findViewById(R.id.btn_replay);
        seekBar = findViewById(R.id.seekBar);
        txtSongTotal = findViewById(R.id.text_total_duration_label);
        txtCurrentDuration = findViewById(R.id.text_current_duration_label);
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
        seekBar.setOnSeekBarChangeListener(presenterPlayer);
    }

    @Override
    public void onClick(View v) {
        //region Switch Button
        switch (v.getId()){
            case R.id.btn_add_favorite_book:
                AddFavoriteBook();
                break;
            case R.id.btn_play :
                presenterPlayer.PlayMedia();
                break;
            case R.id.btn_pause:
                presenterPlayer.PauseMedia();
//                presenterReview.ReviewBookDialog(playControlActivity);
//                presenterReview.ReviewBookDialog2(playControlActivity);
                presenterReview.ReviewBookDialog3(playControlActivity);
                break;
            case R.id.btn_replay:
                presenterPlayer.ReplayMedia();
                break;
            case R.id.btn_next:
                indexChapterAudio++;
                PrepareChapter();
                break;
            case R.id.btn_previous:
                indexChapterAudio--;
                PrepareChapter();
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
    public void AddReviewBookToServer() {
        int userId = session.getUserIdLoggedIn();
        int bookId = chapterFromIntent.getBookId();
        int rateNumber = getRateNumber();
        String review = getReview();
        presenterReview.RequestReviewBook(playControlActivity,userId,bookId,rateNumber,review);
    }

    private void AddFavoriteBook() {
        //region Update to favorite with httpWebCall
        //todo check internet connection
        String jsonAction = "addFavourite";
        String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
        String IdBookHolder = String.valueOf(chapterFromIntent.getBookId());
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String InsertTimeHolder = simpledateformat.format(calendar.getTime());
        presenterUpdateFavorite.RequestUpdateToServer(jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);
        //endregion

        //region ADD BOOK IN TO TABLE FAVORITE SQLite
        String SELECT_BOOK_BY_BOOK_ID =
                "SELECT " +
                        "BookId, " +
                        "BookTitle, " +
                        "BookImage, " +
                        "BookLength, " +
                        "BookAuthor " +
                        "FROM " +
                        "book " +
                        "WHERE BookId = '"+chapterFromIntent.getBookId()+"'" +
                        ";";
        Cursor cursor = dbHelper.GetData(SELECT_BOOK_BY_BOOK_ID);
        Book bookModel = new Book();
        while (cursor.moveToNext()) {
            bookModel = new Book(cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(3),
                    cursor.getString(4)
            );
        }
        try {
            String INSERT_BOOK_INTO_TABLE_FAVORITE =
                    "INSERT INTO favorite VALUES" +
                            "(" +
                            "'"+bookModel.getId()+"', " +
                            "'"+bookModel.getTitle()+"', " +
                            "'"+bookModel.getUrlImage()+"', " +
                            "'"+bookModel.getLength()+"', " +
                            "'"+bookModel.getAuthor()+"'" +
                            ");";
            dbHelper.QueryData(INSERT_BOOK_INTO_TABLE_FAVORITE);
        } catch (Exception ignored) {
            String UPDATE_BOOK_IN_TABLE_FAVORITE =
                    "UPDATE favorite SET " +
                            "BookTitle = '"+bookModel.getTitle()+"', " +
                            "BookImage = '"+bookModel.getUrlImage()+"', " +
                            "BookLength = '"+bookModel.getLength()+"', " +
                            "BookAuthor = '"+bookModel.getAuthor()+"' " +
                            "WHERE " +
                            "BookId = '"+bookModel.getId()+"'" +
                            ";";
            dbHelper.QueryData(UPDATE_BOOK_IN_TABLE_FAVORITE);
        }
        dbHelper.close();
        //endregion
    }

    @Override
    protected void onDestroy() {
        //<editor-fold desc="Remove event handler from seek bar">
        presenterPlayer.RemoveCallBacksUpdateHandler();
        //</editor-fold>
        UpdateHistoryData();
        presenterPlayer.ReleaseMediaPlayer();
        super.onDestroy();
    }

    private void UpdateHistoryData() {
        if (!chapterFromIntent.getFileUrl().isEmpty()) {
            int lastPlayDuration = presenterPlayer.GetLastMediaData();
            String jsonAction = "addHistory";
            String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
            String IdBookHolder = String.valueOf(chapterFromIntent.getBookId());
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String InsertTimeHolder = simpledateformat.format(calendar.getTime());

            //region INSERT VALUE TO SQLite DATABASE
            //region Update Table playHistory
            try {
                String INSERT_PLAY_HISTORY =
                        "INSERT INTO playHistory VALUES" +
                                "(" +
                                "'"+chapterFromIntent.getId()+"', " +
                                "'"+IdBookHolder+"', " +
                                "'"+lastPlayDuration +"', " +
                                "'"+InsertTimeHolder+"'"+
                                ");";
                dbHelper.QueryData(INSERT_PLAY_HISTORY);
            } catch (Exception ignored) {
                String UPDATE_PLAY_HISTORY =
                        "UPDATE playHistory SET " +
                                "PauseTime = '"+ lastPlayDuration +"', " +
                                "LastDate ='"+InsertTimeHolder+"' " +
                                "WHERE " +
                                "ChapterId = '"+chapterFromIntent.getId()+"' " +
                                "AND " +
                                "BookId = '"+chapterFromIntent.getBookId()+"'" +
                                ";";
                dbHelper.QueryData(UPDATE_PLAY_HISTORY);
            }
            //endregion
            //region INSERT BOOK VALUE TO HISTORY (SQLite)
            String SELECT_BOOK_BY_BOOK_ID =
                    "SELECT " +
                            "BookId, " +
                            "BookTitle, " +
                            "BookImage, " +
                            "BookLength, " +
                            "BookAuthor " +
                            "FROM " +
                            "book " +
                            "WHERE BookId = '"+chapterFromIntent.getBookId()+"'" +
                            ";";
            Cursor cursor = dbHelper.GetData(SELECT_BOOK_BY_BOOK_ID);
            Book bookModel = new Book();
            while (cursor.moveToNext()) {
                bookModel = new Book(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getInt(3),
                        cursor.getString(4)
                );
            }
            try {
                String INSERT_BOOK_INTO_TABLE_HISTORY =
                        "INSERT INTO history VALUES" +
                                "(" +
                                "'"+bookModel.getTitle()+"', " +
                                "'"+bookModel.getTitle()+"', " +
                                "'"+bookModel.getUrlImage()+"', " +
                                "'"+bookModel.getLength()+"', " +
                                "'"+bookModel.getAuthor()+"'" +
                                ");";
                dbHelper.QueryData(INSERT_BOOK_INTO_TABLE_HISTORY);
            } catch (Exception ignored) {
                String UPDATE_BOOK_IN_TABLE_HISTORY =
                        "UPDATE history SET " +
                                "BookTitle = '"+bookModel.getTitle()+"', " +
                                "BookImage = '"+bookModel.getUrlImage()+"', " +
                                "BookLength = '"+bookModel.getLength()+"', " +
                                "BookAuthor = '"+bookModel.getAuthor()+"' " +
                                "WHERE " +
                                "BookId = '"+bookModel.getId()+"'" +
                                ";";
                dbHelper.QueryData(UPDATE_BOOK_IN_TABLE_HISTORY);
            }
            //endregion
            dbHelper.close();
            //endregion
            //todo check internet connection
            presenterUpdateHistory.RequestUpdateToServer(jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);
            //todo Rating Book
        }
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

    @Override
    public void UpdateReviewSuccess(String message) {
        Log.d(TAG, "UpdateReviewSuccess: "+message);
    }

    @Override
    public void UpdateReviewFailed(String message) {
        Log.d(TAG, "UpdateReviewFailed: "+message);
    }
}
