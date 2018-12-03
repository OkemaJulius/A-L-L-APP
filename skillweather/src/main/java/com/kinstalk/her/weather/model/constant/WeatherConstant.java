package com.kinstalk.her.weather.model.constant;

/**
 * Created by siqing on 17/6/13.
 */

public interface WeatherConstant {

    String TAG = "WeatherApp";

    String CIPHER = "c40eb83737f90aa59cbc3e81b85d2d59";

    String ROW = "qinjian";

    String DU = "°";


    /**
     * 当前气温页面
     */
    int FRAGMENT_TYPE_TEMPCURRENT = 1001;

    /**
     * 最高最低温度页面
     */
    int FRAGMENT_TYPE_TEMPBASEINFO = 1002;

    /**
     * 趋势页面
     */
    int FRAGMENT_TYPE_TEMPCHART = 1003;

    /**
     * 天气详情页面
     */
    int FRAGMENT_TYPE_TEMPDETAIL = 1004;


    /**
     * 手动点击和AI询问今天天气的页面组成
     */
    int[] PAGE_TODAY_ARGS = new int[]{FRAGMENT_TYPE_TEMPCURRENT, FRAGMENT_TYPE_TEMPBASEINFO, FRAGMENT_TYPE_TEMPCHART, FRAGMENT_TYPE_TEMPDETAIL};


    /**
     * AI询问非今天天气页面组成
     */
    int[] PAGE_NOT_TODAY = new int[]{FRAGMENT_TYPE_TEMPBASEINFO, FRAGMENT_TYPE_TEMPCHART};

    /**
     * chart线的宽度dp
     */
    int CHART_LINE_WIDTH = 10;
}
