package com.swan.coolweather.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.swan.coolweather.R;
import com.swan.coolweather.service.AutoUpdateService;
import com.swan.coolweather.util.BaiduMapWeatherInfo;
import com.swan.coolweather.util.HttpCallBackListener;
import com.swan.coolweather.util.HttpUtil;
import com.swan.coolweather.util.JsonParserUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener {
	public final static String FROM_WEATHER_ACTIVITY = "from_weather_activity";
	
	private static final int MAX_SHOW_DAYS = 4;
	private static final int ID_DAYS[] = new int[] { R.id.tv_day1,
			R.id.tv_day2, R.id.tv_day3, R.id.tv_day4 };
	private TextView textViewDays[] = new TextView[MAX_SHOW_DAYS];
	private ViewPager viewPager;
	private List<View> views;
	private View[] viewArray = new View[MAX_SHOW_DAYS];

	private static final int ID_WEATHER[] = new int[] { R.id.tv_weather,
			R.id.tv_temperature, R.id.tv_wind };
	private TextView textViewWeather[] = new TextView[3];
	private ImageView imageDay;
	private ImageView imageNight;
	private TextView textViewCounty;
	private TextView textViewCurrentTime;
	private ImageButton houseButton;
	private ImageButton refreshButton;
	// private BaiduMapWeatherInfo mWeatherInfo;

	String countyString = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);

		if (getIntent().getStringExtra("County") == null) {
			countyString = JsonParserUtil.getCounty();
		} else {
			countyString = new String(getIntent().getStringExtra("County"));
		}

		initTextView();
		initViewPager();

		if (!TextUtils.isEmpty(countyString)) {
			JsonParserUtil.saveCountySelected();
			queryWeatherInfo();
		} else {
			showLocalWeather();
		}
	}

	private void queryWeatherInfo() {
		HttpUtil.sendHttpRequest(HttpUtil.generateHttpAddress(countyString),
				new HttpCallBackListener() {
					@Override
					public void onFinish(String response) {
						final BaiduMapWeatherInfo weatherInfo = JsonParserUtil
								.getBaiduMapWeatherInfo(response);
						JsonParserUtil.saveWeatherInfo(response);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								updateWeather(weatherInfo);
							}
						});
					}

					@Override
					public void onError() {
					}
				});
	}

	private void updateWeather(BaiduMapWeatherInfo weatherInfo) {
		String dataString;
		StringBuilder dataStringBuilder;
		StringBuilder currentTimeString;
		if (weatherInfo.status.equals("success")) {
			if (countyString.equals("")) {
				countyString = weatherInfo.results.get(0).currentCity;
				textViewCounty.setText(countyString);
			}
			for (int i = 0; i < MAX_SHOW_DAYS; i++) {
				dataString = weatherInfo.results.get(0).weather_data.get(i).date;
				if (i == 0) {
					currentTimeString = new StringBuilder(dataString);
					currentTimeString.delete(0, 2);
					dataStringBuilder = new StringBuilder(dataString);
					dataStringBuilder.delete(2, dataStringBuilder.length());
					textViewDays[i].setText(dataStringBuilder.toString());
					textViewCurrentTime.setText(currentTimeString.toString());
				} else {
					textViewDays[i].setText(dataString);
				}
			}
			for (int i = 0; i < MAX_SHOW_DAYS; i++) {
				for (int j = 0; j < 3; j++) {
					textViewWeather[j] = (TextView) viewArray[i]
							.findViewById(ID_WEATHER[j]);
				}
				imageDay = (ImageView) viewArray[i].findViewById(R.id.image_day);
				imageNight = (ImageView) viewArray[i]
						.findViewById(R.id.image_night);
	
				textViewWeather[0].setText(weatherInfo.results.get(0).weather_data
						.get(i).weather);
				textViewWeather[1].setText(weatherInfo.results.get(0).weather_data
						.get(i).temperature);
				textViewWeather[2].setText(weatherInfo.results.get(0).weather_data
						.get(i).wind);
			}
			viewPager.setVisibility(View.VISIBLE);
		}
		
		Intent service = new Intent(this, AutoUpdateService.class);
		startService(service);
	}

	private void showLocalWeather() {
		if (!TextUtils.isEmpty(JsonParserUtil.loadWeatherInfo())) {
			BaiduMapWeatherInfo weatherInfo = JsonParserUtil
					.getBaiduMapWeatherInfo(JsonParserUtil.loadWeatherInfo());
			updateWeather(weatherInfo);
		} else {
			
		}
	}

	private void initTextView() {
		for (int i = 0; i < MAX_SHOW_DAYS; i++) {
			textViewDays[i] = (TextView) findViewById(ID_DAYS[i]);
		}
		setTextViewHighlight(0);
		textViewCounty = (TextView) findViewById(R.id.tv_county);
		textViewCounty.setText(countyString);
		//textViewCounty.setAlpha(0.5f);
		textViewCurrentTime = (TextView) findViewById(R.id.tv_current_time);
		textViewCurrentTime.setText("请等待...");
		houseButton = (ImageButton) findViewById(R.id.button_house);
		houseButton.setOnClickListener(this);
		refreshButton = (ImageButton) findViewById(R.id.button_refresh);
		refreshButton.setOnClickListener(this);
	}

	private void setTextViewNormal() {
		for (int i = 0; i < MAX_SHOW_DAYS; i++) {
			textViewDays[i].setTextColor(0x40000000);
		}
	}

	private void setTextViewHighlight(int i) {
		setTextViewNormal();
		textViewDays[i].setTextColor(0xff000000);
	}

	@SuppressLint("InflateParams")
	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		for (int i = 0; i < MAX_SHOW_DAYS; i++) {
			viewArray[i] = inflater.inflate(R.layout.weather_one_day, null);
			views.add(viewArray[i]);
		}
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setVisibility(View.INVISIBLE);
		viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
	}

	class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mViews;

		public MyViewPagerAdapter(List<View> mViews) {
			this.mViews = mViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mViews.get(position), 0);
			return mViews.get(position);
		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			setTextViewHighlight(arg0);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_house:
			Intent intent = new Intent(WeatherActivity.this,
					ChooseRegionActivity.class);
			intent.putExtra(FROM_WEATHER_ACTIVITY, true);
			startActivity(intent);
			finish();
			break;
		case R.id.button_refresh:
			textViewCurrentTime.setText("同步中...");
			queryWeatherInfo();
			break;

		default:
			break;
		}
	}
}
