package com.eamon.rtbau.rtbauUser.controller;


import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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

    //存储用户信息，并返回存储结果
    @PostMapping("/saveUserMsg")
    public Boolean saveUserMsg(@RequestBody RtbauUser rtbauUser) {
        Boolean flag = iRtbauUserService.saveUserMsg(rtbauUser);
        System.out.println("保存结果" + flag);
        return flag;
    }
}
