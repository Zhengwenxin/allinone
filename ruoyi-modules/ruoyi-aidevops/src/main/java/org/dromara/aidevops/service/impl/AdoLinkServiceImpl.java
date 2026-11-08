package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.bo.AdoLinkBo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoLinkMapper;
import org.dromara.aidevops.service.IAdoEventService;
import org.dromara.aidevops.service.IAdoLinkService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工件关系服务实现
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class AdoLinkServiceImpl implements IAdoLinkService {

    private final AdoLinkMapper baseMapper;
    private final AdoArtifactMapper artifactMapper;
    private final IAdoEventService eventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createLink(AdoLinkBo bo) {
        AdoArtifact from = artifactMapper.selectById(bo.getFromArtifactId());
        AdoArtifact to = artifactMapper.selectById(bo.getToArtifactId());
        if (from == null || to == null) {
            throw new ServiceException("源工件或目标工件不存在");
        }

        AdoLink link = MapstructUtils.convert(bo, AdoLink.class);
        link.setFromVersion(bo.getFromVersion() == null ? from.getCurrentVersion() : bo.getFromVersion());
        link.setToVersion(bo.getToVersion() == null ? to.getCurrentVersion() : bo.getToVersion());
        if (StringUtils.isBlank(link.getOrigin())) {
            link.setOrigin("manual");
        }
        link.setStatus(AdoConstants.STATUS_ACTIVE);

        AdoLink existing = baseMapper.selectOne(new LambdaQueryWrapper<AdoLink>()
            .eq(AdoLink::getFromArtifactId, link.getFromArtifactId())
            .eq(AdoLink::getToArtifactId, link.getToArtifactId())
            .eq(AdoLink::getRelation, link.getRelation())
            .last("limit 1"));
        if (existing != null) {
            existing.setFromVersion(link.getFromVersion());
            existing.setToVersion(link.getToVersion());
            existing.setOrigin(link.getOrigin());
            existing.setConfidence(link.getConfidence());
            existing.setStatus(AdoConstants.STATUS_ACTIVE);
            baseMapper.updateById(existing);
            return existing.getLinkId();
        }

        baseMapper.insert(link);
        eventService.record("artifact.link.created", "artifact_link", link.getLinkId(), null,
            link.getProjectId(), buildEventPayload(link));
        return link.getLinkId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acknowledge(Long linkId) {
        AdoLink link = baseMapper.selectById(linkId);
        if (link == null) {
            throw new ServiceException("工件关系不存在");
        }
        AdoArtifact from = artifactMapper.selectById(link.getFromArtifactId());
        if (from == null) {
            throw new ServiceException("上游工件不存在");
        }
        link.setFromVersion(from.getCurrentVersion());
        link.setStatus(AdoConstants.STATUS_ACTIVE);
        baseMapper.updateById(link);

        Map<String, Object> payload = new HashMap<>();
        payload.put("linkId", linkId);
        payload.put("fromArtifactId", link.getFromArtifactId());
        payload.put("fromVersion", link.getFromVersion());
        payload.put("toArtifactId", link.getToArtifactId());
        eventService.record("artifact.link.acknowledged", "artifact_link", linkId, link.getFromVersion(),
            link.getProjectId(), payload);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLinkByIds(List<Long> linkIds) {
        return baseMapper.deleteByIds(linkIds) > 0;
    }

    @Override
    public List<AdoLink> selectLinks(Long artifactId, String direction) {
        LambdaQueryWrapper<AdoLink> lqw = new LambdaQueryWrapper<AdoLink>()
            .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE);
        if ("downstream".equalsIgnoreCase(direction)) {
            lqw.eq(AdoLink::getFromArtifactId, artifactId);
        } else if ("upstream".equalsIgnoreCase(direction)) {
            lqw.eq(AdoLink::getToArtifactId, artifactId);
        } else {
            lqw.and(w -> w.eq(AdoLink::getFromArtifactId, artifactId)
                .or()
                .eq(AdoLink::getToArtifactId, artifactId));
        }
        return baseMapper.selectList(lqw.orderByDesc(AdoLink::getCreateTime));
    }

    private Map<String, Object> buildEventPayload(AdoLink link) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("linkId", link.getLinkId());
        payload.put("projectId", link.getProjectId());
        payload.put("fromArtifactId", link.getFromArtifactId());
        payload.put("fromVersion", link.getFromVersion());
        payload.put("toArtifactId", link.getToArtifactId());
        payload.put("toVersion", link.getToVersion());
        payload.put("relation", link.getRelation());
        payload.put("origin", link.getOrigin());
        return payload;
    }

}
