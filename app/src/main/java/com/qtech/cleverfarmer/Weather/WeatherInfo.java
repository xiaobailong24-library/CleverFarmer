package com.qtech.cleverfarmer.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaobailong24 on 2016/5/14.
 * Class:
 * Describe:
 * Version:
 */
public class WeatherInfo {

    public String city;        //城市
    public String update;        //更新时间
    public String tmp;         //当前气温
    public String txt;      // 天气描述
    public String hum;           //湿度
    public String wind_dir;    //风向
    public String wind_level;   //风力


    //构造函数,  可直接赋初始值
    public WeatherInfo() {
        // TODO Auto-generated constructor stub
        city = null;
    }


    //解析从网站上得到的JSON数据,并赋值
    public void parseJson(String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather data service 3.0");
            JSONObject jsonObject_2 = jsonArray.getJSONObject(0);
            JSONObject basic_Object = jsonObject_2.getJSONObject("basic");
            JSONObject update_object = basic_Object.getJSONObject("update");
            JSONObject now_object = jsonObject_2.getJSONObject("now");
            JSONObject cond_object = now_object.getJSONObject("cond");
            JSONObject wind_object = now_object.getJSONObject("wind");

            //获取需要的值
            this.city = basic_Object.getString("city");
            this.update = update_object.getString("loc");
            this.tmp = now_object.getString("tmp");
            this.txt = cond_object.getString("txt");
            this.hum = now_object.getString("hum");
            this.wind_dir = wind_object.getString("dir");
            this.wind_level = wind_object.getString("sc");


            //测试
		/*	System.out.println("城市-->" + city);
			System.out.println("更新时间-->" + update);
			System.out.println("当前气温-->" + tmp);
			System.out.println("天气描述-->" + txt);
			System.out.println("湿度-->" + hum);
			System.out.println("风向-->" + wind_dir);
			System.out.println("风力-->" + wind_level);  */


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            System.out.println("Json parse error");
            e.printStackTrace();
        }
    }
}
