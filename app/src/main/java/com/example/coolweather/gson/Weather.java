package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liangyumei on 2017/12/12.
 */
public class Weather {

    @SerializedName("status")
    public String status;
    @SerializedName("basic")
    public Basic basic;
    @SerializedName("aqi")
    public AQI aqi;
    @SerializedName("now")
    public Now now;
    @SerializedName("suggestion")
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    @SerializedName("hourly_forecast")
    public List<Hourly> hourly;

}
