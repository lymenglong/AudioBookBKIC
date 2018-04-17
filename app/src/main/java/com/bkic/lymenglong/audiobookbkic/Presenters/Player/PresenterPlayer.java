package com.bkic.lymenglong.audiobookbkic.Presenters.Player;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.bkic.lymenglong.audiobookbkic.R;
import com.bkic.lymenglong.audiobookbkic.Views.Player.PlayControl;

public class PresenterPlayer
        extends MediaPlayer
        implements PresenterPlayerImp{

    private PlayControl playControlActivity;
    private ProgressDialog progressDialog;
    private boolean initialStage = true;
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
                progressDialog = ProgressDialog.show(playControlActivity,null,playControlActivity.getString(R.string.buffering_data),true,false);
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
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            initialStage = true;
                            mediaPlayer.stop();
                            mediaPlayer.reset();
                        }
                    });

                    prepared = true;

                } catch (Exception e) {
                    prepared = false;
                    progressDialog.dismiss();
                }

                return prepared;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean&&initialStage) {
                    if (progressDialog.isShowing()) {
                        progressDialog.cancel();
                    }
                    PlayMedia();
                }
                initialStage = false;
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
        if(intCurrentPosition - seekBackwardTime >= 0){
            // forward song
            mediaPlayer.seekTo(intCurrentPosition - seekBackwardTime);
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
    public void PauseMedia() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
//            mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
        }
    }
    @Override
    public void PlayMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            if(mediaPlayer.isPlaying()) Log.d(TAG, "PlayMedia: "+ mediaPlayer.isPlaying());
                    mediaPlayer.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        Log.d(TAG, "onBufferingUpdate: percent = "+percent);
                        intSoundMax = mp.getDuration();
                    }
            });
            if(mediaPlayer.getCurrentPosition()< playControlActivity.PauseTime){
                mediaPlayer.seekTo(playControlActivity.PauseTime);
//                    mediaPlayer.start();
            } else{
                mediaPlayer.start();
//                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 1000);
            }
            if(mediaPlayer.isPlaying()){
                Toast.makeText(playControlActivity,"Đang chạy, vui lòng chờ!",Toast.LENGTH_SHORT).show();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        Toast.makeText(playControlActivity, "Đã chạy xong", Toast.LENGTH_SHORT).show();
//                            lastPlayDuration = 0;
//                            postHistoryDataToServer();
                    }
                });
            }
        }
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
}
