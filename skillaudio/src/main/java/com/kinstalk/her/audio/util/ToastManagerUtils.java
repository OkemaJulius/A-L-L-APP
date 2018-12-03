package com.kinstalk.her.audio.util;

import android.text.TextUtils;

import com.kinstalk.m4.publicapi.CoreApplication;
import com.kinstalk.m4.publicapi.view.Toasty.Toasty;


public class ToastManagerUtils {

    public static void showToastForce(String text) {
        Toasty.success(CoreApplication.getApplicationInstance(), text, true).show();
    }

    public static void showToastForceError(String text) {
        if (!TextUtils.isEmpty(text)) {
            Toasty.error(CoreApplication.getApplicationInstance(), text, true).show();
        }
    }
}
