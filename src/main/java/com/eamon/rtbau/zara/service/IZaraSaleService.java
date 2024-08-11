package com.eamon.rtbau.zara.service;

import com.eamon.rtbau.zara.entity.BrandSale;

import java.util.List;

public interface IZaraSaleService {

    List<BrandSale> getZaraSalesLst();
    Boolean isSendZara(String code,Integer price);

    List<BrandSale> insertSale(BrandSale brandSale);

    Integer deleteSale();
}
