package org.dromara.aidevops.domain.bo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 需求澄清请求
 *
 * @author ruoyi
 */
@Data
public class RequirementClarifyBo {

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "原始需求不能为空")
    @Size(max = 5000, message = "原始需求长度不能超过{max}个字符")
    private String rawRequirement;

    @Size(max = 5000, message = "补充上下文长度不能超过{max}个字符")
    private String context;

}
