package com.eamon.rtbau.weather.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eamon.rtbau.weather.entity.Weather;
import com.eamon.rtbau.weather.entity.WeatherInfo;
import com.eamon.rtbau.weather.service.GetBadWeatherService;
//import net.sf.json.JSON;
//import net.sf.json.JSONObject;
//import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import java.net.URL;
import java.util.List;

/**
 * @author:Eamon
 * @create:2022/3/26,12:12
 * @version:1.0
 */
@Service
public class GetBadWeatherImpl implements GetBadWeatherService {

    @Value("${GDKey}")
    private String GDKey;

    @Override
    public String getBadWeatherCities() throws Exception {

        String cityCode = "110000";

        //外接口路径
        String gdApi = "https://restapi.amap.com/v3/weather/weatherInfo?key=" + GDKey + "&city=" + cityCode + "&extensions=all";
        //链接URL
        URL url = new URL(gdApi);
        //返回结果集
        StringBuffer document = new StringBuffer();
        //创建链接
        URLConnection conn = url.openConnection();
        //读取返回结果集
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            document.append(line);
        }
        reader.close();
        System.out.println(document);
        JSONObject json = JSON.parseObject(document.toString());

        //查看json格式
        System.out.println(json.toString());
        //获取json中某个对象
        String forecasts = json.getString("forecasts");
        //删除开头结尾的[]
        String regex = "^\\[*|\\]*$";
        String forecastsV2 = forecasts.replaceAll(regex, "");
        System.out.println("---" + forecastsV2);
        String s = JSON.parse(forecastsV2).toString();
        WeatherInfo weatherInfo1 = JSONObject.parseObject(s, WeatherInfo.class);
        //获取对象中某个属性的值
//        String val = obj.getString("city");
//        System.out.println(val);

        /**
         * 查询明天有没有恶劣天气
         */


        return null;

    }
}
