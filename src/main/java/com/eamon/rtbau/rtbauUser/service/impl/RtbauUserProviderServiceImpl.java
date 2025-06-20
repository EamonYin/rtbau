package com.eamon.rtbau.rtbauUser.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.eamon.rtbau.api.RtbauUserProviderAPI;
import com.eamon.rtbau.api.bean.RtbauUserDTO;
import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService(version = "1.0.0", timeout = 6000)
@Log4j2
public class RtbauUserProviderServiceImpl implements RtbauUserProviderAPI {

    @Autowired
    IRtbauUserService iRtbauUserService;

    @Override
    public Boolean insertRtbauUser(RtbauUserDTO rtbauUserDTO) {
        Boolean flag = false;
        //判断是修改还是新增
        LambdaQueryChainWrapper<RtbauUser> wrapper = iRtbauUserService.lambdaQuery();
        if (StringUtils.isNotEmpty(rtbauUserDTO.getOpenId())) {
            wrapper.eq(RtbauUser::getOpenId, rtbauUserDTO.getOpenId());
        }
        if (StringUtils.isEmpty(rtbauUserDTO.getOpenId()) && StringUtils.isNotEmpty(rtbauUserDTO.getUid())) {
            wrapper.eq(RtbauUser::getUid, rtbauUserDTO.getUid());
        }
        if (StringUtils.isNotEmpty(rtbauUserDTO.getLastQrcode())) {
            wrapper.eq(RtbauUser::getLastQrcode, rtbauUserDTO.getLastQrcode());
        }
        RtbauUser user = wrapper.one();
        if (user != null && StringUtils.isNotEmpty(user.getOpenId())) { //微信小程序场景
            rtbauUserDTO.setId(user.getId());
            rtbauUserDTO.setIsNew(0);
            RtbauUser updateUser = convertToRtbauUser(rtbauUserDTO);
            flag = iRtbauUserService.updateById(updateUser);
        } else if (user != null && user.getUid() != null && StringUtils.isEmpty(user.getOpenId())) { //wxpusher场景
            rtbauUserDTO.setId(user.getId());
            rtbauUserDTO.setIsNew(0);
            RtbauUser updateUser = convertToRtbauUser(rtbauUserDTO);
            flag = iRtbauUserService.updateById(updateUser);
        } else {
            RtbauUser newUser = convertToRtbauUser(rtbauUserDTO);
            flag = iRtbauUserService.save(newUser);
        }
        return flag;
    }

    /**
     * dto转rtbau
     * @param dto
     * @return
     */
    private RtbauUser convertToRtbauUser(RtbauUserDTO dto) {
        RtbauUser user = new RtbauUser();
        BeanUtils.copyProperties(dto, user);
        return user;
    }
}
