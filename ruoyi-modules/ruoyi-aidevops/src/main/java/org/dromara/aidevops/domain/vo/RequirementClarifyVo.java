package org.dromara.aidevops.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 需求澄清结果
 *
 * @author ruoyi
 */
@Data
public class RequirementClarifyVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long aiRunId;

    private String provider;

    private String model;

    private Long latencyMs;

    /**
     * 是否为 mock 模式
     */
    private boolean mock;

    /**
     * 结构化 Spec 草稿
     */
    private Map<String, Object> spec;

    /**
     * 模型原始输出，便于审计
     */
    private String rawOutput;

}
