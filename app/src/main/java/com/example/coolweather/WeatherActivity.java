package com.example.coolweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.Forecast;
import com.example.coolweather.gson.Hourly;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.service.AutoUpdateService;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @类名: ${type_name}
 * @功能描述:
 * @作者: ${user}
 * @时间: ${date}
 * @最后修改者:
 * @最后修改内容:
 */
public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    private SwipeRefreshLayout swipeRefresh;
    private Button navButton;
    private DrawerLayout drawerLayout;
    private TextView weatherWindSc;
    private TextView airBrf;
    private TextView airText;
    private TextView comfortBrf;
    private TextView carWashBrf;
    private TextView sportBrf;
    private TextView drsgBrf;
    private TextView drsgText;
    private TextView fluBrf;
    private TextView fluText;
    private TextView travBrf;
    private TextView travText;
    private TextView uvBrf;
    private TextView uvText;
    //private LineChartView lineChart;
    private TextView aqiZhiliang;

    //private List<PointValue> mPointValues = new ArrayList<PointValue>();
    //private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT >= 21) {
                 View dicorView = getWindow().getDecorView();
                    dicorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
         .SYSTEM_UI_FLAG_LAYOUT_STABLE);
                  getWindow().setStatusBarColor(Color.TRANSPARENT);
              }
        setContentView(R.layout.activity_weather);
        //ButterKnife.bind(this);
//初始化各控件
        //weatherLayout=(ScrollView) findViewById(R.id.weather_layout);

        initEvent();

    }

    public void initEvent() {
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = sp.getString("bing_pic", null);
        if (null != bingPic) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadingPicImg();
        }
        String weatherStr = sp.getString("weather", null);
        final String weatherId;
        if (null != weatherStr) {
            //有缓存直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherStr);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //无缓存通过服务器查询
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void loadingPicImg() {
        String requestPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                        .edit();
                sp.putString("bing_pic", bingPic);
                sp.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }

    /**
     * 根据天气ID请求城市天气信息
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {

        String weatherUrl = Common.URL_WEATHER_CODE + "weather?city=" + weatherId + "&key=" + Common.URL_KEY;
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != weather && "ok".equalsIgnoreCase(weather.status)) {
                            SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences
                                    (WeatherActivity.this).edit();
                            sp.putString("weather", responseText);
                            sp.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadingPicImg();
    }

    /**
     * 处理并显示数据
     */
    public void showWeatherInfo(Weather weather) {
        if (null != weather && "ok".equalsIgnoreCase(weather.status)) {
            String cityName = weather.basic.cityName;
            String degree = weather.now.temperature + "℃";
            String weatherInfo = weather.now.more.info;
            titleCity.setText(cityName);
            degreeText.setText(degree);
            weatherInfoText.setText(weatherInfo);
            weatherWindSc.setText(weather.now.wind.sc);
            forecastLayout.removeAllViews();
            for (Forecast forecast : weather.forecastList) {
                View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
                TextView dataText = (TextView) view.findViewById(R.id.data_text);
                TextView infoText = (TextView) view.findViewById(R.id.info_text);
                TextView maxText = (TextView) view.findViewById(R.id.max_text);
                TextView windScText = (TextView) view.findViewById(R.id.wind_sc);
                dataText.setText(forecast.data);
                infoText.setText(forecast.more.info);
                maxText.setText(forecast.temperature.max + "℃" + "/" + forecast.temperature.min + "℃");
                windScText.setText(forecast.wind.sc);
                forecastLayout.addView(view);
            }
            if (null != weather.aqi) {
                aqiText.setText(weather.aqi.city.aqi);
                pm25Text.setText(weather.aqi.city.pm25);
                aqiZhiliang.setText(weather.aqi.city.qlty);
            }
            airBrf.setText(weather.suggestion.air.brf);
            airText.setText(weather.suggestion.air.info);
            comfortBrf.setText(weather.suggestion.comfort.brf);
            comfortText.setText(weather.suggestion.comfort.info);
            carWashBrf.setText(weather.suggestion.carWash.brf);
            carWashText.setText(weather.suggestion.carWash.info);
            sportBrf.setText(weather.suggestion.sport.brf);
            sportText.setText(weather.suggestion.sport.info);
            drsgBrf.setText(weather.suggestion.drsg.brf);
            drsgText.setText(weather.suggestion.drsg.info);
            fluBrf.setText(weather.suggestion.flu.brf);
            fluText.setText(weather.suggestion.flu.info);
            travBrf.setText(weather.suggestion.trav.brf);
            travText.setText(weather.suggestion.trav.info);
            uvBrf.setText(weather.suggestion.uv.brf);
            uvText.setText(weather.suggestion.uv.info);
            //mPointValues.clear();
            //mAxisXValues.clear();
            //getAxisXLables(weather.hourly);//获取x轴的标注
            //getAxisPoints(weather.hourly);//获取坐标点
            //initLineChart();//初始化
            Intent intent = new Intent(this,AutoUpdateService.class);
            startService(intent);
        } else {
            Toast.makeText(this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 初始化LineChart的一些设置
     */
    /**
     *

    private void initLineChart() {
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape
        // .SQUARE）
        line.setCubic(false);//曲线是否平滑
        line.setStrokeWidth(2);//线条的粗细，默认是3
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        //		line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X轴下面坐标轴字体是斜的显示还是直的，true是斜的显示
        //        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setTextColor(Color.parseColor("#D6D6D9"));//灰色

        //        	    axisX.setName("未来几天的天气");  //表格名称
        axisX.setTextSize(11);//设置字体大小
        axisX.setMaxLabelChars(5); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //        data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线


        Axis axisY = new Axis();  //Y轴
        axisY.setName("");//y轴标注
        axisY.setTextSize(11);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边
        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);  //缩放类型，水平
        lineChart.setMaxZoom((float) 3);//缩放比例
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);

        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);
    }

    /**
     * X 轴的显示
     */
    /**private void getAxisXLables(List<Hourly> hourly) {
        for (int i = 0; i < hourly.size(); i++) {
            String[] strings = hourly.get(i).data.split(" ");
            mAxisXValues.add(new AxisValue(i).setLabel(strings[1]));
        }
    }

    /**
     * 图表的每个点的显示
     */
    /**private void getAxisPoints(List<Hourly> hourly) {
        for (int i = 0; i < hourly.size(); i++) {
            mPointValues.add(new PointValue(i, Float.valueOf(hourly.get(i).tmp)));
        }
    }*/
}