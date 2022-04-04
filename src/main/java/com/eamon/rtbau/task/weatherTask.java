package com.eamon.rtbau.task;

import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import com.eamon.rtbau.weather.service.GetBadWeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Eamon
 * @Date: 2022/4/3 22:30
 */
@EnableAsync
@Component
public class weatherTask {

    @Autowired
    RtbauUserMapper rtbauUserMapper;

    @Autowired
    GetBadWeatherService getBadWeatherService;

    @Async
    @Scheduled(cron = "0 0 21 * * ? ")
    public void reminderTask() throws Exception {

        //获取数据库中所有用户涉及的地域编list1
        List<String> allRegionCode = rtbauUserMapper.getAllRegionCode();

        //初始化明天恶劣天气的地域编号list2
        List<String> badWeather = new ArrayList<>();

        //遍历list1获取明天恶劣天气的地域编号放到list2中
        for ( String badCode: allRegionCode) {
            Boolean tomorrowIsBadWeather = getBadWeatherService.getTomorrowIsBadWeather(badCode);
            if(tomorrowIsBadWeather){
                badWeather.add(badCode);
            }
        }

        //初始化list3，存放用户uid
        List<String> uids = new ArrayList<>();

        //匹配数据库中list2地域的用户，将uid放入list3


        //给list3中的用户发信息

    }
}
