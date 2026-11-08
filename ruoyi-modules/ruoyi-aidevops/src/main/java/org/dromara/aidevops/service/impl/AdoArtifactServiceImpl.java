package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoArtifactVersion;
import org.dromara.aidevops.domain.bo.AdoArtifactBo;
import org.dromara.aidevops.domain.vo.AdoArtifactVersionVo;
import org.dromara.aidevops.domain.vo.AdoArtifactVo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoArtifactVersionMapper;
import org.dromara.aidevops.service.IAdoArtifactService;
import org.dromara.aidevops.service.IAdoEventService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工件服务实现
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class AdoArtifactServiceImpl implements IAdoArtifactService {

    private final AdoArtifactMapper baseMapper;
    private final AdoArtifactVersionMapper versionMapper;
    private final IAdoEventService eventService;

    @Override
    public TableDataInfo<AdoArtifactVo> selectPageArtifactList(AdoArtifactBo bo, PageQuery pageQuery) {
        Page<AdoArtifactVo> page = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(page);
    }

    @Override
    public AdoArtifactVo selectArtifactById(Long artifactId) {
        return baseMapper.selectVoById(artifactId);
    }

    @Override
    public AdoArtifactVo selectArtifactByKey(Long projectId, String artifactType, String artifactKey) {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<AdoArtifact>()
            .eq(AdoArtifact::getProjectId, projectId)
            .eq(AdoArtifact::getArtifactType, artifactType)
            .eq(AdoArtifact::getArtifactKey, artifactKey));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createArtifact(AdoArtifactBo bo) {
        checkKeyUnique(bo, null);
        AdoArtifact artifact = MapstructUtils.convert(bo, AdoArtifact.class);
        artifact.setCurrentVersion(1);
        if (StringUtils.isBlank(artifact.getStatus())) {
            artifact.setStatus("draft");
        }
        artifact.setSearchText(buildSearchText(artifact));
        baseMapper.insert(artifact);

        saveVersion(artifact, StringUtils.isBlank(bo.getChangeSummary()) ? "创建工件" : bo.getChangeSummary());
        eventService.record("artifact.created", "artifact", artifact.getArtifactId(), 1,
            artifact.getProjectId(), buildEventPayload(artifact));
        return artifact.getArtifactId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateArtifact(AdoArtifactBo bo) {
        if (bo.getArtifactId() == null) {
            throw new ServiceException("工件ID不能为空");
        }
        AdoArtifact existing = baseMapper.selectById(bo.getArtifactId());
        if (existing == null) {
            throw new ServiceException("工件不存在");
        }
        checkKeyUnique(bo, bo.getArtifactId());

        AdoArtifact artifact = MapstructUtils.convert(bo, AdoArtifact.class);
        artifact.setCurrentVersion(existing.getCurrentVersion() + 1);
        artifact.setSearchText(buildSearchText(artifact));
        baseMapper.updateById(artifact);

        AdoArtifact updated = baseMapper.selectById(bo.getArtifactId());
        saveVersion(updated, StringUtils.isBlank(bo.getChangeSummary()) ? "更新工件" : bo.getChangeSummary());
        eventService.record("artifact.updated", "artifact", updated.getArtifactId(), updated.getCurrentVersion(),
            updated.getProjectId(), buildEventPayload(updated));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteArtifactByIds(List<Long> artifactIds) {
        return baseMapper.deleteByIds(artifactIds) > 0;
    }

    @Override
    public List<AdoArtifactVersionVo> selectVersions(Long artifactId) {
        return versionMapper.selectVoList(new LambdaQueryWrapper<AdoArtifactVersion>()
            .eq(AdoArtifactVersion::getArtifactId, artifactId)
            .orderByDesc(AdoArtifactVersion::getVersionNo));
    }

    private LambdaQueryWrapper<AdoArtifact> buildQueryWrapper(AdoArtifactBo bo) {
        Map<String, Object> params = bo.getParams();
        return new LambdaQueryWrapper<AdoArtifact>()
            .eq(bo.getProjectId() != null, AdoArtifact::getProjectId, bo.getProjectId())
            .eq(StringUtils.isNotBlank(bo.getArtifactType()), AdoArtifact::getArtifactType, bo.getArtifactType())
            .like(StringUtils.isNotBlank(bo.getArtifactKey()), AdoArtifact::getArtifactKey, bo.getArtifactKey())
            .like(StringUtils.isNotBlank(bo.getTitle()), AdoArtifact::getTitle, bo.getTitle())
            .eq(StringUtils.isNotBlank(bo.getStatus()), AdoArtifact::getStatus, bo.getStatus())
            .eq(bo.getOwnerId() != null, AdoArtifact::getOwnerId, bo.getOwnerId())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                AdoArtifact::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(AdoArtifact::getUpdateTime);
    }

    private void checkKeyUnique(AdoArtifactBo bo, Long excludeId) {
        boolean exists = baseMapper.exists(new LambdaQueryWrapper<AdoArtifact>()
            .eq(AdoArtifact::getProjectId, bo.getProjectId())
            .eq(AdoArtifact::getArtifactType, bo.getArtifactType())
            .eq(AdoArtifact::getArtifactKey, bo.getArtifactKey())
            .ne(excludeId != null, AdoArtifact::getArtifactId, excludeId));
        if (exists) {
            throw new ServiceException("同一项目下工件编号已存在：" + bo.getArtifactKey());
        }
    }

    private void saveVersion(AdoArtifact artifact, String changeSummary) {
        AdoArtifactVersion version = new AdoArtifactVersion();
        version.setArtifactId(artifact.getArtifactId());
        version.setVersionNo(artifact.getCurrentVersion());
        version.setSnapshot(JsonUtils.toJsonString(artifact));
        version.setChangeSummary(StringUtils.substring(changeSummary, 0, 1000));
        version.setAuthorType(AdoConstants.ACTOR_HUMAN);
        version.setAuthorId(LoginHelper.getUserId());
        version.setAuthorName(LoginHelper.getUsername());
        versionMapper.insert(version);
    }

    private String buildSearchText(AdoArtifact artifact) {
        return artifact.getArtifactKey() + " " + artifact.getTitle() + " "
            + StringUtils.defaultString(artifact.getSummary());
    }

    private Map<String, Object> buildEventPayload(AdoArtifact artifact) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("artifactId", artifact.getArtifactId());
        payload.put("projectId", artifact.getProjectId());
        payload.put("artifactType", artifact.getArtifactType());
        payload.put("artifactKey", artifact.getArtifactKey());
        payload.put("title", artifact.getTitle());
        payload.put("status", artifact.getStatus());
        payload.put("version", artifact.getCurrentVersion());
        return payload;
    }

}
