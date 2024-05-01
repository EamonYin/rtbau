package com.eamon.rtbau.rtbauUser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.eamon.rtbau.config.CopyUtil;
import com.eamon.rtbau.config.HttpUtil;
import com.eamon.rtbau.config.IpAdrressUtil;
import com.eamon.rtbau.rtbauUser.entity.pojo.IPLocation;
import com.eamon.rtbau.rtbauUser.entity.pojo.PushMsg;
import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.eamon.rtbau.rtbauUser.entity.pojo.UserQR;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.eamon.rtbau.config.CopyUtil.*;

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
                log.info("请求ip:{}",strIp);
            }
            HttpUtil httpUtil = new HttpUtil();
            String html = httpUtil.get("https://whois.pconline.com.cn/ipJson.jsp?ip=" + strIp + "&json=true", "", "", new HashMap<>());
            IPLocation ipLocation = JSONObject.parseObject(html, IPLocation.class);
            if (!Objects.isNull(ipLocation.getErr())) {
                // 有可能获取不到所在区，依次取上一级code
                if(!Objects.equals(ipLocation.getRegionCode(),"0")){
                    return ipLocation.getRegionCode();
                }
                if(!Objects.equals(ipLocation.getCityCode(),"0")){
                    return ipLocation.getCityCode();
                }
                if(!Objects.equals(ipLocation.getProCode(),"0")){
                    return ipLocation.getProCode();
                }
            }
        } catch (Exception ex) {
            log.error("获取位置信息错误：{}", ex.getMessage());
        }
        return "";
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
    public String getUserQR() {
        HttpUtil httpUtil = new HttpUtil();
        Map<String, Object> param = new HashMap<>();
        param.put("appToken","AT_fJJCxoV3Qjzl4oVleQ8goJAAvzGiEVFe");
        param.put("extra",1);
        param.put("validTime",1800);
        String params = JSONObject.toJSONString(param);
        String qr = httpUtil.jsonPostV2("https://wxpusher.zjiecode.com/api/fun/create/qrcode", "", "", params);
        UserQR userQR = JSONObject.parseObject(qr, UserQR.class);
        return userQR.getData().getUrl();
    }

    @Override
    public String pushMsg(PushMsg pushMsg) {
        HttpUtil httpUtil = new HttpUtil();
        Map<String, Object> param = new HashMap<>();
        param.put("appToken","AT_fJJCxoV3Qjzl4oVleQ8goJAAvzGiEVFe");
        param.put("content","<h1>H1标题</h1><br/><p style=\\\"color:red;\\\">欢迎来到EamonPlanet！</p>");
        param.put("contentType",2);
        param.put("summary",pushMsg.getUids().toString());
        param.put("uids",pushMsg.getUids());
        String params = JSONObject.toJSONString(param);
        String qr = httpUtil.jsonPostV2("https://wxpusher.zjiecode.com/api/send/message", "", "", params);
        return null;
    }

}
