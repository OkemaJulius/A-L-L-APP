package com.kinstalk.m4.publicmediaplayer.resource;


import com.kinstalk.m4.common.utils.QLog;

/**
 * Created by libin on 2016/9/29.
 */

public enum DataLoadResultCode {
    RESULT_OK,
    ERROR_UNSPECIFIED,
    ERROR_WRONG_PARAMETER,
    ERROR_NO_DATA,
    ERROR_NO_NETWORK,
    ERROR_NETWORK_ASK,
    ERROR_NO_SERVER_URL,
    ERROR_SERVER_ERROR,
    ERROR_NO_TOKEN;

    public static DataLoadResultCode fromInt(int i) {
        int length = DataLoadResultCode.values().length;
        if (i >= 0 && i < length) {
            return DataLoadResultCode.values()[i];
        }
        return ERROR_UNSPECIFIED;
    }

    public boolean allowLoadMore() {
        QLog.d(this, "allowLoadMore: state - " + this);
        return !isError() || this == ERROR_UNSPECIFIED;
    }

    public boolean isError() {
        QLog.d(this, "isError: state - " + this);
        return this != RESULT_OK;
    }
}
