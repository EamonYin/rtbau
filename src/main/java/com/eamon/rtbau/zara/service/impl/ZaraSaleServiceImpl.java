package com.eamon.rtbau.zara.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eamon.rtbau.zara.entity.BrandSale;
import com.eamon.rtbau.zara.mapper.ZaraSaleMapper;
import com.eamon.rtbau.zara.service.IZaraSaleService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
@Log4j2
public class ZaraSaleServiceImpl extends ServiceImpl<ZaraSaleMapper, BrandSale> implements IZaraSaleService {


    @Override
    public List<BrandSale> getZaraSalesLst() {
        QueryWrapper<BrandSale> zaraSaleQW = new QueryWrapper<>();
        zaraSaleQW.lambda().eq(BrandSale::getIsDelete,0);
        return this.baseMapper.selectList(zaraSaleQW);
    }

    @Override
    public Boolean isSendZara(String code, Integer price) {
        QueryWrapper<BrandSale> zaraSaleQW = new QueryWrapper<>();
        zaraSaleQW.lambda().eq(BrandSale::getCode, code).gt(BrandSale::getOldPrice, price);
        BrandSale brandSale = this.baseMapper.selectOne(zaraSaleQW);
        // 没推送记录
        if (Objects.isNull(brandSale) || brandSale.getIsDelete() > 0) {
            return false;
        }
        brandSale.setIsDelete(1);
        this.baseMapper.updateById(brandSale);
        // 有记录没推过
        return true;
    }

    @Override
    public List<BrandSale> insertSale(BrandSale brandSale) {
        QueryWrapper<BrandSale> zaraSaleQW = new QueryWrapper<>();
        zaraSaleQW.lambda().eq(BrandSale::getCode, brandSale.getCode()).eq(BrandSale::getUid, brandSale.getUid());
        BrandSale oldBrandSale = this.baseMapper.selectOne(zaraSaleQW);
        if (Objects.isNull(oldBrandSale)) {
            this.baseMapper.insert(brandSale);
        } else {
            oldBrandSale.setOldPrice(brandSale.getOldPrice());
            oldBrandSale.setUid(brandSale.getUid());
            oldBrandSale.setIsDelete(brandSale.getIsDelete());
            oldBrandSale.setColorName(brandSale.getColorName());
            oldBrandSale.setBrandIdentity(brandSale.getBrandIdentity());
            this.baseMapper.updateById(oldBrandSale);
        }
        QueryWrapper<BrandSale> newZaraSaleQW = new QueryWrapper<>();
        return this.baseMapper.selectList(newZaraSaleQW);
    }

    @Override
    public Integer deleteSale() {
        QueryWrapper<BrandSale> zaraSaleQW = new QueryWrapper<>();
        return this.baseMapper.delete(zaraSaleQW);
    }
}
