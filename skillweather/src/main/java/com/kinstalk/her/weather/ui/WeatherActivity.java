package com.kinstalk.her.weather.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinstalk.her.weather.R;
import com.kinstalk.her.weather.model.entity.AIResult;
import com.kinstalk.her.weather.model.entity.WeatherForecastInfoEntity;
import com.kinstalk.her.weather.model.entity.WeatherInfoEntity;
import com.kinstalk.her.weather.model.helper.WeatherAIDataHelper;
import com.kinstalk.her.weather.model.service.WeatherDelegate;
import com.kinstalk.her.weather.model.service.WeatherSelfDataSource;
import com.kinstalk.her.weather.ui.utils.DateUtils;
import com.kinstalk.her.weather.ui.utils.NetUtils;
import com.kinstalk.her.weather.ui.utils.StatisticsUtils;
import com.kinstalk.her.weather.ui.views.ForcastItem;
import com.kinstalk.m4.publicaicore.AICoreManager;
import com.kinstalk.m4.publicaicore.utils.DebugUtil;
import com.kinstalk.m4.publicaicore.xwsdk.XWCommonDef;
import com.kinstalk.m4.publicapi.activity.M4BaseAudioActivity;
import com.kinstalk.m4.publicapi.view.Toasty.Toasty;

import java.lang.ref.WeakReference;
import java.util.List;

import kinstalk.com.qloveaicore.ITTSCallback;

/**
 * Created by siqing on 2018/2/7.
 * 天气主页面
 */

public class WeatherActivity extends M4BaseAudioActivity {
    public static final String INTENT_AIDATA = "ai_data";
    public static final String TAG = WeatherActivity.class.getSimpleName();
    private LinearLayout forcastBox;
    private ImageView weatherImg;
    private TextView tempText;
    private TextView weatherInfoText;
    private TextView locationText;
    private TextView airText;
    private ImageButton homeBtn;
    private WeatherAIDataHelper helper;
    private AIResult aiResult;

    private boolean isWeatherTTSEnd = false;

