package com.swan.coolweather.util;

public interface HttpCallBackListener {
	void onFinish(String response);
	void onError();
}
