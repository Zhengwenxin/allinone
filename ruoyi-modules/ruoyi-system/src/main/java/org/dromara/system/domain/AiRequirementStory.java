package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_requirement_story")
public class AiRequirementStory extends TenantEntity {

    @TableId(value = "story_id")
    private Long storyId;

    private Long requirementId;

    private String storyTitle;

    private String roleName;

    private String storyValue;

    private String acceptanceCriteria;

    private String priority;

    private String status;

    private String remark;

}
