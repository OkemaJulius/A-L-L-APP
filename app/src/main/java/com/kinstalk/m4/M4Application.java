package com.kinstalk.m4;

import android.content.IntentFilter;
import android.os.PatternMatcher;
import android.support.v4.content.LocalBroadcastManager;

import com.kinstalk.her.audio.receiver.AIAudioReceiver;
import com.kinstalk.her.skillnews.components.receiver.AINewsReceiver;
import com.kinstalk.her.skillwiki.components.receiver.AIWikiReceiver;
import com.kinstalk.her.weather.manager.WeatherManager;
import com.kinstalk.her.weather.ui.receiver.NewAIWeatherReceiver;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.reminder.receiver.AIReminderReceiver;
import com.kinstalk.m4.reminder.util.DebugUtil;
import com.kinstalk.m4.skillmusic.ui.service.MusicAIReceiver;
import com.kinstalk.m4.skilltimer.receiver.AITimerReceiver;

import kinstalk.com.countly.CountlyUtils;

/**
 * Created by mamingzhang on 2018/2/1.
 */

public class M4Application extends CoreApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        AICoreManager.getInstance(this);

        WeatherManager.getInstance(this).init();

        registerLocalSkill();

        CountlyUtils.initCountly(this, DebugUtil.bDebug, !DebugUtil.bDebug);
    }

    private void registerLocalSkill() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        /**************************************ainew*******************************************/
        {
            //music
            MusicAIReceiver musicReceiver = new MusicAIReceiver();
            IntentFilter musicFilter = new IntentFilter("ai_new");
            musicFilter.addDataScheme("kinstalk");
            musicFilter.addDataSchemeSpecificPart("//music", PatternMatcher.PATTERN_LITERAL);
            localBroadcastManager.registerReceiver(musicReceiver, musicFilter);

            //News
            AINewsReceiver newsReceiver = new AINewsReceiver();
            IntentFilter newsFilter = new IntentFilter("ai_new");
            newsFilter.addDataScheme("kinstalk");
            newsFilter.addDataSchemeSpecificPart("//news", PatternMatcher.PATTERN_LITERAL);
            localBroadcastManager.registerReceiver(newsReceiver, newsFilter);

            //fm
            AIAudioReceiver fmReceiver = new AIAudioReceiver();
            IntentFilter fmFilter = new IntentFilter("ai_new");
            fmFilter.addDataScheme("kinstalk");
            fmFilter.addDataSchemeSpecificPart("//fm", PatternMatcher.PATTERN_LITERAL);
            localBroadcastManager.registerReceiver(fmReceiver, fmFilter);

            //timer
            AITimerReceiver timerReceiver = new AITimerReceiver();
            IntentFilter timerFilter = new IntentFilter("ai");
            timerFilter.addDataScheme("kinstalk");
            timerFilter.addDataSchemeSpecificPart("//timer", PatternMatcher.PATTERN_LITERAL);
            localBroadcastManager.registerReceiver(timerReceiver, timerFilter);

            //Wiki
            AIWikiReceiver wikiReceiver = new AIWikiReceiver();
            IntentFilter wikiFilter = new IntentFilter("ai_new");
            wikiFilter.addDataScheme("kinstalk");
            wikiFilter.addDataSchemeSpecificPart("//wiki", PatternMatcher.PATTERN_LITERAL);
            localBroadcastManager.registerReceiver(wikiReceiver, wikiFilter);

            //weather
            NewAIWeatherReceiver weatherReceiver = new NewAIWeatherReceiver();
            IntentFilter weatherFilter = new IntentFilter("ai_new");
            weatherFilter.addDataScheme("kinstalk");
            weatherFilter.addDataSchemeSpecificPart("//weather", PatternMatcher.PATTERN_LITERAL);
            localBroadcastManager.registerReceiver(weatherReceiver, weatherFilter);

            //reminder
            AIReminderReceiver reminderReceiver = new AIReminderReceiver();
            IntentFilter reminderFilter = new IntentFilter("ai_new");
            reminderFilter.addDataScheme("kinstalk");
            reminderFilter.addDataSchemeSpecificPart("//schedule", PatternMatcher.PATTERN_LITERAL);
            localBroadcastManager.registerReceiver(reminderReceiver, reminderFilter);
        }
    }
}
