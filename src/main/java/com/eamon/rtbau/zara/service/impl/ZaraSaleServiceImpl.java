package com.eamon.rtbau.zara.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eamon.rtbau.zara.entity.ZaraSale;
import com.eamon.rtbau.zara.mapper.ZaraSaleMapper;
import com.eamon.rtbau.zara.service.IZaraSaleService;
import io.swagger.models.auth.In;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
@Service
@Log4j2
public class ZaraSaleServiceImpl extends ServiceImpl<ZaraSaleMapper, ZaraSale> implements IZaraSaleService {


    @Override
    public List<ZaraSale> getZaraSalesLst() {
        QueryWrapper<ZaraSale> zaraSaleQW = new QueryWrapper<>();
        zaraSaleQW.lambda().eq(ZaraSale::getIsDelete,0);
        return this.baseMapper.selectList(zaraSaleQW);
    }

    @Override
    public Boolean isSendZara(String code, Integer price) {
        QueryWrapper<ZaraSale> zaraSaleQW = new QueryWrapper<>();
        zaraSaleQW.lambda().eq(ZaraSale::getCode, code).gt(ZaraSale::getOldPrice, price);
        ZaraSale zaraSale = this.baseMapper.selectOne(zaraSaleQW);
        // 没推送记录
        if (Objects.isNull(zaraSale) || zaraSale.getIsDelete() > 0) {
            return false;
        }
        zaraSale.setIsDelete(1);
        this.baseMapper.updateById(zaraSale);
        // 有记录没推过
        return true;
    }

    @Override
    public List<ZaraSale> insertSale(ZaraSale zaraSale) {
        QueryWrapper<ZaraSale> zaraSaleQW = new QueryWrapper<>();
        zaraSaleQW.lambda().eq(ZaraSale::getCode, zaraSale.getCode()).eq(ZaraSale::getUid,zaraSale.getUid());
        ZaraSale oldZaraSale = this.baseMapper.selectOne(zaraSaleQW);
        if (Objects.isNull(oldZaraSale)) {
            this.baseMapper.insert(zaraSale);
        } else {
            oldZaraSale.setOldPrice(zaraSale.getOldPrice());
            oldZaraSale.setUid(zaraSale.getUid());
            oldZaraSale.setIsDelete(zaraSale.getIsDelete());
            oldZaraSale.setColorName(zaraSale.getColorName());
            this.baseMapper.updateById(oldZaraSale);
        }
        QueryWrapper<ZaraSale> newZaraSaleQW = new QueryWrapper<>();
        return this.baseMapper.selectList(newZaraSaleQW);
    }

    @Override
    public Integer deleteSale() {
        QueryWrapper<ZaraSale> zaraSaleQW = new QueryWrapper<>();
        return this.baseMapper.delete(zaraSaleQW);
    }
}
