package com.swan.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.swan.coolweather.R;
import com.swan.coolweather.db.CoolWeatherDB;
import com.swan.coolweather.model.City;
import com.swan.coolweather.model.County;
import com.swan.coolweather.model.Province;
import com.swan.coolweather.util.JsonParserUtil;
import com.swan.coolweather.util.LogUtil;
import com.swan.coolweather.util.XmlParserUtil;

import org.xmlpull.v1.XmlPullParserException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EDEH7311 on 06/12/2016.
 */
public class ChooseRegionActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private final static int XML_PARSE_FINSH = 1;

    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private CoolWeatherDB coolWeatherDB;
    private ProgressDialog progressDialog;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;

    private int currentLevel;
    private boolean isFromWeatherActivity;

    private MyHandler myHandler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromWeatherActivity = getIntent().getBooleanExtra(WeatherActivity.FROM_WEATHER_ACTIVITY, false);
        if (JsonParserUtil.getCountySelected() && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);

        LogUtil.d("ChooseRegionActivity", "onCreate");

        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);
        if (coolWeatherDB.loadProvince().size() <= 0) {
            showProgressDialog();
            myHandler = new MyHandler();
            MyRunnable myRunnable = new MyRunnable();
            new Thread(myRunnable).start();
        } else {
            queryProvinces();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    Intent intent = new Intent(ChooseRegionActivity.this, WeatherActivity.class);
                    intent.putExtra("County", countyList.get(position).getName());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            closeProgressDialog();
            queryProvinces();
        }
    }

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            try {
                XmlParserUtil.handleXml(ChooseRegionActivity.this, coolWeatherDB);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
            Message msg = new Message();
            msg.what = XML_PARSE_FINSH;
            ChooseRegionActivity.this.myHandler.sendMessage(msg);
        }
    }


    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvince();
        LogUtil.d("ChooseRegionActivity", ""+provinceList.size());
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province: provinceList) {
                dataList.add(province.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("China");
            currentLevel = LEVEL_PROVINCE;
        } else {
            Toast.makeText(this, "加载省会列表失败...", Toast.LENGTH_SHORT).show();
        }
    }
    private void queryCities() {
        cityList = coolWeatherDB.loadCity(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city: cityList) {
                dataList.add(city.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getName());
            currentLevel = LEVEL_CITY;
        } else {
            Toast.makeText(this, "加载城市列表失败...", Toast.LENGTH_SHORT).show();
        }
    }
    private void queryCounties() {
        countyList = coolWeatherDB.loadCounty(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county: countyList) {
                dataList.add(county.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getName());
            currentLevel = LEVEL_COUNTY;
        } else {
            Toast.makeText(this, "加载县城列表失败...", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载城市列表...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
