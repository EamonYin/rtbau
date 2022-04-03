package com.eamon.rtbau.rtbauUser.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eamon.rtbau.config.CopyUtil;
import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eamon.rtbau.config.CopyUtil.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-26
 */
@Service
public class RtbauUserServiceImpl extends ServiceImpl<RtbauUserMapper, RtbauUser> implements IRtbauUserService {

    @Autowired
    IRtbauUserService iRtbauUserService;

    @Override
    public Boolean saveUserMsg(RtbauUser rtbauUser) {

        Boolean flag = false;

        //判断是修改还是新增
        RtbauUser user = iRtbauUserService.lambdaQuery().eq(RtbauUser::getUid, rtbauUser.getUid()).last("limit 0,1").one();
        if (user != null && user.getUid() != null) {
            rtbauUser.setId(user.getId());
            flag = iRtbauUserService.updateById(rtbauUser);
        } else {
            flag = iRtbauUserService.save(rtbauUser);
        }
        return flag;
    }
}
