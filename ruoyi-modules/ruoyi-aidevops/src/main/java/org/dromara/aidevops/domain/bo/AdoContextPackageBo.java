package org.dromara.aidevops.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.aidevops.domain.AdoContextPackage;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 任务上下文包业务对象
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AdoContextPackage.class, reverseConvertGenerate = false)
public class AdoContextPackageBo extends BaseEntity {

    private Long packageId;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "Spec 工件ID不能为空")
    private Long specArtifactId;

    /**
     * 不传则使用 Spec 当前版本
     */
    private Integer specVersion;

    private String aiBrief;

    private String remark;

}
