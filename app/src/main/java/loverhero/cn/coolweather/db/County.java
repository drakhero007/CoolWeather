package loverhero.cn.coolweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 鲍日鑫 on 2020/6/5.
 */

public class County extends DataSupport{

    private  String weatherId;

    private String countyName;

    private int cityId;

    public String getId() {
        return weatherId;
    }

    public void setWeatherIdE(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCountyName() {
        return countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }



}
