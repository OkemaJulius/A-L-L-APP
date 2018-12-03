package com.kinstalk.her.weather.model.helper;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.kinstalk.her.weather.model.entity.AIResult;
import com.kinstalk.her.weather.model.entity.WeatherForecastInfoEntity;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.kinstalk.her.weather.ui.utils.DateUtils;
import com.kinstalk.her.weather.ui.utils.SourceUtils;
import com.tencent.xiaowei.info.QLoveResponseInfo;
import com.tencent.xiaowei.info.XWResGroupInfo;
import com.tencent.xiaowei.info.XWResourceInfo;
import com.tencent.xiaowei.info.XWResponseInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siqing on 2018/3/7.
 */

public class WeatherAIDataHelper {

    private AIResult aiResult;

    public WeatherAIDataHelper() {

    }

    public void dealData(String intentData) {
        Gson gson = new Gson();
        aiResult = gson.fromJson(intentData, AIResult.class);
    }

    public AIResult getAIResult() {
        return aiResult;
    }

    /**
     * 获取AI语音询问的天气
     *
     * @return
     */
    public AIResult.DataBean.ResultBean getAskWeather() {
        if (isAIPage()) {
            List<AIResult.DataBean.ResultBean> resultList = aiResult.getData().getResult();
            for (AIResult.DataBean.ResultBean result : resultList) {
                if ("1".equals(result.getIs_asked())) {
                    return result;
                }
            }
        }
        return new AIResult.DataBean.ResultBean();
    }

    public boolean isAIPage() {
        return aiResult != null;
    }

    /**
     * 是否是今天
     *
     * @return
     */
    public boolean isTodayWeather() {
        return DateUtils.getTodayDateStr().equals(getAskWeather().getDate());
    }

    public String getAskHighTemp() {
        return getAskWeather().getMaxTemp();
    }

    public String getAskLowTemp() {
        return getAskWeather().getMinTemp();
    }

    public String getAskCurTemp() {
        return getAskWeather().getCurTemp();
    }

    public int getAskWeatherImageSourceId() {
        return SourceUtils.getImageResByKey(getAskWeather().getWeather());
    }

    public WeatherInfoEntity parseWeatherInfo() {
        WeatherInfoEntity weatherInfoEntity = new WeatherInfoEntity();
        try {
            List<AIResult.DataBean.ResultBean> weatherList = aiResult.getData().getResult();

            //TODO 遍历设置预告天气和询问天气
            List<WeatherForecastInfoEntity> forecastInfoEntityList = new ArrayList<>();
            boolean isStart = false;
            for (int i = 0; i < weatherList.size(); i++) {
                AIResult.DataBean.ResultBean weatherInfo = weatherList.get(i);
                if (DateUtils.getTodayDateStr().equals(weatherInfo.getDate())) {
                    isStart = true;
                }

                if ("1".equals(weatherInfo.getIs_asked())) {
                    //TODO 设置询问天气数据
                    weatherInfoEntity.setAirQuality(weatherInfo.getAirQuality());
                    weatherInfoEntity.setCity(weatherInfo.getCity());
                    weatherInfoEntity.setCityName(weatherInfo.getCity());
                    weatherInfoEntity.setTemperature(weatherInfo.getCurTemp());
                    if (!TextUtils.isEmpty(weatherInfo.getWind())) {
                        weatherInfoEntity.setWindDirection(weatherInfo.getWind());
                    }
                    if (!TextUtils.isEmpty(weatherInfo.getWind_lv())) {
                        weatherInfoEntity.setWindScale(weatherInfo.getWind_lv());
                    }
                    weatherInfoEntity.setWindSpeed(weatherInfo.getWind_lv());
                    weatherInfoEntity.setPm25(weatherInfo.getPm25());
                    weatherInfoEntity.setAirQuality(weatherInfo.getAirQuality());
                    weatherInfoEntity.setText(weatherInfo.getWeather());
                }

                if (isStart) {
                    WeatherForecastInfoEntity forecastInfoEntity = new WeatherForecastInfoEntity();
                    forecastInfoEntity.setLow(weatherInfo.getMinTemp());
                    forecastInfoEntity.setHigh(weatherInfo.getMaxTemp());
                    forecastInfoEntity.setText1(weatherInfo.getWeather());
                    forecastInfoEntity.setText2(weatherInfo.getWeather());
                    forecastInfoEntity.setDay(weatherInfo.getDate());
                    forecastInfoEntityList.add(forecastInfoEntity);
                    if (forecastInfoEntityList.size() >= 5) {
                        break;
                    }
                }
            }
            weatherInfoEntity.setForecast(forecastInfoEntityList);
        } catch (Exception e) {
            weatherInfoEntity.setErrorMsg("数据解析异常");
        }
        return weatherInfoEntity;
    }

    public static AIResult adapter(QLoveResponseInfo aiRspInfo) {
        AIResult aiResult = new AIResult();
        XWResponseInfo xwResponseInfo = aiRspInfo.xwResponseInfo;
        if (xwResponseInfo == null) {
            return null;
        }
        aiResult.setCode(xwResponseInfo.resultCode);
        aiResult.setService(aiRspInfo.qServiceType);
        aiResult.setVoiceID(xwResponseInfo.voiceID);
        AIResult.AnswerBean answer = new AIResult.AnswerBean();

        for (XWResGroupInfo xwResGroupInfo : xwResponseInfo.resources) {
            if (xwResGroupInfo != null) {
                for (XWResourceInfo xwResourceInfo : xwResGroupInfo.resources) {
                    if (xwResourceInfo != null) {
                        answer.setText(xwResourceInfo.content);
                        answer.setFormat(xwResourceInfo.format);
                    }
                }
            }
        }

        aiResult.setAnswer(answer);

        String responseData = xwResponseInfo.responseData;
        if (responseData != null && responseData.isEmpty()) {
            return aiResult;
        }

        try {
            AIResult.DataBean dataBean = new AIResult.DataBean();
            List<AIResult.DataBean.ResultBean> result = new ArrayList<>();

            JSONObject responseObject = new JSONObject(responseData);
            String city = responseObject.optString("loc");
            JSONArray dataArray = responseObject.optJSONArray("data");
            int length = dataArray != null ? dataArray.length() : 0;
            for (int i = 0; i < length; i++) {
                JSONObject data = dataArray.getJSONObject(i);
                AIResult.DataBean.ResultBean resultBean = new AIResult.DataBean.ResultBean();
                if (data != null) {
                    resultBean.setCity(city);
                    resultBean.setMinTemp(data.optString("min_tp"));
                    resultBean.setMaxTemp(data.optString("max_tp"));
                    resultBean.setIs_asked(data.optString("is_asked"));
                    resultBean.setWind_lv(data.optString("wind_lv"));
                    resultBean.setCurTemp(data.optString("tp"));
                    resultBean.setWeather(data.optString("condition"));
                    resultBean.setWind(data.optString("wind_direct"));
                    resultBean.setDate(data.optString("date"));
                    resultBean.setAirQuality(data.optString("quality"));
                    resultBean.setPm25(data.optString("pm25"));
                }
                result.add(resultBean);
            }
            dataBean.setResult(result);
            aiResult.setData(dataBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aiResult;
    }

}
