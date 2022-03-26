package com.eamon.rtbau.weather.service;

/**
 * @author:Eamon
 * @create:2022/3/26,12:12
 * @version:1.0
 */
public interface GetBadWeatherService {
    /**
     * 指定城市明天是否为恶劣天气
     * @param cityCode 城市代码
     * @return true:是恶劣天气；false:不是恶劣天气
     * @throws Exception
     */
    String getTomorrowIsBadWeather(String cityCode) throws Exception;
}
