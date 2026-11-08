package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.vo.ChangeImpactVo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoLinkMapper;
import org.dromara.aidevops.service.IAdoChangePropagationService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 变更传播服务实现
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class AdoChangePropagationServiceImpl implements IAdoChangePropagationService {

    private final AdoArtifactMapper artifactMapper;
    private final AdoLinkMapper linkMapper;

    @Override
    public ChangeImpactVo analyze(Long artifactId, Integer targetVersion) {
        AdoArtifact artifact = artifactMapper.selectById(artifactId);
        if (artifact == null) {
            throw new ServiceException("工件不存在");
        }
        int target = targetVersion == null ? artifact.getCurrentVersion() : targetVersion;
        List<AdoLink> links = linkMapper.selectList(new LambdaQueryWrapper<AdoLink>()
            .eq(AdoLink::getFromArtifactId, artifactId)
            .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE));

        ChangeImpactVo vo = new ChangeImpactVo();
        vo.setArtifactId(artifact.getArtifactId());
        vo.setArtifactKey(artifact.getArtifactKey());
        vo.setCurrentVersion(artifact.getCurrentVersion());
        vo.setTargetVersion(target);
        vo.setGeneratedAt(new Date());

        for (AdoLink link : links) {
            int basedOn = link.getFromVersion() == null ? 0 : link.getFromVersion();
            if (basedOn >= target) {
                continue;
            }
            AdoArtifact downstream = artifactMapper.selectById(link.getToArtifactId());
            if (downstream == null) {
                continue;
            }
            ChangeImpactVo.ImpactItemVo item = new ChangeImpactVo.ImpactItemVo();
            item.setLinkId(link.getLinkId());
            item.setRelation(link.getRelation());
            item.setImpactLevel(resolveImpactLevel(link.getRelation()));
            item.setDownstreamArtifactId(downstream.getArtifactId());
            item.setArtifactType(downstream.getArtifactType());
            item.setArtifactKey(downstream.getArtifactKey());
            item.setTitle(downstream.getTitle());
            item.setBasedOnVersion(basedOn);
            item.setCurrentVersion(downstream.getCurrentVersion());
            item.setReason("下游基于上游 v" + basedOn + "，上游当前 v" + target + "，需要确认是否受影响");
            vo.getImpacts().add(item);
        }
        vo.getImpacts().sort(Comparator.comparingInt(item -> impactOrder(item.getImpactLevel())));
        vo.setAffectedCount(vo.getImpacts().size());
        return vo;
    }

    private int impactOrder(String level) {
        if ("high".equals(level)) {
            return 0;
        }
        if ("medium".equals(level)) {
            return 1;
        }
        return 2;
    }

    private String resolveImpactLevel(String relation) {
        if (StringUtils.equalsAny(relation,
            AdoConstants.REL_IMPLEMENTS, AdoConstants.REL_VERIFIES, AdoConstants.REL_DERIVES_FROM)) {
            return "high";
        }
        if (StringUtils.equalsAny(relation,
            AdoConstants.REL_DEPENDS_ON, AdoConstants.REL_IMPACTS, AdoConstants.REL_RELEASES)) {
            return "medium";
        }
        return "low";
    }

}
