package com.swan.coolweather.service;

import com.swan.coolweather.application.MyApplication;
import com.swan.coolweather.receiver.AutoUpdateRecevier;
import com.swan.coolweather.util.HttpCallBackListener;
import com.swan.coolweather.util.HttpUtil;
import com.swan.coolweather.util.JsonParserUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

public class AutoUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateWeather();
			}
		}).start();
		
		AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
		int fourHour = 8 * 60 * 60 * 1000;
		long triggerAtMillis = SystemClock.elapsedRealtime() + fourHour;
		Intent i = new Intent(this, AutoUpdateRecevier.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME, triggerAtMillis, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}

	private void updateWeather() {
		SharedPreferences pref = MyApplication.getAppContext()
				.getSharedPreferences(JsonParserUtil.COUNTY_SELECTED,
						MODE_PRIVATE);
		String county = pref.getString(JsonParserUtil.COUNTY, "");
		HttpUtil.sendHttpRequest(county, new HttpCallBackListener() {
			@Override
			public void onFinish(String response) {
				JsonParserUtil.saveWeatherInfo(response);
			}
			
			@Override
			public void onError() {
				
			}
		});
		
	}
}
