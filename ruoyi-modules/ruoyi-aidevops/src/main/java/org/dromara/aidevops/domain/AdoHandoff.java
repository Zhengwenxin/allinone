package org.dromara.aidevops.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 交接契约
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ado_handoff")
public class AdoHandoff extends TenantEntity {

    @TableId(value = "handoff_id")
    private Long handoffId;

    private Long projectId;

    private String handoffType;

    private String fromRole;

    private String toRole;

    private String artifactIds;

    private String contractVersion;

    private String checklist;

    private String status;

    private String snapshot;

    private Long signedOffBy;

    private Date signedOffAt;

    private String remark;

}
