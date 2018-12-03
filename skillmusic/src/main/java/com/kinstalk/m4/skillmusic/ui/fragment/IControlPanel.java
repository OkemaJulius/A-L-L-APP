package com.kinstalk.m4.skillmusic.ui.fragment;

import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by jinkailong on 2017/5/17.
 */

public interface IControlPanel extends BaseView<IControlPanelPresenter> {

    void bindView();

    void setPlayPause(boolean play);

    void setStarred(boolean star);

    void startLoading(boolean loading);

    void changPlayMode(int playMode, boolean needToast);

    void updateSongInfo(SongInfo songInfo);

    void updateSongList(SongInfo songInfo);

    void onMusicStateChanged(MusicState state);

    void onPlayPositionChanged(int position);

    void onMusicInfoReset();

    void onNotifyNoCollect();

    void notifyForPowerSave();

    void notifyFocused();

    void viewEnable(boolean enable);

    void updateSongList(ArrayList<SongInfo> songList, boolean isMore);

    void updateUserVipInfo(UserVipInfo vipInfo);

    void bindStatusChanged(boolean status);
}
