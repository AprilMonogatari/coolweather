package app.coolweather.com.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.coolweather.com.R;
import app.coolweather.com.db.CoolWeatherDB;
import app.coolweather.com.model.City;
import app.coolweather.com.model.County;
import app.coolweather.com.model.Province;
import app.coolweather.com.util.HttpCallbackListener;
import app.coolweather.com.util.HttpUtil;
import app.coolweather.com.util.Utility;

public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    private boolean isFromWeatherActivity;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private CoolWeatherDB coolWeatherDB;
    private List<String> dataList=new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity",false);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false)&&!isFromWeatherActivity){
            Intent intent=new Intent(this,WeahterActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        listView=(ListView)findViewById(R.id.list_view);
        titleText=(TextView)findViewById(R.id.title_text);
        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        coolWeatherDB=coolWeatherDB.getInstatnce(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if(currentLevel==LEVEL_COUNTY){
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeahterActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces(){
        provinceList=coolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("china");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer("","province");
        }
    }

    private void queryCities(){
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());
        if(cityList.size()>0){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    private void queryCounties(){
        countyList=coolWeatherDB.loadCounty(selectedCity.getId());
        if(countyList.size()>0){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    private void queryFromServer(final String code,final String type){
        String address="";
        switch (type){
            case "county":address="http://www.weather.com.cn/data/city3jdata/station/"+code+".html";
                break;
            case "city":address="http://www.weather.com.cn/data/city3jdata/provshi/"+code+".html";
                break;
            case "province":address="http://www.weather.com.cn/data/city3jdata/china.html";
                break;
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    result=Utility.handleCityResponse(coolWeatherDB,response,selectedProvince.getProvinceCode(),selectedProvince.getId());
                }
                else if("county".equals(type)){
                    result=Utility.handleCountyResponse(coolWeatherDB,response,selectedCity.getCityCode(),selectedCity.getId());
                }
                if(result){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }
                            else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }
    @Override
    public void onBackPressed(){
        if(currentLevel==LEVEL_COUNTY)
            queryCities();
        else if(currentLevel==LEVEL_CITY)
            queryProvinces();
        else
            if(isFromWeatherActivity){
                Intent intent=new Intent(this,WeahterActivity.class);
                startActivity(intent);
            }
            finish();
    }
}
