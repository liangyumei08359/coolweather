package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liangyumei on 2017/12/17.
 */

public class Hourly {

    @SerializedName("date")
    public String data;
    @SerializedName("tmp")
    public String tmp;
    @SerializedName("cond")
    public Cond cond;
    @SerializedName("wind")
    public Wind wind;


    public class Cond{
        @SerializedName("code")
        public String code;
        @SerializedName("txt")
        public String txt;
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