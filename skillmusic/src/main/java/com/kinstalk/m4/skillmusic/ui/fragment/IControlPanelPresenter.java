package com.kinstalk.m4.skillmusic.ui.fragment;


import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.ui.BasePresenter;

import java.util.ArrayList;

/**
 * Created by jinkailong on 2017/5/17.
 */

public interface IControlPanelPresenter extends BasePresenter {
    void onPlayPauseClicked();

    void onStarClicked();

    void onBeforeClicked();

    void onNextClicked();

    void onPlayModeClick();

    void onPlayPositionChanged(int position);

    void initPlayMode(int playMode, int needToast);

    void onPlayModeChange(int playMode, int needToast);

    void restoreState(IControlPanel cp);

    IControlPanel getCurrentControlPanel();

    void setCurrentControlPanel(IControlPanel cp);

    boolean isPlaying();

    void notifyForPowerSave(IControlPanel cp);

    void notifyFocused(IControlPanel cp);

    void onMusicStateChanged(MusicState state);

    void onSongInfoChanged(SongInfo songInfo);

    void onSongFavoriteChanged(SongInfo songInfo);

    void onMusicInfoReset();

    void onNotifyNoCollect();

    void viewEnable(boolean enable);

    MusicState getMusicState();

    SongInfo getSongInfo();

    int getPlayMode();

    int getPlayPosition();

    void setMusicState(MusicState musicState);

    void setSongInfo(SongInfo songInfo);

    void updateSongList(ArrayList<SongInfo> songList, boolean isMore);

    ArrayList<SongInfo> getSongInfos();

    boolean isSongListMore();

    void updateUserVipInfo(UserVipInfo vipInfo);

    void bindStatusChanged(boolean status);
}
