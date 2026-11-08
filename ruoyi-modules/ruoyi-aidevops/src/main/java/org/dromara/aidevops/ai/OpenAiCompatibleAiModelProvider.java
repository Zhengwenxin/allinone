package org.dromara.aidevops.ai;

import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * OpenAI 兼容模型提供者（DeepSeek / Qwen / vLLM / Ollama 等）
 *
 * @author ruoyi
 */
public class OpenAiCompatibleAiModelProvider implements AiModelProvider {

    private final AiProperties properties;
    private final RestClient restClient;

    public OpenAiCompatibleAiModelProvider(AiProperties properties) {
        this.properties = properties;
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(Math.max(5, properties.getTimeoutSeconds())));
        factory.setReadTimeout(Duration.ofSeconds(Math.max(10, properties.getTimeoutSeconds())));
        String baseUrl = StringUtils.isBlank(properties.getBaseUrl())
            ? "https://api.deepseek.com/v1" : properties.getBaseUrl().replaceAll("/+$", "");
        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(factory)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + StringUtils.defaultString(properties.getApiKey()))
            .build();
    }

    @Override
    public AiCompletion chat(String systemPrompt, String userPrompt) {
        if (StringUtils.isBlank(properties.getApiKey())) {
            throw new ServiceException("AI 未配置 api-key，请在 ai-devops.ai.api-key 中配置模型密钥");
        }
        Map<String, Object> body = Map.of(
            "model", properties.getModel(),
            "messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
            ),
            "temperature", properties.getTemperature(),
            "max_tokens", properties.getMaxTokens()
        );
        String response;
        try {
            response = restClient.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        } catch (Exception e) {
            throw new ServiceException("AI 模型调用失败：" + e.getMessage());
        }
        JsonNode root = JsonUtils.parseObject(response, JsonNode.class);
        if (root == null) {
            throw new ServiceException("AI 模型返回为空");
        }
        JsonNode choice = root.path("choices").path(0);
        String content = choice.path("message").path("content").asText("");
        if (StringUtils.isBlank(content)) {
            content = choice.path("text").asText("");
        }
        if (StringUtils.isBlank(content)) {
            throw new ServiceException("AI 模型未返回有效内容：" + StringUtils.substring(response, 0, 300));
        }
        JsonNode usage = root.path("usage");
        return new AiCompletion(
            stripJsonFence(content),
            providerName(),
            properties.getModel(),
            usage.path("prompt_tokens").asInt(0),
            usage.path("completion_tokens").asInt(0),
            usage.path("total_tokens").asInt(0)
        );
    }

    @Override
    public String providerName() {
        return "openai-compatible";
    }

    @Override
    public String modelName() {
        return properties.getModel();
    }

    private String stripJsonFence(String text) {
        String value = text.trim();
        if (value.startsWith("```")) {
            int firstNewline = value.indexOf('\n');
            if (firstNewline > 0) {
                value = value.substring(firstNewline + 1);
            }
            if (value.endsWith("```")) {
                value = value.substring(0, value.length() - 3).trim();
            }
        }
        return value;
    }

}
