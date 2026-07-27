package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_requirement")
public class AiRequirement extends TenantEntity {

    @TableId(value = "requirement_id")
    private Long requirementId;

    private String requirementCode;

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

    @TableLogic
    private String delFlag;

    private String remark;

}
