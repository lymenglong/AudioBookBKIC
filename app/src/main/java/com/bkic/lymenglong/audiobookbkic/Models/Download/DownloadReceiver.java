package com.bkic.lymenglong.audiobookbkic.Models.Download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Gravity;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Models.Database.DBHelper;
import com.bkic.lymenglong.audiobookbkic.Models.Utils.Const;
import com.bkic.lymenglong.audiobookbkic.Presenters.Download.PresenterDownloadTaskManager;
import com.bkic.lymenglong.audiobookbkic.R;

public class DownloadReceiver
        extends BroadcastReceiver{

    public static DownloadReceiverListener downloadReceiverListener;
    private Context context;
    private PresenterDownloadTaskManager presenterDownloadTaskManager = new PresenterDownloadTaskManager();

    public DownloadReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        //check if the broadcast message is for our Enqueued download
        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        String downloadId = String.valueOf(referenceId);
        //Update data
        UpdateChapterTable(downloadId);
        UpdateBookTable(downloadId);
        //Toast Message
        String message =
                ChapterDownloadedTitle(downloadId)+" "+
                BookDownloadedTitle(downloadId)+" "+
                context.getString(R.string.message_download_complete);
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 25, 400);
        toast.show();

        if (downloadReceiverListener != null) {
            downloadReceiverListener.onDownloadCompleted(referenceId);
        }
    }

    public interface DownloadReceiverListener {
        void onDownloadCompleted(long downloadId);
    }

    private void UpdateBookTable(String downloadId) {
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String UPDATE_STATUS =
                "UPDATE " +
                        "book " +
                "SET " +
                        "BookStatus = '1' " +
                "WHERE " +
                        "BookId = '"+presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getBookId()+"'" +
                        ";"
                ;
        dbHelper.QueryData(UPDATE_STATUS);
        dbHelper.close();
    }

    private void UpdateChapterTable(String downloadId) {
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String UPDATE_STATUS =
                "UPDATE " +
                        "chapter " +
                "SET " +
                        "ChapterStatus = '1' " +
                "WHERE " +
                        "BookId = '"+presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getBookId()+"' " +
                        "AND " +
                        "ChapterId = '"+presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getChapterId()+"'"
                ;
        dbHelper.QueryData(UPDATE_STATUS);
        dbHelper.close();
    }

    private String BookDownloadedTitle (String downloadId){
        String bookTitle = null;
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String SELECT =
                "SELECT BookTitle " +
                "From book " +
                "WHERE BookId = '"+presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getBookId()+"'" +
                ";";
        Cursor cursor = dbHelper.GetData(SELECT);
        if(cursor.moveToFirst()) bookTitle = cursor.getString(0);
        return bookTitle;
    }

    private String ChapterDownloadedTitle (String downloadId){
        String bookTitle = null;
        DBHelper dbHelper = new DBHelper(context, Const.DB_NAME,null, Const.DB_VERSION);
        String SELECT =
                "SELECT ChapterTitle " +
                "From chapter " +
                "WHERE " +
                        "BookId = '"+presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getBookId()+"' " +
                        "AND " +
                        "ChapterId = '"+presenterDownloadTaskManager.DownloadingIndexHashMap().get(downloadId).getChapterId()+"'"+
                ";";
        Cursor cursor = dbHelper.GetData(SELECT);
        if(cursor.moveToFirst()) bookTitle = cursor.getString(0);
        return bookTitle;
    }
}