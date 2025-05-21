package com.eamon.rtbau.rtbauUser.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class IP9Data {
    // 国家/地区
    private String country;
    /**
     * ip
     */
    private String ip;
    /**
     * country_code
     */
    private String country_code;
    /**
     * prov
     */
    private String prov;
    /**
     * city
     */
    private String city;
    /**
     * city_code
     */
    private String city_code;
    /**
     * city_short_code
     */
    private String city_short_code;
    /**
     * area
     */
    private String area;
    /**
     * post_code
     */
    private String post_code;
    /**
     * area_code
     */
    private String area_code;
    /**
     * isp
     */
    private String isp;
    /**
     * lng
     */
    private String lng;
    /**
     * lat
     */
    private String lat;
    /**
     * long_ip
     */
    private Integer long_ip;
    /**
     * big_area
     */
    private String big_area;
}
