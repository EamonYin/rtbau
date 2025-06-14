package com.eamon.rtbau.weather.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaSubscribeMessage;
import com.eamon.rtbau.config.WxMaConfiguration;
import com.eamon.rtbau.config.WxMaProperties;
import com.eamon.rtbau.rtbauUser.entity.pojo.IPLocationOutput;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.eamon.rtbau.weather.service.impl.GetBadWeatherImpl;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.binarywang.wx.miniapp.bean.WxMaTemplateData;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author:Eamon
 * @create:2022/3/26,12:57
 * @version:1.0
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    GetBadWeatherImpl getBadWeatherImpl;

    @Autowired
    IRtbauUserService iRtbauUserService;

    @Autowired
    WxMaProperties properties;

    @GetMapping("/get")
    public Boolean loginAdminUser(HttpServletRequest request) throws Exception {
        String cityCode = "";
        IPLocationOutput ipLocation = iRtbauUserService.getIPLocation("", request);
        if (Objects.equals(ipLocation.getLocationCode(), "999999") || Objects.equals(ipLocation.getLocationCode(), "0") || Objects.isNull(ipLocation)) {
            //北京代码
            cityCode = "450900";
        }
        cityCode = ipLocation.getLocationCode();
        return getBadWeatherImpl.getTomorrowIsBadWeather(cityCode);
    }

    /**
     * 微信小程序推送订阅消息
     * create By KingYiFan on 2020/01/06
     */
    @ApiOperation(value = "微信小程序推送订阅消息", notes = "微信小程序推送订阅消息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "小程序appId", dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "openId", value = "用户openId", dataType = "String", paramType = "query")})
    @GetMapping(value = "/sendDYTemplateMessage")
    @ResponseBody
    public Object sendDYTemplateMessage(String appId, String openId) throws Exception {

        WxMaSubscribeMessage subscribeMessage = new WxMaSubscribeMessage();
        // 获取当前日期时间
        LocalDateTime currentDate = LocalDateTime.now();
        String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
        String formattedDateTime = currentDate.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss"));


        //跳转小程序页面路径
        subscribeMessage.setPage("pages/webView/rtbau");
        //模板消息id
        subscribeMessage.setTemplateId("hyBA7_Hm8oN82VIf2C_Q22CE1TzPsx75gDN-W_RH1EQ");
        //给谁推送 用户的openid （可以调用根据code换openid接口)
        subscribeMessage.setToUser(openId);
        subscribeMessage.setMiniprogramState("trial");

        subscribeMessage.setData(Lists.newArrayList(
                new WxMaSubscribeMessage.MsgData("path","www.baidu.com"),
                new WxMaSubscribeMessage.MsgData("time1",formattedDate),
                new WxMaSubscribeMessage.MsgData("thing2","地点"),
                new WxMaSubscribeMessage.MsgData("thing3","测试哈哈哈哈"),
                new WxMaSubscribeMessage.MsgData("time5",formattedDateTime)
        ));

        try {
            //获取微信小程序配置：
            final WxMaService wxService = WxMaConfiguration.getMaService(appId);
            //进行推送
            wxService.getMsgService().sendSubscribeMsg(subscribeMessage);
            return "推送成功";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "推送失败";
    }

}
