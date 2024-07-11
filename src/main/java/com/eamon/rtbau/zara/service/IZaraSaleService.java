package com.eamon.rtbau.zara.service;

import com.eamon.rtbau.zara.entity.ZaraSale;

import java.util.List;

public interface IZaraSaleService {

    List<ZaraSale> getZaraSalesLst();
    Boolean isSendZara(String code,Integer price);

    List<ZaraSale> insertSale(ZaraSale zaraSale);

    Integer deleteSale();
}
