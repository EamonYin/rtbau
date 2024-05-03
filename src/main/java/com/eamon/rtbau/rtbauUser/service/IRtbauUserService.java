package com.eamon.rtbau.rtbauUser.service;

import com.eamon.rtbau.rtbauUser.entity.pojo.*;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author EamonYin
 * @since 2022-03-26
 */
public interface IRtbauUserService extends IService<RtbauUser> {
    Boolean saveUserMsg(RtbauUser rtbauUser);

    IPLocationOutput getIPLocation(String strIp, HttpServletRequest request);

    Boolean userIsExist(RtbauUser rtbauUser);

    GetUserQROutput getUserQR(GetUserQRInput input);

    String pushMsg(PushMsg pushMsg);

}
