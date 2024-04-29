package com.eamon.rtbau.weather.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eamon.rtbau.weather.entity.Weather;
import com.eamon.rtbau.weather.entity.WeatherInfo;
import com.eamon.rtbau.weather.service.GetBadWeatherService;
//import net.sf.json.JSON;
//import net.sf.json.JSONObject;
//import net.sf.json.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author:Eamon
 * @create:2022/3/26,12:12
 * @version:1.0
 */
@Service
@Log4j2
public class GetBadWeatherImpl implements GetBadWeatherService {

    @Value("${GDKey}")
    private String GDKey;

    @Override
    public Boolean getTomorrowIsBadWeather(String cityCode) throws Exception {

        //获得指定城市天气
        WeatherInfo weatherInfo = parseWeather(cityCode);

        /**
         * 查询明天有没有恶劣天气
         * flag:
         * false:没有恶劣天气
         * true：有恶劣天气
         */
        Boolean flag = false;
        List<Weather> casts = weatherInfo.getCasts();
        //转天的天气情况
        Weather tomorrowWeather = casts.get(1);
        log.info("明天的天气:{}", JSON.toJSON(tomorrowWeather));
        //雨雪匹配规则
        Pattern pattern = Pattern.compile("(雨|雪)");
        String dayweather = tomorrowWeather.getDayweather();
        String nightweather = tomorrowWeather.getNightweather();
        //明天白天是否为雨雪天气
        boolean dayIsBad = pattern.matcher(dayweather).find();
        log.info("dayIsBad:{}",dayIsBad);
        //明天晚上是否为雨雪天气
        boolean nightIsBad = pattern.matcher(nightweather).find();
        log.info("nightIsBad:{}",nightIsBad);
        //全天含雨雪，则判断为明天有恶劣天气
        if(dayIsBad||nightIsBad){
            flag=true;
        }
        return flag;
    }

    //解析高德天气json
    private WeatherInfo parseWeather(String cityCode) throws Exception {
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
        String res = JSON.parse(forecastsV2).toString();
        WeatherInfo weatherInfo = JSONObject.parseObject(res, WeatherInfo.class);

        return weatherInfo;
    }

}
