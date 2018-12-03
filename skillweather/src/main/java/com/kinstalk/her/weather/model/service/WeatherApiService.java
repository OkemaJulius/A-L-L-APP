package com.kinstalk.her.weather.model.service;

import com.kinstalk.her.weather.model.entity.WeatherInfoRootEntity;
import com.kinstalk.m4.publicdomain.M4Domain;
import com.kinstalk.m4.publichttplib.HttpResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by siqing on 17/4/18.
 */

public interface WeatherApiService {

    String BASE_URL = M4Domain.RequestHttpUrl;

    /**
     * 拉取天气接口
     *
     * @return
     */
    @GET("/weather/qlove")
    Observable<HttpResult<WeatherInfoRootEntity>> getWeatherInfo(@Query("loc") String loc,
                                                                 @Query("raw") String row,
                                                                 @Query("cipher") String cipher);

    /**
     * 拉取天气接口
     *
     * @return
     */
    @GET("/weather/qlove")
    Call<HttpResult<WeatherInfoRootEntity>> requestWeatherInfo(@Query("loc") String loc,
                                                               @Query("raw") String row,
                                                               @Query("cipher") String cipher);


}
