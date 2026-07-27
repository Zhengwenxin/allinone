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
import org.dromara.system.domain.AiDesignReview;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = AiDesignReview.class, reverseConvertGenerate = false)
public class AiDesignReviewBo extends BaseEntity {

    @NotNull(message = "评审ID不能为空", groups = { EditGroup.class })
    private Long reviewId;

    @NotNull(message = "设计ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long designId;

    @NotBlank(message = "评审标题不能为空", groups = { AddGroup.class, EditGroup.class })
    @Size(max = 200, message = "评审标题长度不能超过{max}个字符", groups = { AddGroup.class, EditGroup.class })
    private String reviewTitle;

    private String reviewerName;

    private String reviewType;

    private String severity;

    private String status;

    private Date reviewTime;

    private String issueDesc;

    private String suggestion;

    private String remark;

}
