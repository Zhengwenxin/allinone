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
@TableName("ai_design_project")
public class AiDesignProject extends TenantEntity {

    @TableId(value = "design_id")
    private Long designId;

    private String designCode;

    private String designName;

    private Long requirementId;

    private String requirementTitle;

    private String designType;

    private String designTool;

    private Long ownerId;

    private String ownerName;

    private String collaborator;

    private String status;

    private String reviewStatus;

    private String prototypeUrl;

    private String designUrl;

    private String versionNo;

    private Integer pageCount;

    private Integer interactionCount;

    private Date dueDate;

    private String designBrief;

    private String aiSuggestion;

    @TableLogic
    private String delFlag;

    private String remark;

}
