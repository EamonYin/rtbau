package com.eamon.rtbau.weather.controller;

import com.eamon.rtbau.rtbauUser.entity.pojo.IPLocationOutput;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.eamon.rtbau.weather.service.impl.GetBadWeatherImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

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

    @Autowired
    IRtbauUserService iRtbauUserService;

    @GetMapping("/get")
    public Boolean loginAdminUser(HttpServletRequest request) throws Exception {
        String cityCode = "";
        IPLocationOutput ipLocation = iRtbauUserService.getIPLocation("", request);
        if (Objects.equals(ipLocation.getLocationCode(), "999999") || Objects.equals(ipLocation.getLocationCode(), "0") || Objects.isNull(ipLocation)) {
            //北京代码
            cityCode = "450900";
        }
        cityCode = ipLocation.getLocationCode();
        return getBadWeatherImpl.getTomorrowIsBadWeather(cityCode);
    }

}
