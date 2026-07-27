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
import org.dromara.system.domain.AiDesignPage;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiDesignPage.class, reverseConvertGenerate = false)
public class AiDesignPageBo extends BaseEntity {

    @NotNull(message = "页面ID不能为空", groups = { EditGroup.class })
    private Long pageId;

    @NotNull(message = "设计ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long designId;

    @NotBlank(message = "页面名称不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 120, message = "页面名称长度不能超过{max}个字符", groups = { AddGroup.class, EditGroup.class })
    private String pageName;

    private String pageType;

    private String routePath;

    private String userScenario;

    private String interactionDesc;

    private String componentSpec;

    private String status;

    private Integer sortOrder;

    private String remark;

}
