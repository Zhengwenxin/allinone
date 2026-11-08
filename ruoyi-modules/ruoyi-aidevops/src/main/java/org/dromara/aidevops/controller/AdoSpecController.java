package org.dromara.aidevops.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.domain.vo.AcCoverageVo;
import org.dromara.aidevops.domain.vo.SpecValidationVo;
import org.dromara.aidevops.service.ISpecService;
import org.dromara.common.core.domain.R;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.web.core.BaseController;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 结构化 Spec 与 AC 覆盖率
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-devops/spec")
public class AdoSpecController extends BaseController {

    private final ISpecService specService;

    @SaCheckPermission("ai:devops:spec:schema")
    @GetMapping("/schema")
    public R<Object> schema() {
        return R.ok(specService.schema());
    }

    @SaCheckPermission("ai:devops:spec:validate")
    @PostMapping(value = "/validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public R<SpecValidationVo> validate(@RequestBody JsonNode spec) {
        return R.ok(specService.validate(JsonUtils.toJsonString(spec)));
    }

    @SaCheckPermission("ai:devops:spec:coverage")
    @GetMapping("/{specArtifactId}/ac-coverage")
    public R<AcCoverageVo> acCoverage(@PathVariable Long specArtifactId) {
        return R.ok(specService.acCoverage(specArtifactId));
    }

}
