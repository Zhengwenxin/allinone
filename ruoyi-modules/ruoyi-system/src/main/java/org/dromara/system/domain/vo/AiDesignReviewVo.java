package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.AiDesignReview;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AutoMapper(target = AiDesignReview.class)
public class AiDesignReviewVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

    private Date createTime;

}
