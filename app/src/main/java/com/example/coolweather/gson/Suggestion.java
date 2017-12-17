package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liangyumei on 2017/12/12.
 */

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    @SerializedName("sport")
    public Sport sport;
    @SerializedName("air")
    public Air air;
    @SerializedName("drsg")
    public Drsg drsg;
    @SerializedName("flu")
    public Flu flu;
    @SerializedName("trav")
    public Trav trav;
    @SerializedName("uv")
    public Uv uv;



    public class Comfort{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }

    public class CarWash{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }

    public class Sport{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }
    public class Air{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }
    public class Drsg{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }
    public class Flu{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }
    public class Trav{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }
    public class Uv{

        @SerializedName("txt")
        public String info;
        @SerializedName("brf")
        public String brf;
    }

}
