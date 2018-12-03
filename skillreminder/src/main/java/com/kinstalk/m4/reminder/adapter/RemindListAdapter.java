/**
 * created by jiang, 12/3/15
 * Copyright (c) 2015, jyuesong@gmail.com All Rights Reserved.
 * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #                                                   #
 */

package com.kinstalk.m4.reminder.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiang.android.lib.adapter.BaseAdapter;
import com.jiang.android.lib.adapter.expand.StickyRecyclerHeadersAdapter;
import com.jiang.android.lib.widget.SwipeItemLayout;
import com.kinstalk.m4.publicapi.view.Toasty.Toasty;
import com.kinstalk.m4.reminder.R;
import com.kinstalk.m4.reminder.constant.CountlyConstant;
import com.kinstalk.m4.reminder.entity.CalendarEvent;
import com.kinstalk.m4.reminder.provider.AIRequestHelper;
import com.kinstalk.m4.reminder.util.NetworkUtils;
import com.kinstalk.m4.reminder.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import ly.count.android.sdk.Countly;

public class RemindListAdapter extends BaseAdapter<CalendarEvent, RemindListAdapter.RemindViewHolder>
        implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();

    private Context context;
    private RecyclerView recyclerView;
    private OnDeleteListener mDeleteListener;

    public RemindListAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    public void setDeleteListener(OnDeleteListener listener) {
        this.mDeleteListener = listener;
    }

    public void refreshData(List<CalendarEvent> calendarEventList) {
        this.addAll(calendarEventList);
    }

    @Override
    public RemindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remind_content, parent, false);
        return new RemindViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RemindViewHolder holder, final int position) {
        SwipeItemLayout swipeRoot = holder.rootLayout;

        final CalendarEvent calendarEvent = getItem(position);
        holder.idView.setText("" + (position + 1));
        holder.titleView.setText(TextUtils.isEmpty(calendarEvent.getTitle()) ? "提醒" : calendarEvent.getTitle());

        String timeStr = TimeUtils.getTimeApm(context, calendarEvent.getStartTime()) + TimeUtils.getFormatTime(calendarEvent.getStartTime());
//        holder.timeSlotView.setText(TimeUtils.getTimeApm(context, calendarEvent.getStartTime()));
        if (!TextUtils.isEmpty(calendarEvent.getRruleFormat())) {
            holder.timeView.setText(calendarEvent.getRruleFormat() + "  " + timeStr);

//            holder.repeatView.setText(calendarEvent.getRruleFormat());
        } else {
            holder.timeView.setText(TimeUtils.getFormatDate(context, calendarEvent.getStartTime()) + "  " + timeStr);

//            holder.repeatView.setText(TimeUtils.getFormatDate(context, calendarEvent.getStartTime()));
        }

        swipeRoot.setSwipeAble(true);
        swipeRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
            @Override
            public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
                mOpenedSil.add(swipeItemLayout);
            }

            @Override
            public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                mOpenedSil.remove(swipeItemLayout);
            }

            @Override
            public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                closeOpenedSwipeItemLayoutWithAnim();
            }
        });
        holder.deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isNetworkAvailable(context)) {
                    Toasty.error(context, "网络不给力，请稍后重试", true).show();
                    return;
                }

                if (null != mDeleteListener) {
                    mDeleteListener.onDelete();
                }
                AIRequestHelper.getInstance().requestScheduleDelete(calendarEvent.getEventId());
                Countly.sharedInstance().recordEvent("schedule", CountlyConstant.T_DELETE_REMINDER);
            }
        });
    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (SwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    @Override
    public long getHeaderId(int position) {
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        CalendarEvent calendarEvent = getItem(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText(TimeUtils.getFormatDate(context, calendarEvent.getStartTime()));
    }

    public class RemindViewHolder extends RecyclerView.ViewHolder {
        public SwipeItemLayout rootLayout;
        public TextView deleteView;
        public TextView titleView;
        public TextView timeView;
        //        public TextView repeatView;
        public TextView idView;

        public RemindViewHolder(View itemView) {
            super(itemView);

            rootLayout = (SwipeItemLayout) itemView.findViewById(R.id.remind_content_root);
            deleteView = (TextView) itemView.findViewById(R.id.remind_delete);
            idView = (TextView) itemView.findViewById(R.id.reminder_itemid);
            titleView = (TextView) itemView.findViewById(R.id.remind_itemtitle);
            timeView = (TextView) itemView.findViewById(R.id.remind_itemtime);
//            repeatView = (TextView) itemView.findViewById(R.id.remind_itemrepeat);
        }
    }

    public interface OnDeleteListener {
        void onDelete();
    }
}
