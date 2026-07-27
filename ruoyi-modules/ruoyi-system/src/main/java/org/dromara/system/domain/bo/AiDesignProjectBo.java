package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.AiDesignProject;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiDesignProject.class, reverseConvertGenerate = false)
public class AiDesignProjectBo extends BaseEntity {

    @NotNull(message = "设计ID不能为空", groups = { EditGroup.class })
    private Long designId;

    @NotBlank(message = "设计编号不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 64, message = "设计编号长度不能超过{max}个字符", groups = { AddGroup.class, EditGroup.class })
    private String designCode;

    @NotBlank(message = "设计名称不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 200, message = "设计名称长度不能超过{max}个字符", groups = { AddGroup.class, EditGroup.class })
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

    private String remark;

}
