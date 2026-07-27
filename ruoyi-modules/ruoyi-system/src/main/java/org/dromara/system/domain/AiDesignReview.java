package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_design_review")
public class AiDesignReview extends TenantEntity {

    @TableId(value = "review_id")
    private Long reviewId;

    private Long designId;

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
