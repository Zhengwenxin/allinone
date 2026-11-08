package org.dromara.aidevops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.vo.TraceabilityVo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoLinkMapper;
import org.dromara.aidevops.service.IAdoTraceabilityService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工件追溯服务实现
 *
 * @author ruoyi
 */
@Service
@RequiredArgsConstructor
public class AdoTraceabilityServiceImpl implements IAdoTraceabilityService {

    private final AdoArtifactMapper artifactMapper;
    private final AdoLinkMapper linkMapper;

    @Override
    public TraceabilityVo trace(Long artifactId, String direction, int depth) {
        AdoArtifact root = artifactMapper.selectById(artifactId);
        if (root == null) {
            throw new ServiceException("工件不存在");
        }
        String dir = StringUtils.isBlank(direction) ? "both" : direction.toLowerCase();
        int maxDepth = Math.max(1, Math.min(depth, 5));

        Map<Long, AdoArtifact> nodeMap = new LinkedHashMap<>();
        Map<Long, Integer> levelMap = new LinkedHashMap<>();
        nodeMap.put(root.getArtifactId(), root);
        levelMap.put(root.getArtifactId(), 0);

        Set<Long> visited = new HashSet<>();
        visited.add(root.getArtifactId());
        Set<Long> edgeIds = new HashSet<>();
        List<TraceabilityVo.TraceEdgeVo> edges = new ArrayList<>();
        Deque<LeveledNode> queue = new ArrayDeque<>();
        queue.add(new LeveledNode(root.getArtifactId(), 0));

        boolean upstream = "upstream".equals(dir) || "both".equals(dir);
        boolean downstream = "downstream".equals(dir) || "both".equals(dir);

        while (!queue.isEmpty()) {
            LeveledNode current = queue.poll();
            if (current.level >= maxDepth) {
                continue;
            }
            if (downstream) {
                List<AdoLink> links = linkMapper.selectList(new LambdaQueryWrapper<AdoLink>()
                    .eq(AdoLink::getFromArtifactId, current.artifactId)
                    .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE));
                for (AdoLink link : links) {
                    addEdge(link, edgeIds, edges);
                    discover(link.getToArtifactId(), current.level + 1, visited, queue, nodeMap, levelMap);
                }
            }
            if (upstream) {
                List<AdoLink> links = linkMapper.selectList(new LambdaQueryWrapper<AdoLink>()
                    .eq(AdoLink::getToArtifactId, current.artifactId)
                    .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE));
                for (AdoLink link : links) {
                    addEdge(link, edgeIds, edges);
                    discover(link.getFromArtifactId(), current.level + 1, visited, queue, nodeMap, levelMap);
                }
            }
        }

        TraceabilityVo vo = new TraceabilityVo();
        vo.setRoot(toNode(root, 0));
        List<TraceabilityVo.TraceNodeVo> nodes = new ArrayList<>();
        for (Map.Entry<Long, AdoArtifact> entry : nodeMap.entrySet()) {
            nodes.add(toNode(entry.getValue(), levelMap.getOrDefault(entry.getKey(), 0)));
        }
        vo.setNodes(nodes);
        vo.setEdges(edges);
        return vo;
    }

    private void discover(Long artifactId, int level, Set<Long> visited, Deque<LeveledNode> queue,
                          Map<Long, AdoArtifact> nodeMap, Map<Long, Integer> levelMap) {
        if (!visited.add(artifactId)) {
            return;
        }
        AdoArtifact artifact = artifactMapper.selectById(artifactId);
        if (artifact == null) {
            return;
        }
        nodeMap.put(artifactId, artifact);
        levelMap.put(artifactId, level);
        queue.add(new LeveledNode(artifactId, level));
    }

    private void addEdge(AdoLink link, Set<Long> edgeIds, List<TraceabilityVo.TraceEdgeVo> edges) {
        if (!edgeIds.add(link.getLinkId())) {
            return;
        }
        TraceabilityVo.TraceEdgeVo edge = new TraceabilityVo.TraceEdgeVo();
        edge.setLinkId(link.getLinkId());
        edge.setFromArtifactId(link.getFromArtifactId());
        edge.setFromVersion(link.getFromVersion());
        edge.setToArtifactId(link.getToArtifactId());
        edge.setToVersion(link.getToVersion());
        edge.setRelation(link.getRelation());
        edge.setConfidence(link.getConfidence());
        edge.setOrigin(link.getOrigin());
        edge.setStatus(link.getStatus());
        edges.add(edge);
    }

    private TraceabilityVo.TraceNodeVo toNode(AdoArtifact artifact, int level) {
        TraceabilityVo.TraceNodeVo node = new TraceabilityVo.TraceNodeVo();
        node.setArtifactId(artifact.getArtifactId());
        node.setArtifactType(artifact.getArtifactType());
        node.setArtifactKey(artifact.getArtifactKey());
        node.setTitle(artifact.getTitle());
        node.setStatus(artifact.getStatus());
        node.setCurrentVersion(artifact.getCurrentVersion());
        node.setOwnerName(artifact.getOwnerName());
        node.setLevel(level);
        return node;
    }

    private record LeveledNode(Long artifactId, int level) {
    }

}
