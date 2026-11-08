package org.dromara.aidevops.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 工件注册表
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ado_artifact")
public class AdoArtifact extends TenantEntity {

    @TableId(value = "artifact_id")
    private Long artifactId;

    private Long projectId;

    private String artifactType;

    private String artifactKey;

    private String title;

    private String summary;

    private String status;

    private Long ownerId;

    private String ownerName;

    private Integer currentVersion;

    private String sourceSystem;

    private String sourceRef;

    private String payload;

    private String searchText;

    @TableLogic
    private String delFlag;

    private String remark;

}
