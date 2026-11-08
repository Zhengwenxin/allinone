package org.dromara.aidevops.service;

import java.util.Map;

/**
 * 领域事件服务
 *
 * @author ruoyi
 */
public interface IAdoEventService {

    /**
     * 记录领域事件（与业务操作同事务写入 outbox）
     *
     * @param eventType        事件类型
     * @param aggregateType    聚合类型
     * @param aggregateId      聚合ID
     * @param aggregateVersion 聚合版本
     * @param projectId        项目ID
     * @param payload          事件载荷
     */
    void record(String eventType, String aggregateType, Long aggregateId, Integer aggregateVersion,
                Long projectId, Map<String, Object> payload);

    /**
     * 投递待发布事件
     *
     * @param limit 单次最大数量
     * @return 实际投递数量
     */
    int relayPending(int limit);

}
