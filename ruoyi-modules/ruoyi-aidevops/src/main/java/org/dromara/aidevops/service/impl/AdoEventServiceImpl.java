package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoEvent;
import org.dromara.aidevops.event.AdoDomainEvent;
import org.dromara.aidevops.mapper.AdoEventMapper;
import org.dromara.aidevops.service.IAdoEventService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 领域事件服务实现
 *
 * @author ruoyi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdoEventServiceImpl implements IAdoEventService {

    private final AdoEventMapper eventMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void record(String eventType, String aggregateType, Long aggregateId, Integer aggregateVersion,
                       Long projectId, Map<String, Object> payload) {
        AdoEvent event = new AdoEvent();
        event.setProjectId(projectId);
        event.setEventType(eventType);
        event.setAggregateType(aggregateType);
        event.setAggregateId(aggregateId);
        event.setAggregateVersion(aggregateVersion);
        Long userId = LoginHelper.getUserId();
        event.setActorId(userId);
        event.setActorType(userId == null ? AdoConstants.ACTOR_SYSTEM : AdoConstants.ACTOR_HUMAN);
        event.setPayload(payload == null ? "{}" : JsonUtils.toJsonString(payload));
        event.setStatus(AdoConstants.EVENT_PENDING);
        event.setRetryCount(0);
        event.setOccurredAt(new Date());
        eventMapper.insert(event);
    }

    @Override
    public int relayPending(int limit) {
        int batchSize = Math.max(1, Math.min(limit, 1000));
        // 事件中继是跨租户任务，忽略租户过滤
        List<AdoEvent> events = TenantHelper.ignore(() -> eventMapper.selectList(
            new LambdaQueryWrapper<AdoEvent>()
                .eq(AdoEvent::getStatus, AdoConstants.EVENT_PENDING)
                .orderByAsc(AdoEvent::getOccurredAt)
                .last("limit " + batchSize)));

        int published = 0;
        for (AdoEvent event : events) {
            try {
                AdoDomainEvent domainEvent = new AdoDomainEvent(
                    event.getEventId(), event.getTenantId(), event.getProjectId(), event.getEventType(),
                    event.getAggregateType(), event.getAggregateId(), event.getAggregateVersion(), event.getPayload());
                if (StringUtils.isNotBlank(event.getTenantId())) {
                    TenantHelper.dynamic(event.getTenantId(), () -> eventPublisher.publishEvent(domainEvent));
                } else {
                    eventPublisher.publishEvent(domainEvent);
                }
                markPublished(event.getEventId());
                published++;
            } catch (Exception e) {
                log.error("AI-DevOps 事件投递失败: eventId={}, type={}", event.getEventId(), event.getEventType(), e);
                markFailed(event);
            }
        }
        return published;
    }

    private void markPublished(Long eventId) {
        eventMapper.update(null, new LambdaUpdateWrapper<AdoEvent>()
            .eq(AdoEvent::getEventId, eventId)
            .set(AdoEvent::getStatus, AdoConstants.EVENT_PUBLISHED)
            .set(AdoEvent::getPublishedAt, new Date()));
    }

    private void markFailed(AdoEvent event) {
        int retry = (event.getRetryCount() == null ? 0 : event.getRetryCount()) + 1;
        eventMapper.update(null, new LambdaUpdateWrapper<AdoEvent>()
            .eq(AdoEvent::getEventId, event.getEventId())
            .set(AdoEvent::getRetryCount, retry)
            .set(AdoEvent::getStatus, retry >= 5 ? AdoConstants.EVENT_FAILED : AdoConstants.EVENT_PENDING));
    }

}
