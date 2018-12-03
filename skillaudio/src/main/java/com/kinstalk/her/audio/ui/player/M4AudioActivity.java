package com.kinstalk.her.audio.ui.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kinstalk.her.audio.R;
import com.kinstalk.her.audio.R2;
import com.kinstalk.her.audio.constant.CountlyConstant;
import com.kinstalk.her.audio.controller.AudioPlayerController;
import com.kinstalk.her.audio.controller.PlayListDataSource;
import com.kinstalk.her.audio.entity.AudioEntity;
import com.kinstalk.her.audio.entity.SystemEventEntity;
import com.kinstalk.her.audio.ui.view.RoundedCornersTransformation;
import com.kinstalk.her.audio.util.TimeUtils;
import com.kinstalk.her.audio.util.ToastManagerUtils;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.activity.M4BaseActivity;
import com.kinstalk.m4.publicapi.view.Toasty.Toasty;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import kinstalk.com.qloveaicore.AICoreDef;
import kinstalk.com.qloveaicore.AICoreDef.AppState;
import ly.count.android.sdk.Countly;

public class M4AudioActivity extends M4BaseActivity implements AudioPlayerContract.View {
    private static final long UPDATE_PROGRESS_INTERVAL = 1000;
    private static final String TAG = "M4AudioLog";
    private Unbinder butterKnifeUnBinder;
    @BindView(R2.id.fm_bg_iv)
    ImageView bgView;
    @BindView(R2.id.fm_title_tv)
    TextView titleTv;
    @BindView(R2.id.fm_title2_tv)
    TextView title2Tv;
    @BindView(R2.id.fm_control_iv)
    ImageView controlBtn;
    @BindView(R2.id.fm_pre_iv)
    ImageView preBtn;
    @BindView(R2.id.fm_next_iv)
    ImageView nextBtn;
    @BindView(R2.id.fm_list_iv)
    ImageView playlistBtn;
    @BindView(R2.id.fm_progress_seekbar)
    SeekBar fmSeekBar;
    @BindView(R2.id.fm_loadingview)
    public ProgressBar loadingView;
    /* @BindView(R2.id.control_panel_time_layout)
     public LinearLayout mTimeLayout;*/
    @BindView(R2.id.current_time)
    public TextView mCurrentTimeView;
    @BindView(R2.id.total_time)
    public TextView mTotalTimeView;
    private PlayListPopWindow mPlayListPopWindow;

