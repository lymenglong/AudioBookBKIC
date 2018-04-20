package com.bkic.lymenglong.audiobookbkic.Presenters.Player;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.Presenters.Review.PresenterReview;
import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Player.PlayControl;

import java.text.SimpleDateFormat;

public class PresenterPlayer
        extends MediaPlayer
        implements PresenterPlayerImp, SeekBar.OnSeekBarChangeListener {

    private PlayControl playControlActivity;
    private ProgressDialog progressDialog;
    private static String TAG = "PresenterPlayer";
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private int intSoundMax;

    public PresenterPlayer(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }

    @Override
    public void PrepareMediaPlayer(final String httpUrlMedia) {
        @SuppressLint("StaticFieldLeak")
        class PrepareMediaPlayerClass extends AsyncTask<String, Void, Boolean> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(playControlActivity,null,playControlActivity.getString(R.string.buffering_data),true,true);
                progressDialog.show();
            }
            @Override
            protected Boolean doInBackground(String... strings) {
                Boolean prepared;
                try {
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(playControlActivity, Uri.parse(strings[0]));
                    mediaPlayer.prepare();
                    prepared = true;
                    /*final String path = strings[0];
                    new AudioStreamWorkerTask(playControlActivity, new OnCacheCallBack() {

                        @Override
                        public void onSuccess(FileInputStream fileInputStream) {
                            Log.i(getClass().getSimpleName() + ".MediaPlayer", "now playing...");
                            if (fileInputStream != null) {
                                // reset media player here if necessary
                   *//* mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(fileInputStream.getFD());
                        mediaPlayer.prepare();
                        mediaPlayer.setVolume(1f, 1f);
                        mediaPlayer.setLooping(false);
                        mediaPlayer.start();
                        fileInputStream.close();
                    } catch (IOException | IllegalStateException e) {
                        e.printStackTrace();
                    }*//*
                            } else {
                                Log.e(getClass().getSimpleName() + ".MediaPlayer", "fileDescriptor is not valid");
                            }
                        }

                        @Override
                        public void onError() {
                            Log.e(getClass().getSimpleName() + ".MediaPlayer", "Can't play audio file");
                        }
                    }).execute(path);*/

                } catch (Exception e) {
                    prepared = false;
                    progressDialog.dismiss();
                }

                return prepared;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        playControlActivity.finish();
                    }
                });
                if (aBoolean) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    PlayMedia();
                }
//                initialStage = false;
            }
        }
        PrepareMediaPlayerClass prepareMediaPlayerClass = new PrepareMediaPlayerClass();
        prepareMediaPlayerClass.execute(httpUrlMedia);
    }
    @Override
    public void ReplayMedia() { // nghe lại từ đầu
        mediaPlayer.pause();
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }
    @Override
    public void RewindMedia() {
        int intCurrentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        int seekBackwardTime = 10000; //10sec
        int targetPosition = intCurrentPosition - seekBackwardTime;
        if(targetPosition >= 0){
            // forward song
            mediaPlayer.seekTo(targetPosition);
            Log.d(TAG, "RewindMedia: "+targetPosition);
        }else{
            // backward to starting position
            mediaPlayer.seekTo(0);
        }
    }
    @Override
    public void ForwardMedia() {
        int intCurrentPosition = mediaPlayer.getCurrentPosition();
        int seekForwardTime = 10000;//10sec
        int targetPosition = intCurrentPosition + seekForwardTime;
        if(targetPosition < intSoundMax){
            // forward song
            mediaPlayer.seekTo(targetPosition);
            Log.d(TAG, "forwardMedia: "+ targetPosition);
        }else{
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }
    @Override
    public void PreviousMedia() {
    }
    @Override
    public void NextMedia() {

    }

    @Override
    public void StopMedia() {
        playControlActivity.getSeekBar().setProgress(0);
        mUpdateHandler.removeCallbacks(mUpdate);
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    @Override
    public void PauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    @Override
    public void PlayMedia() {
        intSoundMax = mediaPlayer.getDuration();
        playControlActivity.getSeekBar().setMax(intSoundMax);
        mUpdateHandler.postDelayed(mUpdate,100);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    playControlActivity.getSeekBar().setSecondaryProgress(mediaPlayer.getDuration() * percent/100);
                    Log.d(TAG, "onBufferingUpdate: percent = "+percent);
                }
            });
            mediaPlayer.start();
            if(mediaPlayer.getCurrentPosition()< playControlActivity.PauseTime){
                mediaPlayer.seekTo(playControlActivity.PauseTime);
            }
            if(mediaPlayer.isPlaying()){
//                Toast.makeText(playControlActivity,"Đang chạy, vui lòng chờ!",Toast.LENGTH_SHORT).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        String message = "Đã chạy xong chương này";
                        Toast.makeText(playControlActivity, message, Toast.LENGTH_SHORT).show();
                        PresenterReview presenterReview = new PresenterReview(playControlActivity);
                        presenterReview.ReviewBookDialog();
                    }
                });
            }
        }
    }

    //region Method to update time
    private Handler mUpdateHandler = new Handler();
    private Runnable mUpdate= new Runnable() {
        @Override
        public void run() {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
            playControlActivity.getTxtCurrentDuration().setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
            playControlActivity.getTxtSongTotal().setText(simpleDateFormat.format(mediaPlayer.getDuration()));
            playControlActivity.getSeekBar().setProgress(mediaPlayer.getCurrentPosition());
            mUpdateHandler.postDelayed(this, 100);
        }
    };
    //endregion

    @Override
    public void RemoveCallBacksUpdateHandler (){
        mUpdateHandler.removeCallbacks(mUpdate);
    }


    @Override
    public int GetLastMediaData(){
        int lastPlayDuration;
        if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()){
            lastPlayDuration = 0;
        }else {
            lastPlayDuration = mediaPlayer.getCurrentPosition();
        }
        mediaPlayer.release();
        return lastPlayDuration;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
    }
}
