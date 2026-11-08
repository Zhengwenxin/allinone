package org.dromara.aidevops.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.aidevops.ai.AiCompletion;
import org.dromara.aidevops.ai.AiModelProvider;
import org.dromara.aidevops.ai.MockAiModelProvider;
import org.dromara.aidevops.domain.AdoAiRun;
import org.dromara.aidevops.domain.bo.RequirementClarifyBo;
import org.dromara.aidevops.domain.vo.RequirementClarifyVo;
import org.dromara.aidevops.mapper.AdoAiRunMapper;
import org.dromara.aidevops.service.IRequirementClarifierService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 需求澄清 Agent 实现
 *
 * @author ruoyi
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequirementClarifierServiceImpl implements IRequirementClarifierService {

    private static final String AGENT_TYPE = "requirement-clarifier";

    private final AiModelProvider modelProvider;
    private final AdoAiRunMapper aiRunMapper;

    private String systemPrompt;

    @PostConstruct
    public void init() {
        systemPrompt = loadPrompt("prompts/requirement-clarifier-system.txt");
    }

    @Override
    public RequirementClarifyVo clarify(RequirementClarifyBo bo) {
        long start = System.currentTimeMillis();
        AiCompletion completion = null;
        try {
            completion = modelProvider.chat(systemPrompt, buildUserPrompt(bo));
            long latency = System.currentTimeMillis() - start;
            Map<String, Object> spec = parseSpec(completion.content());
            normalize(spec, bo.getRawRequirement());
            AdoAiRun run = saveRun(bo, completion, latency, "success", null);

            RequirementClarifyVo vo = new RequirementClarifyVo();
            vo.setAiRunId(run.getRunId());
            vo.setProvider(completion.provider());
            vo.setModel(completion.model());
            vo.setLatencyMs(latency);
            vo.setMock(modelProvider instanceof MockAiModelProvider);
            vo.setSpec(spec);
            vo.setRawOutput(completion.content());
            return vo;
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - start;
            saveRun(bo, completion, latency, "failed", e.getMessage());
            log.error("需求澄清失败, projectId={}", bo.getProjectId(), e);
            if (e instanceof ServiceException serviceException) {
                throw serviceException;
            }
            throw new ServiceException("需求澄清失败：" + e.getMessage());
        }
    }

    private String buildUserPrompt(RequirementClarifyBo bo) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("项目ID: ").append(bo.getProjectId()).append('\n');
        prompt.append("原始需求:\n").append(bo.getRawRequirement()).append('\n');
        if (StringUtils.isNotBlank(bo.getContext())) {
            prompt.append("\n补充上下文:\n").append(bo.getContext()).append('\n');
        }
        prompt.append("\n请输出符合约定结构的纯 JSON。");
        return prompt.toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseSpec(String output) {
        String json = extractJson(output);
        Map<String, Object> spec = JsonUtils.parseObject(json, new TypeReference<Map<String, Object>>() {
        });
        if (spec == null || spec.isEmpty()) {
            throw new ServiceException("模型输出不是有效的 Spec JSON");
        }
        return spec;
    }

    private String extractJson(String output) {
        if (StringUtils.isBlank(output)) {
            throw new ServiceException("模型输出为空");
        }
        String value = output.trim();
        if (value.startsWith("```")) {
            int firstNewline = value.indexOf('\n');
            if (firstNewline > 0) {
                value = value.substring(firstNewline + 1);
            }
            if (value.endsWith("```")) {
                value = value.substring(0, value.length() - 3).trim();
            }
        }
        int first = value.indexOf('{');
        int last = value.lastIndexOf('}');
        if (first < 0 || last <= first) {
            throw new ServiceException("模型输出中未找到 JSON 对象");
        }
        return value.substring(first, last + 1);
    }

    @SuppressWarnings("unchecked")
    private void normalize(Map<String, Object> spec, String rawRequirement) {
        spec.putIfAbsent("spec_id", "REQ-" + Year.now().getValue() + "-000");
        spec.putIfAbsent("version", 1);
        spec.putIfAbsent("title", StringUtils.substring(rawRequirement, 0, 80));
        spec.putIfAbsent("status", "draft");
        spec.putIfAbsent("problem", rawRequirement);
        putList(spec, "goals", List.of("完成需求：" + rawRequirement));
        putList(spec, "non_goals", List.of("本期范围之外的目标待产品确认"));
        putList(spec, "personas", List.of(Map.of("role", "待确认用户", "scenario", "待补充使用场景")));
        putList(spec, "scenarios", List.of(Map.of("id", "S1", "name", "主流程", "steps", List.of("待补充步骤"))));
        putList(spec, "business_rules", List.of(Map.of("id", "BR-1", "rule", "待澄清业务规则")));
        putList(spec, "state_machine", new ArrayList<>());
        putList(spec, "fields", new ArrayList<>());
        putList(spec, "permissions", new ArrayList<>());
        putList(spec, "nfr", List.of(Map.of("id", "NFR-1", "rule", "待补充非功能要求")));
        putList(spec, "acceptance_criteria", List.of(Map.of(
            "id", "AC-1", "given", "前置条件待澄清", "when", "用户执行核心操作", "then", "系统返回预期结果"
        )));
        putList(spec, "analytics", new ArrayList<>());
        putList(spec, "open_questions", List.of(
            Map.of("id", "Q-1", "question", "目标用户和核心场景是什么？", "owner", "产品", "blocking", true)
        ));
        putList(spec, "decisions", new ArrayList<>());
        putList(spec, "links", new ArrayList<>());

        Object acObj = spec.get("acceptance_criteria");
        if (!(acObj instanceof List<?> acList) || acList.isEmpty()) {
            spec.put("acceptance_criteria", List.of(Map.of(
                "id", "AC-1", "given", "前置条件待澄清", "when", "用户执行核心操作", "then", "系统返回预期结果"
            )));
        }
    }

    private void putList(Map<String, Object> spec, String key, List<?> defaultValue) {
        Object value = spec.get(key);
        if (!(value instanceof List<?>)) {
            spec.put(key, defaultValue);
        }
    }

    private AdoAiRun saveRun(RequirementClarifyBo bo, AiCompletion completion, long latency,
                             String status, String errorMessage) {
        AdoAiRun run = new AdoAiRun();
        run.setProjectId(bo.getProjectId());
        run.setAgentType(AGENT_TYPE);
        run.setProvider(completion == null ? modelProvider.providerName() : completion.provider());
        run.setModelName(completion == null ? modelProvider.modelName() : completion.model());
        run.setSystemPrompt(systemPrompt);
        String input = bo.getRawRequirement();
        if (StringUtils.isNotBlank(bo.getContext())) {
            input = input + "\n\n补充上下文:\n" + bo.getContext();
        }
        run.setInputText(input);
        run.setOutputText(completion == null ? null : completion.content());
        run.setStatus(status);
        run.setErrorMessage(errorMessage);
        run.setPromptTokens(completion == null ? 0 : completion.promptTokens());
        run.setCompletionTokens(completion == null ? 0 : completion.completionTokens());
        run.setTotalTokens(completion == null ? 0 : completion.totalTokens());
        run.setLatencyMs(latency);
        aiRunMapper.insert(run);
        return run;
    }

    private String loadPrompt(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.warn("加载 Prompt 失败: {}", path, e);
            return "你是需求澄清 Agent。请把原始需求输出为纯 JSON 结构化 Spec。";
        }
    }

}
