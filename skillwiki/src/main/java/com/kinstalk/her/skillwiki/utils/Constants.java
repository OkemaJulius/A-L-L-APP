package com.kinstalk.her.skillwiki.utils;

import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;

import kinstalk.com.qloveaicore.AICoreDef;

/**
 * Created by siqing on 17/4/19.
 */

public interface Constants {

    String INTENT_WIKI_INFO = "INTENT_WIKI_INFO";

    interface ServiceType {
        String TYPE_WIKI = AICoreDef.QLServiceType.TYPE_WIKI;
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

    interface AppState {
        int APP_STATE_BASE = AICoreDef.AppState.APP_STATE_BASE;
        int APP_STATE_ONCREATE = AICoreDef.AppState.APP_STATE_ONCREATE;
        int APP_STATE_ONRESUME = AICoreDef.AppState.APP_STATE_ONRESUME;
        int APP_STATE_ONPAUSE = AICoreDef.AppState.APP_STATE_ONPAUSE;
        int APP_STATE_ONDESTROY = AICoreDef.AppState.APP_STATE_ONDESTROY;
        int PLAY_STATE_PLAY = AICoreDef.AppState.PLAY_STATE_PLAY;
        int PLAY_STATE_PAUSE = AICoreDef.AppState.PLAY_STATE_PAUSE;
    }

}
