package com.kinstalk.m4.skillmusic.model.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;

public class PlaySuperMusicReceiver extends BroadcastReceiver {

    public PlaySuperMusicReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        QLog.d("PlaySuperMusicReceiver", "MusicAI onReceive: action " + intent.getAction());

        if (TextUtils.equals(intent.getAction(), "play_super_music")) {
            if (MusicPlayerController.getInstance().isPlaying()) {
                SuperPresenter.getInstance().requestPause(true);
            } else {
                SuperPresenter.getInstance().requestPlaySuperMusic(true);
            }
        }
    }
}
