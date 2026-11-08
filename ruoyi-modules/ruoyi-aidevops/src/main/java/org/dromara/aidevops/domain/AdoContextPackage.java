package org.dromara.aidevops.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 任务上下文包
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ado_context_package")
public class AdoContextPackage extends TenantEntity {

    @TableId(value = "package_id")
    private Long packageId;

    private Long projectId;

    private Long taskId;

    private Long specArtifactId;

    private Integer specVersion;

    private String content;

    private String aiBrief;

    private String status;

    private Date generatedAt;

    private String remark;

}
