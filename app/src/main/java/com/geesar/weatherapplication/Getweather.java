package com.geesar.weatherapplication;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Getweather {
    @GET("weather")
    Call<WeatherGetdata> getCurrentWeather(@Query("lat") double lat, @Query("lon") double lon, @Query("appid") String apiKey);
}
