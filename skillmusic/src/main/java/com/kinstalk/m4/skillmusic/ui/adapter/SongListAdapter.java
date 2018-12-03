package com.kinstalk.m4.skillmusic.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.skillmusic.R;
import com.kinstalk.m4.skillmusic.R2;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.model.entity.UserVipInfo;
import com.kinstalk.m4.skillmusic.model.player.MusicPlayerController;
import com.kinstalk.m4.skillmusic.model.presenter.SuperPresenter;
import com.kinstalk.m4.skillmusic.model.usecase.musiccontrol.PlayReset;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;
import com.kinstalk.m4.skillmusic.ui.utils.ToastManagerUtils;
import com.kinstalk.m4.skillmusic.ui.view.SongListPopWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongListAdapter extends RecyclerView.Adapter<ViewHolder> {
    protected LayoutInflater mLayoutInflater;
    protected int mHeaderCount = 0;// 头部View个数
    private Context mContext;
    private ArrayList<SongInfo> mItems = new ArrayList<>();
    private SongListPopWindow mSongListPopWindow;
    private SongInfo mClickEntity;
    private UserVipInfo mVipInfo;

    private int lastIndex = -1;
    private boolean mViewEnable = true;

    public SongListAdapter(Context context, SongListPopWindow popWindow) {
        this.mContext = context;
        this.mSongListPopWindow = popWindow;
    }

    public void updateSongInfo(SongInfo songInfo, ArrayList<SongInfo> songArrayList, boolean isMore) {
        mItems = songArrayList;

        if (null == mClickEntity) {
            mClickEntity = songArrayList.get(0);
        }

        if (songInfo == null || isMore) {
            notifyDataSetChanged();
            songInfo = MusicPlayerController.getInstance().getCurSongInfo();
            if (songInfo != null) {
                for (int i = 0; i < mItems.size(); i++) {
                    if (TextUtils.equals(songInfo.getPlayId(), mItems.get(i).getPlayId())) {
                        lastIndex = i + mHeaderCount;
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < mItems.size(); i++) {
                if (TextUtils.equals(songInfo.getPlayId(), mItems.get(i).getPlayId())) {
                    notifyItemChanged(i + mHeaderCount);
                    if (lastIndex > -1) {
                        notifyItemChanged(lastIndex);
                    }
                    lastIndex = i + mHeaderCount;
                    break;
                }
            }
        }
//        notifyDataSetChanged();
    }

    public void setVipInfo(UserVipInfo mVipInfo) {
        this.mVipInfo = mVipInfo;
        notifyDataSetChanged();
    }

    public int getHeaderCount() {
        return mHeaderCount;
    }

    public ViewHolder onCreateHeaderView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_songllist_headview, parent, false);
        HeaderViewHolder headerViewHolder = new HeaderViewHolder(view);
        return headerViewHolder;
    }

    public ViewHolder onCreateContentView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_songllist, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof ItemViewHolder && null != mItems && position - mHeaderCount < mItems.size() && position - mHeaderCount >= 0) {
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            int pos = position - mHeaderCount;
            final SongInfo entity = mItems.get(pos);

            itemViewHolder.mSongName.setText(entity.getSongName() + "-" + entity.getSingerName());
            if (mVipInfo == null || mVipInfo.getVip_flag() == 0) {
                itemViewHolder.mFavIcon.setVisibility(View.INVISIBLE);
            } else {
                itemViewHolder.mFavIcon.setVisibility(View.VISIBLE);
            }
        //    itemViewHolder.mFavIcon.setImageResource((entity.getIsFavorite() == 1) ?
        //            R.drawable.btn_play_bar_btn_love : R.drawable.btn_play_bar_btn_unlove);

            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Utils.checkNetworkAvailable()) {
                        ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
                        return;
                    }

                    if (mClickEntity == null || !mClickEntity.equals(entity)) {
                        MusicPlayerController.getInstance().requestStopPlayer();

                        MusicPlayerController.getInstance().cancelRetryErrorPlay();

                        PlayReset.RequestValue playRest = new PlayReset.RequestValue();
                        EventBus.getDefault().post(playRest);

                        QAIMusicConvertor.getInstance().playMusicInfo(entity, true);

                        QAIMusicConvertor.getInstance().refreshPlayListIfNeed(false);

                        QAIMusicConvertor.getInstance().getMorePlayListIfNeed(false);
                    }

                    mClickEntity = entity;
                }
            });

            itemViewHolder.mFavIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QLog.d(SongListAdapter.this, "mFavIcon click");

                    if (!Utils.checkNetworkAvailable()) {
                        ToastManagerUtils.showToastForceError("网络开小差了，请稍后再试");
                        return;
                    }

                    if (!mViewEnable) {
                        ToastManagerUtils.showToastForceError("语音播报中\n请稍后再试");
                        QLog.d(SongListAdapter.this, "tts not end ! view disable!");
                        return;
                    }

                    SuperPresenter.getInstance().requestCollect(entity, entity.getIsFavorite() != 1);
                }
            });

            SongInfo curSongInfo = MusicPlayerController.getInstance().getCurSongInfo();
            if (curSongInfo != null
                    && TextUtils.equals(curSongInfo.getPlayId(), entity.getPlayId())) {
                itemViewHolder.mSongName.setTextColor(Color.parseColor("#1F97FB"));

//                if (MusicPlayerController.getInstance().isPlaying()) {
//                    itemViewHolder.mPlayIcon.setVisibility(View.INVISIBLE);
//                } else {
                itemViewHolder.mPlayIcon.setVisibility(View.VISIBLE);
//                }

                mClickEntity = entity;
            } else {
                itemViewHolder.mPlayIcon.setVisibility(View.INVISIBLE);
                itemViewHolder.mSongName.setTextColor(Color.parseColor("#005A98"));
            }

            if (position == getItemCount() - 1) {
                itemViewHolder.mLine.setVisibility(View.GONE);
            } else {
                itemViewHolder.mLine.setVisibility(View.VISIBLE);
            }
        }
    }

    public void viewEnable(boolean enable) {
        mViewEnable = enable;
    }

    private void clearAnimation(ImageView view) {
        if (view.getDrawable() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) view.getDrawable();
            animationDrawable.stop();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderCount != 0 && position < mHeaderCount) {// 头部View
            return ITEM_TYPE.ITEM_TYPE_HEADER.ordinal();
        } else {
            return ITEM_TYPE.ITEM_TYPE_CONTENT.ordinal();
        }
    }

    public int getContentItemCount() {
        return null == mItems ? 0 : mItems.size();
    }

    @Override
    public int getItemCount() {
        return mHeaderCount + getContentItemCount();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM_TYPE_HEADER.ordinal()) {
            return onCreateHeaderView(parent);
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_CONTENT.ordinal()) {
            return onCreateContentView(parent);
        }
        return null;
    }

    public enum ITEM_TYPE {
        ITEM_TYPE_HEADER, ITEM_TYPE_CONTENT
    }

    public static class HeaderViewHolder extends ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ItemViewHolder extends ViewHolder {
        @BindView(R2.id.songinfo_item_songname)
        public TextView mSongName;
        @BindView(R2.id.songinfo_item_playing)
        public ImageView mPlayIcon;
        @BindView(R2.id.songinfo_item_favorite)
        public ImageView mFavIcon;
        @BindView(R2.id.songinfo_item_line)
        public ImageView mLine;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
