package com.kinstalk.m4.skillmusic.ui.fragment;

import com.kinstalk.m4.skillmusic.model.entity.ChannelInfo;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.ui.BasePresenter;

/**
 * Created by jinkailong on 2017/5/17.
 */

public interface ISuperPresenter extends BasePresenter {
    IControlPanelPresenter getControlPanelPresenter();

    ICategoryListPresenter getCategoryListPresenter();

    void updateMusicPlayViewP(final IControlPanel cp);

    void updateCategoryListP(final ICategoryList cl);

    MusicState getMusicState();

    SongInfo getCurrentSelectedSongInfo();

    void requestPlay(SongInfo songInfo, boolean isNew);

    void requestPlayListWithId(DissInfo dissInfo, int playIndex);

    void requestPause(boolean abandonFocus);

    void requestPlayBefore(boolean fromUser);

    void requestPlayNext(boolean fromUser);

    void requestChangePlayMode(final int playMode);

    void requestCollect(final SongInfo songInfo, final boolean collect);

    void requestPlaySuperMusic(boolean hideSuperMusic);

    void requestPlayCategory(final ChannelInfo channel, boolean isUI);

    void requestPlayDiss(final DissInfo dissInfo);
}