    private final int MSG_FINISH = 1;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == MSG_FINISH) {
//                switchLuncher();
                DebugUtil.LogD(TAG, "dispatchMessage: finish");
                finish();
            }
        }
    };

    private WeatherCallback callback = new WeatherCallback(this);

    private static class WeatherCallback implements WeatherDelegate {
        private WeakReference<WeatherActivity> reference;

        public WeatherCallback(WeatherActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void onWeatherResultSucc(final WeatherInfoEntity weatherInfo) {
            if (reference != null && reference.get() != null) {
                reference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reference.get().inflateWeatherViewsByHttp(weatherInfo);
                    }
                });
            }
        }

        @Override
        public void onWeatherResultError(final String errorMsg) {
            if (reference != null && reference.get() != null) {
                reference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.error(reference.get(), errorMsg, true).show();
                    }
                });
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new WeatherAIDataHelper();
        initBaseData();
        initViews();
        initActions();
        initData();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
        initBaseData();
        initData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        DebugUtil.LogD(TAG, "onStop: ");
    }

    private ITTSCallback mInitBaseDataTTSCallback = new ITTSCallback.Stub() {

        @Override
        public void onTTSPlayBegin(String s) {
            DebugUtil.LogD(TAG, "onTTSPlayBegin");
            setAutoSwitchLauncher(false);
        }

        @Override
        public void onTTSPlayEnd(String s) {
            DebugUtil.LogD(TAG, "onTTSPlayEnd");
            setAutoSwitchLauncher(true);
            countDown();
        }

        @Override
        public void onTTSPlayProgress(String s, int i) {
            DebugUtil.LogD(TAG, "onTTSPlayProgress");
        }

        @Override
        public void onTTSPlayError(String s, int i, String s1) {
            DebugUtil.LogD(TAG, "onTTSPlayError");
            setAutoSwitchLauncher(true);
            countDown();
        }
    };

    private void initBaseData() {
        mHandler.removeCallbacksAndMessages(null);
        String data = getIntent().getStringExtra(INTENT_AIDATA);
        helper.dealData(data);
        aiResult = helper.getAIResult();

        DebugUtil.LogD(TAG, "initBaseData aiResult: " + aiResult);

        if (XWCommonDef.ResourceFormat.TTS == aiResult.getAnswer().getFormat()) {
            DebugUtil.LogD(TAG, "Handle TTS: " + aiResult.getAnswer().getText());
            AICoreManager.getInstance(this).playTextWithId(aiResult.getVoiceID(), mInitBaseDataTTSCallback);
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_weather);
        weatherImg = findViewById(R.id.weather_img);
        forcastBox = findViewById(R.id.forcast_layout);
        tempText = findViewById(R.id.temp_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        locationText = findViewById(R.id.location_text);
        homeBtn = findViewById(R.id.home_btn);
        airText = findViewById(R.id.air_text);
    }

    private void initActions() {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLauncher();
            }
        });
    }

    /**
     * 初始化数据如果是AI询问，直接用Intent传过来的数据；如果是手动点击进入，直接请求网络QServer(新知天气)
     */
    private void initData() {

        forcastBox.removeAllViews();

        if (helper.isAIPage()) {//使用AI数据更新界面
            try {
                inflateForcastViews(helper.parseWeatherInfo());
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), e.toString());
            }
            isWeatherTTSEnd = false;
            StatisticsUtils.askPageRecord(helper.getAskWeather().getCity(), helper.getAskWeather().getDate());
        } else {//请求网络接口
            WeatherSelfDataSource.getInstance(getApplicationContext()).requestSelfWeatherInfo(callback);
            isWeatherTTSEnd = true;
            StatisticsUtils.touchPageRecord();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DebugUtil.LogD(TAG, "onResume: ");
        if (isWeatherTTSEnd) {
            countDown();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DebugUtil.LogD(TAG, "onPause: ");
        isWeatherTTSEnd = true;
    }

    @Override
    protected void onDestroy() {
        DebugUtil.LogD(TAG, "onDestroy: ");
        WeatherSelfDataSource.getInstance(getApplicationContext()).unregisterCallback(callback);
        super.onDestroy();
    }

    /**
     * 倒计时，没有操作关闭页面
     */
    private void countDown() {
        DebugUtil.LogD(TAG, "countDown: ");
        mHandler.removeCallbacksAndMessages(null);
        Message msg = mHandler.obtainMessage();
        msg.what = MSG_FINISH;
        mHandler.sendMessageDelayed(msg, 10000);
    }

    /**
     * 更新控件值（网络接口更新）
     *
     * @param weatherInfo
     */
    private synchronized void inflateWeatherViewsByHttp(WeatherInfoEntity weatherInfo) {
        forcastBox.removeAllViews();

        weatherImg.setImageResource(weatherInfo.getTodayWeatherImageSourceId());
        locationText.setText(weatherInfo.getCityName());

        weatherInfoText.setVisibility(View.VISIBLE);
        airText.setVisibility(View.VISIBLE);
        tempText.setText(String.format(getString(R.string.weather_temp), weatherInfo.getTemperature()));

        airText.setText(weatherInfo.getTodayAirAualityDescription());
        weatherInfoText.setText(String.format(getString(R.string.weather_wind_info), weatherInfo.getWindDirection(), weatherInfo.getWindScale()));

        if (weatherInfo.getForecast() != null && !weatherInfo.getForecast().isEmpty()) {
            List<WeatherForecastInfoEntity> forecastList = weatherInfo.getForecast();
            for (int i = 0; i < forecastList.size(); i++) {
                WeatherForecastInfoEntity entity = forecastList.get(i);
                ForcastItem view = (ForcastItem) LayoutInflater.from(this).inflate(R.layout.activity_forcast_item, null);
                LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                view.notifyContentChange(DateUtils.getWeekStringByDate(entity.getDay()), entity.getTodayWeatherImageSourceId(), entity.getLow() + "/" + entity.getHigh() + "℃");
                if (DateUtils.getTodayDateStr().equals(entity.getDay())) {
                    view.setAlpha(1f);
                } else {
                    view.setAlpha(0.3f);
                }
                forcastBox.addView(view, lps);
            }
        }

        //by king
        if (!NetUtils.isNetworkAvailable(getApplicationContext())) {
            Toasty.error(getApplicationContext(), getString(R.string.query_weather_info_net_error), true).show();
        }
    }

    /**
     * 更新语音询问界面
     *
     * @param weatherInfo
     */
    private void inflateForcastViews(WeatherInfoEntity weatherInfo) {

        locationText.setText(helper.getAskWeather().getCity());

        if (helper.isTodayWeather()) {
            weatherInfoText.setVisibility(View.VISIBLE);
            airText.setVisibility(View.VISIBLE);
            tempText.setText(String.format(getString(R.string.weather_temp), helper.getAskCurTemp()));
            weatherImg.setImageResource(weatherInfo.getAskWeatherImageSourceId());
        } else {
            weatherInfoText.setVisibility(View.GONE);
            airText.setVisibility(View.GONE);
            tempText.setText(String.format(getString(R.string.weather_temp_range), helper.getAskLowTemp(), helper.getAskHighTemp()));
            weatherImg.setImageResource(helper.getAskWeatherImageSourceId());
        }

        if (TextUtils.isEmpty(weatherInfo.getAirQuality()) || !helper.isTodayWeather()) {
            airText.setVisibility(View.GONE);
        } else {
            airText.setVisibility(View.VISIBLE);
            airText.setText(weatherInfo.getAirQuality());
        }

        weatherInfoText.setText(String.format(getString(R.string.weather_ai_wind_info), weatherInfo.getWindDirection(), weatherInfo.getWindScale()));

        if (weatherInfo.getForecast() != null && !weatherInfo.getForecast().isEmpty()) {
            List<WeatherForecastInfoEntity> forecastList = weatherInfo.getForecast();
            for (int i = 0; i < forecastList.size(); i++) {
                WeatherForecastInfoEntity entity = forecastList.get(i);
                ForcastItem view = (ForcastItem) LayoutInflater.from(this).inflate(R.layout.activity_forcast_item, null);
                LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                view.notifyContentChange(DateUtils.getWeekStringByDate(entity.getDay()), entity.getTodayWeatherImageSourceId(), entity.getLow() + "/" + entity.getHigh() + "℃");
                if (helper.getAskWeather().getDate().equals(entity.getDay())) {
                    view.setAlpha(1f);
                } else {
                    view.setAlpha(0.3f);
                }
                forcastBox.addView(view, lps);
            }
        }

//        Map<String, String> segmentation = new HashMap<>();
//        segmentation.put("city", weatherInfo.getCityName());
//        segmentation.put("date", helper.getAskWeather().getDate());
//        String localCity = OwnerProviderLib.getInstance(WeatherApplication.shareInstance()).getLocation().getCity();
//        if (!TextUtils.isEmpty(localCity) && localCity.contains(helper.getAskWeather().getCity())) {
//            segmentation.put("isLocal", "true");
//        } else {
//            segmentation.put("isLocal", "false");
//        }
//        Countly.sharedInstance().recordEvent("weather", "v_ask_weather", segmentation, 1);
    }
}
