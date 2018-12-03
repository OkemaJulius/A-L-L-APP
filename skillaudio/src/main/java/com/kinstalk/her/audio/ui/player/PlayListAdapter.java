package com.kinstalk.her.audio.ui.player;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinstalk.her.audio.R;
import com.kinstalk.her.audio.R2;
import com.kinstalk.her.audio.constant.CountlyConstant;
import com.kinstalk.her.audio.controller.AudioPlayerController;
import com.kinstalk.her.audio.controller.PlayListDataSource;
import com.kinstalk.her.audio.entity.AudioEntity;
import com.kinstalk.m4.common.utils.Utils;
import com.kinstalk.m4.publicapi.view.Toasty.Toasty;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import ly.count.android.sdk.Countly;

public class PlayListAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context mContext;
    private PlayListPopWindow popWindow;
    private ArrayList<AudioEntity> mItems = new ArrayList<>();
    private AudioEntity mClickEntity;

    public PlayListAdapter(Context context, PlayListPopWindow popWindow) {
        this.mContext = context;
        this.popWindow = popWindow;
    }

    public void updateSongInfo(ArrayList<AudioEntity> songArrayList) {
        mItems = new ArrayList<>(songArrayList);

        mClickEntity = PlayListDataSource.getInstance().getPlaySong();

        notifyDataSetChanged();
    }

    public ViewHolder onCreateContentView(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_playlist, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        final AudioEntity entity = mItems.get(position);

        if (!TextUtils.isEmpty(entity.getName()) && !TextUtils.isEmpty(entity.getAlbum())) {
            itemViewHolder.mSongName.setText(entity.getAlbum() + " - " + entity.getName());
        } else if (!TextUtils.isEmpty(entity.getName())) {
            itemViewHolder.mSongName.setText(entity.getName());
        } else {
            itemViewHolder.mSongName.setText("");
        }

        itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.checkNetworkAvailable()) {
                    Toasty.error(mContext, "网络开小差了，请稍后再试", true).show();
                    return;
                }

                if (mClickEntity.equals(entity)) {
                    return;
                }

                AudioPlayerController.getInstance().requestPlayWithEntity(entity);
                Countly.sharedInstance().recordEvent(CountlyConstant.SKILL_TYPE, CountlyConstant.T_FM_SELECT);
                notifyDataSetChanged();

                mClickEntity = entity;
                popWindow.dismiss();
            }
        });

        AudioEntity curSongInfo = AudioPlayerController.getInstance().getCurSongInfo();
        if (curSongInfo != null
                && TextUtils.equals(curSongInfo.getPlayId(), entity.getPlayId())) {
            itemViewHolder.mSongName.setTextColor(Color.parseColor("#005A98"));

            itemViewHolder.mPlayIcon.setVisibility(View.VISIBLE);

            mClickEntity = entity;
        } else {
            itemViewHolder.mPlayIcon.setVisibility(View.INVISIBLE);
            itemViewHolder.mSongName.setTextColor(Color.parseColor("#1F97FB"));
        }

        if (position == getItemCount() - 1) {
            itemViewHolder.mLine.setVisibility(View.GONE);
        } else {
            itemViewHolder.mLine.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return null == mItems ? 0 : mItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateContentView(parent);
    }

    public static class ItemViewHolder extends ViewHolder {
        @BindView(R2.id.songinfo_item_songname)
        public TextView mSongName;
        @BindView(R2.id.songinfo_item_playing)
        public ImageView mPlayIcon;
        @BindView(R2.id.songinfo_item_line)
        public ImageView mLine;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
