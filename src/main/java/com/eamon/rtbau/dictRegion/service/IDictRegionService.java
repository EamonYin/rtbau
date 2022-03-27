package com.eamon.rtbau.dictRegion.service;

import com.eamon.rtbau.dictRegion.entity.pojo.DictRegion;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-27
 */
public interface IDictRegionService extends IService<DictRegion> {

    //获取省级城市
    public List<DictRegion> getProvince();

}
