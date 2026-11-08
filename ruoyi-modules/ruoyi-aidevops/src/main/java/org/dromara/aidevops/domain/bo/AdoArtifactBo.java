package org.dromara.aidevops.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 工件业务对象
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AdoArtifact.class, reverseConvertGenerate = false)
public class AdoArtifactBo extends BaseEntity {

    private Long artifactId;

    @NotNull(message = "项目ID不能为空")
    private Long projectId;

    @NotBlank(message = "工件类型不能为空")
    @Size(max = 32, message = "工件类型长度不能超过{max}个字符")
    private String artifactType;

    @NotBlank(message = "工件编号不能为空")
    @Size(max = 128, message = "工件编号长度不能超过{max}个字符")
    private String artifactKey;

    @NotBlank(message = "工件标题不能为空")
    @Size(max = 500, message = "工件标题长度不能超过{max}个字符")
    private String title;

    private String summary;

    private String status;

    private Long ownerId;

    private String ownerName;

    private String sourceSystem;

    private String sourceRef;

    private String payload;

    private String remark;

    /**
     * 版本变更摘要（只用于写版本记录，不落到工件表）
     */
    private String changeSummary;

}
