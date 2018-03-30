package com.bkic.lymenglong.audiobookbkic.Presenters.Player;


public interface PresenterPlayerImp {

    void PrepareMediaPlayer(String httpUrlMedia);

    void ReplayMedia();

    void RewindMedia();

    void ForwardMedia();

    void PreviousMedia();

    void NextMedia();

    void PauseMedia();

    void PlayMedia();

    void GetLastMediaData();
}
