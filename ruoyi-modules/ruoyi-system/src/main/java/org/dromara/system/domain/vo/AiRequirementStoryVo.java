package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.AiRequirementStory;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AutoMapper(target = AiRequirementStory.class)
public class AiRequirementStoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long storyId;

    private Long requirementId;

    private String storyTitle;

    private String roleName;

    private String storyValue;

    private String acceptanceCriteria;

    private String priority;

    private String status;

    private String remark;

    private Date createTime;

}
