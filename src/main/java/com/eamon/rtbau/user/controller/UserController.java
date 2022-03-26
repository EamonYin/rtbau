package com.eamon.rtbau.user.controller;

import com.eamon.rtbau.user.DataRepo;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.callback.AppSubscribeBean;
import com.zjiecode.wxpusher.client.bean.callback.BaseCallBackReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;


/**
 * @author:Eamon
 * @create:2022/3/26,19:21
 * @version:1.0
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Value("${appToken}")
    private String appToken;

    @PostMapping("/callback")
    public String callback(@RequestBody BaseCallBackReq callBackReq) {
        log.info("收到wxpusher回调:{}", JSONObject.toJSONString(callBackReq));
        if (BaseCallBackReq.ACTION_APP_SUBSCRIBE.equalsIgnoreCase(callBackReq.getAction())) {
            AppSubscribeBean appSubscribeBean = JSONObject.parseObject(JSONObject.toJSONString(callBackReq.getData()), AppSubscribeBean.class);
            if (!StringUtils.isEmpty(appSubscribeBean.getExtra())) {
                DataRepo.put(appSubscribeBean.getExtra(), appSubscribeBean.getUid());
                log.info("存储回调数据:{}", JSONObject.toJSONString(appSubscribeBean));
                //扫码以后，发送一条消息给用户
                Message message = new Message();
                message.setContent("扫描成功，你可以使用demo演示程序发送消息");
                message.setContentType(Message.CONTENT_TYPE_TEXT);
                message.setUid(appSubscribeBean.getUid());
                message.setAppToken(appToken);
                WxPusher.send(message);
            } else {
                //无参数二维码（默认二维码）
            }
        }

        //直接返回 空串 即可
        return "";
    }

    @GetMapping("/getWxCallback")
    public void getWxCallback(String str) {
        System.out.println(str);
    }

}
