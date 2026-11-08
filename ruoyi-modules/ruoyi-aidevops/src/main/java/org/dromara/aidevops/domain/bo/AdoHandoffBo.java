package org.dromara.aidevops.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.aidevops.domain.AdoHandoff;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 交接契约业务对象
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AdoHandoff.class, reverseConvertGenerate = false)
public class AdoHandoffBo extends BaseEntity {

    private Long handoffId;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "交接类型不能为空")
    private String handoffType;

    private String fromRole;

    private String toRole;

    /**
     * 交接工件ID列表，JSON 数组字符串，如 [1,2,3]
     */
    private String artifactIds;

    private String contractVersion;

    /**
     * 检查清单 JSON
     */
    private String checklist;

    private String remark;

}
