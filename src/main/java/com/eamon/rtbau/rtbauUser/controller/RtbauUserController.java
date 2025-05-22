package com.eamon.rtbau.rtbauUser.controller;


import com.alibaba.fastjson.JSONObject;
import com.eamon.rtbau.rtbauUser.entity.pojo.*;
import com.eamon.rtbau.rtbauUser.mapper.RtbauUserMapper;
import com.eamon.rtbau.rtbauUser.service.IRtbauUserService;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.Content;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
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
@Log4j2
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
    public IPLocationOutput getIPLocation(HttpServletRequest request) {
        return iRtbauUserService.getIPLocation("", request);
    }

    // 获取用户是否存在
    @PostMapping("/userIsExist")
    public Boolean userIsExist(@RequestBody RtbauUser rtbauUser) {
        return iRtbauUserService.userIsExist(rtbauUser);
    }

    @PostMapping("/getUserQR")
    public GetUserQROutput getUserQR(@RequestBody GetUserQRInput input) {
        log.info("getUserQR请求:{}", JSONObject.toJSONString(input));
        return iRtbauUserService.getUserQR(input);
    }

    @PostMapping("/userQRCallBack")
    public String userQRCallBack(@RequestBody QRCallBack qrCallBack) {
        log.info("wxPusher回调信息：{}", JSONObject.toJSONString(qrCallBack));
        PushMsg pushMsg = new PushMsg();
        List<String> uids = new ArrayList<>();
        uids.add(qrCallBack.getData().getUid());
        pushMsg.setUids(uids);
        pushMsg.setExtra(qrCallBack.getData().getExtra());
        iRtbauUserService.pushMsg(pushMsg);
        // 保存或更新用户数据
        GetUserQRInput getUserQRInput = JSONObject.parseObject(qrCallBack.getData().getExtra(), GetUserQRInput.class);
        RtbauUser rtbauUser = new RtbauUser();
        rtbauUser.setUid(qrCallBack.getData().getUid());
        rtbauUser.setRegionCode(getUserQRInput.getCityCode());
        rtbauUser.setCreateTime(new Date());
        rtbauUser.setUpdateTime(new Date());
        rtbauUser.setIsDeleted(0);
        iRtbauUserService.saveUserMsg(rtbauUser);
        return "";
    }

    public static void main(String[] args) {
        String me = "helloWord!";
        System.out.println("用户：" + me);
        OpenAiChatModel demo = OpenAiChatModel.builder()
                .baseUrl("https://yunwu.ai/v1")
                .apiKey("sk-补全")
                .modelName("deepseek-r1")
                .build();
        UserMessage userMessage = new UserMessage(me);
        ChatRequest build = new ChatRequest.Builder().messages(userMessage).build();
        ChatResponse chat = demo.chat(build);
        String string = chat.aiMessage().toString();
        System.out.println("AI：" + string);
    }
}
