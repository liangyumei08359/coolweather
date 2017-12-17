package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liangyumei on 2017/12/12.
 */

public class AQI {
    @SerializedName("city")
    public AQICity city;

    public class AQICity{
        @SerializedName("aqi")
        public String aqi;
        @SerializedName("pm25")
        public String pm25;
        @SerializedName("pm10")
        public String pm10;
        @SerializedName("qlty")
        public String qlty;
    }

}
