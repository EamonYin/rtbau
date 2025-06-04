package com.eamon.rtbau.OpenAi.entity;

import lombok.Data;

@Data
public class TokenUsage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;

    @Override
    public String toString() {
        return "TokenUsage [promptTokens=" + promptTokens + ", completionTokens=" + completionTokens + ", totalTokens=" + totalTokens + "]";
    }
}
