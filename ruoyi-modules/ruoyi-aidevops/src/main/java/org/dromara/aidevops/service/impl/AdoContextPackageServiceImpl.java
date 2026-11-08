package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoArtifactVersion;
import org.dromara.aidevops.domain.AdoContextPackage;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.bo.AdoContextPackageBo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoArtifactVersionMapper;
import org.dromara.aidevops.mapper.AdoContextPackageMapper;
import org.dromara.aidevops.mapper.AdoLinkMapper;
import org.dromara.aidevops.service.IAdoContextPackageService;
import org.dromara.aidevops.service.IAdoEventService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务上下文包服务实现
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class AdoContextPackageServiceImpl implements IAdoContextPackageService {

    private final AdoContextPackageMapper baseMapper;
    private final AdoArtifactMapper artifactMapper;
    private final AdoArtifactVersionMapper versionMapper;
    private final AdoLinkMapper linkMapper;
    private final IAdoEventService eventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long buildPackage(AdoContextPackageBo bo) {
        AdoArtifact task = artifactMapper.selectById(bo.getTaskId());
        AdoArtifact spec = artifactMapper.selectById(bo.getSpecArtifactId());
        if (task == null || spec == null) {
            throw new ServiceException("任务或 Spec 工件不存在");
        }
        int specVersion = bo.getSpecVersion() == null ? spec.getCurrentVersion() : bo.getSpecVersion();
        AdoArtifactVersion version = versionMapper.selectOne(new LambdaQueryWrapper<AdoArtifactVersion>()
            .eq(AdoArtifactVersion::getArtifactId, spec.getArtifactId())
            .eq(AdoArtifactVersion::getVersionNo, specVersion)
            .last("limit 1"));

        Map<String, Object> content = new HashMap<>();
        content.put("task", artifactBrief(task));
        Map<String, Object> specBrief = artifactBrief(spec);
        specBrief.put("version", specVersion);
        String snapshotJson = version == null ? JsonUtils.toJsonString(spec) : version.getSnapshot();
        specBrief.put("snapshot", JsonUtils.parseObject(snapshotJson, Object.class));
        content.put("spec", specBrief);
        content.put("linkedArtifacts", collectLinkedArtifacts(spec.getArtifactId()));
        content.put("generatedAt", new Date());
        content.put("aiBrief", bo.getAiBrief());

        // 同一任务只保留一个 active 上下文包
        invalidateByTaskId(bo.getTaskId());

        AdoContextPackage pkg = new AdoContextPackage();
        pkg.setProjectId(bo.getProjectId());
        pkg.setTaskId(bo.getTaskId());
        pkg.setSpecArtifactId(spec.getArtifactId());
        pkg.setSpecVersion(specVersion);
        pkg.setContent(JsonUtils.toJsonString(content));
        pkg.setAiBrief(bo.getAiBrief());
        pkg.setStatus(AdoConstants.STATUS_ACTIVE);
        pkg.setGeneratedAt(new Date());
        pkg.setRemark(bo.getRemark());
        baseMapper.insert(pkg);

        Map<String, Object> payload = new HashMap<>();
        payload.put("packageId", pkg.getPackageId());
        payload.put("taskId", pkg.getTaskId());
        payload.put("specArtifactId", pkg.getSpecArtifactId());
        payload.put("specVersion", pkg.getSpecVersion());
        eventService.record("context_package.built", "context_package", pkg.getPackageId(), specVersion,
            pkg.getProjectId(), payload);
        return pkg.getPackageId();
    }

    @Override
    public AdoContextPackage selectByTaskId(Long taskId) {
        return baseMapper.selectOne(new LambdaQueryWrapper<AdoContextPackage>()
            .eq(AdoContextPackage::getTaskId, taskId)
            .eq(AdoContextPackage::getStatus, AdoConstants.STATUS_ACTIVE)
            .orderByDesc(AdoContextPackage::getGeneratedAt)
            .last("limit 1"));
    }

    @Override
    public boolean invalidateByTaskId(Long taskId) {
        return baseMapper.update(null, new LambdaUpdateWrapper<AdoContextPackage>()
            .eq(AdoContextPackage::getTaskId, taskId)
            .eq(AdoContextPackage::getStatus, AdoConstants.STATUS_ACTIVE)
            .set(AdoContextPackage::getStatus, AdoConstants.STATUS_INVALIDATED)) >= 0;
    }

    private List<Map<String, Object>> collectLinkedArtifacts(Long specArtifactId) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<AdoLink> downstream = linkMapper.selectList(new LambdaQueryWrapper<AdoLink>()
            .eq(AdoLink::getFromArtifactId, specArtifactId)
            .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE));
        for (AdoLink link : downstream) {
            addLinkedArtifact(result, link, link.getToArtifactId(), "downstream");
        }
        List<AdoLink> upstream = linkMapper.selectList(new LambdaQueryWrapper<AdoLink>()
            .eq(AdoLink::getToArtifactId, specArtifactId)
            .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE));
        for (AdoLink link : upstream) {
            addLinkedArtifact(result, link, link.getFromArtifactId(), "upstream");
        }
        return result;
    }

    private void addLinkedArtifact(List<Map<String, Object>> result, AdoLink link, Long artifactId, String direction) {
        AdoArtifact artifact = artifactMapper.selectById(artifactId);
        if (artifact == null) {
            return;
        }
        Map<String, Object> item = artifactBrief(artifact);
        item.put("linkId", link.getLinkId());
        item.put("relation", link.getRelation());
        item.put("direction", direction);
        item.put("sourceVersion", link.getFromVersion());
        item.put("targetVersion", link.getToVersion());
        result.add(item);
    }

    private Map<String, Object> artifactBrief(AdoArtifact artifact) {
        Map<String, Object> item = new HashMap<>();
        item.put("artifactId", artifact.getArtifactId());
        item.put("artifactType", artifact.getArtifactType());
        item.put("artifactKey", artifact.getArtifactKey());
        item.put("title", artifact.getTitle());
        item.put("status", artifact.getStatus());
        item.put("currentVersion", artifact.getCurrentVersion());
        item.put("ownerName", artifact.getOwnerName());
        if (StringUtils.isNotBlank(artifact.getPayload())) {
            item.put("payload", artifact.getPayload());
        }
        return item;
    }

}
