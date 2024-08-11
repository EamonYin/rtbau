package com.eamon.rtbau.zara.entity;

import lombok.Data;

@Data
public class BrandSale {
    private Long id;
    private String code;
    private Integer isDelete;
    private Integer oldPrice;
    private String uid;
    private String colorName;
    // 品牌标识 zara-zara yyk-优衣库
    private String brandIdentity;
    // 尺码(20240811只有优衣库有尺码筛选)
    private String sizeText;
}
