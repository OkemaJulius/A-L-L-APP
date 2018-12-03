package com.kinstalk.m4.skillmusic.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.activity.M4BaseActivity;
import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.R2;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesConstant;
import com.kinstalk.m4.skillmusic.model.cache.SharedPreferencesHelper;
import com.kinstalk.m4.skillmusic.model.entity.MusicState;
import com.kinstalk.m4.skillmusic.model.entity.MusicState.PlayerState;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.ui.constant.CommonConstant;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;
import com.kinstalk.m4.skillmusic.ui.utils.RCaster;
import com.kinstalk.m4.skillmusic.ui.utils.ToastManagerUtils;
import com.kinstalk.m4.skillmusic.ui.view.SongListPopWindow;
import com.kinstalk.m4.skillmusic.ui.view.mylrcview.LrcView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ly.count.android.sdk.Countly;

public class M4MusicPlayFragment extends BaseFragment implements IControlPanel {
    private boolean mIsNotification = false;
    private boolean mIsAIStart = false;
    public boolean mResumeSong = false;

    @BindView(R2.id.musicmain_back)
    public View mBackView;
    @BindView(R2.id.cp_star)
    public ImageView mStarButton;
//    @BindView(R2.id.musicmain_user_vipstatus_view)
//    public TextView mUserVipFlagView;

    @BindView(R2.id.musicinfo_cover)
    public ImageView mCoverView;
    @BindView(R2.id.musicinfo_name)
    public TextView mNameView;
    @BindView(R2.id.musicinfo_singer)
    public TextView mSingerView;

    @BindView(R2.id.music_lrcview)
    public LrcView mLrcView;

    @BindView(R2.id.musicplay_scrolltime)
    public TextView mScrollTime;


    @BindView(R2.id.control_panel_layout)
    public View mControlView;
    @BindView(R2.id.cp_before)
    public ImageView mBeforeButton;
    @BindView(R2.id.cp_play)
    public ImageView mPlayButton;
    @BindView(R2.id.cp_next)
    public ImageView mNextButton;
    @BindView(R2.id.cp_recycle)
    public ImageView mRecycleButton;


    @BindView(R2.id.video_loadingview)
    public ProgressBar mLoadingView;

    @BindView(R2.id.current_time)
    public TextView mCurrentTimeView;
    @BindView(R2.id.video_seekbar)
    public SeekBar mMusicSeekBar;
    @BindView(R2.id.total_time)
    public TextView mTotalTimeView;

    private IControlPanelPresenter mPresenter;

    //    private BuyVipPopWindow mBuyVipPopWindow;
    private SongListPopWindow mSongListPopWindow;

    private MusicState mMusicState;
    private SongInfo mSongInfo;
    private UserVipInfo mVipInfo;
    private boolean mViewEnable = true;

    private int mProgress;
    private boolean mSeekBarFromUser;
    private int mMusicDuration = 1;

    private static final int TIME_UNIT = 1000;
    private static final int VIEW_TIMEOUT = 5 * 1100;

    private Unbinder mUnbinder;

    private static final int WHAT_LRC_TIMEOUT = 1;
    private final int WHAT_ONRESUME = 3;
    private final int WHAT_GET_VIPINFO = 4;

