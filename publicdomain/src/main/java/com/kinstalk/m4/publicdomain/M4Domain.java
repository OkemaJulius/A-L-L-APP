package com.kinstalk.m4.publicdomain;

import android.os.Build;

/**
 * Created by mamingzhang on 2018/2/2.
 */

public class M4Domain {
    //API接口
    public static String RequestHttpUrl;
    //Download接口
    public static String DownloadHttpUrl;
    //Upload接口
    public static String UploadHttpUrl;

    static {
        if (Build.TYPE.equals("user")) {
            // 生产环境
            RequestHttpUrl = "https://dragon-api.kinstalk.com";
            DownloadHttpUrl = "https://dragon-download.kinstalk.com";
            UploadHttpUrl = "https://dragon-upload.kinstalk.com";
        } else {
            // 测试环境
            RequestHttpUrl = "https://tiger-api.kinstalk.com";
            DownloadHttpUrl = "https://tiger-download.kinstalk.com";
            UploadHttpUrl = "https://tiger-upload.kinstalk.com";
        }
    }
}
