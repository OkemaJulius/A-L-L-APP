package com.kinstalk.her.skillwiki.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.kinstalk.m4.publicapi.CoreApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;

public class Api {

    private static final String TAG = "WikiApi";
    public static String TEST_URL = "http://wx2.api.qspeaker.com/";//测试服务器2.0
    private static final String WX_CREDIT = "wx/v2/taskCredit";//獲取學分
    public static String sn;
    private static String sWifiMacAddress = "";

    /**
     * 完成任务，获取学分
     *
     * @param token
     * @param sn
     * @param id     任务编号
     * @param credit 任务学分
     * @param type   任务类型
     * @param action 传”got”
     */
    public static void postReceiveTask(String token, String sn, int id, int credit, int type, String action) {

        //发送请求获取响应
        Call call = OkhttpClientHelper.getOkHttpClient().newCall(createPostReceiveTask(token, sn, id, credit, type, action));
        Log.d(TAG, "post http");

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "onFailure: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse");
                if (response == null) {
                    Log.d(TAG, "postTask response empty");
                } else if (!response.isSuccessful()) {
                    Log.d(TAG, "postTask response failed" + response);
                } else {
                    if (!HttpHeaders.hasBody(response)) {
                        Log.d(TAG, "postTask rspBody empty");
                    } else {
                        //打印服务端返回结果
                        ResponseBody rspBody = response.body();
                        long contentLength = rspBody.contentLength();
                        if (contentLength != 0) {
                            String sBody = rspBody.string();
                            Log.d(TAG, "postReceiveTask body = : " + sBody);
                        }
                    }
                }
            }
        });
    }

    private static String getQloveSN() {
        String qlovesn = SystemPropertiesProxy.getString(CoreApplication.getApplicationInstance().getApplicationContext(),
                "ro.serialno");
        String qloveSn = TextUtils.isEmpty(qlovesn) ? "" : qlovesn;

        return qloveSn;
    }

    public static String getMacForSn() {
        String serialNum = getQloveSN();
        boolean isSnGot = false;
        if (!TextUtils.isEmpty(serialNum)) {
            QAILog.d(TAG, "getSn: serialNum = " + serialNum);
            if (serialNum.length() == 18) {
                sn = serialNum.substring(2, 18);
                isSnGot = true;
                QAILog.d(TAG, "getSn: sn = " + sn);
            } else {
                sn = "1234567890";
                QAILog.e(TAG, "getSn: wrong serial number");
            }
        } else {
            QAILog.e(TAG, "getSn: empty serial number ");
            String macAddr = getLocalMacAddress();
            if (!TextUtils.isEmpty(macAddr)) {
                QAILog.d(TAG, "getSn: mac = " + macAddr);
                if (macAddr.length() == 17) {
                    sn = macAddr.substring(0, 2) + macAddr.substring(3, 17);
                    isSnGot = true;
                    QAILog.d(TAG, "getSn: mac sn = " + sn);
                } else {
                    sn = "1234567890";
                    QAILog.e(TAG, "getSn: wrong mac Addr");
                }
            } else {
                sn = "1234567890";
                QAILog.e(TAG, "getSn: macAddr is empty");
            }
        }
        return sn;
    }

    /**
     * 获得mac
     *
     * @return
     */
    public static String getLocalMacAddress() {

        if (!TextUtils.isEmpty(sWifiMacAddress)) {
            return sWifiMacAddress;
        }

        String Mac = null;
        try {
            String path = "sys/class/net/wlan0/address";
            if ((new File(path)).exists()) {
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192];
                int byteCount = fis.read(buffer);
                if (byteCount > 0) {
                    Mac = new String(buffer, 0, byteCount, "utf-8");
                }
                fis.close();
            }

            if (Mac == null || Mac.length() == 0) {
                path = "sys/class/net/eth0/address";
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer_name = new byte[8192];
                int byteCount_name = fis.read(buffer_name);
                if (byteCount_name > 0) {
                    Mac = new String(buffer_name, 0, byteCount_name, "utf-8");
                }
                fis.close();
            }

            if (!TextUtils.isEmpty(Mac)) {
                Mac = Mac.substring(0, Mac.length() - 1);
            }
        } catch (Exception io) {
        }

        if (TextUtils.isEmpty(Mac)) {
            WifiManager wifiManager = (WifiManager) CoreApplication.getApplicationInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (wifiInfo.getMacAddress() != null) {
                Mac = wifiInfo.getMacAddress();
            }
        }

        QAILog.d(TAG, "wifi Mac = " + Mac);
        sWifiMacAddress = Mac;

        return TextUtils.isEmpty(Mac) ? "" : Mac;
    }

    private static Request createPostReceiveTask(String token, String sn, int id, int credit, int type, String action) {
        HttpUrl httpUrl = new Request.Builder()
                .url(TEST_URL)
                .build()
                .url()
                .newBuilder()
                .addEncodedPathSegment(WX_CREDIT)
                .addQueryParameter("token", token)
                .build();

        JSONObject reqJson = new JSONObject();
        try {
            reqJson.put("sn", sn);
            reqJson.put("action", action);
            reqJson.put("id", id);
            reqJson.put("credit", credit);
            reqJson.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "reqJsonError = " + e.getMessage());
        }
        String postInfoStr = reqJson.toString();
        Log.i(TAG, "token=" + token + "  sn=" + sn + "  postInfoStr=" + postInfoStr);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), postInfoStr);

        return new Request.Builder()
                .url(httpUrl)
                .post(requestBody)
                .build();
    }
}
