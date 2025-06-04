package com.eamon.rtbau.OpenAi.entity;

import lombok.Data;

import java.awt.*;
import java.util.List;

@Data
public class OpenAiResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private TokenUsage usage;

    /**
     * 获取第一个回复的内容
     */
    public String getFirstChoiceContent() {
        if (choices != null && !choices.isEmpty() && choices.get(0).getMessage() != null) {
            return choices.get(0).getMessage().getContent();
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OpenAiResponse [id=").append(id)
                .append(", object=").append(object)
                .append(", created=").append(created)
                .append(", model=").append(model);

        if (choices != null && !choices.isEmpty()) {
            builder.append(", firstChoice=").append(getFirstChoiceContent());
        }

        if (usage != null) {
            builder.append(", usage=").append(usage);
        }

        builder.append("]");
        return builder.toString();
    }
}
