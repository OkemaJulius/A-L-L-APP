package com.kinstalk.her.audio.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.her.audio.service.AudioAIService;
import com.kinstalk.m4.publicaicore.constant.AIConstants;
import com.tencent.xiaowei.info.QLoveResponseInfo;

import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_EXTENDDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_REPDATA;
import static com.kinstalk.m4.publicaicore.constant.AIConstants.AIResultKey.KEY_VOICEID;

public class AIAudioReceiver extends BroadcastReceiver {

    public enum PlayerState {
        MUSIC_STATE_ONPAUSE,
        MUSIC_STATE_ONRESUME
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
//        QLoveResponseInfo responseInfo = intent.getParcelableExtra(AIConstants.AIResultKey.KEY_REPDATA);
//        Log.e("AIAudioReceiver", responseInfo.xwResponseInfo.toString());
//        AudioEntity currentPlayInfo = JsonUtil.getObject(responseInfo.xwResponseInfo.responseData, AudioEntity.class);

        String voiceId = intent.getStringExtra(AIConstants.AIResultKey.KEY_VOICEID);
        QLoveResponseInfo rspData = intent.getParcelableExtra(AIConstants.AIResultKey.KEY_REPDATA);
        byte[] extendData = intent.getByteArrayExtra(AIConstants.AIResultKey.KEY_EXTENDDATA);


        Intent intentS = new Intent(context, AudioAIService.class);
        intentS.putExtra(KEY_VOICEID, voiceId);
        intentS.putExtra(KEY_REPDATA, rspData);
        intentS.putExtra(KEY_EXTENDDATA, extendData);
        context.startService(intentS);

//            if (currentPlayInfo != null) {
//                session2CurPlayId.put(sessionId, currentPlayInfo.playId);
//                Bundle bundle = new Bundle();
//                bundle.putInt(EXTRA_KEY_MUSIC_ON_EVENT_SESSION_ID, sessionId);
//                sendBroadcast(ACTION_MUSIC_ON_PLAY, bundle);
//
//                getDetailInfoIfNeed(sessionInfo.skillName, sessionInfo.skillId, id2PlayInfo.get(currentPlayInfo.playId));
//
//                refreshPlayListIfNeed(sessionId, false);
//
//                // 预加载播放资源
//                ArrayList<String> playIdArray = session2PlayIdArray.get(sessionId);
//                int index = playIdArray == null ? -1 : playIdArray.indexOf(currentPlayInfo.playId);
//                if (index != -1 && index + 1 >= playIdArray.size()) {
//                    loadMorePlayList(sessionId);
//                }
//            }
//        String jsonResult = intent.getStringExtra(AIConstants.AIResultKey.KEY_RESULTJSON);
//        DebugUtil.LogV("AIAudioReceiver", "receiver : " + jsonResult);
//        try {
//            JSONObject jsonObject = new JSONObject(jsonResult);
//            String operation = jsonObject.optString("operation");
//            if (!TextUtils.isEmpty(operation)) {
//                if(TextUtils.equals(operation, "showFM")) {
//                    M4AudioActivity.actionStart(context);
//                } else if (TextUtils.equals(operation, "onInit")) {
//                    JSONObject dataObj = jsonObject.optJSONObject("data");
//                    if (null != dataObj) {
//                        String para2 = dataObj.optString("para2");
//                        JSONObject para2Obj = new JSONObject(para2);
//                        String playId = para2Obj.optString("playId");
//                        String playUrl = para2Obj.optString("res");
//                        int offset = para2Obj.optInt("offset");
//
//                        AudioEntity song = new AudioEntity();
//                        song.setPlayId(playId);
//                        song.setPlayUrl(playUrl);
//                        song.setOffset(offset);
//                        if (playUrl.toLowerCase().contains(".m3u8".toLowerCase())) {
//                            song.setLive(true);
//                        }
//                        song = PlayListDataSource.getInstance().addSongInfo(song);
//                        AudioPlayerController.getInstance().requestPlay(song);
//                    }
//                    Countly.sharedInstance().startEvent(CountlyConstant.T_FM_TIMED);
//                } else if (TextUtils.equals(operation, "onPlayBufferList")) {
//                    JSONObject dataObj = jsonObject.optJSONObject("data");
//                    int para1 = dataObj.optInt("para1");
//                    String para2 = dataObj.optString("para2");
//                    JSONObject para2Obj = new JSONObject(para2);
//                    boolean needClear = para2Obj.optBoolean("needClear");
//                    if(needClear) {
//                        PlayListDataSource.getInstance().clearPlayList();
//                    }
//                    JSONArray para2DataArray = para2Obj.optJSONArray("data");
//                    if (null != para2DataArray && para2DataArray.length() > 0) {
//                        String cacheAlbum = "";
//                        String cacheCover = "";
//                        for(int i=0; i<para2DataArray.length(); i++) {
//                            JSONObject songObj = para2DataArray.optJSONObject(i);
//                            AudioEntity song = new AudioEntity();
//                            song.setPlayId(songObj.optString("playId"));
//                            song.setPlayUrl(songObj.optString("content"));
//                            String tmpAlbum = songObj.optString("album");
//                            if(TextUtils.isEmpty(tmpAlbum)) {
//                                song.setAlbum(cacheAlbum);
//                            } else {
//                                song.setAlbum(tmpAlbum);
//                                cacheAlbum = tmpAlbum;
//                            }
//                            song.setArtist(songObj.optString("artist"));
//                            song.setName(songObj.optString("name"));
//                            String tmpCover = songObj.optString("cover");
//                            if(TextUtils.isEmpty(tmpCover)) {
//                                song.setCover(cacheCover);
//                            } else {
//                                song.setCover(tmpCover);
//                                cacheCover = tmpCover;
//                            }
//                            if (song.getPlayUrl().toLowerCase().contains(".m3u8".toLowerCase())) {
//                                song.setLive(true);
//                            }
//                            PlayListDataSource.getInstance().addSong(song);
//                        }
//                        if(3 == para1) {
//                            EventBus.getDefault().postSticky(PlayListDataSource.getInstance().getPlayList().get(0));
//                        } else {
//                            EventBus.getDefault().postSticky(PlayListDataSource.getInstance().getPlaySong());
//                        }
//                    }
//                } else if (TextUtils.equals(operation, "onPause") || TextUtils.equals(operation, "onStop")) {
//                    AudioPlayerController.getInstance().onReceivePauseCmd();
//                    HashMap<String, String> segment = new HashMap<>();
//                    AudioEntity playSong = PlayListDataSource.getInstance().getPlaySong();
//                    if(null != playSong) {
//                        if(!TextUtils.isEmpty(playSong.getAlbum())) {
//                            segment.put("album", playSong.getAlbum());
//                        }
//                        if(!TextUtils.isEmpty(playSong.getName())) {
//                            segment.put("name", playSong.getName());
//                        }
//                        if(!TextUtils.isEmpty(playSong.getArtist())) {
//                            segment.put("artist", playSong.getArtist());
//                        }
//                        Countly.sharedInstance().endEvent(CountlyConstant.T_FM_TIMED, segment, 1, 0);
//                    }
//                } else if (TextUtils.equals(operation, "onResume")) {
//                    AudioPlayerController.getInstance().onReceiveContineCmd();
//                    Countly.sharedInstance().startEvent(CountlyConstant.T_FM_TIMED);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
