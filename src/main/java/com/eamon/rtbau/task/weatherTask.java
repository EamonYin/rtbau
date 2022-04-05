package com.eamon.rtbau.task;

import com.eamon.rtbau.config.WxPushMessageUtil;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.eamon.rtbau.weather.service.GetBadWeatherService;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Eamon
 * @Date: 2022/4/3 22:30
 */
@EnableAsync
@Component
@Slf4j
public class weatherTask {

    @Autowired
    RtbauUserMapper rtbauUserMapper;

    @Autowired
    GetBadWeatherService getBadWeatherService;

    @Value("${appToken}")
    private String appToken;

    private String sign = "";

    @Async
    @Scheduled(cron = "0 0 21 * * ? ")
    public void reminderTask() throws Exception {

        //获取数据库中所有用户涉及的地域编list1
        List<String> allRegionCode = rtbauUserMapper.getAllRegionCode();

        //初始化明天恶劣天气的地域编号list2
        List<String> badWeatherRegionCode = new ArrayList<>();

        //遍历list1获取明天恶劣天气的地域编号放到list2中
        for (String badCode : allRegionCode) {
            Boolean tomorrowIsBadWeather = getBadWeatherService.getTomorrowIsBadWeather(badCode);
            if (tomorrowIsBadWeather) {
                badWeatherRegionCode.add(badCode);
            }
        }

        //如果存在恶劣天气城市，执行下面的逻辑
        if (badWeatherRegionCode.size() != 0) {
            log.info("【*】运行时间：[" + new Date() + "] 存在恶劣天气城市，执行发送消息逻辑");
            //获取数据库中恶劣天气地域的所有用户uid
            List<String> sendUids = rtbauUserMapper.getSendUids(badWeatherRegionCode);

            //给list2中的用户发信息
            for (String uid : sendUids) {
                Result result = sendText(uid);
                System.out.println("消息:" + result);
            }
        } else {
            log.info("运行时间：[" + new Date() + "] 不存在恶劣天气城市");
        }
    }

    public Result sendText(String uid) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_TEXT);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setContent("【记得带伞！！！】\n\n\n" + sign);
        return WxPusher.send(message);
    }
}
