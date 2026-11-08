package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.vo.ConsistencyReportVo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoLinkMapper;
import org.dromara.aidevops.service.IAdoConsistencyService;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 一致性巡检服务实现
 * <p>
 * Phase 0 先覆盖最关键的几类信息漂移，后续可扩展为规则引擎 + AI 语义检查。
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class AdoConsistencyServiceImpl implements IAdoConsistencyService {

    private static final int MAX_FINDINGS = 200;

    private final AdoArtifactMapper artifactMapper;
    private final AdoLinkMapper linkMapper;

    @Override
    public ConsistencyReportVo check(Long projectId) {
        List<AdoArtifact> artifacts = artifactMapper.selectList(new LambdaQueryWrapper<AdoArtifact>()
            .eq(AdoArtifact::getProjectId, projectId)
            .last("limit 1000"));
        List<AdoLink> links = linkMapper.selectList(new LambdaQueryWrapper<AdoLink>()
            .eq(AdoLink::getProjectId, projectId)
            .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE));

        Map<Long, AdoArtifact> artifactById = artifacts.stream()
            .collect(Collectors.toMap(AdoArtifact::getArtifactId, Function.identity(), (a, b) -> a));
        Map<Long, List<AdoLink>> outgoing = links.stream()
            .collect(Collectors.groupingBy(AdoLink::getFromArtifactId));
        Map<Long, List<AdoLink>> incoming = links.stream()
            .collect(Collectors.groupingBy(AdoLink::getToArtifactId));

        List<ConsistencyReportVo.FindingVo> findings = new ArrayList<>();
        for (AdoArtifact artifact : artifacts) {
            List<AdoLink> out = outgoing.getOrDefault(artifact.getArtifactId(), List.of());
            List<AdoLink> in = incoming.getOrDefault(artifact.getArtifactId(), List.of());

            if (out.isEmpty() && in.isEmpty() && isCoreArtifact(artifact.getArtifactType())) {
                findings.add(finding("orphan_artifact", "low", artifact,
                    "工件没有任何上下游关系", "补充需求/任务/用例/契约关系，避免成为信息孤岛"));
            }
            if (AdoConstants.ARTIFACT_REQUIREMENT.equals(artifact.getArtifactType())
                && !hasDownstreamType(out, artifactById, AdoConstants.ARTIFACT_TEST_CASE, AdoConstants.REL_VERIFIES)) {
                findings.add(finding("requirement_without_test", "high", artifact,
                    "需求没有验证用例", "补充测试用例并建立 verifies 关系，或在交接单中记录豁免"));
            }
            if (AdoConstants.ARTIFACT_CONTRACT.equals(artifact.getArtifactType())
                && !hasDownstreamType(out, artifactById, AdoConstants.ARTIFACT_TASK, AdoConstants.REL_IMPLEMENTS)) {
                findings.add(finding("contract_without_task", "high", artifact,
                    "接口契约没有实现任务", "建立契约到开发任务的 implements 关系"));
            }
            if (AdoConstants.ARTIFACT_TASK.equals(artifact.getArtifactType()) && in.isEmpty()) {
                findings.add(finding("task_without_source", "medium", artifact,
                    "任务没有来源需求/契约", "补充 derives_from 或 implements 关系，保证任务可追溯"));
            }
        }

        for (AdoLink link : links) {
            AdoArtifact source = artifactById.get(link.getFromArtifactId());
            if (source == null || link.getFromVersion() == null) {
                continue;
            }
            if (link.getFromVersion() < source.getCurrentVersion()) {
                AdoArtifact downstream = artifactById.get(link.getToArtifactId());
                if (downstream != null) {
                    findings.add(finding("stale_link", "medium", downstream,
                        "下游仍基于上游 v" + link.getFromVersion() + "，上游当前 v" + source.getCurrentVersion(),
                        "确认影响后更新下游工件，并调用关系确认接口推进消费版本",
                        "linkId=" + link.getLinkId()));
                }
            }
        }

        findings.sort(Comparator.comparingInt(item -> severityOrder(item.getSeverity())));
        if (findings.size() > MAX_FINDINGS) {
            findings = new ArrayList<>(findings.subList(0, MAX_FINDINGS));
        }

        ConsistencyReportVo report = new ConsistencyReportVo();
        report.setProjectId(projectId);
        report.setGeneratedAt(new Date());
        report.setTotal(findings.size());
        Map<String, Integer> counts = new HashMap<>();
        for (ConsistencyReportVo.FindingVo finding : findings) {
            counts.merge(finding.getType(), 1, Integer::sum);
        }
        report.setCounts(counts);
        report.setFindings(findings);
        return report;
    }

    private boolean isCoreArtifact(String type) {
        return StringUtils.equalsAny(type,
            AdoConstants.ARTIFACT_REQUIREMENT, AdoConstants.ARTIFACT_CONTRACT,
            AdoConstants.ARTIFACT_TASK, AdoConstants.ARTIFACT_TEST_CASE);
    }

    private boolean hasDownstreamType(List<AdoLink> links, Map<Long, AdoArtifact> artifactById,
                                      String targetType, String relation) {
        for (AdoLink link : links) {
            if (!StringUtils.equals(link.getRelation(), relation)) {
                continue;
            }
            AdoArtifact target = artifactById.get(link.getToArtifactId());
            if (target != null && StringUtils.equals(target.getArtifactType(), targetType)) {
                return true;
            }
        }
        return false;
    }

    private ConsistencyReportVo.FindingVo finding(String type, String severity, AdoArtifact artifact,
                                                  String message, String suggestion) {
        return finding(type, severity, artifact, message, suggestion, "artifact=" + artifact.getArtifactKey());
    }

    private ConsistencyReportVo.FindingVo finding(String type, String severity, AdoArtifact artifact,
                                                  String message, String suggestion, String evidence) {
        ConsistencyReportVo.FindingVo finding = new ConsistencyReportVo.FindingVo();
        finding.setType(type);
        finding.setSeverity(severity);
        finding.setArtifactId(artifact.getArtifactId());
        finding.setArtifactKey(artifact.getArtifactKey());
        finding.setTitle(artifact.getTitle());
        finding.setMessage(message);
        finding.setSuggestion(suggestion);
        finding.setEvidence(evidence);
        return finding;
    }

    private int severityOrder(String severity) {
        if ("high".equals(severity)) {
            return 0;
        }
        if ("medium".equals(severity)) {
            return 1;
        }
        return 2;
    }

}
