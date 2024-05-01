package com.eamon.rtbau.rtbauUser.controller;


import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-26
 */
@RestController
@RequestMapping("/rtbau-user")
public class RtbauUserController {

    @Autowired
    IRtbauUserService iRtbauUserService;

    @Autowired
    RtbauUserMapper rtbauUserMapper;

    //存储用户信息，并返回存储结果
    @PostMapping("/saveUserMsg")
    public Boolean saveUserMsg(@RequestBody RtbauUser rtbauUser) {
        Boolean flag = iRtbauUserService.saveUserMsg(rtbauUser);
        System.out.println("保存结果" + flag);
        return flag;
    }

    @GetMapping("/getSendUids")
    public List<String> getSendUids() {
        List<String> strings = new ArrayList<>();
        strings.add("120000");
        strings.add("130000");
        return rtbauUserMapper.getSendUids(strings);
    }

    // 获取用户ip所在地code
    @GetMapping("/getIPLocation")
    public String getIPLocation(HttpServletRequest request){
        return iRtbauUserService.getIPLocation("",request);
    }

    // 获取用户是否存在
    @PostMapping("/userIsExist")
    public Boolean userIsExist(@RequestBody RtbauUser rtbauUser){
        return iRtbauUserService.userIsExist(rtbauUser);
    }
}
