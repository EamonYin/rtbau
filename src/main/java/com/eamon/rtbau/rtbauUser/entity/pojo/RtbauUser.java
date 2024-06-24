package com.eamon.rtbau.rtbauUser.entity.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RtbauUser对象", description = "")
public class RtbauUser extends Model {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "WxPusherUID")
    private String uid;

    @ApiModelProperty(value = "用户名字")
    private String userName;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "行政区划代码，对应WxPusher的adcode")
    private String regionCode;

    @ApiModelProperty(value = "行政区划名称，对应WxPusher的adcode")
    private String regionName;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @ApiModelProperty(value = "逻辑删除 0未删  1删")
    private Integer isDeleted;

    @ApiModelProperty(value = "最近一次使用的qr")
    private String lastQrcode;

    @ApiModelProperty(value = "是否为新用户")
    private Integer isNew;

}