    private static final int LRC_VIEW_TIMEOUT = 5 * 1000;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QLog.d(TAG, "mHandler msg.what:" + msg.what);
            switch (msg.what) {
                case WHAT_LRC_TIMEOUT:
                    if (null != mLrcView) {
                        mLrcView.setSongInfo(null);
                        mLrcView.loadLrc(null, 0);
                    }
                    break;
                case WHAT_ONRESUME:
                    MusicState state = mPresenter.getMusicState();
                    SongInfo songInfo = mPresenter.getSongInfo();
                    QLog.d(TAG, "onResume state:" + state);
                    QLog.d(TAG, "onResume songInfo:" + songInfo);

                    if (state != null) {
                        musicStatusChangePrepared();
                        onMusicStateChanged(state);
                    }

                    if (songInfo != null) {
                        updateSongInfo(songInfo);
                    }
                    break;
                case WHAT_GET_VIPINFO:
                    QAIMusicConvertor.getInstance().getMusicVipInfo();
                    break;
            }
        }
    };

    public M4MusicPlayFragment() {
    }

    public static M4MusicPlayFragment newInstance(boolean isNotification, boolean isAIStart) {
        M4MusicPlayFragment fragment = new M4MusicPlayFragment();
        Bundle argument = new Bundle();
        argument.putBoolean(CommonConstant.INTENT_CONTENT, isNotification);
        argument.putBoolean(CommonConstant.INTENT_AI_START, isAIStart);
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsNotification = getArguments().getBoolean(CommonConstant.INTENT_CONTENT);
        mIsAIStart = getArguments().getBoolean(CommonConstant.INTENT_AI_START);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m4_musicplay, container, false);
        initViews(rootView);
        return rootView;
    }

    private void initViews(View rootView) {
        mUnbinder = ButterKnife.bind(this, rootView);

        setPresenter(SuperPresenter.getInstance().getControlPanelPresenter());

        mMusicSeekBar.setOnSeekBarChangeListener(mSeekListener);

        if (null != SuperPresenter.getInstance().mVipInfo) {
            updateUserVipInfo(SuperPresenter.getInstance().mVipInfo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        toDealWithOnResume();
    }

    public void onNewIntent(Intent intent) {

        mIsNotification = intent.getBooleanExtra(CommonConstant.INTENT_NOTIFICATION, false);
        mIsAIStart = intent.getBooleanExtra(CommonConstant.INTENT_AI_START, false);
    }

    private void toDealWithOnResume() {
        int playMode = SharedPreferencesHelper.getInstance().getInt(SharedPreferencesConstant.PLAT_MODE_INDEX, CommonConstant.PLAYMODE_LOOP);
        mPresenter.onPlayModeChange(playMode, 0);

//        mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
//        mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));

        QLog.d(TAG, "onResume mIsNotification:" + mIsNotification + ",mIsAIStart:" + mIsAIStart);
        if (mIsAIStart) {
            mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
            mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
            mMusicSeekBar.setProgress(0);
            mMusicSeekBar.setMax(0);
            mLrcView.setLoading(true);
        } else {
            refreshProgress(0);
        }

        if (mIsNotification || mIsAIStart) {
            if (null != mSongListPopWindow && mSongListPopWindow.isShowing()) {
                mSongListPopWindow.dismiss();
                mSongListPopWindow = null;
            }
        }

        if (mIsNotification || mResumeSong) {
            if (!mIsAIStart) {
                refreshProgress(0);
                musicStatusChangeOnResume();
            }

            if (mResumeSong) {
                mPresenter.setMusicState(new MusicState(PlayerState.MUSIC_STATE_PLAYING));
            }
            mPresenter.notifyFocused(this);

            sendEmptyMessageDelayed(WHAT_ONRESUME, 100);
        }

        if (null == SuperPresenter.getInstance().mVipInfo) {
            sendEmptyMessageDelayed(WHAT_GET_VIPINFO, 100);
        }

        mIsNotification = false;
        mIsAIStart = false;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
//        if (mBuyVipPopWindow != null && mBuyVipPopWindow.isShowing()) {
//            mBuyVipPopWindow.dismiss();
//            mBuyVipPopWindow = null;
//        }
        if (null != mSongListPopWindow && mSongListPopWindow.isShowing()) {
            mSongListPopWindow.dismiss();
            mSongListPopWindow = null;
        }
        if (null != mUnbinder) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void bindView() {

    }

    @Override
    public void setPlayPause(boolean play) {
        QLog.d(TAG, "setPlayPause play:" + play);
        if (null != mPlayButton) {
            mPlayButton.setImageResource(play ? R.drawable.btn_music_bar_btn_stop :
                    R.drawable.btn_music_bar_btn_play);
        }
    }

    @Override
    public void setStarred(boolean star) {
        QLog.d(TAG, "setStarred star:" + star);
        if (null != mStarButton) {
           mStarButton.setImageResource(star ? R.mipmap.btn_stared :
                   R.mipmap.btn_unstar);
        }
        if (star) {//收藏
            Countly.sharedInstance().recordEvent("music", "v_music_like");
        } else {//取消收藏
            Countly.sharedInstance().recordEvent("music", "v_music_unlike");
        }
    }

    @Override
    public void startLoading(boolean loading) {
        QLog.d(TAG, "startLoading loading:" + loading);
        if (null != mLoadingView) {
            if (loading) {
                mMusicSeekBar.setEnabled(false);
                mPlayButton.setVisibility(View.INVISIBLE);
                mLoadingView.setVisibility(View.VISIBLE);
            } else {
                mMusicSeekBar.setEnabled(true);
                mPlayButton.setVisibility(View.VISIBLE);
                mLoadingView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void changPlayMode(int playMode, boolean needToast) {
        QLog.d(TAG, "changPlayMode playMode:" + playMode);
        if (null != mRecycleButton) {
            switch (playMode) {
                case CommonConstant.PLAYMODE_ORDER:
                case CommonConstant.PLAYMODE_LOOP:
                    mRecycleButton.setImageResource(R.drawable.btn_play_bar_btn_loop);
                    if (needToast) {
//                    ToastManagerUtils.showToastForce(Utils.getString(R.string.change_platmode_order));
                    }
                    break;
                case CommonConstant.PLAYMODE_SINGLE_LOOP:
                    mRecycleButton.setImageResource(R.drawable.btn_play_bar_btn_single);
                    if (needToast) {
//                    ToastManagerUtils.showToastForce(Utils.getString(R.string.change_platmode_single_top));
                    }
                    break;
                case CommonConstant.PLAYMODE_RANDOM:
                    mRecycleButton.setImageResource(R.drawable.btn_play_bar_btn_random);
                    if (needToast) {
//                    ToastManagerUtils.showToastForce(Utils.getString(R.string.change_platmode_random));
                    }
                    break;
            }
        }
    }

    @Override
    public void updateSongInfo(SongInfo songInfo) {
        QLog.d(TAG, "onSongInfoChanged mSongInfo:" + mSongInfo);

        if (null == mBackView) {
            QLog.d(TAG, "onSongInfoChanged null == mBackView");
            return;
        }

        setStarred(songInfo == null ? false : songInfo.getIsFavorite() == 1);

        updateSongInfoView(songInfo);

        if (null != mSongListPopWindow && mSongListPopWindow.isShowing()) {
            mSongListPopWindow.updateSongInfo(songInfo, mPresenter.getSongInfos(), false);
        }

        if (null != songInfo) {
            mSongInfo = songInfo.clone();
        } else {
            mSongInfo = null;
        }
    }

    public void updateSongInfoView(SongInfo songInfo) {
        try {
            QLog.d(TAG, "onSongInfoChanged songInfo2:" + songInfo);

            if (mSongInfo != null && songInfo != null && mSongInfo.equals(songInfo)) {
                QLog.d(TAG, "onSongInfoChanged same info! not refresh!");
                return;
            }
            if (null == mCoverView) {
                QLog.d(TAG, "onSongInfoChanged ui error!");
                return;
            }
            if (null != songInfo) {
                Glide.with(CoreApplication.getApplicationInstance())
                        .load(songInfo.getAlbumPicDir())
                        .placeholder(R.mipmap.music_morenbeijingtu)
                        .error(R.mipmap.music_morenbeijingtu)
                        .skipMemoryCache(false)
//                        .bitmapTransform(new BlurTransformation(CoreApplication.getApplicationInstance(), 5))
                        .into(mCoverView);

                if (!TextUtils.isEmpty(songInfo.getSongName())) {
                    mNameView.setText(songInfo.getSongName());
                }

                if (!TextUtils.isEmpty(songInfo.getSingerName())) {
                    mSingerView.setText(songInfo.getSingerName());
                }

            } else {
                mCoverView.setImageResource(R.mipmap.music_morenbeijingtu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateSongList(SongInfo songInfo) {
        if (null != mSongListPopWindow && mSongListPopWindow.isShowing()) {
            mSongListPopWindow.updateSongInfo(songInfo, mPresenter.getSongInfos(), false);
        }
    }

    @Override
    public void onMusicStateChanged(MusicState state) {
        mMusicState = state;
        QLog.d(TAG, "onMusicStateChanged mMusicState:" + mMusicState);
        QLog.d(TAG, "onMusicStateChanged mSongInfo:" + mSongInfo);

        if (null == mMusicSeekBar) {
            QLog.d(TAG, "onMusicStateChanged null == mMusicSeekBar");
            return;
        }

        if (state.getPlayerState() == PlayerState.MUSIC_STATE_ONPREPARED) {
            QLog.d(TAG, "autoBackLauncher MUSIC_STATE_ONPREPARED");
            Utils.autoBackLauncher(mActivity, false);
            musicStatusChangePrepared();
        } else if (state.getPlayerState() == PlayerState.MUSIC_STATE_ONPAUSE) {
            QLog.d(TAG, "autoBackLauncher MUSIC_STATE_ONPAUSE");
            Utils.autoBackLauncher(mActivity, true);
            musicStatusChangeStop();
            MusicPlayerController.getInstance().removeLauncherMusicWidget();
        } else if (state.getPlayerState() == PlayerState.MUSIC_STATE_ONRESUME) {
            QLog.d(TAG, "autoBackLauncher MUSIC_STATE_ONRESUME");
            Utils.autoBackLauncher(mActivity, false);
            musicStatusChangeOnResume();
        } else if (state.getPlayerState() == PlayerState.MUSIC_STATE_ONLOADING) {
            Utils.autoBackLauncher(mActivity, false);
            musicStatusChangeLoading();
        } else if (state.getPlayerState() == PlayerState.MUSIC_STATE_LRCINFO) {
            QLog.d(TAG, "autoBackLauncher MUSIC_STATE_LRCINFO");
            Utils.autoBackLauncher(mActivity, false);
            musicStatusChangeLrcInfo();
        } else if (state.getPlayerState() == PlayerState.MUSIC_STATE_PLAYING) {
            QLog.d(TAG, "autoBackLauncher MUSIC_STATE_PLAYING");
            Utils.autoBackLauncher(mActivity, false);
            musicStatusChangePlaying();
        } else if (state.getPlayerState() == PlayerState.MUSIC_STATE_ONINIT) {
            QLog.d(TAG, "autoBackLauncher MUSIC_STATE_ONINIT");
            Utils.autoBackLauncher(mActivity, false);
            musicStatusChangeOninit();
        } else if (state.getPlayerState() == PlayerState.MUSIC_STATE_ONCOMPLETION) {
            musicStatusChangeOnCompletion();
        }
    }

    @Override
    public void onPlayPositionChanged(int position) {
        refreshProgress(position);
    }

    private void musicStatusChangePrepared() {
        QLog.d(TAG, "musicStatusChangePrepared");

//        mMusicDuration = -1;
//        mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(getDuration()));
//        mMusicSeekBar.setProgress(0);
//        mMusicSeekBar.setMax(getDuration());
//        mMusicSeekBar.setEnabled(true);
    }

    private void musicStatusChangeStop() {
        QLog.d(TAG, "musicStatusChangeStop");
        mMusicSeekBar.setEnabled(true);
    }

    private void musicStatusChangeOnResume() {
        QLog.d(TAG, "musicStatusChangeOnResume");
        mMusicSeekBar.setEnabled(true);

        if (null != mSongInfo) {
            updateLrcSongInfo(mSongInfo, mMusicSeekBar.getProgress());
        }
    }

    private void musicStatusChangeLoading() {
        QLog.d(TAG, "musicStatusChangeLoading");
        mMusicSeekBar.setEnabled(false);
        mLrcView.setLoading(true);

        mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
        mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
        mMusicSeekBar.setProgress(0);
    }

    private void musicStatusChangeLrcInfo() {
        QLog.d(TAG, "musicStatusChangeLrcInfo");

        if (null != mSongInfo) {
            updateLrcSongInfo(mSongInfo, mMusicSeekBar.getProgress());
        }
    }

    private void musicStatusChangePlaying() {
        QLog.d(TAG, "musicStatusChangePlaying");

        if (null != mSongListPopWindow && mSongListPopWindow.isShowing()) {
            mSongListPopWindow.updateSongInfo(mPresenter.getSongInfo(), mPresenter.getSongInfos(), false);
        }

        mMusicDuration = -1;
        mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(getDuration()));
//        mMusicSeekBar.setProgress(0);
        mMusicSeekBar.setMax(getDuration());
        mMusicSeekBar.setEnabled(true);
    }

    private void musicStatusChangeOninit() {
        QLog.d(TAG, "musicStatusChangeOninit");
        mMusicSeekBar.setProgress(0);
        mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
        mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
    }

    private void musicStatusChangeOnCompletion() {
        QLog.d(TAG, "musicStatusChangeOnCompletion");
        if (Utils.checkNetworkAvailable()) {
            mMusicSeekBar.setProgress(0);
            mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
            mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
        } else {
            ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
        }

    }

    public void updateLrcSongInfo(SongInfo songInfo, long time) {
        QLog.d(TAG, "updateSongInfo " + songInfo);
        if (songInfo != null && mLrcView != null) {
            mHandler.removeMessages(WHAT_LRC_TIMEOUT);
            SongInfo cacheInfo = mLrcView.getSongInfo();

//            if (cacheInfo != null && songInfo != null && cacheInfo.equals(songInfo)) {
//                QLog.d(TAG, "updateSongInfo same");
//                mLrcView.postInvalidate();
//            } else {
            mLrcView.setSongInfo(songInfo.clone());
            mLrcView.loadLrc(songInfo.getLyric(), time);
//            }
        }
    }

    private void removeMessages(int what) {
        QLog.d(TAG, "mHandler.removeMessages what:" + what);
        mHandler.removeMessages(what);
    }

    private void sendEmptyMessage(int what) {
        QLog.d(TAG, "mHandler.sendEmptyMessage what:" + what);
        mHandler.sendEmptyMessage(what);
    }

    private void sendEmptyMessageDelayed(int what, long time) {
        QLog.d(TAG, "mHandler.sendEmptyMessageDelayed what:" + what);
        mHandler.sendEmptyMessageDelayed(what, time);
    }

    @Override
    public void onMusicInfoReset() {
        QLog.d(TAG, "onMusicInfoReset");

        if (null == mMusicSeekBar) {
            QLog.d(TAG, "onMusicInfoReset null == mMusicSeekBar");
            return;
        }

        mPresenter.setSongInfo(null);
        mPresenter.setMusicState(null);
        mMusicDuration = -1;
        mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
        mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
        mMusicSeekBar.setProgress(0);
        mMusicSeekBar.setEnabled(true);

        onInfoMusicInfoReset();
        onLrcViewLoading();
    }

    public void onInfoMusicInfoReset() {
        try {
            mProgress = 0;
            mMusicSeekBar.setProgress(0);
            mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
            mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
            mMusicSeekBar.setMax(0);
            seekLrcToTime(mProgress, false);

            mCoverView.setImageResource(android.R.color.transparent);
            mNameView.setText("");
            mSingerView.setText("");
            mSongInfo = null;
        } catch (Exception e) {
        }
    }

    public void onLrcViewLoading() {
        QLog.d(TAG, "onMusicInfoReset mLrcView:" + mLrcView);
        if (null != mLrcView) {
            mLrcView.setLoading(true);
        }
        mSongInfo = null;
        mHandler.removeMessages(WHAT_LRC_TIMEOUT);
        mHandler.sendEmptyMessageDelayed(WHAT_LRC_TIMEOUT, LRC_VIEW_TIMEOUT);
    }

    @Override
    public void onNotifyNoCollect() {
//        ToastManagerUtils.showNoFavoriteToast();
//        mActivity.finish();
    }

    @Override
    public void notifyForPowerSave() {
        mPresenter.notifyForPowerSave(this);
    }

    @Override
    public void notifyFocused() {
        mPresenter.notifyFocused(this);
    }

    @Override
    public void viewEnable(boolean enable) {
        mViewEnable = enable;
    }

    @Override
    public void updateSongList(ArrayList<SongInfo> songList, boolean isMore) {
        if (null != mSongListPopWindow) {
            mSongListPopWindow.updateSongInfo(null, mPresenter.getSongInfos(), isMore);
        }
    }

    @Override
    public void updateUserVipInfo(UserVipInfo vipInfo) {
        QLog.d(TAG, "updateUserVipInfo vipInfo:" + vipInfo);
        mVipInfo = vipInfo;
        if (null == mBackView) {
            QLog.d(TAG, "updateUserVipInfo ui error!");
            return;
        }
        if (vipInfo != null) {
//            mUserVipFlagView.setVisibility(View.VISIBLE);

            if (vipInfo.getVip_flag() == 1) {
//                String timeString = Utils.getUserVipFlagTime(vipInfo.getEndTime());
//                mUserVipFlagView.setText(timeString + "到期");
//                mUserVipFlagView.setTextColor(Color.WHITE);

                mStarButton.setVisibility(View.VISIBLE);
            } else {
//                mUserVipFlagView.setText("免费开通会员");
//                mUserVipFlagView.setTextColor(Color.WHITE);

                mStarButton.setVisibility(View.VISIBLE);

                boolean needShow = SharedPreferencesHelper.getInstance().getBoolean(SharedPreferencesConstant.NEED_AUTO_SHOW_BUYVIP, true);
                QLog.d(TAG, "updateUserVipInfo needShow:" + needShow);
//                needShow = true;
                if (needShow) {
//                    if (mBuyVipPopWindow != null && mBuyVipPopWindow.isShowing()) {
//                        mBuyVipPopWindow.dismiss();
//                        mBuyVipPopWindow = null;
//                    }
//                    mBuyVipPopWindow = new BuyVipPopWindow(getActivity(), R.style.Dialog_FS);
//                    mBuyVipPopWindow.show();
                }
            }
        } else {
            mStarButton.setVisibility(View.VISIBLE);
        }

        if (null != mSongListPopWindow && mSongListPopWindow.isShowing()) {
            mSongListPopWindow.setVipInfo(mVipInfo);
        }
    }

    @Override
    public void bindStatusChanged(boolean status) {
        QLog.d(TAG, "bindStatusChanged status:" + status);
        if (!status && null != mActivity) {
            MusicPlayerController.getInstance().setCurDissInfo(null);
            mActivity.finish();
        }
    }

    @Override
    public void setPresenter(IControlPanelPresenter presenter) {
        mPresenter = presenter;
    }

    @OnClick({R2.id.musicmain_back, R2.id.cp_before, R2.id.cp_play, R2.id.cp_next, R2.id.cp_star,
            R2.id.cp_recycle, R2.id.cp_songlist, R2.id.musicmain_user_vipstatus_view})
    public void onClick(View view) {
        QLog.d(TAG, "onClick viewId:" + view.getId());
        if (Utils.isFastDoubleClick()) {
            QLog.d(TAG, "isFastDoubleClick");
            return;
        }
        RCaster caster = new RCaster(R.class, R2.class);
        int viewId = caster.cast(view.getId());

        if (viewId == R2.id.musicmain_back) {
            ((M4BaseActivity) getActivity()).switchLauncher();
            Countly.sharedInstance().recordEvent("music", "t_music_back");
            return;
        }

        SuperPresenter.getInstance().isOperateByUI = true;
        SuperPresenter.getInstance().mIsClickUi = true;

        switch (viewId) {
            case R2.id.cp_before:
                QLog.d(TAG, "before");
                Countly.sharedInstance().recordEvent("music", "t_music_last");

                if (!Utils.checkNetworkAvailable()) {
                    ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
                } else {
                    mMusicSeekBar.setProgress(0);
                    mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
                    mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
                    mPresenter.onBeforeClicked();
                }
                break;
            case R2.id.cp_play:
                QLog.d(TAG, "play");
                mPresenter.onPlayPauseClicked();
                break;
            case R2.id.cp_next:
                QLog.d(TAG, "next");
                Countly.sharedInstance().recordEvent("music", "t_music_next");

                if (!Utils.checkNetworkAvailable()) {
                    ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
                } else {
                    mMusicSeekBar.setProgress(0);
                    mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
                    mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
                    mPresenter.onNextClicked();
                }
                break;
            case R2.id.cp_star:
                QLog.d(TAG, "star");
                if (!Utils.checkNetworkAvailable()) {
                    ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
                    return;
                } else {
               //     mStarButton.setImageResource((mPresenter.getSongInfo().getIsFavorite() == 1)? R.mipmap.btn_unstar:R.mipmap.btn_stared);//
                    mPresenter.onStarClicked();
                }
                break;
            case R2.id.cp_recycle:
                QLog.d(TAG, "playmode");
                mPresenter.onPlayModeClick();
                break;

            case R2.id.cp_songlist:
                Countly.sharedInstance().recordEvent("music", "t_music_list");

                if (mPresenter.getSongInfos() != null && mPresenter.getSongInfos().size() > 0) {
                    mSongListPopWindow = new SongListPopWindow(getActivity(), R.style.Dialog_FS);
                    mSongListPopWindow.updateSongInfo(null, mPresenter.getSongInfos(), false);
                    mSongListPopWindow.setIsMore(mPresenter.isSongListMore());
                    mSongListPopWindow.setVipInfo(mVipInfo);
                    mSongListPopWindow.show();
                } else {
                    ToastManagerUtils.showToastForceError("歌单列表为空");
                }
                break;
            case R2.id.musicmain_user_vipstatus_view:
//                if (mVipInfo != null && mVipInfo.getVip_flag() == 0) {
//                    mBuyVipPopWindow = new BuyVipPopWindow(getActivity(), R.style.Dialog_FS);
//                    mBuyVipPopWindow.show();
//                }
                break;
            default:
                break;
        }
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            bar.setTag(Boolean.valueOf(true));
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            QLog.d(TAG, "onProgressChanged changed");
            mProgress = progress;
            mSeekBarFromUser = fromuser;
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            QLog.d(TAG, "onStopTrackingTouch stop");
            if (Utils.checkNetworkAvailable()) {
                bar.setTag(Boolean.valueOf(false));
                MusicPlayerController.getInstance().seekTo(bar.getProgress());
                mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(bar.getProgress()));
                seekLrcToTime(bar.getProgress(), false);
            } else {
                bar.setTag(Boolean.valueOf(false));
                MusicPlayerController.getInstance().seekTo(bar.getProgress());
                mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(bar.getProgress()));
                seekLrcToTime(bar.getProgress(), false);
                ToastManagerUtils.showToastForceError("网络开小差了，请检查网络");
            }


//            if (mSeekBarFromUser) {
//                if (!MusicPlayerController.getInstance().isPlaying()) {
//                    mPresenter.onPlayPauseClicked();
//                }
//            }
        }
    };

    public void seekLrcToTime(long time, boolean animtor) {
        if (null != mLrcView) {
            mLrcView.updateTime(time, animtor);
        }
    }

    private void refreshProgress(final int position) {
        if (null == mMusicSeekBar) {
            QLog.d(TAG, "refreshProgress view error! return");
            return;
        }
        mProgress = position;
        if (mProgress < 1) {
            mProgress = MusicPlayerController.getInstance().getCurrentPosition();
        }
        int duration = getDuration();
        QLog.d(TAG, "refreshProgress position:" + mProgress + ",duration:" + duration);
        if (duration < 1000 || duration > 10000000 || mProgress < 0) {
            mMusicSeekBar.setProgress(0);
            mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(0));
            mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(0));
            return;
        }

        mMusicSeekBar.setMax(duration);
        if (mProgress > duration) {
            mProgress = 0;
        }

        QLog.d(TAG, "refreshProgress mScrollMode:" + mScrollMode);
        if (mScrollMode != SCROLL_H) {
            if (mProgress > 0
                    && mMusicSeekBar.getProgress() != mProgress
                    && (mMusicSeekBar.getTag() == null
                    || (mMusicSeekBar.getTag() instanceof Boolean && !(Boolean) mMusicSeekBar.getTag()))) {
                mMusicSeekBar.setProgress(mProgress);
            }
            mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(mProgress));
            mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(duration));

            seekLrcToTime(mProgress, position != 0);
        }
    }

    private int getDuration() {
        if (mMusicDuration < 1000) {
            mMusicDuration = MusicPlayerController.getInstance().getDuration();
            QLog.d(TAG, "getDuration mMusicDuration:" + mMusicDuration);
            mMusicDuration = Math.max(0, mMusicDuration);
            if (mMusicDuration > 50 * 60 * 1000) {
                mMusicDuration = 0;
            }
            mTotalTimeView.setText(Utils.getTimeFormat4Hsm2(mMusicDuration));
            mMusicSeekBar.setMax(mMusicDuration);
        }
        return mMusicDuration;
    }

    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    public void onTouchEventChanged(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL
                || event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (mScrollMode == SCROLL_H) {
                MusicPlayerController.getInstance().seekTo(mProgress);

                seekLrcToTime(mProgress, false);

//                if (!MusicPlayerController.getInstance().isPlaying()) {
//                    mPresenter.onPlayPauseClicked();
//                }

                mScrollTime.setVisibility(View.GONE);
            } else if (mScrollMode == SCROLL_V) {
                mLrcView.onViewTouchEvent(event);
            }
            mScrollMode = NONE;
        }
    }

    private static final int NONE = 0, SCROLL_V = 2, SCROLL_H = 3;

    int mScrollMode = NONE;

    public boolean onDown(MotionEvent e) {
        QLog.d(TAG, "onDown");
        //每次按下都重置为NONE
        mScrollMode = NONE;
        //每次按下的时候更新当前亮度和音量，还有进度
        oldProgress = (float) mMusicSeekBar.getProgress() / mMusicDuration;

        mLrcView.mSimpleOnGestureListener.onDown(e);
        return true;
    }

    //横向偏移检测，让快进快退不那么敏感
    private int offsetX = 2, offsetY = 2;
    private float newProgress = 0, oldProgress = 0;

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        switch (mScrollMode) {
            case NONE:
                QLog.d(TAG, "NONE: ");
                //offset是让快进快退不要那么敏感的值
                if (Math.abs(distanceX) - Math.abs(distanceY) > offsetX) {
                    mScrollMode = SCROLL_H;
                } else if (Math.abs(distanceY) - Math.abs(distanceX) > offsetX) {
                    mScrollMode = SCROLL_V;
                }
                break;
            case SCROLL_V:
                if (e1 == null || e2 == null) {
                    return false;
                }
                mLrcView.mSimpleOnGestureListener.onScroll(e1, e2, distanceX, distanceY);
                QLog.d(TAG, "SCROLL_V: ");
                break;
            case SCROLL_H:
                if (e1 == null || e2 == null) {
                    return false;
                }
                float offset = (e2.getX() - e1.getX()) / 1;
                int screenWidth = Utils.getScreenWidth();

                //根据移动的正负决定快进还是快退
                if (offset > 0) {
                    newProgress = oldProgress + offset / screenWidth;
                    if (newProgress > 1) {
                        newProgress = 1;
                    }
                } else {
                    newProgress = oldProgress + offset / screenWidth;
                    if (newProgress < 0) {
                        newProgress = 0;
                    }
                }

                mProgress = (int) (newProgress * mMusicDuration);
                QLog.d(TAG, "onScroll old:" + mMusicSeekBar.getProgress() + ",new:" + mProgress + ",offset:" + offset);
                mMusicSeekBar.setProgress(mProgress);

                mCurrentTimeView.setText(Utils.getTimeFormat4Hsm2(mProgress));

                mScrollTime.setVisibility(View.VISIBLE);
                mScrollTime.setText(Utils.getTimeFormat4Hsm2(mProgress));

                QLog.d(TAG, "SCROLL_H: ");
                break;
        }
        return true;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return mLrcView.mSimpleOnGestureListener.onScroll(e1, e2, velocityX, velocityY);
    }

    private void setSeekBarClickable(int i) {
        if (i == 1) {
            //启用状态
            mMusicSeekBar.setClickable(true);
            mMusicSeekBar.setEnabled(true);
            mMusicSeekBar.setSelected(true);
            mMusicSeekBar.setFocusable(true);
            mMusicSeekBar.setProgress(0);
        } else {
            //禁用状态
            mMusicSeekBar.setClickable(false);
            mMusicSeekBar.setEnabled(false);
            mMusicSeekBar.setSelected(false);
            mMusicSeekBar.setFocusable(false);
            mMusicSeekBar.setProgress(0);
        }
    }
}
