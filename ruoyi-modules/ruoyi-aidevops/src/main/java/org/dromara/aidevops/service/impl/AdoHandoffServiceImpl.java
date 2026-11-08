package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoHandoff;
import org.dromara.aidevops.domain.bo.AdoHandoffBo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoHandoffMapper;
import org.dromara.aidevops.service.IAdoEventService;
import org.dromara.aidevops.service.IAdoHandoffService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交接契约服务实现
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class AdoHandoffServiceImpl implements IAdoHandoffService {

    private final AdoHandoffMapper baseMapper;
    private final AdoArtifactMapper artifactMapper;
    private final IAdoEventService eventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createHandoff(AdoHandoffBo bo) {
        AdoHandoff handoff = MapstructUtils.convert(bo, AdoHandoff.class);
        handoff.setStatus(AdoConstants.HANDOFF_DRAFT);
        handoff.setArtifactIds(StringUtils.isBlank(bo.getArtifactIds()) ? "[]" : bo.getArtifactIds());
        handoff.setChecklist(StringUtils.isBlank(bo.getChecklist()) ? "[]" : bo.getChecklist());
        handoff.setSnapshot(buildSnapshot(handoff.getArtifactIds()));
        baseMapper.insert(handoff);

        Map<String, Object> payload = new HashMap<>();
        payload.put("handoffId", handoff.getHandoffId());
        payload.put("projectId", handoff.getProjectId());
        payload.put("handoffType", handoff.getHandoffType());
        payload.put("status", handoff.getStatus());
        eventService.record("handoff.created", "handoff", handoff.getHandoffId(), null,
            handoff.getProjectId(), payload);
        return handoff.getHandoffId();
    }

    @Override
    public AdoHandoff selectById(Long handoffId) {
        return baseMapper.selectById(handoffId);
    }

    @Override
    public List<AdoHandoff> selectList(Long projectId, String handoffType, String status) {
        return baseMapper.selectList(new LambdaQueryWrapper<AdoHandoff>()
            .eq(projectId != null, AdoHandoff::getProjectId, projectId)
            .eq(StringUtils.isNotBlank(handoffType), AdoHandoff::getHandoffType, handoffType)
            .eq(StringUtils.isNotBlank(status), AdoHandoff::getStatus, status)
            .orderByDesc(AdoHandoff::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signOff(Long handoffId, String status) {
        if (!StringUtils.equalsAny(status,
            AdoConstants.HANDOFF_PASSED, AdoConstants.HANDOFF_REJECTED, AdoConstants.HANDOFF_SIGNED)) {
            throw new ServiceException("不支持的交接状态：" + status);
        }
        AdoHandoff handoff = baseMapper.selectById(handoffId);
        if (handoff == null) {
            throw new ServiceException("交接单不存在");
        }
        handoff.setStatus(status);
        handoff.setSignedOffBy(LoginHelper.getUserId());
        handoff.setSignedOffAt(new Date());
        baseMapper.updateById(handoff);

        Map<String, Object> payload = new HashMap<>();
        payload.put("handoffId", handoffId);
        payload.put("status", status);
        payload.put("signedOffBy", handoff.getSignedOffBy());
        eventService.record("handoff.signed", "handoff", handoffId, null,
            handoff.getProjectId(), payload);
        return true;
    }

    private String buildSnapshot(String artifactIdsJson) {
        List<Long> artifactIds = JsonUtils.parseObject(artifactIdsJson, new TypeReference<List<Long>>() {
        });
        List<Map<String, Object>> artifacts = new ArrayList<>();
        if (artifactIds != null) {
            for (Long artifactId : artifactIds) {
                AdoArtifact artifact = artifactMapper.selectById(artifactId);
                if (artifact == null) {
                    continue;
                }
                Map<String, Object> item = new HashMap<>();
                item.put("artifactId", artifact.getArtifactId());
                item.put("artifactType", artifact.getArtifactType());
                item.put("artifactKey", artifact.getArtifactKey());
                item.put("title", artifact.getTitle());
                item.put("version", artifact.getCurrentVersion());
                artifacts.add(item);
            }
        }
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("artifacts", artifacts);
        return JsonUtils.toJsonString(snapshot);
    }

}
