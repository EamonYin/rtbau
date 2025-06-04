package com.eamon.rtbau.OpenAi.service.impl;

import com.eamon.rtbau.OpenAi.entity.*;
import com.eamon.rtbau.OpenAi.service.IOpenAiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiServiceImpl implements IOpenAiService, DisposableBean {

    // 使用连接池的HttpClient单例
    private static final CloseableHttpClient httpClient;
    // ObjectMapper单例
    private static final ObjectMapper objectMapper = new ObjectMapper();
    // Guava限流器，每秒最多5次请求（可根据实际需求调整）
    private static final RateLimiter rateLimiter = RateLimiter.create(5.0);
    private static final Logger logger = LoggerFactory.getLogger(OpenAiServiceImpl.class);
    private final ChatConfig chatConfig;

    static {
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setMaxTotal(100); // 最大连接数，可根据实际需求调整
        connManager.setDefaultMaxPerRoute(20); // 每个路由最大连接数
        httpClient = HttpClients.custom().setConnectionManager(connManager).build();
    }

    @Autowired
    public OpenAiServiceImpl(ChatConfig chatConfig) {
        this.chatConfig = chatConfig;
    }

    /**
     * 发送带图像的请求
     *
     * @param model     模型名称
     * @param prompt    提示文本
     * @param imageUrl  图像URL
     * @param maxTokens 最大生成的token数
     * @return API响应结果
     * @throws IOException 网络或JSON解析异常
     */
    public OpenAiResponse sendImageRequest(String model, String prompt, String imageUrl, int maxTokens) throws Exception {
        // 限流，获取许可
        double waitTime = rateLimiter.acquire();
        if (waitTime > 0) {
            logger.info("RateLimiter: waited {} seconds before sending request", waitTime);
        }
        int maxRetries = 3;
        int attempt = 0;
        while (true) {
            try {
                // 创建请求体
                ObjectNode requestBody = objectMapper.createObjectNode();
                requestBody.put("model", model);
                requestBody.put("max_tokens", maxTokens);

                // 创建消息数组
                ArrayNode messages = objectMapper.createArrayNode();
                ObjectNode userMessage = objectMapper.createObjectNode();
                userMessage.put("role", "user");

                // 创建内容数组
                ArrayNode content = objectMapper.createArrayNode();

                // 添加文本内容
                ObjectNode textContent = objectMapper.createObjectNode();
                textContent.put("type", "text");
                textContent.put("text", prompt);
                content.add(textContent);

                // 添加图像内容
                ObjectNode imageContent = objectMapper.createObjectNode();
                imageContent.put("type", "image_url");

                ObjectNode imageUrlObj = objectMapper.createObjectNode();
                imageUrlObj.put("url", imageUrl);
                imageContent.set("image_url", imageUrlObj);

                content.add(imageContent);

                // 设置用户消息内容
                userMessage.set("content", content);
                messages.add(userMessage);

                // 设置消息到请求体
                requestBody.set("messages", messages);

                // 创建HTTP请求
                HttpPost httpPost = new HttpPost(chatConfig.getUrl());
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setHeader("Authorization", "Bearer " + chatConfig.getToken());

                // 设置请求体
                String jsonBody = objectMapper.writeValueAsString(requestBody);
                httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

                // 执行请求
                try (CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(httpPost)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        String responseBody = EntityUtils.toString(entity);
                        logger.info("OpenAI response received, length={} bytes", responseBody.length());
                        return parseResponse(responseBody);
                    } else {
                        logger.error("OpenAI response entity is null");
                        throw new IOException("Response entity is null");
                    }
                }
            } catch (Exception ex) {
                attempt++;
                logger.warn("OpenAI request failed (attempt {}/{}): {}", attempt, maxRetries, ex.getMessage());
                if (attempt >= maxRetries) {
                    logger.error("OpenAI request failed after {} attempts", maxRetries, ex);
                    throw ex;
                }
                try {
                    Thread.sleep(1000L); // 1秒后重试
                    logger.info("Retrying OpenAI request, attempt {}", attempt + 1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry interrupted", ie);
                    throw new IOException("Retry interrupted", ie);
                }
            }
        }
    }

    /**
     * 解析API响应
     *
     * @param responseJson 响应JSON字符串
     * @return 解析后的响应对象
     * @throws IOException JSON解析异常
     */
    private OpenAiResponse parseResponse(String responseJson) throws IOException {
        JsonNode root = objectMapper.readTree(responseJson);

        OpenAiResponse result = new OpenAiResponse();

        // 解析ID
        if (root.has("id")) {
            result.setId(root.get("id").asText());
        }

        // 解析对象类型
        if (root.has("object")) {
            result.setObject(root.get("object").asText());
        }

        // 解析创建时间
        if (root.has("created")) {
            result.setCreated(root.get("created").asLong());
        }

        // 解析模型
        if (root.has("model")) {
            result.setModel(root.get("model").asText());
        }

        // 解析使用的tokens
        if (root.has("usage") && root.get("usage").isObject()) {
            JsonNode usage = root.get("usage");

            TokenUsage tokenUsage = new TokenUsage();
            if (usage.has("prompt_tokens")) {
                tokenUsage.setPromptTokens(usage.get("prompt_tokens").asInt());
            }
            if (usage.has("completion_tokens")) {
                tokenUsage.setCompletionTokens(usage.get("completion_tokens").asInt());
            }
            if (usage.has("total_tokens")) {
                tokenUsage.setTotalTokens(usage.get("total_tokens").asInt());
            }

            result.setUsage(tokenUsage);
        }

        // 解析选择结果
        if (root.has("choices") && root.get("choices").isArray()) {
            List<Choice> choices = new ArrayList<>();

            for (JsonNode choiceNode : root.get("choices")) {
                Choice choice = new Choice();

                if (choiceNode.has("message") && choiceNode.get("message").isObject()) {
                    JsonNode messageNode = choiceNode.get("message");

                    Message message = new Message();
                    if (messageNode.has("role")) {
                        message.setRole(messageNode.get("role").asText());
                    }

                    if (messageNode.has("content")) {
                        message.setContent(messageNode.get("content").asText());
                    }

                    choice.setMessage(message);
                }

                if (choiceNode.has("finish_reason")) {
                    choice.setFinishReason(choiceNode.get("finish_reason").asText());
                }

                if (choiceNode.has("index")) {
                    choice.setIndex(choiceNode.get("index").asInt());
                }

                choices.add(choice);
            }

            result.setChoices(choices);
        }

        return result;
    }

    @Override
    public void destroy() throws Exception {
        if (httpClient != null) {
            logger.info("Closing HttpClient connection pool");
            httpClient.close();
        }

    }
}