    private AudioPlayerContract.Presenter mPresenter;
    private Handler mHandler = new Handler();
    private Runnable mProgressCallback = new Runnable() {
        @Override
        public void run() {
            if (isDestroyed()) return;

            if (AudioPlayerController.getInstance().isPlaying()) {
                int progress = AudioPlayerController.getInstance().getCurrentPosition();
                mCurrentTimeView.setText(TimeUtils.formatDuration(progress));
                if (progress >= 0 && progress <= fmSeekBar.getMax()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        fmSeekBar.setProgress(progress, true);
                    } else {
                        fmSeekBar.setProgress(progress);
                    }
                }
                mHandler.postDelayed(this, UPDATE_PROGRESS_INTERVAL);
            }
        }
    };

    public static void actionStart(Context context) {
        Intent intentAudio = new Intent(context, M4AudioActivity.class);
        intentAudio.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intentAudio);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m4_audio);
        IntentFilter intentFilter = new IntentFilter("kinstalk.com.aicore.action.window_shown");
        registerReceiver(mAiUIReceiver, intentFilter);

        butterKnifeUnBinder = ButterKnife.bind(this);
        new AudioPlayerPresenter(this, this).subscribe();
        fmSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mCurrentTimeView.setText(TimeUtils.formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(mProgressCallback);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!Utils.checkNetworkAvailable()) {
                    Toasty.error(M4AudioActivity.this, "网络开小差了，请稍后再试", true).show();
                }
                AudioPlayerController.getInstance().seekTo(seekBar.getProgress());
                if (AudioPlayerController.getInstance().isPlaying()) {
                    mHandler.removeCallbacks(mProgressCallback);
                    mHandler.postDelayed(mProgressCallback, UPDATE_PROGRESS_INTERVAL);
                }
            }
        });
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(AICoreDef.QLServiceType.TYPE_FM, AppState.APP_STATE_ONCREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(AICoreDef.QLServiceType.TYPE_FM, AppState.APP_STATE_ONRESUME);
        if (AudioPlayerController.getInstance().isPlaying()) {
            onPlayStatusChanged(true);
            setAutoToLauncher(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(AICoreDef.QLServiceType.TYPE_FM, AppState.APP_STATE_ONPAUSE);
        super.onPause();
        if (null != mPlayListPopWindow && mPlayListPopWindow.isShowing()) {
            mPlayListPopWindow.cancel();
            mPlayListPopWindow = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AICoreManager.getInstance(CoreApplication.getApplicationInstance())
                .updateAppState(AICoreDef.QLServiceType.TYPE_FM, AppState.APP_STATE_ONDESTROY);
        if (butterKnifeUnBinder != null) {
            butterKnifeUnBinder.unbind();
        }
        mPresenter.unsubscribe();
        if (null != mPlayListPopWindow && mPlayListPopWindow.isShowing()) {
            mPlayListPopWindow.cancel();
            mPlayListPopWindow = null;
        }
        unregisterReceiver(mAiUIReceiver);
    }

    @Subscribe
    public void onUnBindNotify(SystemEventEntity eventEntity) {
        if (eventEntity.getAction() == SystemEventEntity.ACTION_EXIT) {
            finish();
        }
    }

    @OnClick(R2.id.fm_control_iv)
    public void onPlayToggleAction(View view) {
        if (AudioPlayerController.getInstance().getCurSongInfo() == null) return;

        if (!Utils.checkNetworkAvailable()) {
            Toasty.error(this, "网络开小差了，请稍后再试", true).show();
            return;
        }
        if (AudioPlayerController.getInstance().isPlaying()) {
            mPresenter.requestPause();
            Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.T_FM_PAUSE);
        } else {
            mPresenter.requestContinue();
            Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.T_FM_PLAY);
        }
    }

    @OnClick(R2.id.fm_pre_iv)
    public void onPlayLastAction(View view) {
        if (AudioPlayerController.getInstance().getCurSongInfo() == null) return;

        if (!Utils.checkNetworkAvailable()) {
            Toasty.error(this, "网络开小差了，请稍后再试", true).show();
            return;
        }
        mPresenter.requestPrePlay();
        Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.T_FM_PREV);
    }

    @OnClick(R2.id.fm_next_iv)
    public void onPlayNextAction(View view) {
        if (AudioPlayerController.getInstance().getCurSongInfo() == null) return;

        if (!Utils.checkNetworkAvailable()) {
            Toasty.error(this, "网络开小差了，请稍后再试", true).show();
            return;
        }
        mPresenter.requestNextPlay();
        Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.T_FM_NEXT);
    }

    @OnClick(R2.id.fm_list_iv)
    public void onPlayListAction(View view) {
        if (AudioPlayerController.getInstance().getCurSongInfo() == null) {
            Log.i("M4AudioLog", "bug15913 AudioPlayerController.getInstance().getCurSongInfo()");
            return;
        }
        Log.i("M4AudioLog", "bug15913 fm_list_iv isOnClick");

        if (PlayListDataSource.getInstance().getPlayList() != null && !PlayListDataSource.getInstance().getPlayList().isEmpty()) {
            mPlayListPopWindow = new PlayListPopWindow(this, R.style.Dialog_FS);
            mPlayListPopWindow.show();
        } else {
            Toasty.error(this, "歌单为空，请稍后再试", true).show();
        }
    }

    @OnClick(R2.id.fm_home_iv)
    public void toHome(View view) {
        switchLauncher();
        Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, "v_fm_back");
    }

    @Override
    public void setPresenter(AudioPlayerContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingStatus(boolean loading) {
        if (null != loadingView) {
            if (loading) {
                controlBtn.setVisibility(View.INVISIBLE);
                loadingView.setVisibility(View.VISIBLE);
            } else {
                controlBtn.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onSongUpdated(@Nullable AudioEntity entity) {
        Log.i(TAG, "onSongUpdated entity=" + entity.toString());
        //bug16385 [100%][网络电台]播放小说列表界面语音：下一首/上一首，列表中播放状态未及时刷新
        if (null != mPlayListPopWindow && mPlayListPopWindow.isShowing() && !PlayListDataSource.getInstance().getPlayList().isEmpty()) {
            mPlayListPopWindow.updateData();
        }
        if (entity == null) {
            controlBtn.setImageDrawable(getResources().getDrawable(R.drawable.audio_play));
            fmSeekBar.setProgress(0);
            titleTv.setText("");
            mCurrentTimeView.setText(TimeUtils.formatDuration(0));
            mHandler.removeCallbacks(mProgressCallback);
            return;
        }

        if (entity.isLive()) {
            //mTimeLayout.setVisibility(View.GONE);
            mCurrentTimeView.setVisibility(View.GONE);
            mTotalTimeView.setVisibility(View.GONE);
            preBtn.setVisibility(View.GONE);
            nextBtn.setVisibility(View.GONE);
            playlistBtn.setSelected(false);
            playlistBtn.setVisibility(View.GONE);
            fmSeekBar.setVisibility(View.GONE);
        } else {
            //mTimeLayout.setVisibility(View.VISIBLE);
            mCurrentTimeView.setVisibility(View.VISIBLE);
            mTotalTimeView.setVisibility(View.VISIBLE);
            preBtn.setVisibility(View.VISIBLE);
            nextBtn.setVisibility(View.VISIBLE);
            playlistBtn.setVisibility(View.VISIBLE);
            fmSeekBar.setVisibility(View.VISIBLE);
            preBtn.setEnabled(true);
            nextBtn.setEnabled(true);

        }

        fmSeekBar.setProgress(0);
        mCurrentTimeView.setText(TimeUtils.formatDuration(0));
        if (!TextUtils.isEmpty(entity.getName())) {
            titleTv.setText(entity.getName());
        } else {
            titleTv.setText("");
        }
        if (!TextUtils.isEmpty(entity.getAlbum())) {
            title2Tv.setText("专辑：" + entity.getAlbum());
        } else {
            title2Tv.setText("");
        }
        // if (!TextUtils.isEmpty(entity.getCover())) {
        bgView.setVisibility(View.VISIBLE);
        Glide.with(this).
                load(entity.getCover()).
                asBitmap().
                transform(new RoundedCornersTransformation(this, 10, 0)).
                placeholder(R.mipmap.audio_loading).
                into(bgView);

       /* } else {
           bgView.setVisibility(View.GONE);
        }*/
    }

    @Override
    public void onPlayListUpdated(@Nullable List<AudioEntity> dataList) {
        if (null != mPlayListPopWindow && mPlayListPopWindow.isShowing() && !PlayListDataSource.getInstance().getPlayList().isEmpty()) {
            mPlayListPopWindow.updateData();
        }
    }

    @Override
    public void onPrepared(@Nullable AudioEntity entity) {
        Log.i(TAG, "onPrepared entity=" + entity.toString());
        onSongUpdated(entity);
        fmSeekBar.setMax(AudioPlayerController.getInstance().getDuration());
        mTotalTimeView.setText(TimeUtils.formatDuration(AudioPlayerController.getInstance().getDuration()));
    }

    @Override
    public void onComplete(@Nullable AudioEntity entity) {
        Log.i(TAG, "onComplete entity=" + entity.toString());
        controlBtn.setImageDrawable(getResources().getDrawable(R.drawable.audio_play));

        mHandler.removeCallbacks(mProgressCallback);
    }

    @Override
    public void onError(@Nullable AudioEntity entity) {
        Log.i(TAG, "onError entity=" + entity.toString());
        controlBtn.setImageDrawable(getResources().getDrawable(R.drawable.audio_play));

        mHandler.removeCallbacks(mProgressCallback);
        fmSeekBar.setProgress(0);
        mCurrentTimeView.setText(TimeUtils.formatDuration(0));
        mTotalTimeView.setText(TimeUtils.formatDuration(0));
        Utils.autoBackLauncher(this, true);
        if (!Utils.checkNetworkAvailable()) {
            ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
        }
    }

    @Override
    public void onPlayStatusChanged(boolean isPlaying) {
        controlBtn.setImageDrawable(isPlaying ? getResources().getDrawable(R.drawable.audio_play) : getResources().getDrawable(R.drawable.audio_pause));

        if (isPlaying) {
            fmSeekBar.setMax(AudioPlayerController.getInstance().getDuration());
            mTotalTimeView.setText(TimeUtils.formatDuration(AudioPlayerController.getInstance().getDuration()));
            mHandler.removeCallbacks(mProgressCallback);
            mHandler.post(mProgressCallback);
        } else {
            mHandler.removeCallbacks(mProgressCallback);
        }
    }


    @Override
    public void setAutoToLauncher(boolean autoHome) {
        Utils.autoBackLauncher(this, autoHome);
    }

    //bug15778 [SA_MustFix][100%][ST][电台]播放电台内容时唤醒设备，电台内容暂停播放，但播放暂停键仍显示“播放”状态
    private BroadcastReceiver mAiUIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("kinstalk.com.aicore.action.window_shown".equals(intent.getAction())) {
                boolean isShow = intent.getBooleanExtra("isShown", true);
                if (isShow) {
                    controlBtn.setImageDrawable(getResources().getDrawable(R.drawable.audio_pause));
                } else {

                }
            }
        }
    };
}
