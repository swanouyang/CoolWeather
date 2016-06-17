package com.swan.coolweather.receiver;

import com.swan.coolweather.service.AutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateRecevier extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, AutoUpdateService.class);
		context.startService(service);
	}
}
