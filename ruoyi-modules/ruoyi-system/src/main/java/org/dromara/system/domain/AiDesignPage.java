package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ai_design_page")
public class AiDesignPage extends TenantEntity {

    @TableId(value = "page_id")
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

}
