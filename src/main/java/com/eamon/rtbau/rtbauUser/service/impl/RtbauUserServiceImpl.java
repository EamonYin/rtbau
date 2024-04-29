package com.eamon.rtbau.rtbauUser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eamon.rtbau.config.CopyUtil;
import com.eamon.rtbau.config.HttpUtil;
import com.eamon.rtbau.config.IpAdrressUtil;
import com.eamon.rtbau.rtbauUser.entity.pojo.IPLocation;
import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eamon.rtbau.config.CopyUtil.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-26
 */
@Service
@Log4j2
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

    @Override
    public String getIPLocation(String strIp, HttpServletRequest request) {
        try {
            if (strIp == "") {
                IpAdrressUtil ipAdrressUtil = new IpAdrressUtil();
                strIp = ipAdrressUtil.getIpAdrress(request);
            }
            HttpUtil httpUtil = new HttpUtil();
            String html = httpUtil.get("https://whois.pconline.com.cn/ipJson.jsp?ip=" + strIp + "&json=true", "", "", new HashMap<>());
            IPLocation ipLocation = JSONObject.parseObject(html, IPLocation.class);
            if (!Objects.isNull(ipLocation.getErr())) {
                return ipLocation.getRegionCode();
            }
        } catch (Exception ex) {
            log.error("获取位置信息错误：{}", ex.getMessage());
        }
        return "";
    }

}
