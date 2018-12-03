package com.kinstalk.m4.skillmusic.ui.constant;


import com.kinstalk.m4.skillmusic.R;

/**
 * Created by siqing on 17/4/19.
 */

public interface CommonConstant {
    String INTENT_SONGINFO = "intent_songinfo";
    String INTENT_SONGINFO2 = "intent_songinfo2";
    String INTENT_CHANNEL = "intent_channel";
    String INTENT_DISSINFO = "intent_dissinfo";
    String INTENT_NOTIFICATION = "intent_notification";
    String INTENT_AI_START = "intent_ai_start";

    String ACTION_GET_CATEGORY = "get_category";
    String ACTION_PLAY_CATEGORY = "play_category";
    String ACTION_PLAY_SUPER_MUSIC = "play_super_music";
    String ACTION_SET_PLAY_MODE = "set_play_mode";
    String ACTION_VIRYUAL_CMD = "virtual_cmd";
    String ACTION_GET_STATE = "get_state";
    String ACTION_SET_STATE = "set_state";
    String ACTION_GET_DISS_LIST = "get_diss_list";
    String ACTION_GET_FAVORITE_LIST = "get_favorites_list";
    String ACTION_GET_PLAY_LIST_WITH_ID = "get_play_list_with_id";
    String ACTION_PLAY_LIST_WITH_ID = "play_list_with_id";
    String ACTION_SET_FAVORITE_WITH_ID = "set_favorite_with_id";
    String ACTION_PLAY_WITH_ID = "play_with_id";
    String ACTION_GET_MORE_WITH_ID = "get_more_with_id";

    String ACTION_GET_LOGIN_STATUS = "get_login_status";
    String ACTION_GET_MUSIC_VIP_INFO = "get_music_vip_info";

    String INTENT_TEXT = "intent_text";
    String INTENT_ACTION = "intent_action";
    String INTENT_CONTENT = "intent_content";

    String ACTION_AICORE_WINDOW_SHOWN = "kinstalk.com.aicore.action.window_shown";
    String EXTRA_AICORE_WINDOW_SHOWN = "isShown";

    int CACHE_SONG_NUM_PER_CHANNEL = 3;

    int SOCKET_CONNECT_TIMEOUT = 15 * 1000;
    int SOCKET_READ_TIMEOUT = 15 * 1000;

    String ACTION_ONLINE = "ONLINE";
    String ACTION_OFFLINE = "OFFLINE";
    String ACTION_LOGIN_SUCCESS = "ACTION_LOGIN_SUCCESS";
    String ACTION_MUSIC_PLAY = "her.media.play";
    String ACTION_MUSIC_PAUSE = "her.media.pause";
    String ACTION_AUDIO_BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";

    String ACTION_BIND_STATUS = "kinstalk.com.aicore.action.txsdk.bind_status";
    String ACTION_BIND_EXTRA_STATUS = "bind_status";

    // collect song levelID and channelID
    int AUDIO_SONG_LEVELID = -1002;
    int AUDIO_SONG_CHANNELID = -1002;
    int AUDIO_NAME_ID = R.string.channel_audio;


    // collect song levelID and channelID
    int COLLECT_SONG_LEVELID = -1003;
    int COLLECT_SONG_CHANNELID = -1003;
    int COLLECT_NAME_ID = R.string.collect;


    int PLAYMODE_RANDOM = 0;
    int PLAYMODE_SINGLE_LOOP = 1;
    int PLAYMODE_ORDER = 2;
    int PLAYMODE_LOOP = 3;

    String ACTION_TXSDK_TTS = "kinstalk.com.aicore.action.txsdk.tts";
    String ACTION_TXSDK_EXTRA_TTS_STATE = "kinstalk.com.aicore.action.txsdk.tts_state";
    String ACTION_TXSDK_EXTRA_TTS_START = "start";
    String ACTION_TXSDK_EXTRA_TTS_STOP = "stop";

    String ACTION_MASTER_CLEAR = "action android.intent.action.MASTER_CLEAR";

    String ACTION_ASSISY_KEY = "com.kinstalk.action.assistkey";
}
