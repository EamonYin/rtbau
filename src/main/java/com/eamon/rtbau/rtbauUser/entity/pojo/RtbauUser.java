package com.eamon.rtbau.rtbauUser.entity.pojo;

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
 * @since 2022-03-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value="RtbauUser对象", description="")
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


}
