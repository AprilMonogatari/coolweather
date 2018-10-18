package app.coolweather.com.util;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

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
}
