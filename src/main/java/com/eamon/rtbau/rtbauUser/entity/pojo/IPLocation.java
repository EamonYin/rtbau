package com.eamon.rtbau.rtbauUser.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class IPLocation {

    /**
     * ip
     */
    private String ip;
    /**
     * pro
     */
    private String pro;
    /**
     * proCode
     */
    private String proCode;
    /**
     * city
     */
    private String city;
    /**
     * cityCode
     */
    private String cityCode;
    /**
     * region
     */
    private String region;
    /**
     * regionCode
     */
    private String regionCode;
    /**
     * addr
     */
    private String addr;
    /**
     * regionNames
     */
    private String regionNames;
    /**
     * err
     */
    private String err;
}
