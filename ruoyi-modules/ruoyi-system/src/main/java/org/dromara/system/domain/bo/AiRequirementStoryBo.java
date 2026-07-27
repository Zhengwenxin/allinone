package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.AiRequirementStory;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiRequirementStory.class, reverseConvertGenerate = false)
public class AiRequirementStoryBo extends BaseEntity {

    private Long storyId;

    @NotNull(message = "需求ID不能为空")
    private Long requirementId;

    @NotBlank(message = "用户故事标题不能为空")
    @Size(max = 200, message = "用户故事标题长度不能超过{max}个字符")
    private String storyTitle;

    private String roleName;

    private String storyValue;

    private String acceptanceCriteria;

    private String priority;

    private String status;

    private String remark;

}
