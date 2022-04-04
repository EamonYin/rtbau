package com.eamon.rtbau.config;

import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.Result;
import org.springframework.beans.factory.annotation.Value;


/**
 * @Author: Eamon
 * @Date: 2022/4/3 22:20
 */
public class WxPushMessageUtil {

    @Value("${appToken}")
    private String appToken;

    private String sign = "";

    /**
     * 发送普通文本
     */
    public Result sendText(String uid) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_TEXT);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setContent("【记得带伞！！！】\n\n\n" + sign);
        return WxPusher.send(message);
    }
    /**
     * 发送html文本
     */
    public Result sendHtml(String uid) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_HTML);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setContent("WxPusher演示消息，这是一个html消息<br />标题：<span style='color:red;'>这是标题</span><br />状态：<span style='color:green;'>成功</span>"
                + "<br /><br /><br />" + sign);
        return WxPusher.send(message);
    }


    /**
     * 发送markdown
     */
    public Result sendMarkdown(String uid) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_MD);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setContent("WxPusher演示消息，这是一个Markdown消息\n# 目录\n- 什么是Wxpusher\n- Wxpusher可好用了\n## 发送状态：_成功_"
                + "\n\n<br /><br /><br />" + sign);
        return WxPusher.send(message);
    }

    /**
     * 发送markdown
     */
    public Result sendCustom(String uid, String content) {
        Message message = new Message();
        message.setContentType(Message.CONTENT_TYPE_TEXT);
        message.setUid(uid);
        message.setAppToken(appToken);
        message.setContent(content + "\n\n\n" + sign);
        return WxPusher.send(message);
    }
}
