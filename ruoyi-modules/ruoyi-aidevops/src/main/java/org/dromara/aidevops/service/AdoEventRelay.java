package org.dromara.aidevops.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Outbox 事件中继
 * <p>
 * Phase 0 使用单实例定时投递；多实例部署时建议改为 MQ 或基于数据库的抢占式消费。
 *
 * @author ruoyi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdoEventRelay {

    private final IAdoEventService eventService;

    @Value("${ai-devops.event.relay-batch-size:100}")
    private int batchSize;

    @Scheduled(fixedDelayString = "${ai-devops.event.relay-interval:5000}", initialDelay = 10000)
    public void relay() {
        try {
            int published = eventService.relayPending(batchSize);
            if (published > 0) {
                log.debug("AI-DevOps 事件中继完成，投递 {} 条", published);
            }
        } catch (Exception e) {
            log.error("AI-DevOps 事件中继异常", e);
        }
    }

}
