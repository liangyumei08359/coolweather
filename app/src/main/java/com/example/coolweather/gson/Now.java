package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liangyumei on 2017/12/12.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;
    @SerializedName("cond")
    public More more;
    @SerializedName("wind")
    public Wind wind;


    public class More{

        @SerializedName("txt")
        public String info;
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
