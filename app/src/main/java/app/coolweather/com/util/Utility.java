package app.coolweather.com.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.prefs.PreferenceChangeEvent;

import app.coolweather.com.db.CoolWeatherDB;
import app.coolweather.com.model.City;
import app.coolweather.com.model.County;
import app.coolweather.com.model.Province;

/**
 * Created by tg on 2018/10/17.
 */

public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONObject jsonObject=new JSONObject(response);
                Iterator<String> iterator=jsonObject.keys();
                while(iterator.hasNext()){
                    Province province=new Province();
                    String key=iterator.next();
                    province.setProvinceCode(key);
                    province.setProvinceName(jsonObject.getString(key));
                    coolWeatherDB.saveProvince(province);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public synchronized static boolean handleCityResponse(CoolWeatherDB coolWeatherDB,String response,String provinceCode,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONObject jsonObject=new JSONObject(response);
                Iterator<String> iterator=jsonObject.keys();
                while(iterator.hasNext()){
                    City city=new City();
                    String key=iterator.next();
                    city.setCityCode(provinceCode+key);
                    city.setCityName(jsonObject.getString(key));
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public synchronized static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,String cityCode,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONObject jsonObject=new JSONObject(response);
                Iterator<String> iterator=jsonObject.keys();
                while(iterator.hasNext()){
                    County county=new County();
                    String key=iterator.next();
                    county.setCountyCode(cityCode+key);
                    county.setCountyName(jsonObject.getString(key));
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void handleWeatherResponse(Context context, String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherinfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherinfo.getString("city");
            String weatherCode=weatherinfo.getString("cityid");
            String temp1=weatherinfo.getString("temp1");
            String temp2=weatherinfo.getString("temp2");
            String weatherDesp=weatherinfo.getString("weather");
            String publishTime=weatherinfo.getString("ptime");
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }
}
