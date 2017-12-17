package com.example.coolweather.util;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.coolweather.Common;
import com.example.coolweather.MainActivity;
import com.example.coolweather.R;
import com.example.coolweather.WeatherActivity;
import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
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
public class ChooseAreaFragment extends Fragment {

    private TextView titleText;
    private Button backButton;
    private ListView listview;

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog mProgressDialog;
    private ArrayAdapter<String> mAdapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listview = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, dataList);
        listview.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = mProvinceList.get(position);
                    queryCity();
                } else if (currentLevel == LEVEL_CITY){
                    selectedCity = mCityList.get(position);
                    queryCounty();
                } else if (currentLevel == LEVEL_COUNTY){
                    String weatherId = mCountyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity) {
                        MainActivity mainActivity = (MainActivity) getActivity();
                        Intent intent = new Intent(mainActivity, WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        mainActivity.startActivity(intent);
                        mainActivity.finish();
                    } else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }
                }

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_COUNTY){
                    queryCity();
                } else if (currentLevel == LEVEL_CITY){
                    queryProvince();
                }
            }
        });
        queryProvince();

    }

    /**
     * 查询 NAME
     */
    public void queryProvince(){
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0){
            dataList.clear();
            for (Province province : mProvinceList){
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFormServer(Common.URL_ADDRESS, "province");
        }
    }

    public void queryCity(){
        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (mCityList.size() > 0){
            dataList.clear();
            for (City city : mCityList){
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            queryFormServer(Common.URL_ADDRESS + selectedProvince.getProvinceCode(), "city");
        }
    }

    public void queryCounty(){
        titleText.setText(selectedProvince.getProvinceName() + ">" + selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (mCountyList.size() > 0){
            dataList.clear();
            for (County county : mCountyList){
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            listview.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFormServer(Common.URL_ADDRESS + selectedProvince.getProvinceCode()
                    + "/" + selectedCity.getCityCode(), "county");
        }
    }


    public void queryFormServer(String address, final String type){
        showDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dissDialog();
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                } else if ("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dissDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            } else if ("city".equals(type)){
                                queryCity();
                            } else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }


    public void showDialog(){
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("加载中");
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }
    public void dissDialog(){
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}

