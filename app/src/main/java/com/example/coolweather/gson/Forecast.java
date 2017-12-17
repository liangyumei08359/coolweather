package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liangyumei on 2017/12/12.
 */

public class Forecast {

    @SerializedName("date")
    public String data;

    @SerializedName("tmp")
    public Temperature temperature;

    @SerializedName("cond")
    public More more;

    @SerializedName("wind")
    public Wind wind;

    public class Temperature{
        @SerializedName("max")
        public String max;
        @SerializedName("min")
        public String min;
    }

    public class More{

        @SerializedName("txt_d")
        public String info;
        @SerializedName("txt_n")
        public String info2;
    }

    public class Wind{
        @SerializedName("deg")
        public String deg;
        @SerializedName("dir")
        public String dir;  //风向
        @SerializedName("sc")
        public String sc;  //风力
        @SerializedName("spd")
        public String spd;  //风速

    }
}
