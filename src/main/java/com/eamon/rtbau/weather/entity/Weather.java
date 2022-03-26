package com.eamon.rtbau.weather.entity;

import lombok.Data;

/**
 * @author:Eamon
 * @create:2022/3/26,14:45
 * @version:1.0
 */
@Data
public class Weather {
    private String date;
    private Integer week;
    private String dayweather;
    private String nightweather;
    private Integer daytemp;
    private Integer nighttemp;
    private String daywind;
    private String nightwind;
    private String daypower;
    private String nightpower;
}
