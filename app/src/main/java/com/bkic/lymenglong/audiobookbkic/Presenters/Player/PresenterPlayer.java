package com.bkic.lymenglong.audiobookbkic.Presenters.Player;

import com.bkic.lymenglong.audiobookbkic.Views.Player.PlayControl;

public class PresenterPlayer implements PresenterPlayerImp {
    private PlayControl playControlActivity;

    public PresenterPlayer(PlayControl playControlActivity) {
        this.playControlActivity = playControlActivity;
    }
}
