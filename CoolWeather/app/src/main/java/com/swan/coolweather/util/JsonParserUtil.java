package com.swan.coolweather.util;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.swan.coolweather.application.MyApplication;

public class JsonParserUtil {
	public static final String WEATHER_INFO_STRING = "weather_info";
	public static final String COUNTY_SELECTED = "county_selected";
	public static final String COUNTY = "county";

	public static BaiduMapWeatherInfo getBaiduMapWeatherInfo(String jsonData) {
		return new Gson().fromJson(jsonData, BaiduMapWeatherInfo.class);
	}

	// Context.MODE_PRIVATE
	//
	public static boolean saveCountySelected() {
		SharedPreferences pref = MyApplication.getAppContext()
				.getSharedPreferences(COUNTY_SELECTED, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean(COUNTY_SELECTED, true);
		editor.putString(COUNTY, "");
		return editor.commit();
	}

	public static boolean getCountySelected() {
		SharedPreferences pref = MyApplication.getAppContext()
				.getSharedPreferences(COUNTY_SELECTED, 0);
		return pref.getBoolean(COUNTY_SELECTED, false);
	}
	public static String getCounty() {
		SharedPreferences pref = MyApplication.getAppContext()
				.getSharedPreferences(COUNTY_SELECTED, 0);
		return pref.getString(COUNTY, "");
	}

	public static boolean saveWeatherInfo(String info) {
		SharedPreferences pref = MyApplication.getAppContext()
				.getSharedPreferences(WEATHER_INFO_STRING, 0);
		return pref.edit().putString(WEATHER_INFO_STRING, info).commit();
	}

	public static String loadWeatherInfo() {
		SharedPreferences pref = MyApplication.getAppContext()
				.getSharedPreferences(WEATHER_INFO_STRING, 0);
		return pref.getString(WEATHER_INFO_STRING, "");
	}
}
