package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.AiDesignPage;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@AutoMapper(target = AiDesignPage.class)
public class AiDesignPageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long pageId;

    private Long designId;

    private String pageName;

    private String pageType;

    private String routePath;

    private String userScenario;

    private String interactionDesc;

    private String componentSpec;

    private String status;

    private Integer sortOrder;

    private String remark;

    private Date createTime;

}
