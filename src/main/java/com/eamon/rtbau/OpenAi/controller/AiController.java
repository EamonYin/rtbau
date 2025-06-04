package com.eamon.rtbau.OpenAi.controller;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.time.Duration;

public class AiController {
        public static void main(String[] args) {
        String me = "helloWord!";
        System.out.println("用户：" + me);
        OpenAiChatModel demo = OpenAiChatModel.builder()
                .baseUrl("https://yunwu.ai/v1")
                .apiKey("sk-补全")
                .modelName("deepseek-r1")
                .timeout(Duration.ofSeconds(30))
                .build();
        UserMessage userMessage = new UserMessage(me);
        ChatRequest build = new ChatRequest.Builder().messages(userMessage).build();
        ChatResponse chat = demo.chat(build);
        String string = chat.aiMessage().toString();
        System.out.println("AI：" + string);
    }
}
