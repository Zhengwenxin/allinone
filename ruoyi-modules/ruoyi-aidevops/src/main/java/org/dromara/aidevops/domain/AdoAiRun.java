package org.dromara.aidevops.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * AI 运行审计
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ado_ai_run")
public class AdoAiRun extends TenantEntity {

    @TableId(value = "run_id")
    private Long runId;

    private Long projectId;

    private String agentType;

    private String provider;

    private String modelName;

    private String systemPrompt;

    private String inputText;

    private String outputText;

    private String status;

    private String errorMessage;

    private Integer promptTokens;

    private Integer completionTokens;

    private Integer totalTokens;

    private Long latencyMs;

    private String traceId;

    private String remark;

}
