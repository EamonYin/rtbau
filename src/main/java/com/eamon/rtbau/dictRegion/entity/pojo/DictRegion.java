package com.eamon.rtbau.dictRegion.entity.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="DictRegion对象", description="")
public class DictRegion extends Model {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "行政区划代码")
    @TableId(value = "region_code", type = IdType.ASSIGN_ID)
    private Integer regionCode;

    @ApiModelProperty(value = "行政区划名称")
    private String regionName;

    @ApiModelProperty(value = "行政区划等级")
    private String regionLevel;

    @ApiModelProperty(value = "上级行政区划'代码")
    private String parentRegionCode;


}
