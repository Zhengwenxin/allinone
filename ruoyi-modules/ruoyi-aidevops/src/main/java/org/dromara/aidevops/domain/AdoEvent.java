package org.dromara.aidevops.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 领域事件 Outbox
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ado_event")
public class AdoEvent extends TenantEntity {

    @TableId(value = "event_id")
    private Long eventId;

    private Long projectId;

    private String eventType;

    private String aggregateType;

    private Long aggregateId;

    private Integer aggregateVersion;

    private String actorType;

    private Long actorId;

    private String payload;

    private String traceId;

    private String correlationId;

    private String status;

    private Integer retryCount;

    private Date occurredAt;

    private Date publishedAt;

    private String remark;

}
