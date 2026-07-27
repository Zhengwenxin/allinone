package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.AiRequirement;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiRequirement.class, reverseConvertGenerate = false)
public class AiRequirementBo extends BaseEntity {

    private Long requirementId;

    @NotBlank(message = "需求编号不能为空")
    @Size(max = 64, message = "需求编号长度不能超过{max}个字符")
    private String requirementCode;

    @NotBlank(message = "需求标题不能为空")
    @Size(max = 200, message = "需求标题长度不能超过{max}个字符")
    private String requirementTitle;

    private String requirementType;

    private String source;

    private String priority;

    private String status;

    private Long ownerId;

    private String ownerName;

    private String iterationName;

    private Integer businessValue;

    private Integer technicalComplexity;

    private String riskLevel;

    private Date dueDate;

    private String summary;

    private String description;

    private String acceptanceCriteria;

    private String aiAnalysis;

    private String remark;

}
