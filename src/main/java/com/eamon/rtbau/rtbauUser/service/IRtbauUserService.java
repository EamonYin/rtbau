package com.eamon.rtbau.rtbauUser.service;

import com.eamon.rtbau.rtbauUser.entity.pojo.RtbauUser;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-26
 */
public interface IRtbauUserService extends IService<RtbauUser> {
    Boolean saveUserMsg(RtbauUser rtbauUser);

    String getIPLocation(String strIp, HttpServletRequest request);
}
