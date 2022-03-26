package com.eamon.rtbau.weather.entity;

import lombok.Data;

import java.util.List;

/**
 * @author:Eamon
 * @create:2022/3/26,15:24
 * @version:1.0
 */
@Data
public class WeatherInfo {
    private String province;
    List<Weather> casts;
    private String city;
    private Integer adcode;
    private String reporttime;
}
