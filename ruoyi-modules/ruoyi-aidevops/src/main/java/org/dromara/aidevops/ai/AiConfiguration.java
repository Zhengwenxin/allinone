package org.dromara.aidevops.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 网关装配
 *
 * @author ruoyi
 */
@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiConfiguration {

    @Bean
    public AiModelProvider aiModelProvider(AiProperties properties) {
        String provider = properties.getProvider() == null ? "mock" : properties.getProvider().trim().toLowerCase();
        if ("openai".equals(provider) || "openai-compatible".equals(provider)) {
            return new OpenAiCompatibleAiModelProvider(properties);
        }
        return new MockAiModelProvider();
    }

}
