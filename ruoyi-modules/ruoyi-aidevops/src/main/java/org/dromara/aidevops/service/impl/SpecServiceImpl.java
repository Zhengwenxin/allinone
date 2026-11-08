package org.dromara.aidevops.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.aidevops.constant.AdoConstants;
import org.dromara.aidevops.domain.AdoArtifact;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.vo.AcCoverageVo;
import org.dromara.aidevops.domain.vo.SpecValidationVo;
import org.dromara.aidevops.mapper.AdoArtifactMapper;
import org.dromara.aidevops.mapper.AdoLinkMapper;
import org.dromara.aidevops.service.ISpecService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 结构化 Spec 服务实现
 *
 * @author ruoyi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpecServiceImpl implements ISpecService {

    private final AdoArtifactMapper artifactMapper;
    private final AdoLinkMapper linkMapper;

    private String schemaJson;
    private JsonSchema jsonSchema;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("spec/spec-schema.json");
            schemaJson = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
            jsonSchema = factory.getSchema(JsonUtils.parseObject(schemaJson, JsonNode.class));
        } catch (Exception e) {
            log.error("加载 Spec JSON Schema 失败", e);
        }
    }

    @Override
    public Object schema() {
        return JsonUtils.parseObject(schemaJson, Object.class);
    }

    @Override
    public SpecValidationVo validate(String specJson) {
        SpecValidationVo result = new SpecValidationVo();
        if (StringUtils.isBlank(specJson)) {
            result.setValid(false);
            result.getErrors().add(error("/", "Spec 不能为空"));
            return result;
        }
        JsonNode node;
        try {
            node = JsonUtils.parseObject(specJson, JsonNode.class);
        } catch (Exception e) {
            result.setValid(false);
            result.getErrors().add(error("/", "Spec 不是合法 JSON：" + e.getMessage()));
            return result;
        }
        if (jsonSchema == null) {
            result.setValid(false);
            result.getErrors().add(error("/", "Spec Schema 未加载"));
            return result;
        }
        Set<ValidationMessage> messages = jsonSchema.validate(node);
        if (messages.isEmpty()) {
            result.setValid(true);
            return result;
        }
        result.setValid(false);
        for (ValidationMessage message : messages) {
            String path = message.getInstanceLocation() == null ? "/" : message.getInstanceLocation().toString();
            result.getErrors().add(error(path, message.getMessage()));
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AcCoverageVo acCoverage(Long specArtifactId) {
        AdoArtifact spec = artifactMapper.selectById(specArtifactId);
        if (spec == null) {
            throw new ServiceException("Spec 工件不存在");
        }
        if (StringUtils.isBlank(spec.getPayload())) {
            throw new ServiceException("Spec 工件没有结构化 payload");
        }
        Map<String, Object> specMap = JsonUtils.parseObject(spec.getPayload(), new TypeReference<Map<String, Object>>() {
        });
        Object acObj = specMap == null ? null : specMap.get("acceptance_criteria");
        List<Map<String, Object>> acs = acObj instanceof List<?> list ? (List<Map<String, Object>>) list : List.of();

        List<String> acIds = new ArrayList<>();
        for (Map<String, Object> ac : acs) {
            Object id = ac.get("id");
            if (id != null) {
                acIds.add(String.valueOf(id));
            }
        }

        Map<String, Set<String>> coverage = new HashMap<>();
        for (String acId : acIds) {
            coverage.put(acId, new LinkedHashSet<>());
        }

        List<AdoLink> links = linkMapper.selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AdoLink>()
            .eq(AdoLink::getFromArtifactId, specArtifactId)
            .eq(AdoLink::getRelation, AdoConstants.REL_VERIFIES)
            .eq(AdoLink::getStatus, AdoConstants.STATUS_ACTIVE));
        for (AdoLink link : links) {
            AdoArtifact target = artifactMapper.selectById(link.getToArtifactId());
            if (target == null || !AdoConstants.ARTIFACT_TEST_CASE.equals(target.getArtifactType())) {
                continue;
            }
            Set<String> targetAcIds = resolveTestCaseAcIds(target);
            if (targetAcIds.isEmpty()) {
                // 未标注 AC 的用例：如果工件编号包含 AC 编号，按编号归属；否则不计入任何 AC
                for (String acId : acIds) {
                    if (StringUtils.containsIgnoreCase(target.getArtifactKey(), acId)) {
                        targetAcIds.add(acId);
                    }
                }
            }
            for (String acId : targetAcIds) {
                Set<String> testCases = coverage.get(acId);
                if (testCases != null) {
                    testCases.add(target.getArtifactKey());
                }
            }
        }

        AcCoverageVo vo = new AcCoverageVo();
        vo.setSpecArtifactId(specArtifactId);
        vo.setTotal(acIds.size());
        int covered = 0;
        for (String acId : acIds) {
            AcCoverageVo.AcItem item = new AcCoverageVo.AcItem();
            item.setAcId(acId);
            Set<String> testCases = coverage.getOrDefault(acId, Set.of());
            item.setTestCaseKeys(new ArrayList<>(testCases));
            item.setCovered(!testCases.isEmpty());
            if (item.isCovered()) {
                covered++;
            }
            vo.getDetails().add(item);
        }
        vo.setCovered(covered);
        vo.setUncovered(acIds.size() - covered);
        vo.setCoverageRate(acIds.isEmpty() ? 0D : (double) covered / acIds.size());
        return vo;
    }

    @SuppressWarnings("unchecked")
    private Set<String> resolveTestCaseAcIds(AdoArtifact testCase) {
        Set<String> result = new LinkedHashSet<>();
        if (StringUtils.isBlank(testCase.getPayload())) {
            return result;
        }
        try {
            Map<String, Object> payload = JsonUtils.parseObject(testCase.getPayload(), new TypeReference<Map<String, Object>>() {
            });
            Object acIds = payload == null ? null : payload.get("acIds");
            if (acIds instanceof List<?> list) {
                for (Object acId : list) {
                    result.add(String.valueOf(acId));
                }
            }
        } catch (Exception e) {
            log.debug("解析测试用例 payload 失败: {}", testCase.getArtifactKey(), e);
        }
        return result;
    }

    private SpecValidationVo.ErrorItem error(String path, String message) {
        SpecValidationVo.ErrorItem item = new SpecValidationVo.ErrorItem();
        item.setPath(path);
        item.setMessage(message);
        return item;
    }

}
