package org.dromara.aidevops.ai;

import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;

import java.time.Year;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Mock 模型：无模型密钥时用于打通需求澄清流程。
 * <p>
 * 输出是确定性的结构化 Spec 草稿，不代表真实 AI 分析结果。
 *
 * @author ruoyi
 */
public class MockAiModelProvider implements AiModelProvider {

    @Override
    public AiCompletion chat(String systemPrompt, String userPrompt) {
        String raw = extractRawRequirement(userPrompt);
        String title = StringUtils.substring(raw, 0, 80);

        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("spec_id", "REQ-" + Year.now().getValue() + "-000");
        spec.put("version", 1);
        spec.put("title", StringUtils.isBlank(title) ? "AI 需求草稿" : title);
        spec.put("status", "draft");
        spec.put("problem", raw);
        spec.put("goals", List.of("完成需求：" + raw));
        spec.put("non_goals", List.of("本期范围之外的目标待产品确认"));
        spec.put("personas", List.of(Map.of("role", "待确认用户", "scenario", "待补充使用场景")));
        spec.put("scenarios", List.of(Map.of(
            "id", "S1",
            "name", "主流程",
            "steps", List.of("用户触发需求场景", "系统完成核心动作", "用户获得预期结果")
        )));
        spec.put("business_rules", List.of(Map.of("id", "BR-1", "rule", "待澄清业务规则")));
        spec.put("state_machine", List.of());
        spec.put("fields", List.of());
        spec.put("permissions", List.of());
        spec.put("nfr", List.of(Map.of("id", "NFR-1", "rule", "待补充非功能要求", "metric", "待定", "target", "待定")));
        spec.put("acceptance_criteria", List.of(Map.of(
            "id", "AC-1",
            "given", "用户处于主流程场景",
            "when", "用户执行核心操作",
            "then", "系统返回符合预期的结果"
        )));
        spec.put("analytics", List.of());
        spec.put("open_questions", List.of(
            Map.of("id", "Q-1", "question", "目标用户和核心使用场景是什么？", "owner", "产品", "blocking", true),
            Map.of("id", "Q-2", "question", "成功的衡量指标是什么？", "owner", "产品", "blocking", true),
            Map.of("id", "Q-3", "question", "异常、边界和权限场景如何处理？", "owner", "研发/测试", "blocking", false)
        ));
        spec.put("decisions", List.of());
        spec.put("links", List.of());

        return new AiCompletion(JsonUtils.toJsonString(spec), providerName(), modelName(), 0, 0, 0);
    }

    @Override
    public String providerName() {
        return "mock";
    }

    @Override
    public String modelName() {
        return "mock-requirement-clarifier";
    }

    private String extractRawRequirement(String userPrompt) {
        String raw = userPrompt == null ? "" : userPrompt;
        int start = raw.indexOf("原始需求:");
        if (start >= 0) {
            raw = raw.substring(start + "原始需求:".length()).trim();
        }
        int end = raw.indexOf("补充上下文:");
        if (end >= 0) {
            raw = raw.substring(0, end).trim();
        }
        return raw;
    }

}
