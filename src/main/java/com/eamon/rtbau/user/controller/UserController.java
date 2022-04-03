package com.eamon.rtbau.user.controller;

import com.eamon.rtbau.config.DataRepo;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import com.zjiecode.wxpusher.client.bean.callback.AppSubscribeBean;
import com.zjiecode.wxpusher.client.bean.callback.BaseCallBackReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


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

    /**
     * wxpusher回调
     *
     * @param callBackReq
     * @return
     */
    @PostMapping("/callback")
    public String callback(@RequestBody BaseCallBackReq callBackReq, HttpServletResponse response) throws IOException {
        log.info("收到wxpusher回调:{}", JSONObject.toJSONString(callBackReq));

        if (BaseCallBackReq.ACTION_APP_SUBSCRIBE.equalsIgnoreCase(callBackReq.getAction())) {
            AppSubscribeBean appSubscribeBean = JSONObject.parseObject(JSONObject.toJSONString(callBackReq.getData()), AppSubscribeBean.class);
            log.info("【appSubscribeBean】" + appSubscribeBean);
            if (!StringUtils.isEmpty(appSubscribeBean.getUid())) {

                DataRepo.put(appSubscribeBean.getExtra(), appSubscribeBean.getUid());
                log.info("存储回调数据:{}", JSONObject.toJSONString(appSubscribeBean));
                //扫码以后，发送一条消息给用户
                Message message = new Message();
                message.setAppToken(appToken);
                message.setContentType(Message.CONTENT_TYPE_TEXT);
                message.setContent("不加限制的自由是很可怕的，因为很容易让任何人滑向深渊。");
                message.setUid(appSubscribeBean.getUid());
                message.setUrl("http://www.xiaoming100.club:1263/#/home?uid="+appSubscribeBean.getUid());//可选参数
                Result<List<MessageResult>> result = WxPusher.send(message);
                log.info("消息发送：" + result);

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
