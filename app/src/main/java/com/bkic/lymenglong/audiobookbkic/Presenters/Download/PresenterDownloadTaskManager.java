package com.bkic.lymenglong.audiobookbkic.Presenters.Download;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Download.CheckForSDCard;
import com.bkic.lymenglong.audiobookbkic.Models.Download.Utils;
import com.bkic.lymenglong.audiobookbkic.Models.Services.MyDownloadService;
import com.bkic.lymenglong.audiobookbkic.R;

import java.io.File;
import java.util.HashMap;

import static android.content.Context.DOWNLOAD_SERVICE;

public class PresenterDownloadTaskManager implements PresenterDownloadTaskManagerImp {

    private static final String TAG = "Download Task";
    private Context context;
    private String downloadUrl;
    private String subFolderPath;
    private int BookId, ChapterId;
    private Button buttonText;
    private static HashMap<String,DownloadingIndex> downloadingIndexHashMap = new HashMap<>();
    private long downloadId;
    private DownloadManager downloadManager;

    public class DownloadingIndex {
        private int bookId;
        private int chapterId;

        DownloadingIndex(int bookId, int chapterId) {
            this.bookId = bookId;
            this.chapterId = chapterId;
        }

        public int getBookId() {
            return bookId;
        }

        public int getChapterId() {
            return chapterId;
        }
    }

    @Override
    public void DownloadTaskManager(Context context, Button buttonText, String downloadUrl, String subFolderPath, String fileName, int BookId, int ChapterId) {
        this.context = context;
        this.downloadUrl = downloadUrl;
        this.BookId = BookId;
        this.ChapterId = ChapterId;
        this.buttonText = buttonText;
        this.subFolderPath = subFolderPath; //subFolderPath we use the name of each book

        String downloadFileName = fileName.replace(" ", "_") + ".mp3";
        Log.d(TAG, downloadFileName);

        //Start Downloading Task
        new DownloadingTask().execute();
    }


    @SuppressLint("StaticFieldLeak")
    public class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File apkStorage = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonText.setEnabled(false);
            buttonText.setText(R.string.downloadStarted);//Set Button Text when download started
            //Start Background Service
            if(!isMyServiceRunning(MyDownloadService.class))
                context.startService(new Intent(context, MyDownloadService.class));
        }

        @Override
        protected Void doInBackground(Void... arg0) {
                //Get File if SD card is present
                if (new CheckForSDCard().isSDCardPresent()) apkStorage = new File(
                        Environment.getExternalStorageDirectory() + "/"
                                + Utils.downloadDirectory + "/" + subFolderPath);
                else
                    Toast.makeText(context, "Oops!! There is no SD Card.", Toast.LENGTH_SHORT).show();

                //Check permission for api 24 or higher
                int code = context.getPackageManager().checkPermission(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        context.getPackageName());
                if (code == PackageManager.PERMISSION_GRANTED) {
                    //If File is not present create directory
                    boolean isDirectoryCreated=apkStorage.exists();
                    if (!isDirectoryCreated) {
                        isDirectoryCreated = apkStorage.mkdir();
                        Log.e(TAG, "Directory Created.");
                    }
                    if(isDirectoryCreated) {
                        //download file using download manager
                        downloadId = DownloadData(Uri.parse(downloadUrl), BookId, ChapterId);
                        DownloadingIndex index = new DownloadingIndex
                                (
                                        BookId,
                                        ChapterId
                                );
                        downloadingIndexHashMap.put(String.valueOf(downloadId),index);
                    }
                }
            return null;
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public HashMap<String,DownloadingIndex> DownloadingIndexHashMap(){
        return downloadingIndexHashMap;
    }

    @Override
    public Boolean isCurrentChapter(long downloadId, int chapterId){
        return downloadingIndexHashMap.get(String.valueOf(downloadId)).getChapterId()==chapterId;
    }

    private long DownloadData (Uri uri, int bookId, int chapterId ) {

        long downloadReference;

        downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);

        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Tải Xuống");

        //Setting description of request
        request.setDescription("Sách Đang Tải Xuống");

        //Set the local destination for the downloaded file to a path within the application's external files directory
//          request.setDestinationInExternalFilesDir(MainActivityDownloadManager.this, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.mp3");
            request.setDestinationInExternalPublicDir(Utils.downloadDirectory+"/"+bookId,chapterId+".mp3");

        //Enqueue download and save the referenceId
        assert downloadManager != null;
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

/*    private void RemoveAllDownloading(){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById (DownloadManager.STATUS_FAILED|DownloadManager.STATUS_PENDING|DownloadManager.STATUS_RUNNING);
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        assert dm != null;
        Cursor cursor = dm.query(query);
        while(cursor.moveToNext()) {
            // Here you have all the downloades list which are running, failed, pending
            // and for abort your downloads you can call the `dm.remove(downloadsID)` to cancel and delete them from download manager.
            dm.remove(cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
            Toast.makeText(context, cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))+"\n", Toast.LENGTH_SHORT).show();
        }
    }*/

}

