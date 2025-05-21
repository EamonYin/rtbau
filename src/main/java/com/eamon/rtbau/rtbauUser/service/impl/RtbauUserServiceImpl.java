package com.eamon.rtbau.rtbauUser.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.eamon.rtbau.config.HttpUtil;
import com.eamon.rtbau.config.IpAdrressUtil;
import com.eamon.rtbau.rtbauUser.entity.pojo.*;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.Result;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
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


    @Value("${appToken}")
    private String appToken;

    @Override
    public Boolean saveUserMsg(RtbauUser rtbauUser) {
        Boolean flag = false;
        //判断是修改还是新增
        LambdaQueryChainWrapper<RtbauUser> wrapper = iRtbauUserService.lambdaQuery();
        if (StringUtils.isNotEmpty(rtbauUser.getUid())) {
            wrapper.eq(RtbauUser::getUid, rtbauUser.getUid());
        }
        if (StringUtils.isNotEmpty(rtbauUser.getLastQrcode())) {
            wrapper.eq(RtbauUser::getLastQrcode, rtbauUser.getLastQrcode());
        }
//        RtbauUser user = wrapper.last("limit 0,1").one();
        RtbauUser user = wrapper.one();
        if (user != null && user.getUid() != null) {
            rtbauUser.setId(user.getId());
            rtbauUser.setIsNew(0);
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
//                IpAdrressUtil ipAdrressUtil = new IpAdrressUtil();
//                strIp = ipAdrressUtil.getIpAdrress(request);

                //服务器域名
//                String Host = request.getHeader("Host");
//                //（方式一）来访者公网IP
//                String realIp = request.getHeader("X-Real-IP");
//                //（方式二）来访者公网IP
//                String XForwardedFor = request.getHeader("X-Forwarded-For");
                //WEB应用IP（127.0.0.1）
//                String getRemoteAddr = request.getRemoteAddr();

                //获取真实IP
                if (strIp == null || strIp.length() == 0 || "unknown".equalsIgnoreCase(strIp)) {
                    strIp = request.getHeader("X-Real-IP");
                }
                if (strIp == null || strIp.length() == 0 || "unknown".equalsIgnoreCase(strIp)) {
                    try {
                        int first = request.getHeader("X-Forwarded-For").indexOf(",");
                        strIp = request.getHeader("X-Forwarded-For").substring(0, first);
                        log.info("/rtbau-user/getIPLocation 有多级代理ip:{}", request.getHeader("X-Forwarded-For"));
                        log.info("请求 first:{},X-Forwarded-For:{}", first, request.getHeader("X-Forwarded-For").substring(0, first));
                    } catch (Exception e) {
                        strIp = request.getHeader("X-Forwarded-For");
                        log.warn("/rtbau-user/getIPLocation 只有一级代理,故直接取ip:{}", strIp);
                    }
                }
//                //上面都还没有，有可能就是局域网内的IP进行操作的，则获取请求头的局域网IP
                if (strIp == null || strIp.length() == 0 || "unknown".equalsIgnoreCase(strIp)) {
                    strIp = request.getRemoteAddr();
                    log.error("/rtbau-user/getIPLocation 未获取到客户端真实ip！");
                }
                log.info("请求 ip:{}", strIp);
            }
            HttpUtil httpUtil = new HttpUtil();
            String html = httpUtil.get("https://whois.pconline.com.cn/ipJson.jsp?ip=" + strIp + "&json=true", "", "", new HashMap<>());
            IPLocation ipLocation = JSONObject.parseObject(html, IPLocation.class);
            if (!Objects.isNull(ipLocation.getErr()) || (Objects.equals(ipLocation.getRegionCode(), "0") && Objects.equals(ipLocation.getCityCode(), "0") && Objects.equals(ipLocation.getProCode(), "0"))) {
                // 有可能获取不到所在区，依次取上一级code
                if (!Objects.equals(ipLocation.getRegionCode(), "0")) {
                    output.setLocationCode(ipLocation.getRegionCode());
                    output.setLocationName(ipLocation.getRegionNames());
                    return output;
                }
                if (!Objects.equals(ipLocation.getCityCode(), "0")) {
                    output.setLocationCode(ipLocation.getCityCode());
                    output.setLocationName(ipLocation.getCity());
                    return output;
                }
                if (!Objects.equals(ipLocation.getProCode(), "0")) {
                    output.setLocationCode(ipLocation.getProCode());
                    output.setLocationName(ipLocation.getPro());
                    return output;
                }
            }
            // TODO: ip9不能返回对应城市的Code后期换一个兜底方案
//            else {
//                // todo:如果失败了，换一个接口获取ip地址信息https://www.ip9.com.cn/?source=iui
//                html = httpUtil.get("https://ip9.com.cn/get?ip=" + strIp, "", "", new HashMap<>());
//                IP9Msg ip9Msg = JSONObject.parseObject(html, IP9Msg.class);
//                IP9Data ip9MsgData = ip9Msg.getData();
//                if (Objects.equals(ip9Msg.getRet(),200)) {
//                    output.setLocationCode(ip9MsgData.get);
//                    output.setLocationName();
//                }
//            }

        } catch (Exception ex) {
            log.error("获取位置信息错误：{}", ex.getMessage());
        }
        return output;
    }

    @Override
    public Boolean userIsExist(RtbauUser rtbauUser) {
        RtbauUser user = iRtbauUserService.lambdaQuery().eq(RtbauUser::getUid, rtbauUser.getUid()).eq(RtbauUser::getIsDeleted,0).last("limit 0,1").one();
        log.info("userIsExist:{}",JSONObject.toJSONString(user));
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
        param.put("appToken", appToken);
        param.put("extra", JSONObject.toJSONString(input));
        param.put("validTime", 1800);
        String params = JSONObject.toJSONString(param);
        String qr = httpUtil.jsonPostV2("https://wxpusher.zjiecode.com/api/fun/create/qrcode", "", "", params);
        UserQR userQR = JSONObject.parseObject(qr, UserQR.class);
        if (!Objects.isNull(userQR.getData())) {
            output.setQrCode(userQR.getData().getCode());
            output.setQrUrl(userQR.getData().getUrl());
        }
        log.info("getUserQR输出:{}",JSONObject.toJSONString(output));
        return output;
    }

    @Override
    public String pushMsg(PushMsg pushMsg) {
        GetUserQRInput getUserQRInput = JSONObject.parseObject(pushMsg.getExtra(), GetUserQRInput.class);
        String uid = pushMsg.getUids().get(0);
        log.info("回调pushMsg:{}", uid);
//        RtbauUser user = iRtbauUserService.lambdaQuery().eq(RtbauUser::getUid, uid).last("limit 0,1").one();
        RtbauUser user = iRtbauUserService.lambdaQuery().eq(RtbauUser::getUid, uid).one();
//        HttpUtil httpUtil = new HttpUtil();
//        Map<String, Object> param = new HashMap<>();
//        param.put("appToken",appToken);
//        param.put("content","<h1>欢迎来到EamonPlanet！</h1><br/><p style=\"color:red;\">"+uid+"在"+getUserQRInput.getCityName()+"于"+user.getCreateTime()+"登录"+"</p>");
//        param.put("contentType",2);
//        param.put("summary",uid+"在"+getUserQRInput.getCityName()+"登录");
//        param.put("uids",pushMsg.getUids());
//        param.put("url","http://hello.xiaoming100.club");
//        String params = JSONObject.toJSONString(param);
//        String qr = httpUtil.jsonPostV2("https://wxpusher.zjiecode.com/api/send/message", "", "", params);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_HTML);
        message.setUid(uid);
        message.setAppToken(appToken);
        boolean isNewFlg = Objects.equals(user.getIsNew(), 1);
        if(isNewFlg){ //新用户第一次注册
            message.setSummary("EamonPlanet注册成功,点击卡片查看");
            message.setContent("<h1>\uD83C\uDF89恭喜您注册EamonPlanet成功\uD83C\uDF89</h1>\n" +
                    "<br/>\n" +
                    "<h4>如果未来某一天存在雨雪天气，EamonPlanet会在这里推送微信消息提醒您带伞的～</h4>\n" +
                    "<br/>\n" +
                    "<p style=\"color:red;\">" + uid + "在" + getUserQRInput.getCityName() + "于" + sdf.format(user.getCreateTime()) + " 登录 </p>\n" +
                    "<br/>\n" +
                    "<span>您可以通过上方的「链接：点击查看」</span><br/>\n" +
                    "<button onclick=\"location.href='http://hello.xiaoming100.club#/guide?uid="+uid+"'\">点击查看详情</button>");
        }else { // 老用户再次扫码
            message.setSummary("欢迎回到EamonPlanet,点击卡片查看");
            message.setContent("<h1>\uD83C\uDF89欢迎回到EamonPlanet！\uD83C\uDF89</h1>\n" +
                    "<br/>\n" +
                    "<h4>如果未来某一天存在雨雪天气，EamonPlanet会在这里推送微信消息提醒您带伞的～</h4>\n" +
                    "<br/>\n" +
                    "<p style=\"color:red;\">" + uid + "在" + getUserQRInput.getCityName() + "于" + sdf.format(user.getCreateTime()) + " 登录 </p>\n" +
                    "<br/>\n" +
                    "<span>您可以通过上方的「链接：点击查看」</span><br/>\n" +
                    "<button onclick=\"location.href='http://hello.xiaoming100.club#/guide?uid="+uid+"'\">点击查看详情</button>");
        }
        message.setUrl("http://hello.xiaoming100.club/#/guide?uid="+uid);
        WxPusher.send(message);
        return null;
    }
}
