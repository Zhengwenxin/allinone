package org.dromara.aidevops.ai;

/**
 * 模型调用结果
 *
 * @param content          输出文本
 * @param provider         供应商
 * @param model            模型
 * @param promptTokens     输入 Token
 * @param completionTokens 输出 Token
 * @param totalTokens      总 Token
 * @author ruoyi
 */
public record AiCompletion(String content,
                           String provider,
                           String model,
                           int promptTokens,
                           int completionTokens,
                           int totalTokens) {
}
