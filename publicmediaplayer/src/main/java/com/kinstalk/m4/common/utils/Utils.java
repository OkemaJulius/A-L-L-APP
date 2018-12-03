package com.kinstalk.m4.common.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.view.View;
import android.view.WindowManager;

import com.kinstalk.m4.common.constant.Constants;
import com.kinstalk.m4.publicapi.CoreApplication;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ly.count.android.sdk.Countly;

import static android.content.pm.PackageManager.GET_META_DATA;

public class Utils {
    private static final String TAG = "Music.Utils";
    private static final String KEY_PROCESS_PID = "KEY_PROCESS_PID";
    public static final String STICKY_BOOT_COMPLETE_BROADCAST = "com.kinstalk.m4.music.STICKY_BOOT_COMPLETE";
    private static final String PRELOAD_MEDIA_FOLDER = "/system/media/music/";

    private static int mWidth = 0;
    private static int mHeight = 0;

    private static String s_appVersion = null;

    public static final int NET_NOT_AVAILABLE = 0;
    public static final int NET_WIFI = 1;
    public static final int NET_PROXY = 2;
    public static final int NET_NORMAL = 3;
    private static final String NET_TYPE_WIFI = "WIFI";
    private static long lastClickTime;
    /**
     * 网络类型, 以下的几个方法都是根据TuixinService1中的广播设置的值处理网络连接的
     */
    private volatile static int networkType = NET_NOT_AVAILABLE;


