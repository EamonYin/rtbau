package com.eamon.rtbau.OpenAi.entity;

import lombok.Data;

@Data
public class Choice {
    private int index;
    private Message message;
    private String finishReason;
}
