package org.dromara.aidevops.ai;

/**
 * AI 模型提供者
 *
 * @author ruoyi
 */
public interface AiModelProvider {

    /**
     * 对话调用
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt   用户输入
     * @return 模型输出
     */
    AiCompletion chat(String systemPrompt, String userPrompt);

    /**
     * 供应商名称
     */
    String providerName();

    /**
     * 模型名称
     */
    String modelName();

}
