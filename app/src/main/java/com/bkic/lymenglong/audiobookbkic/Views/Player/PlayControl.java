package com.bkic.lymenglong.audiobookbkic.Views.Player;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Account.Login.Session;
import com.bkic.lymenglong.audiobookbkic.Models.CheckInternet.ConnectivityReceiver;
import com.bkic.lymenglong.audiobookbkic.Models.CheckInternet.MyApplication;
import com.bkic.lymenglong.audiobookbkic.Models.Customizes.CustomActionBar;
import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.Download.DownloadTask;
import com.bkic.lymenglong.audiobookbkic.Models.Download.Utils;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.History.Utils.PlaybackHistory;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Book;
import com.bkic.lymenglong.audiobookbkic.Models.HandleLists.Utils.Chapter;
import com.bkic.lymenglong.audiobookbkic.Models.Utils.Const;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.Favorite.UpdateFavorite.PresenterUpdateFavorite;
import com.bkic.lymenglong.audiobookbkic.Presenters.HandleLists.History.UpdateHistory.PresenterUpdateHistory;
import com.bkic.lymenglong.audiobookbkic.Presenters.Player.PresenterPlayer;
import com.bkic.lymenglong.audiobookbkic.Presenters.Review.PresenterReview;
import com.bkic.lymenglong.audiobookbkic.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_NAME;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.DB_VERSION;
import static com.bkic.lymenglong.audiobookbkic.Models.Utils.Const.HttpURL_Audio;

