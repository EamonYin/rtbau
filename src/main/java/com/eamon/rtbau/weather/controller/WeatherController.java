package com.eamon.rtbau.weather.controller;

import com.eamon.rtbau.weather.service.impl.GetBadWeatherImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author:Eamon
 * @create:2022/3/26,12:57
 * @version:1.0
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    GetBadWeatherImpl getBadWeatherImpl;

    @GetMapping("/get")
    public String loginAdminUser() throws Exception {
        //北京代码
        String cityCode = "110000";
        return getBadWeatherImpl.getTomorrowIsBadWeather(cityCode);
    }

}
