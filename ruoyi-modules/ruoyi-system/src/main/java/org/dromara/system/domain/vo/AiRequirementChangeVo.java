package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.AiRequirementChange;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AutoMapper(target = AiRequirementChange.class)
public class AiRequirementChangeVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long changeId;

    private Long requirementId;

    private String changeTitle;

    private String changeReason;

    private String impactScope;

    private String status;

    private String remark;

    private Date createTime;

}
