package loverhero.cn.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import loverhero.cn.coolweather.db.City;
import loverhero.cn.coolweather.db.County;
import loverhero.cn.coolweather.db.Province;
import loverhero.cn.coolweather.gson.Weather;

/**
 * Created by 鲍日鑫 on 2020/6/5.
 */

public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){

        if(!TextUtils.isEmpty(response)){

            try{
                JSONArray allProvinces = new JSONArray(response);

                Log.d("Utility","=====allProvinces"+allProvinces);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject = allProvinces.getJSONObject(i);

                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    Log.d("Utility","=====name:"+provinceObject.getString("name")+"======id:"+provinceObject.getString("id"));

                    province.save();
                }

                return  true;
            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){

        if(!TextUtils.isEmpty(response)){

            try{
                JSONArray allCities = new JSONArray(response);
                for(int i=0;i < allCities.length();i++){
                    JSONObject cityObject = allCities.getJSONObject(i);

                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);

                    city.save();
                }

                return  true;
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        return false;
    }


    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){

        if(!TextUtils.isEmpty(response)){

            try{
                JSONArray allCounties = new JSONArray(response);
                for(int i=0; i<allCounties.length();i++){
                    JSONObject countObject = allCounties.getJSONObject(i);

                    County county = new County();
                    county.setCountyName(countObject.getString("name"));
                    county.setWeatherId(countObject.getString("weather_id"));
                    county.setCityId(cityId);

                    county.save();
                }

                return true;
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        return false;
    }




    public static Weather handleWeatherResponse(String response){

        try{
           JSONObject jsonObject = new JSONObject(response);
           JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
           String weatherContent = jsonArray.getJSONObject(0).toString();

           return new Gson().fromJson(weatherContent,Weather.class);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }



}