    public static String getProp(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            QLog.e(TAG, e, "getProp: error!");
        }
        QLog.d(TAG, "getProp: key - %s, value - %s", key, value);
        return value;
    }

    public static boolean getProp(String key, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("getBoolean", String.class, boolean.class);
            value = (boolean) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            QLog.e(TAG, e, "getProp: error!");
        }
        QLog.d(TAG, "getProp: key - %s, value - %s", key, value);
        return value;
    }

    public static int getProp(String key, int defaultValue) {
        int value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("getInt", String.class, int.class);
            value = (int) (get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            QLog.e(TAG, e, "getProp: error!");
        }
        QLog.d(TAG, "getProp: key - %s, value - %s", key, value);
        return value;
    }

    public static String dump(String[] args) {
        if (args == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String arg : args
                ) {
            sb.append(arg);
            sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    public static int randomInt(int min, int max, int exclude) {
        QLog.d(TAG, "randomInt: min - %d, max - %d, exclude - %d", min, max, exclude);
        if (max <= min) {
            QLog.w(TAG, "randomSet: wrong parameter!");
            throw new AndroidRuntimeException("wrong parameter!");
        }
        boolean shouldExclude = false;
        if (exclude >= min && exclude < max && (max - min) > 1) {
            max = max - 1;
            shouldExclude = true;
        }
        int value = (int) (Math.random() * (max - min)) + min;
        if (shouldExclude && value >= exclude) {
            value = value + 1;
        }
        return value;
    }

    public static void randomSet(int min, int max, int n, Collection<Integer> include,
                                 List<Integer> result) {
        QLog.d(TAG, "randomSet: min - %d, max - %d, n - %d, result - " + result, min, max, n);
        if (n <= 0 || n > (max - min) || max <= min || result == null) {
            QLog.w(TAG, "randomSet: wrong parameter!");
            return;
        }
        // It's a full list with all element in [min, max), we random move element
        // in this list to result collection to make result collect random.
        ArrayList<Integer> tempList = new ArrayList<>(max - min);
        for (int i = min; i < (max); i++) {
            tempList.add(i);
        }
        if (include != null) {
            QLog.d(TAG, "randomSet: add include - " + include);
            Iterator<Integer> iterator = include.iterator();
            while (iterator != null && iterator.hasNext()) {
                Integer item = iterator.next();
                if (item == null || item.intValue() < min || item.intValue() >= max) {
                    QLog.d(TAG, "randomSet: remove invalid include item - " + item);
                    iterator.remove();
                }
            }
            result.addAll(include);
            tempList.removeAll(include);
        }
        while (result.size() < n) {
            int tempListIndex = (int) (Math.random() * (tempList.size()));
            result.add(tempList.get(tempListIndex));
            tempList.remove(tempListIndex);
        }
        QLog.d(TAG, "randomSet: final result - " + result);
    }

    public static void sendStickyBootCompleteBroadcast(Context context) {
        QLog.d(TAG, "sendStickyBootCompleteBroadcast: " + STICKY_BOOT_COMPLETE_BROADCAST);
        Intent i = new Intent(STICKY_BOOT_COMPLETE_BROADCAST);
        context.sendStickyBroadcast(i);
    }

    public static void recordProcessPid(Context context, final int pid) {
        QLog.d(TAG, "recordProcessPid: " + pid);
        PersistHelper.saveString(context, KEY_PROCESS_PID, String.valueOf(pid));
    }

    public static int getRecordedProcessPid(Context context) {
        final String pidStr = PersistHelper.getString(context, KEY_PROCESS_PID, "0");
        int pid = 0;
        try {
            pid = Integer.parseInt(pidStr);
        } catch (Exception e) {
        }

        return pid;
    }

    public static <T> T safeArrayGet(T[] array, int index, T defValue) {
        if (array == null || array.length <= index || index < 0) {
            QLog.d(TAG, "safeArrayGet: parameter invalid");
            return defValue;
        }
        try {
            return array[index];
        } catch (Exception e) {
            QLog.e(TAG, e, "safeArrayGet: error");
        }
        return defValue;
    }

    public static <T> T safeArrayListGet(List<T> list, int index, T defValue) {
        if (list == null || list.size() <= index || index < 0) {
            QLog.d(TAG, "safeArrayListGet: parameter invalid");
            return defValue;
        }
        try {
            return list.get(index);
        } catch (Exception e) {
            QLog.e(TAG, e, "safeArrayGet: error");
        }
        return defValue;
    }

    public static <T> List<T> safeAsList(T[] array) {
        if (array == null) {
            return null;
        }
        return Arrays.asList(array);
    }

    public static String numbersToString(List<? extends Number> ints) {
        QLog.w(TAG, "numbersToString: ints - " + ints);
        if (ints == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(ints.size() * 7);
        Iterator<? extends Number> iterator = ints.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next());
            sb.append(",");
        }
        return sb.toString();
    }

    public static <T> List<T> rearrangeList(List<T> array, T key) {
        ArrayList<T> result = new ArrayList<>();
        if (array == null || array.size() <= 0) {
            result.add(key);
            return result;
        }
        int index = array.indexOf(key);

        if (index >= 0 && index < array.size()) {
            result.addAll(array.subList(index, array.size()));
            result.addAll(array.subList(0, index));
        } else {
            result.add(key);
            result.addAll(array);
        }
        return result;
    }

    public static String safeLogString(String info) {
        if (info == null) {
            return "null";
        } else if (TextUtils.isEmpty(info)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(info.length());
        for (int i = 0; i < info.length(); i++) {
            sb.append("*");
        }
        return sb.toString();
    }

    public static void safeClose(Closeable o) {
        if (o != null) {
            try {
                o.close();
            } catch (Exception e) {
                QLog.e(TAG, e, "safeClose: ignore");
            }
        }
    }

    public static boolean contentEquals(final File file1, final File file2) {
        try {
            final boolean file1Exists = file1.exists();
            if (file1Exists != file2.exists()) {
                return false;
            }

            if (!file1Exists) {
                // two not existing files are equal
                return true;
            }

            if (file1.isDirectory() || file2.isDirectory()) {
                // don't want to compare directory contents
                throw new IOException("Can't compare directories, only files");
            }

            if (file1.length() != file2.length()) {
                // lengths differ, cannot be equal
                return false;
            }

            if (file1.getCanonicalFile().equals(file2.getCanonicalFile())) {
                // same file
                return true;
            }

            InputStream input1 = null;
            InputStream input2 = null;
            try {
                input1 = new FileInputStream(file1);
                input2 = new FileInputStream(file2);
                return contentEquals(input1, input2);

            } finally {
                safeClose(input1);
                safeClose(input2);
            }
        } catch (Exception e) {
            QLog.e(TAG, e, "contentEquals: ignore");
        }
        return false;
    }

    public static boolean contentEquals(InputStream input1, InputStream input2)
            throws IOException {
        if (input1 == input2) {
            return true;
        }
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }

        int ch = input1.read();
        while (Constants.EOF != ch) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
            ch = input1.read();
        }

        final int ch2 = input2.read();
        return ch2 == Constants.EOF;
    }

    private static final long FILE_COPY_BUFFER_SIZE = 1024 * 30;

    public static void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate)
            throws IOException {
        if (destFile.exists() && destFile.isDirectory()) {
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel input = null;
        FileChannel output = null;
        try {
            fis = new FileInputStream(srcFile);
            fos = new FileOutputStream(destFile);
            input = fis.getChannel();
            output = fos.getChannel();
            final long size = input.size(); // TODO See IO-386
            long pos = 0;
            long count = 0;
            while (pos < size) {
                final long remain = size - pos;
                count = remain > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : remain;
                final long bytesCopied = output.transferFrom(input, pos, count);
                if (bytesCopied == 0) { // IO-385 - can happen if file is truncated after caching the size
                    break; // ensure we don't loop forever
                }
                pos += bytesCopied;
            }
        } finally {
            safeClose(output);
            safeClose(fos);
            safeClose(input);
            safeClose(fis);
        }

        final long srcLen = srcFile.length(); // TODO See IO-386
        final long dstLen = destFile.length(); // TODO See IO-386
        if (srcLen != dstLen) {
            throw new IOException("Failed to copy full contents from '" +
                    srcFile + "' to '" + destFile + "' Expected length: " + srcLen + " Actual: " + dstLen);
        }
        if (preserveFileDate) {
            destFile.setLastModified(srcFile.lastModified());
        }
    }

    public static String[] copyDirectory(String srcDir, String dstDir, boolean replaceExisted) {
        if (srcDir == null || dstDir == null) {
            QLog.w(TAG, "copyDirectory: null srcDir ot dstFir");
            return null;
        }

        List<String> copiedList = new LinkedList<String>();
        File[] files = new File(srcDir).listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    String filename = file.getName();
                    File destFile = null;
                    try {
                        destFile = new File(dstDir, filename);
                        if (replaceExisted || !destFile.exists() || destFile.length() != file.length()) {
                            copyFile(file, destFile, false);
                            copiedList.add(destFile.getAbsolutePath());
                            QLog.d(TAG, "copying " + file + " to " + destFile);
                        } else {
                            QLog.d(TAG, "File " + destFile + " already exists, no copy here.");
                        }
                    } catch (Exception e) {
                        QLog.w(TAG, "Failed to copy file - " + destFile);
                    }
                }
            }
        }

        return copiedList.isEmpty() ? null : copiedList.toArray(new String[0]);
    }

    public static void triggerMediaScan(Context context, String[] paths) {
        QLog.d(TAG, "Trigger media scan for - " + Arrays.toString(paths));
        if (paths == null || paths.length == 0) {
            QLog.w(TAG, "Invalid paths!");
            return;
        }

        MediaScannerConnection.scanFile(
                context,
                paths, //paths
                null, //mimeTypes
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        QLog.v(TAG, "MediaScanner completed: path - " + path + ", uri - " + uri);
                    }
                }
        );
    }

    public static void asyncCopyInternalMusicToSdcard(final Context context, final boolean replaceExisted) {
        QLog.v(TAG, "asyncCopyInternalMusicToSdcard - " + replaceExisted);
        try {
            final String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    .getAbsolutePath();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String[] files = copyDirectory(PRELOAD_MEDIA_FOLDER, destPath, replaceExisted);
                    if (files != null) {
                        triggerMediaScan(context, files);
                    } else
                        QLog.d(TAG, "No media scan here as no music file copied.");
                }
            }).start();
        } catch (Exception e) {
            //ignore
        }
    }

    public static String getAppVersion(Context context) {
        if (s_appVersion == null) {
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
                        context.getPackageName(), GET_META_DATA);
                s_appVersion = ai.metaData.getString(Constants.PRIVATE_VER_KEY);
            } catch (Exception e) {
                QLog.e(TAG, e, "Fail to get version Name");
            }
        }
        return TextUtils.isEmpty(s_appVersion) ? Constants.UNKNOWN : s_appVersion;
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(10);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (TextUtils.equals("kinstalk.com.wateranimapp.MainActivity", cpn.getClassName())) {
                if (list.size() > 1) {
                    cpn = list.get(1).topActivity;
                    return className.equals(cpn.getClassName());
                }
            } else {
                return className.equals(cpn.getClassName());
            }
        }

        return false;
    }


    /**
     * 判断网络连接是否可用
     */
    public static boolean checkNetworkAvailable() {
        int type = getNetworkType(CoreApplication.getApplicationInstance());
        return !(type == NET_NOT_AVAILABLE || type == NET_PROXY);
    }

    /**
     * 获取网络连接类型
     */
    public synchronized static int getNetworkType(Context inContext) {
        Context context = inContext.getApplicationContext();
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        if (networkinfo == null || !networkinfo.isAvailable()) {
            // 当前网络不可用
            networkType = NET_NOT_AVAILABLE;
        } else {
            // 如果当前是WIFI连接
            if (NET_TYPE_WIFI.equals(networkinfo.getTypeName())) {
                networkType = NET_WIFI;
            }
            // 非WIFI联网
            else {
                String proxyHost = android.net.Proxy.getDefaultHost();
                // 代理模式
                if (proxyHost != null) {
                    networkType = NET_PROXY;
                }
                // 直连模式
                else {
                    networkType = NET_NORMAL;
                }
            }
        }
        return networkType;
    }

    public static void countlyStartEvent(String key) {
        QLog.d("MusicAI", "countlyStartEvent key:" + key);
        Countly.sharedInstance().startEvent("music", key);
    }

    public static void countlyEndEvent(final String key, final Map<String, String> segmentation, final int count, final double sum) {
        QLog.d("MusicAI", "countlyEndEvent key:" + key);
        Countly.sharedInstance().endEvent("music", key, segmentation, 1, 0);
    }

    public static void countlyRecordEvent(String key, int count) {
        QLog.d("MusicAI", "countlyRecordEvent key:" + key);
        Countly.sharedInstance().recordEvent("music", key, count);
    }

    public static void countlyRecordEvent(String key, Map<String, String> segmentation, int count) {
        QLog.d("MusicAI", "countlyRecordEvent key:" + key + ",segmentation:" + segmentation);
        Countly.sharedInstance().recordEvent("music", key, segmentation, count);
    }

    public static String getTimeFormat4Hsm2(long time) {
        String timeStr = "00:00";
        time /= 1000;
        int minute = (int) (time / 60);
        int hour = minute / 60;
        int second = (int) (time % 60);
        minute %= 60;
        if (hour == 0) {
            timeStr = String.format("%02d:%02d", minute, second);
        } else {
            timeStr = String.format("%02d:%02d:%02d", hour, minute, second);
        }
        return timeStr;
    }

    public static Bitmap getImageFromNet(String btp) {
        HttpURLConnection conn = null;
        try {
            URL myUri = new URL(btp); // 创建URL对象
            // 创建链接
            conn = (HttpURLConnection) myUri.openConnection();
            conn.setConnectTimeout(10000);// 设置链接超时
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");// 设置请求方法为get
            conn.connect();// 开始连接
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                InputStream is = conn.getInputStream();
                // 根据流数据创建 一个Bitmap位图对象
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                return bitmap;
                // 访问成功
            } else {
                QLog.w(TAG, "访问失败：responseCode=" + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();

            }
        }
        return null;
    }

    public static int getScreenWidth() {
        if (0 == mWidth) {
            mWidth = ((WindowManager) CoreApplication.getApplicationInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getWidth();
        }
        return mWidth;
    }

    public static int getScreenHeight() {
        if (0 == mHeight) {
            mHeight = ((WindowManager) CoreApplication.getApplicationInstance().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                    .getHeight();
        }
        return mHeight;
    }

    public static <T> void printListInfo(String tag, String message, ArrayList<T> list) {
        if (QLog.isLoggable()) {
            if (list != null && !list.isEmpty()) {
                QLog.i(tag, "-----------printListInfo start  " + message + " -----------");
                for (int i = 0; i < list.size(); i++) {
                    QLog.i(tag, i + ":" + list.get(i));
                }
                QLog.i(tag, "-----------printListInfo end " + message + "-----------");
            } else {
                QLog.i(tag, "printListInfo empty!!");
            }
        }
    }

    public static void updateAnimation(Context context, String resName, int max, View view, int duration) {
        view.setBackground(null);
        final AnimationDrawable mAnimationDrawableBg = new AnimationDrawable();
        for (int i = 1; i <= max; i++) {

            int resId = context.getResources().getIdentifier(resName + (i > 9 ? i : "0" + i), "mipmap", context.getPackageName());
            mAnimationDrawableBg.addFrame(context.getResources().getDrawable(resId), duration);
        }
        mAnimationDrawableBg.setOneShot(false);

        view.setBackground(mAnimationDrawableBg);
        mAnimationDrawableBg.start();

        QLog.d("SongListAdapter", "updateAnimation succeed");
    }

    public static int dip2px(float dpValue) {
        final float scale = CoreApplication.getApplicationInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getDimen(int resId) {
        if (resId > 0) {
            return CoreApplication.getApplicationInstance().getResources().getDimensionPixelSize(resId);
        }
        return 0;
    }

    public static String getString(int resId) {
        if (resId > 0) {
            return CoreApplication.getApplicationInstance().getResources().getString(resId);
        }
        return "";
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {       //500毫秒内按钮无效，这样可以控制快速点击，自己调整频率
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static String htmlReplace(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        str = str.replace("&apos;", "'");
        str = str.replace("&ldquo;", "“");
        str = str.replace("&rdquo;", "”");
        str = str.replace("&nbsp;", " ");
        str = str.replace("&amp;", "&");
        str = str.replace("&#39;", "'");
        str = str.replace("&rsquo;", "’");
        str = str.replace("&mdash;", "—");
        str = str.replace("&ndash;", "–");
        return str;
    }

    public static void autoBackLauncher(Activity activity, boolean isBack) {
        QLog.d(TAG, "autoBackLauncher isBack:" + isBack);
        WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
        try {
            Class<WindowManager.LayoutParams> attrClass = WindowManager.LayoutParams.class;
            Method method = attrClass.getMethod("setAutoActivityTimeout", boolean.class);
            method.setAccessible(true);
            Object object = method.invoke(attr, isBack);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        activity.getWindow().setAttributes(attr);
    }

    public static String getUserVipFlagTime(long time) {
        Date date = new Date(time);
        DateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        return format.format(date);
    }
}
