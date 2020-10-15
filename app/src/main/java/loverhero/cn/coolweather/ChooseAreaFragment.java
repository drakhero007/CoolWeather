package loverhero.cn.coolweather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loverhero.cn.coolweather.db.City;
import loverhero.cn.coolweather.db.County;
import loverhero.cn.coolweather.db.Province;
import loverhero.cn.coolweather.util.HttpUtil;
import loverhero.cn.coolweather.util.Utility;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 鲍日鑫 on 2020/6/19.
 */

public class ChooseAreaFragment extends Fragment {

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;



    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();




    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private Province selectedProvince;

    private City selectedCity;

    private int currentLevel;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.choose_area,container,false);

        titleText = view.findViewById(R.id.title_text);
        backButton = view.findViewById(R.id.back_button);
        listView = view.findViewById(R.id.list_view);

        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);

        listView.setAdapter(adapter);

        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(currentLevel == LEVEL_PROVINCE){

                    selectedProvince = provinceList.get(i);
                    queryCities();

                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(i);
                    queryCounties();
                }else if(currentLevel == LEVEL_COUNTY){

                    String weatherId = countyList.get(i).getWeatherId();

                    if(getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(),WeatherActivity.class);
                        intent.putExtra("weather_id",weatherId);

                        startActivity(intent);
                        getActivity().finish();
                    }else if(getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.drawerLayout.closeDrawers();
                        activity.swipeRefresh.setRefreshing(true);
                        activity.requestWeather(weatherId);
                    }



                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if(currentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });

        queryProvinces();

    }


    private void queryProvinces(){

        titleText.setText("中国");
        backButton.setVisibility(View.GONE);

        provinceList = DataSupport.findAll(Province.class);

        if(provinceList.size() > 0){
            dataList.clear();

            for(Province province : provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;

        }else{
            String address = "http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }

    }


    private void queryCities(){

        titleText.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceid = ?" ,String.valueOf(selectedProvince.getId())).find(City.class);

        if(cityList.size() > 0){
            dataList.clear();

            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else{

            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }



    }

    private void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);

        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size() > 0){
            dataList.clear();
            for(County county : countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;

        }else{
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();

            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;

            queryFromServer(address,"county");
        }

    }


    private void queryFromServer(String address, final String type){

        Toast.makeText(getContext(),"开始加载",Toast.LENGTH_SHORT).show();

        showProgressDialog();

        Log.d("ChooseAreaFragment","test1========================");

        HttpUtil.sendOkHttpRequest(address, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                Log.d("ChooseAreaFragment","test2========================");

                String responseText = response.body().string();
                boolean result = false;

                Log.d("ChooseAreaFragment","test3========================responseText:"+responseText);

                if("province".equals(type)){


                    result = Utility.handleProvinceResponse(responseText);


                } else if("city".equals(type)){
                    Log.d("ChooseAreaFragment","test4========================");
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());
                    Log.d("ChooseAreaFragment","test5========================result:"+result);
                }else if("county".equals(type)){

                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());

                }

                Log.d("ChooseAreaFragment","test6========================");

                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            closeProgressDialog();

                            if("province".equals(type)){
                                    queryProvinces();
                            }else if("city".equals(type)){
                                    queryCities();
                            }else if("county".equals(type)){
                                    queryCounties();
                            }


                        }
                    });
                }


            }
        });


    }





    private void showProgressDialog(){


        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载....");
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();


    }


    private void closeProgressDialog(){

        if(progressDialog != null){
            progressDialog.dismiss();
        }

    }


}