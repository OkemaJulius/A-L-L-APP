package com.kinstalk.m4.publicapi.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


public class M4BaseAudioActivity extends M4BaseActivity {

    public static final String ACTION_ASSISY_KEY = "com.kinstalk.action.assistkey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterPowerKeyReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerPowerKeyReceiver();
    }

    private void registerPowerKeyReceiver() {
        IntentFilter intentFilter = new IntentFilter(ACTION_ASSISY_KEY);
        registerReceiver(powerKeyReceiver, intentFilter);
    }

    private void unRegisterPowerKeyReceiver() {
        unregisterReceiver(powerKeyReceiver);
    }

    private BroadcastReceiver powerKeyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_ASSISY_KEY)) {
                receiveAssistkeyPressedMsg();
            }
        }
    };

    protected void receiveAssistkeyPressedMsg() {
        //noting todo
    }
}