public class PlayControl extends AppCompatActivity
        implements PlayerImp, ConnectivityReceiver.ConnectivityReceiverListener{
    private PresenterPlayer presenterPlayer = new PresenterPlayer(this);
    private PresenterUpdateHistory presenterUpdateHistory = new PresenterUpdateHistory(this);
    private PresenterUpdateFavorite presenterUpdateFavorite = new PresenterUpdateFavorite(this);
    private PresenterReview presenterReview = new PresenterReview(this);
    private static final String TAG = "PlayControl";
    private Activity playControlActivity = PlayControl.this;
    private Session session;
    private int ResumeTime;
    private DBHelper dbHelper;
    private Button btnPlay, btnStop, btnPause, btnForward, btnBackward, btnNext, btnPrev, btnFavorite, btnDownload;
    private SeekBar seekBar;
    private TextView txtSongTotal;
    private TextView txtCurrentDuration;
    private int RateNumber;
    private String Review;
    private Chapter chapterFromIntent;
    private String AudioUrl;

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
    private int indexChapterMap = -1;
    private HashMap<String, Chapter> hashMapChapter = new HashMap<>();
    private Boolean InitialState = true;
    private HashMap<String, PlaybackHistory> historyHashMap = new HashMap<>();

    private IntentFilter intentFilter;
    private ConnectivityReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_control);
        initIntentFilter();
        initDataFromIntent();
        initView();
        initToolbar(chapterFromIntent.getTitle());
        initObject();
        initCollectChapterData();
        initCheckChapterStatus();
        initHistoryState();
        initCheckAudioUrl();
        intListener();

    }

    //Module checkIfAlreadyhavePermission() is implemented as :
    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    //Module requestForSpecificPermission() is implemented as :
    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions
                (
                        this,
                        new String[]
                                {
//                                        Manifest.permission.GET_ACCOUNTS,
//                                        Manifest.permission.RECEIVE_SMS,
//                                        Manifest.permission.READ_SMS,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                        101
                );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    new DownloadTask
                            (
                                    playControlActivity.getBaseContext(),
                                    btnDownload,
                                    AudioUrl,
                                    String.valueOf(chapterFromIntent.getBookId()),
                                    String.valueOf(chapterFromIntent.getId()),
                                    chapterFromIntent.getBookId(),
                                    chapterFromIntent.getId()
                            );
                } else {
                    //not granted
                    Toast.makeText(playControlActivity, "Check", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private Boolean initCheckChapterStatus() {
        if(!InitialState){
            initReloadChapterData();
        }
        Boolean isDownloaded = hashMapChapter.get(String.valueOf(indexChapterMap)).getStatus() != 0;
        if(isDownloaded){
            btnDownload.setEnabled(false);
            btnDownload.setText(R.string.downloadCompleted);//If Download completed then change button text
        } else {
            btnDownload.setEnabled(true);
            btnDownload.setText(R.string.download);//If Download completed then change button text
        }
        return isDownloaded;
    }

    private void initIntentFilter() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new ConnectivityReceiver();
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

    private void initCollectChapterData(){
        String SELECT_FROM_CHAPTER = "SELECT * FROM CHAPTER WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_FROM_CHAPTER);
        while(cursor.moveToNext()){
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            chapterModel.setStatus(cursor.getInt(5));
            hashMapChapter.put(String.valueOf(cursor.getPosition()), chapterModel);
            if(chapterModel.getId() == chapterFromIntent.getId()){
                indexChapterMap = cursor.getPosition();
            }
        }
    }

    private void initReloadChapterData(){
        String SELECT_FROM_CHAPTER = "SELECT * FROM CHAPTER WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_FROM_CHAPTER);
        while(cursor.moveToNext()){
            Chapter chapterModel = new Chapter();
            chapterModel.setId(cursor.getInt(0));
            chapterModel.setTitle(cursor.getString(1));
            chapterModel.setFileUrl(cursor.getString(2));
            chapterModel.setLength(cursor.getInt(3));
            chapterModel.setBookId(cursor.getInt(4));
            chapterModel.setStatus(cursor.getInt(5));
            hashMapChapter.put(String.valueOf(cursor.getPosition()), chapterModel);
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

    private void initCheckAudioUrl() {
        if (chapterFromIntent.getFileUrl().isEmpty()) {
            Toast.makeText(playControlActivity, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            playControlActivity.finish();
        } else{
            PrepareChapter();
        }
    }

    @Override
    public void PrepareChapter() {
        if (0 <= indexChapterMap && indexChapterMap < hashMapChapter.size()) {
            if (!InitialState) presenterPlayer.StopMedia();
            String indexChapterTitle = hashMapChapter.get(String.valueOf(indexChapterMap)).getTitle();
            initToolbar(indexChapterTitle);
            chapterFromIntent.setId(hashMapChapter.get(String.valueOf(indexChapterMap)).getId());
            initHistoryState();
            try {
                int ChapterIdFromIndex = hashMapChapter.get(String.valueOf(indexChapterMap)).getId();
                int ResumePosition = historyHashMap.get(String.valueOf(ChapterIdFromIndex)).getPauseTime();
                setResumeTime(ResumePosition);
            } catch (Exception ignored) {
                setResumeTime(0);
            }
            Boolean isDownloadedAudio = initCheckChapterStatus();
            if(isDownloadedAudio){
                AudioUrl =      Environment.getExternalStorageDirectory().getPath()+ "/"
                        + Utils.downloadDirectory + "/"
                        + chapterFromIntent.getBookId() + "/"
                        + chapterFromIntent.getId() + ".mp3";
            } else{
                String ChapterUrlFromIndex = hashMapChapter.get(String.valueOf(indexChapterMap)).getFileUrl();
                AudioUrl = HttpURL_Audio + ChapterUrlFromIndex;
            }
            if (ConnectivityReceiver.isConnected()) presenterPlayer.PrepareMediaPlayer(AudioUrl, isDownloadedAudio);
            InitialState = false;
        } else if(indexChapterMap < 0 ) {
            indexChapterMap = 0;
            String message = "Chương bạn đã yêu cầu không tồn tại";
            Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
        } else {
            indexChapterMap = hashMapChapter.size()-1;
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
        btnDownload = findViewById(R.id.btn_download);
        seekBar = findViewById(R.id.seekBar);
        txtSongTotal = findViewById(R.id.text_total_duration_label);
        txtCurrentDuration = findViewById(R.id.text_current_duration_label);
    }

    private void intListener() {
        btnFavorite.setOnClickListener(onClickListener);
        btnPlay.setOnClickListener(onClickListener);
        btnPause.setOnClickListener(onClickListener);
        btnForward.setOnClickListener(onClickListener);
        btnBackward.setOnClickListener(onClickListener);
        btnNext.setOnClickListener(onClickListener);
        btnPrev.setOnClickListener(onClickListener);
        btnStop.setOnClickListener(onClickListener);
        btnDownload.setOnClickListener(onClickListener);
        seekBar.setOnSeekBarChangeListener(presenterPlayer);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
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
                    presenterReview.ReviewBookDialog(playControlActivity);
//                presenterReview.ReviewBookDialog2(playControlActivity);
//                presenterReview.ReviewBookDialog3(playControlActivity);
                    break;
                case R.id.btn_replay:
                    presenterPlayer.ReplayMedia();
                    break;
                case R.id.btn_next:
                    UpdateHistoryData();
                    indexChapterMap++;
                    PrepareChapter();
                    break;
                case R.id.btn_previous:
                    UpdateHistoryData();
                    indexChapterMap--;
                    PrepareChapter();
                    break;
                case R.id.btn_ffw:
                    presenterPlayer.ForwardMedia();
                    break;
                case R.id.btn_backward:
                    presenterPlayer.RewindMedia();
                    break;
                case R.id.btn_download:
                    //check if my app has permission and than request if it does not have permission
                    int MyVersion = Build.VERSION.SDK_INT;
                    if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if (!checkIfAlreadyhavePermission()) {
                            requestForSpecificPermission();
                        }
                    }else {
                        new DownloadTask
                                (
                                        playControlActivity.getBaseContext(),
                                        btnDownload,
                                        AudioUrl,
                                        String.valueOf(chapterFromIntent.getBookId()),
                                        String.valueOf(chapterFromIntent.getId()),
                                        chapterFromIntent.getBookId(),
                                        chapterFromIntent.getId()
                                );
                    }
                    break;
            }
            //endregion
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // register receiver
        registerReceiver(receiver, intentFilter);
        // register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister receiver
        unregisterReceiver(receiver);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ToastConnectionMessage(isConnected);
    }

    private void ToastConnectionMessage(boolean isConnected) {
        String message;
        if (isConnected) {
            message = "Good! Connected to Internet";
        } else {
            message = "Sorry! Not connected to internet";
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
        String jsonAction = "addFavourite";
        String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
        String IdBookHolder = String.valueOf(chapterFromIntent.getBookId());
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String InsertTimeHolder = simpledateformat.format(calendar.getTime());
        if (ConnectivityReceiver.isConnected()&&!CheckBookSynced("favorite")) {
            presenterUpdateFavorite.RequestUpdateToServer(jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);
        }
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
                "WHERE " +
                        "BookId = '"+chapterFromIntent.getBookId()+"'" +
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
                                    "'"+bookModel.getAuthor()+"', " +
                                    "'"+Const.BOOK_NOT_SYNCED_WITH_SERVER+"'" +// BookSync Is Default Equal 0
                            ");";
            dbHelper.QueryData(INSERT_BOOK_INTO_TABLE_FAVORITE);
        } catch (Exception ignored) {
            String UPDATE_BOOK_IN_TABLE_FAVORITE =
                    "UPDATE " +
                            "favorite " +
                    "SET " +
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

    @NonNull
    private Boolean CheckBookSynced(String tableName){
        String SELECT_BOOK_SYNC =
                "SELECT " +
                        "BookSync " +
                "FROM " +
                        ""+tableName+" " +
                "WHERE BookId = '"+chapterFromIntent.getBookId()+"'";
        Cursor cursor = dbHelper.GetData(SELECT_BOOK_SYNC);
        int BookSync = 0;
        while (cursor.moveToNext()){
            BookSync = cursor.getInt(0);
        }
        return BookSync == 1;
    }

    @Override
    public void UpdateHistoryData() {
        if (!chapterFromIntent.getFileUrl().isEmpty()) {
            int lastPlayDuration = presenterPlayer.GetLastMediaData();
            String jsonAction = "addHistory";
            String IdUserHolder = String.valueOf(session.getUserIdLoggedIn());
            String IdBookHolder = String.valueOf(chapterFromIntent.getBookId());
            Calendar calendar = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpledateformat =
                    new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String InsertTimeHolder = simpledateformat.format(calendar.getTime());
            if(ConnectivityReceiver.isConnected() && !CheckBookSynced("history"))presenterUpdateHistory.RequestUpdateToServer
                    (jsonAction,IdUserHolder,IdBookHolder,InsertTimeHolder);

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
                        "UPDATE " +
                                "playHistory " +
                        "SET " +
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
                    "WHERE " +
                            "BookId = '"+chapterFromIntent.getBookId()+"'" +
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
                                        "'"+bookModel.getId()+"', " +
                                        "'"+bookModel.getTitle()+"', " +
                                        "'"+bookModel.getUrlImage()+"', " +
                                        "'"+bookModel.getLength()+"', " +
                                        "'"+bookModel.getAuthor()+"', " +
                                        "'"+Const.BOOK_NOT_SYNCED_WITH_SERVER+"'" +
                                ");";
                dbHelper.QueryData(INSERT_BOOK_INTO_TABLE_HISTORY);
            } catch (Exception ignored) {
                String UPDATE_BOOK_IN_TABLE_HISTORY =
                        "UPDATE " +
                                "history " +
                        "SET " +
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
            //todo Rating Book
        }
    }

    //SyncBook For History & Favorite
    private void SyncBook(String tableName) {
        String UPDATE_BOOK_SYNC =
                "UPDATE " +
                        "'"+tableName+"' " +
                "SET " +
                        "BookSync = '"+ Const.BOOK_SYNCED_WITH_SERVER+"' " +
                "WHERE " +
                        "BookId = '"+chapterFromIntent.getBookId()+"'" +
                ";";
        dbHelper.QueryData(UPDATE_BOOK_SYNC);
        dbHelper.close();
    }

    @Override
    public void UpdateHistorySuccess(String message) {
        SyncBook("history");
        Log.d(TAG, "UpdateHistorySuccess: "+message);
    }

    @Override
    public void UpdateHistoryFailed(String message) {
        Log.d(TAG, "UpdateHistoryFailed: "+message);
    }

    @Override
    public void UpdateFavoriteSuccess(String message) {
        SyncBook("favorite");
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
