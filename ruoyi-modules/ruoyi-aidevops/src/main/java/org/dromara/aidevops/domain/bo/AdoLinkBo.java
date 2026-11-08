package org.dromara.aidevops.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;

/**
 * 工件关系业务对象
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AdoLink.class, reverseConvertGenerate = false)
public class AdoLinkBo extends BaseEntity {

    private Long linkId;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "源工件ID不能为空")
    private Long fromArtifactId;

    private Integer fromVersion;

    @NotNull(message = "目标工件ID不能为空")
    private Long toArtifactId;

    private Integer toVersion;

    @NotBlank(message = "关系类型不能为空")
    private String relation;

    private BigDecimal confidence;

    private String origin;

    private String remark;

}
