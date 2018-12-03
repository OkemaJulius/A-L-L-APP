package com.kinstalk.m4.publicownerlib;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by mamingzhang on 2017/4/20.
 */

public class OwnerProviderLib {

    private Context mAppContext;

    private Owner mOwner;
    private Location mLocation;

    private static OwnerProviderLib sInstance;

    private OwnerProviderLib(Context context) {
        mAppContext = context.getApplicationContext();
        mOwner = new Owner();
        mLocation = new Location();

        loadOwner();
        loadLocation();
        registerContentObserver();
    }

    public static OwnerProviderLib getInstance(Context context) {
        if (sInstance == null) {
            synchronized (OwnerProviderLib.class) {
                if (sInstance == null) {
                    sInstance = new OwnerProviderLib(context);
                }
            }
        }

        return sInstance;
    }

    /**
     * 返回用户信息，包括Token等
     *
     * @return
     */
    public Owner getOwner() {
        return mOwner;
    }

    /**
     * 返回地理位置信息
     *
     * @return
     */
    public Location getLocation() {
        return mLocation;
    }

    /**
     * 用户Uid
     *
     * @return
     */
    public long getUid() {
        return mOwner.getUid();
    }

    /**
     * 用户UserCode
     *
     * @return
     */
    public String getUserCode() {
        return mOwner.getUserCode();
    }

    /**
     * 用户Token
     *
     * @return
     */
    public String getToken() {
        return mOwner.getToken();
    }

    /**
     * 设备号
     *
     * @return
     */
    public String getDeviceId() {
        return mOwner.getDeviceId();
    }

    private void registerContentObserver() {
        mAppContext.getContentResolver().registerContentObserver(OwnerUri.OWNER_URI, true, new OwnerContentObserver(new Handler(Looper.getMainLooper())));
        mAppContext.getContentResolver().registerContentObserver(OwnerUri.LOCATION_URI, true, new LocationContentObserver(new Handler(Looper.getMainLooper())));
    }

    private void loadOwner() {
        Cursor cursor = null;
        try {
            cursor = mAppContext.getContentResolver().query(OwnerUri.OWNER_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mOwner.setUid(cursor.getLong(cursor.getColumnIndex(OwnerColumn.UID)));
                mOwner.setUserCode(cursor.getString(cursor.getColumnIndex(OwnerColumn.USERCODE)));
                mOwner.setDeviceId(cursor.getString(cursor.getColumnIndex(OwnerColumn.DEVICEID)));
                mOwner.setToken(cursor.getString(cursor.getColumnIndex(OwnerColumn.ACCESSTOKEN)));
                if (cursor.getColumnIndex(OwnerColumn.DUDUAPPID) >= 0) {
                    mOwner.setDuduAppId(cursor.getString(cursor.getColumnIndex(OwnerColumn.DUDUAPPID)));
                    mOwner.setDuduVoIPAccount(cursor.getString(cursor.getColumnIndex(OwnerColumn.DUDUVOIPACCOUNT)));
                    mOwner.setDuduVoIPPwd(cursor.getString(cursor.getColumnIndex(OwnerColumn.DUDUVOIPPWD)));
                }
                if (cursor.getColumnIndex(OwnerColumn.MERCHANTID) >= 0) {
                    mOwner.setMerchantId(cursor.getString(cursor.getColumnIndex(OwnerColumn.MERCHANTID)));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void loadLocation() {
        Cursor cursor = null;
        try {
            cursor = mAppContext.getContentResolver().query(OwnerUri.LOCATION_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                mLocation.setProvince(cursor.getString(cursor.getColumnIndex(LocationColumn.LOCATIONPROVINCE)));
                mLocation.setCity(cursor.getString(cursor.getColumnIndex(LocationColumn.LOCATIONCITY)));
                mLocation.setDistrict(cursor.getString(cursor.getColumnIndex(LocationColumn.LOCATIONDISTRICT)));
                mLocation.setAddress(cursor.getString(cursor.getColumnIndex(LocationColumn.LOCATIONADDRESS)));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private class OwnerContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public OwnerContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            loadOwner();
        }
    }

    private class LocationContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public LocationContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            loadLocation();
        }
    }
}
