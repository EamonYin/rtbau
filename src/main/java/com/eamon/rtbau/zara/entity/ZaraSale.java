package com.eamon.rtbau.zara.entity;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class ZaraSale {
    private Long id;
    private String code;
    private Integer isDelete;
    private Integer oldPrice;
    private String uid;
    private String colorName;
}
