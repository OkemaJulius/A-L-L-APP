package com.kinstalk.m4.common.utils;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

public class ToastUtil {

    private static WeakReference<Toast> sAliveToastRef;

    private static Toast getPotentialAliveToast() {
        return sAliveToastRef != null ? sAliveToastRef.get() : null;
    }

    private static void cancelPreviousToast() {
        final Toast toast = getPotentialAliveToast();
        if (toast != null)
            toast.cancel();
    }

    public static void showToast(Context context, String info) {
        showToast(context, info, true);
    }

    public static void showToast(Context context, String info, boolean important) {
        cancelPreviousToast();
        final Toast toast = makeToast(context, important, info, Toast.LENGTH_SHORT);
        if (toast != null) {
            sAliveToastRef = new WeakReference<Toast>(toast);
            toast.show();
        }
    }

    private static synchronized Toast makeToast(
            Context context,
            boolean important,
            CharSequence text,
            int duration) {
        return QToast.makeText(
                context,
                text,
                important ? QToast.Type.IMPORTANT : QToast.Type.FINE,
                duration
        );
    }
}
