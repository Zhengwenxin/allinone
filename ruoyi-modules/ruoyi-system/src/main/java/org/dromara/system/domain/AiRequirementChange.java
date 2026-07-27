package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_requirement_change")
public class AiRequirementChange extends TenantEntity {

    @TableId(value = "change_id")
    private Long changeId;

    private Long requirementId;

    private String changeTitle;

    private String changeReason;

    private String impactScope;

    private String status;

    private String remark;

}
