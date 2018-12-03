package com.kinstalk.her.skillnews.utils;

import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;

import kinstalk.com.qloveaicore.AICoreDef;

/**
 * Created by siqing on 17/4/19.
 */

public interface Constants {

    interface ServiceType {
        String TYPE_NEWS = AICoreDef.QLServiceType.TYPE_NEWS;
    }

    interface ResourceFormat {
        /**
         * URL类型
         */
        int URL = XWCommonDef.ResourceFormat.URL;
        /**
         * 文本类型
         */
        int TEXT = XWCommonDef.ResourceFormat.TEXT;
        /**
         * TTS类型
         */
        int TTS = XWCommonDef.ResourceFormat.TTS;
        /**
         * 本地文件类型
         */
        int FILE = XWCommonDef.ResourceFormat.FILE;
        /**
         * 位置类型
         */
        int LOCATION = XWCommonDef.ResourceFormat.LOCATION;
        /**
         * 指令类型
         */
        int COMMAND = XWCommonDef.ResourceFormat.COMMAND;
        /**
         *
         */
        int INTENT = XWCommonDef.ResourceFormat.INTENT;
        /**
         * 未知类型
         */
        int UNKNOW = XWCommonDef.ResourceFormat.UNKNOW;
    }

    interface AppControlCmd {
        int CONTROL_CMD_BASE = AICoreDef.AppControlCmd.CONTROL_CMD_RESUME;
        int CONTROL_CMD_RESUME = AICoreDef.AppControlCmd.CONTROL_CMD_RESUME;
        int CONTROL_CMD_PAUSE = AICoreDef.AppControlCmd.CONTROL_CMD_PAUSE;
        int CONTROL_CMD_STOP = AICoreDef.AppControlCmd.CONTROL_CMD_STOP;
        int CONTROL_CMD_PREV = AICoreDef.AppControlCmd.CONTROL_CMD_PREV;
        int CONTROL_CMD_NEXT = AICoreDef.AppControlCmd.CONTROL_CMD_NEXT;
        int CONTROL_CMD_RANDOM = AICoreDef.AppControlCmd.CONTROL_CMD_RANDOM;
        int CONTROL_CMD_ORDER = AICoreDef.AppControlCmd.CONTROL_CMD_ORDER;
        int CONTROL_CMD_LOOP = AICoreDef.AppControlCmd.CONTROL_CMD_LOOP;
        int CONTROL_CMD_SINGLE = AICoreDef.AppControlCmd.CONTROL_CMD_SINGLE;
        int CONTROL_CMD_REPEAT = AICoreDef.AppControlCmd.CONTROL_CMD_REPEAT;
        int CONTROL_CMD_SHARE = AICoreDef.AppControlCmd.CONTROL_CMD_SHARE;
    }

    interface AppState {
        int APP_STATE_BASE = AICoreDef.AppState.APP_STATE_BASE;
        int APP_STATE_ONCREATE = AICoreDef.AppState.APP_STATE_ONCREATE;
        int APP_STATE_ONRESUME = AICoreDef.AppState.APP_STATE_ONRESUME;
        int APP_STATE_ONPAUSE = AICoreDef.AppState.APP_STATE_ONPAUSE;
        int APP_STATE_ONDESTROY = AICoreDef.AppState.APP_STATE_ONDESTROY;
        int PLAY_STATE_PLAY = AICoreDef.AppState.PLAY_STATE_PLAY;
        int PLAY_STATE_PAUSE = AICoreDef.AppState.PLAY_STATE_PAUSE;
    }

    /**
     * 上报状态类型定义
     */
    interface PlayState {
        /**
         * 一首歌开始播放
         */
        int START = XWCommonDef.PlayState.START;
        /**
         * 暂停播放
         */
        int PAUSE = XWCommonDef.PlayState.PAUSE;
        /**
         * 一首歌播放完毕
         */
        int STOP = XWCommonDef.PlayState.STOP;
        /**
         * 歌单播放结束，停止播放了
         */
        int FINISH = XWCommonDef.PlayState.FINISH;
        /**
         * 空闲
         */
        int IDLE = XWCommonDef.PlayState.IDLE;
        /**
         * 继续播放
         */
        int RESUME = XWCommonDef.PlayState.RESUME;
    }


    int PLAYMODE_ORDER = 2;

    String INTENT_NEWS_RESULT = "INTENT_NEWS_RESULT";
    String INTENT_NEWS_PLAY = "INTENT_NEWS_PLAY";
    String INTENT_NEWS_ARRAY = "INTENT_NEWS_ARRAY";
    String INTENT_REMOTE = "INTENT_REMOTE";

    String REQUEST_STATE_START = "start";
    String REQUEST_STATE_COMPLETE = "complete";
    String REQUEST_STATE_ERROR = "error";

    String REQUEST_CMD_PLAY = "播放";
    String REQUEST_CMD_PAUSE = "暂停";
    String REQUEST_CMD_PREVUS = "上一首";
    String REQUEST_CMD_NEXT = "下一首";

    String ACTION_VIRYUAL_CMD = "virtual_cmd";
    String ACTION_SET_STATE = "set_state";

    String ACTION_TXSDK_TTS = "kinstalk.com.aicore.action.txsdk.tts";
    String ACTION_TXSDK_EXTRA_TTS_STATE = "kinstalk.com.aicore.action.txsdk.tts_state";
    String ACTION_TXSDK_EXTRA_TTS_START = "start";
    String ACTION_TXSDK_EXTRA_TTS_STOP = "stop";
    String ACTION_WIFI_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    String ACTION_LOGIN_SUCCESS = "ACTION_LOGIN_SUCCESS";
    String ACTION_ASSISY_KEY = "com.kinstalk.action.assistkey";
}
