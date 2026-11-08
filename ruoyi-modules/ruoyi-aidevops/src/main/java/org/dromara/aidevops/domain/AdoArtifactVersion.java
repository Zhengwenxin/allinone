package org.dromara.aidevops.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 工件版本快照
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ado_artifact_version")
public class AdoArtifactVersion extends TenantEntity {

    @TableId(value = "version_id")
    private Long versionId;

    private Long artifactId;

    private Integer versionNo;

    private String snapshot;

    private String diff;

    private String changeSummary;

    private String authorType;

    private Long authorId;

    private String authorName;

    private Long approvedBy;

    private Date approvedAt;

    private String remark;

}
