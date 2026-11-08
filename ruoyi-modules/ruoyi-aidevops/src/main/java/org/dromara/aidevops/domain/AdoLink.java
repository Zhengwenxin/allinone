package org.dromara.aidevops.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 工件关系
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ado_link")
public class AdoLink extends TenantEntity {

    @TableId(value = "link_id")
    private Long linkId;

    private Long projectId;

    private Long fromArtifactId;

    private Integer fromVersion;

    private Long toArtifactId;

    private Integer toVersion;

    private String relation;

    private BigDecimal confidence;

    private String origin;

    private String status;

    private String remark;

}
