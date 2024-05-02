package com.eamon.rtbau.rtbauUser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.eamon.rtbau.config.HttpUtil;
import com.eamon.rtbau.config.IpAdrressUtil;
import com.eamon.rtbau.rtbauUser.entity.pojo.*;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
        LambdaQueryChainWrapper<RtbauUser> wrapper = iRtbauUserService.lambdaQuery();
        if(StringUtils.isNotEmpty(rtbauUser.getUid())){
            wrapper.eq(RtbauUser::getUid, rtbauUser.getUid());
        }
        if (StringUtils.isNotEmpty(rtbauUser.getLastQrcode())){
            wrapper.eq(RtbauUser::getLastQrcode, rtbauUser.getLastQrcode());
        }
        RtbauUser user = wrapper.last("limit 0,1").one();
        if (user != null && user.getUid() != null) {
            rtbauUser.setId(user.getId());
            flag = iRtbauUserService.updateById(rtbauUser);
        } else {
            flag = iRtbauUserService.save(rtbauUser);
        }
        return flag;
    }

    @Override
    public IPLocationOutput getIPLocation(String strIp, HttpServletRequest request) {
        IPLocationOutput output = new IPLocationOutput();
        try {
            if (strIp == "") {
                IpAdrressUtil ipAdrressUtil = new IpAdrressUtil();
                strIp = ipAdrressUtil.getIpAdrress(request);
                log.info("请求ip:{}",strIp);
            }
            HttpUtil httpUtil = new HttpUtil();
            String html = httpUtil.get("https://whois.pconline.com.cn/ipJson.jsp?ip=" + strIp + "&json=true", "", "", new HashMap<>());
            IPLocation ipLocation = JSONObject.parseObject(html, IPLocation.class);
            if (!Objects.isNull(ipLocation.getErr())) {
                // 有可能获取不到所在区，依次取上一级code
                if(!Objects.equals(ipLocation.getRegionCode(),"0")){
                    output.setLocationCode(ipLocation.getRegionCode());
                    output.setLocationName(ipLocation.getRegionNames());
                    return output;
                }
                if(!Objects.equals(ipLocation.getCityCode(),"0")){
                    output.setLocationCode(ipLocation.getCityCode());
                    output.setLocationName(ipLocation.getCity());
                    return output;
                }
                if(!Objects.equals(ipLocation.getProCode(),"0")){
                    output.setLocationCode(ipLocation.getProCode());
                    output.setLocationName(ipLocation.getPro());
                    return output;
                }
            }
        } catch (Exception ex) {
            log.error("获取位置信息错误：{}", ex.getMessage());
        }
        return output;
    }

    @Override
    public Boolean userIsExist(RtbauUser rtbauUser) {
        RtbauUser user = iRtbauUserService.lambdaQuery().eq(RtbauUser::getUid, rtbauUser.getUid()).last("limit 0,1").one();
        if (user != null && user.getUid() != null) {
            return true;
        }
        return false;
    }

    @Override
    public GetUserQROutput getUserQR(GetUserQRInput input) {
        GetUserQROutput output = new GetUserQROutput();
        HttpUtil httpUtil = new HttpUtil();
        Map<String, Object> param = new HashMap<>();
        param.put("appToken","AT_fJJCxoV3Qjzl4oVleQ8goJAAvzGiEVFe");
        param.put("extra",JSONObject.toJSONString(input));
        param.put("validTime",1800);
        String params = JSONObject.toJSONString(param);
        String qr = httpUtil.jsonPostV2("https://wxpusher.zjiecode.com/api/fun/create/qrcode", "", "", params);
        UserQR userQR = JSONObject.parseObject(qr, UserQR.class);
        if(!Objects.isNull(userQR.getData())){
            output.setQrCode(userQR.getData().getCode());
            output.setQrUrl(userQR.getData().getUrl());
        }
        return output;
    }

    @Override
    public String pushMsg(PushMsg pushMsg) {
        GetUserQRInput getUserQRInput = JSONObject.parseObject(pushMsg.getExtra(), GetUserQRInput.class);
        String uid = pushMsg.getUids().get(0);
        System.out.println(uid);
        RtbauUser user = iRtbauUserService.lambdaQuery().eq(RtbauUser::getUid, uid).last("limit 0,1").one();
        HttpUtil httpUtil = new HttpUtil();
        Map<String, Object> param = new HashMap<>();
        param.put("appToken","AT_fJJCxoV3Qjzl4oVleQ8goJAAvzGiEVFe");
        param.put("content","<h1>欢迎来到EamonPlanet！</h1><br/><p style=\"color:red;\">"+uid+"在"+getUserQRInput.getCityName()+"于"+user.getCreateTime()+"登录"+"</p>");
        param.put("contentType",2);
        param.put("summary",uid+"在"+getUserQRInput.getCityName()+"登录");
        param.put("uids",pushMsg.getUids());
        param.put("url","http://hello.xiaoming100.club");
        String params = JSONObject.toJSONString(param);
        String qr = httpUtil.jsonPostV2("https://wxpusher.zjiecode.com/api/send/message", "", "", params);
        return null;
    }
}
