package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.AiRequirementChange;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiRequirementChange.class, reverseConvertGenerate = false)
public class AiRequirementChangeBo extends BaseEntity {

    private Long changeId;

    @NotNull(message = "需求ID不能为空")
    private Long requirementId;

    @NotBlank(message = "变更标题不能为空")
    @Size(max = 200, message = "变更标题长度不能超过{max}个字符")
    private String changeTitle;

    private String changeReason;

    private String impactScope;

    private String status;

    private String remark;

}
