package com.kinstalk.m4.skillmusic.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.activity.M4BaseActivity;
import com.kinstalk.m4.publicmediaplayer.entity.MediaInfo;
import com.kinstalk.m4.publicmediaplayer.player.MediaPlayerProxy;
import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.R2;
import com.kinstalk.m4.skillmusic.model.entity.ChannelInfo;
import com.kinstalk.m4.skillmusic.model.entity.DissInfo;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.PresentationContext;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.ViewEnable;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;
import com.kinstalk.m4.skillmusic.ui.fragment.M4MusicPlayFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import kinstalk.com.qloveaicore.AICoreDef.AppState;
import kinstalk.com.qloveaicore.AICoreDef.QLServiceType;


public class M4MusicPlayActivity extends M4BaseActivity implements GestureDetector.OnGestureListener {
    private static final String TAG = M4MusicPlayActivity.class.getSimpleName();

    @BindView(R2.id.mainview)
    public View mMainView;

    private boolean mIsNotification = false;
    private boolean mIsAIStart = false;
    private M4MusicPlayFragment mHerMusicFragment;

    public GestureDetector mGestureDetector;

    public static void actionStart(Context context, boolean isNewTask) {
        Intent intent = new Intent(context, M4MusicPlayActivity.class);
        if (isNewTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(CommonConstant.INTENT_NOTIFICATION, false);
        intent.putExtra(CommonConstant.INTENT_AI_START, true);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, SongInfo songInfo) {
        Intent intent = new Intent(context, M4MusicPlayActivity.class);
        intent.putExtra(CommonConstant.INTENT_SONGINFO, songInfo);
        intent.putExtra(CommonConstant.INTENT_NOTIFICATION, true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void actionStart2(Context context, SongInfo songInfo) {
        Intent intent = new Intent(context, M4MusicPlayActivity.class);
        intent.putExtra(CommonConstant.INTENT_SONGINFO2, songInfo);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, ChannelInfo musicChannel) {
        Intent intent = new Intent(context, M4MusicPlayActivity.class);
        intent.putExtra(CommonConstant.INTENT_CHANNEL, musicChannel);
        context.startActivity(intent);
    }

    public static void actionStart(Context context, DissInfo dissInfo) {
        Intent intent = new Intent(context, M4MusicPlayActivity.class);
        intent.putExtra(CommonConstant.INTENT_DISSINFO, dissInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (QLog.isLoggable()) {
            Log.d(TAG, "QUI-KPI---onCreate");
        }
        PresentationContext.init(this);

        mIsNotification = getIntent().getBooleanExtra(CommonConstant.INTENT_NOTIFICATION, false);
        mIsAIStart = getIntent().getBooleanExtra(CommonConstant.INTENT_AI_START, false);

        setContentView(R.layout.activity_magellan_play_main);

        initViews();
        initData();
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(QLServiceType.TYPE_MUSIC, AppState.APP_STATE_ONCREATE);
    }

    private void initViews() {
        ButterKnife.bind(this);

        mGestureDetector = new GestureDetector(this, this);
        mGestureDetector.setIsLongpressEnabled(false);
    }


    @SuppressLint("WrongConstant")
    private void initData() {
        changeShowFragment();
    }

    private void changeShowFragment() {
        mHerMusicFragment = M4MusicPlayFragment.newInstance(mIsNotification, mIsAIStart);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_content, mHerMusicFragment, M4MusicPlayFragment.class.getSimpleName())
                .commitAllowingStateLoss();

        SuperPresenter.getInstance().updateMusicPlayViewP(mHerMusicFragment);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (null != mHerMusicFragment) {
            mHerMusicFragment.onNewIntent(intent);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mHerMusicFragment != null) {
            mHerMusicFragment.onTouchEventChanged(event);
        }
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(QLServiceType.TYPE_MUSIC, AppState.APP_STATE_ONRESUME);
        if (QLog.isLoggable()) {
            Log.d(TAG, "QUI-KPI---onResume");
        }
        QLog.d(this, "onResume: " + this);
//        WakeLockUtils.getInstance().requireScreenOn();

        SuperPresenter.getInstance().foreground(true);
        mHerMusicFragment.mResumeSong = false;

        {
            //在这里判断有没有可以播放音乐数据
            SongInfo songInfo = getIntent().getParcelableExtra(CommonConstant.INTENT_SONGINFO);
            QLog.d(this, "onResume songBase: " + songInfo);
            if (null != songInfo && !TextUtils.isEmpty(songInfo.getSongName())) {
                SongInfo songInfo2 = new SongInfo();
                getIntent().putExtra(CommonConstant.INTENT_SONGINFO, songInfo2);

                MusicState musicState = new MusicState();
                musicState.setPlayerState(PlayerState.MUSIC_STATE_ONRESUME);
                musicState.setSongInfo(songInfo);
                MusicPlayerController.getInstance().notifyMusicState(musicState);
            } else if (mIsNotification) {
                if (MediaPlayerProxy.init().isPlaying()) {
                    MediaInfo mediaInfo = MediaPlayerProxy.init().getPlayingInfo();
                    if (mediaInfo != null && mediaInfo.getMusicType() != MediaInfo.TYPE_MUSIC) {
                        SuperPresenter.getInstance().requestPlay(null, false);

                        songInfo = MusicPlayerController.getInstance().getCurSongInfo();
                        MusicState musicState = new MusicState();
                        musicState.setPlayerState(PlayerState.MUSIC_STATE_PLAYING);
                        musicState.setSongInfo(songInfo);
                        MusicPlayerController.getInstance().notifyMusicState(musicState);
                    } else {
                        MusicPlayerController.getInstance().notifyMusicState(
                                new MusicState(PlayerState.MUSIC_STATE_ONRESUME, MusicPlayerController.getInstance().getCurSongInfo()));
                        MusicPlayerController.getInstance().notifyMusicState(
                                new MusicState(PlayerState.MUSIC_STATE_LRCINFO, MusicPlayerController.getInstance().getCurSongInfo()));
                    }
                } else {
                    MusicPlayerController.getInstance().notifyMusicState(
                            new MusicState(PlayerState.MUSIC_STATE_ONPAUSE, MusicPlayerController.getInstance().getCurSongInfo()));
                }
            }
        }

        {
            final SongInfo songInfo = getIntent().getParcelableExtra(CommonConstant.INTENT_SONGINFO2);
            QLog.d(this, "onResume songInfo: " + songInfo);
            if (songInfo != null && !TextUtils.isEmpty(songInfo.getPlayId())) {
                SongInfo songInfo2 = new SongInfo();
                getIntent().putExtra(CommonConstant.INTENT_SONGINFO2, songInfo2);
                mHerMusicFragment.mResumeSong = true;
                if (!MusicPlayerController.getInstance().isPlaying()) {
//                    MusicPlayerController.getInstance().requestPlayWithId(songInfo.getPlayId());

                    SuperPresenter.getInstance().requestPlay(null, false);

                    MusicState musicState = new MusicState();
                    musicState.setPlayerState(PlayerState.MUSIC_STATE_PLAYING);
                    musicState.setSongInfo(songInfo);
                    MusicPlayerController.getInstance().notifyMusicState(musicState);
                } else {
//                    PlayReset.RequestValue playRest = new PlayReset.RequestValue();
//                    EventBus.getDefault().post(playRest);

//                    WakeLockUtils.getInstance().requireScreenOn();

                    ViewEnable.ViewEnableRequest enableRequest = new ViewEnable.ViewEnableRequest(true);
                    EventBus.getDefault().post(enableRequest);

                    MusicState musicState = new MusicState();
                    musicState.setSongInfo(songInfo);
                    musicState.setPlayerState(PlayerState.MUSIC_STATE_ONRESUME);
                    MusicPlayerController.getInstance().notifyMusicState(musicState);
                }
            }
        }

        {
            ChannelInfo musicChannel = getIntent().getParcelableExtra(CommonConstant.INTENT_CHANNEL);
            QLog.d(this, "onResume musicChannel: " + musicChannel);
            if (musicChannel != null && musicChannel.getChannelId() != 0) {
                ChannelInfo channelInfo = new ChannelInfo();
                getIntent().putExtra(CommonConstant.INTENT_CHANNEL, channelInfo);

                MusicPlayerController.getInstance().notifyMusicState(
                        new MusicState(PlayerState.MUSIC_STATE_ONLOADING));

                if (musicChannel.getChannelId() == CommonConstant.AUDIO_SONG_CHANNELID) {
                    SuperPresenter.getInstance().requestPlaySuperMusic(false);
                } else if (musicChannel.getChannelId() == CommonConstant.COLLECT_SONG_CHANNELID) {
                    SuperPresenter.getInstance().requestPlayCategory(musicChannel, true);
                }
            }
        }

        {
            DissInfo dissInfo = getIntent().getParcelableExtra(CommonConstant.INTENT_DISSINFO);
            QLog.d(this, "onResume dissInfo: " + dissInfo);
            if (dissInfo != null && dissInfo.getDissId() != 0) {
                DissInfo dissInfo2 = new DissInfo();
                getIntent().putExtra(CommonConstant.INTENT_DISSINFO, dissInfo2);

                MusicPlayerController.getInstance().notifyMusicState(
                        new MusicState(PlayerState.MUSIC_STATE_ONLOADING));

                SuperPresenter.getInstance().requestPlayDiss(dissInfo);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        QLog.d(this, "onPause: " + this);
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(QLServiceType.TYPE_MUSIC, AppState.APP_STATE_ONPAUSE);
        SuperPresenter.getInstance().foreground(false);
    }

    @Override
    protected void onDestroy() {
        QLog.d(this, "onDestroy: " + this);
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(QLServiceType.TYPE_MUSIC, AppState.APP_STATE_ONDESTROY);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        QLog.d(this, "onStart: " + this);
        SuperPresenter.getInstance().start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        QLog.d(this, "onStop: " + this);
        SuperPresenter.getInstance().stop();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        QLog.d(this, "onDown");

        return mHerMusicFragment == null ? false : mHerMusicFragment.onDown(e);
    }

    @Override
    public void onShowPress(MotionEvent e) {
        QLog.d(this, "onShowPress e:" + e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        QLog.d(this, "onSingleTapUp e:" + e);
        return mHerMusicFragment == null ? false : mHerMusicFragment.onSingleTapUp(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        QLog.d(this, "onScroll");

        return mHerMusicFragment == null ? false : mHerMusicFragment.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        QLog.d(this, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        QLog.d(this, "onFling");

        return mHerMusicFragment == null ? false : mHerMusicFragment.onFling(e1, e2, velocityX, velocityY);
    }
}
