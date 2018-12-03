package com.kinstalk.m4;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.kinstalk.m4.publicaicore.AICoreManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        AICoreManager.getInstance(context);
    }
}
