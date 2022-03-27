package com.eamon.rtbau.dictRegion.controller;


import com.eamon.rtbau.dictRegion.entity.pojo.DictRegion;
import com.eamon.rtbau.dictRegion.service.IDictRegionService;
import com.eamon.rtbau.dictRegion.service.impl.DictRegionServiceImpl;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-27
 */
@RestController
@RequestMapping("/dict-region")
public class DictRegionController {

    @Autowired
    IDictRegionService iDictRegionService;

    //获取省级城市
    @GetMapping("/getProvince")
    @ApiModelProperty(value = "获取省级城市")
    public List<DictRegion> getProvince(){
        List<DictRegion> province = iDictRegionService.getProvince();
        return province;
    }
}
