package com.swan.coolweather.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.swan.coolweather.R;
import com.swan.coolweather.db.CoolWeatherDB;
import com.swan.coolweather.model.City;
import com.swan.coolweather.model.County;
import com.swan.coolweather.model.Province;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by EDEH7311 on 06/12/2016.
 */
public class XmlParserUtil {
    public synchronized static void handleXml(Context context, CoolWeatherDB coolWeatherDB) throws XmlPullParserException {
        LogUtil.d("CoolWeather", "handleXml start");
		XmlResourceParser xmlResourceParser = context.getResources().getXml(R.xml.region);
        if (xmlResourceParser != null) {
            int provinceId = 1;
            int cityId = 1;
            
            Province province = new Province();
            City city = new City();
            County county = new County();
            int eventType;
            
			try {
				eventType = xmlResourceParser.getEventType();
				coolWeatherDB.beginTransaction();
				while (eventType != XmlResourceParser.END_DOCUMENT) {
	                String strName = xmlResourceParser.getName();
	                switch (eventType) {
	                    case XmlResourceParser.START_TAG:
	                        if ("province".equals(strName)) {
	                            province.setCode(xmlResourceParser.getAttributeValue(0));
	                            province.setName(xmlResourceParser.getAttributeValue(1));
	                            coolWeatherDB.saveProvince(province);
	                        } else if ("city".equals(strName)) {
	                            city.setProvinceId(provinceId);
	                            city.setCode(xmlResourceParser.getAttributeValue(0));
	                            city.setName(xmlResourceParser.getAttributeValue(1));
	                            coolWeatherDB.saveCity(city);
	                        } else if ("county".equals(strName)) {
	                            county.setCityId(cityId);
	                            county.setCode(xmlResourceParser.getAttributeValue(0));
	                            county.setName(xmlResourceParser.getAttributeValue(1));
	                            coolWeatherDB.saveCounty(county);
	                        }
	                        break;
	                    case XmlPullParser.END_TAG:
	                        if ("province".equals(strName)) {
	                            provinceId++;
	                        } else if ("city".equals(strName)) {
	                            cityId++;
	                        }
	                        break;
	                }
	                try {
						eventType = xmlResourceParser.next();
					} catch (IOException e) {
						e.printStackTrace();
					}
	            }
				coolWeatherDB.setTransactionSuccessful();
			} catch (XmlPullParserException e1) {
				e1.printStackTrace();
			} finally {
				coolWeatherDB.endTransaction();
			}
        }
        LogUtil.d("CoolWeather", "handleXml end");
    }
}
