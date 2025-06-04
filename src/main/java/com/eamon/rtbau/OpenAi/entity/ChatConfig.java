package com.eamon.rtbau.OpenAi.entity;

import com.eamon.rtbau.OpenAi.utils.SpringUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "chat")
public class ChatConfig {
    private String url;
    private String token;
    private String miniTxt;
    private String sunoTxt;
    private static final String DEFAULT_MODEL = "gpt-4-turbo-2024-04-09";
    private static final String DEV_MODEL = "qwen-plus-2025-01-25";

    public String getUrl() {
        if ("dev".equals(SpringUtils.getActiveProfile())) {
            return SpringUtils.getRequiredProperty("qwen.url");
        }
        return url;
    }

    public String getToken() {
        if ("dev".equals(SpringUtils.getActiveProfile())) {
            return SpringUtils.getRequiredProperty("qwen.key");
        }
        return token;
    }

    public String getMiniTxt() {
        if ("dev".equals(SpringUtils.getActiveProfile())) {
            return SpringUtils.getRequiredProperty("qwen.miniTxt");
        }
        return miniTxt;
    }

    public String getSunoTxt() {
        if ("dev".equals(SpringUtils.getActiveProfile())) {
            return SpringUtils.getRequiredProperty("qwen.sunoTxt");
        }
        return sunoTxt;
    }

    public String getModel() {
        if ("dev".equals(SpringUtils.getActiveProfile())) {
            return DEV_MODEL;
        }
        return DEFAULT_MODEL;
    }
}
