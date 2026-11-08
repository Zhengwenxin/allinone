package org.dromara.aidevops.event;

/**
 * 领域事件（本地 Spring 事件）
 * <p>
 * Phase 0 先用本地事件解耦；后续可替换为 MQ/Kafka，事件载荷保持不变。
 *
 * @param eventId          事件ID
 * @param tenantId         租户ID
 * @param projectId        项目ID
 * @param eventType        事件类型
 * @param aggregateType    聚合类型
 * @param aggregateId      聚合ID
 * @param aggregateVersion 聚合版本
 * @param payload          事件载荷 JSON
 * @author ruoyi
 */
public record AdoDomainEvent(Long eventId,
                             String tenantId,
                             Long projectId,
                             String eventType,
                             String aggregateType,
                             Long aggregateId,
                             Integer aggregateVersion,
                             String payload) {
}
