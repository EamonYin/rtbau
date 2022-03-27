package com.eamon.rtbau.dictRegion.service.impl;

import com.eamon.rtbau.dictRegion.entity.pojo.DictRegion;
import com.eamon.rtbau.dictRegion.mapper.DictRegionMapper;
import com.eamon.rtbau.dictRegion.service.IDictRegionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-27
 */
@Service
public class DictRegionServiceImpl extends ServiceImpl<DictRegionMapper, DictRegion> implements IDictRegionService {

    @Autowired
    IDictRegionService iDictRegionService;

    @Override
    public List<DictRegion> getProvince() {
        List<DictRegion> list = iDictRegionService.lambdaQuery().eq(DictRegion::getParentRegionCode, 0).list();
        return list;
    }
}
