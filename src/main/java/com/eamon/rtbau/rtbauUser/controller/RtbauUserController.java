package com.eamon.rtbau.rtbauUser.controller;


import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        ArrayList<String> strings = new ArrayList<>();
        strings.add("120000");
        strings.add("130000");
        return rtbauUserMapper.getSendUids(strings);
    }
}
