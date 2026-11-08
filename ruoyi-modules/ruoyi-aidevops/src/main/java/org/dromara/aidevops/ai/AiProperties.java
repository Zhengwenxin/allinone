package org.dromara.aidevops.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 网关配置
 *
 * @author ruoyi
 */
@Data
@ConfigurationProperties(prefix = "ai-devops.ai")
public class AiProperties {

    /**
     * 模型供应商标识：mock / openai
     */
    private String provider = "mock";

    /**
     * OpenAI 兼容接口地址，如 https://api.deepseek.com/v1
     */
    private String baseUrl = "https://api.deepseek.com/v1";

    private String apiKey;

    /**
     * 模型名称，如 deepseek-chat / qwen-plus / gpt-4o-mini
     */
    private String model = "deepseek-chat";

    private int timeoutSeconds = 60;

    private int maxTokens = 4096;

    private double temperature = 0.2;

}
