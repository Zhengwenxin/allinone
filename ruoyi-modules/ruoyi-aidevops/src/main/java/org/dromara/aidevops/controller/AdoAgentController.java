package org.dromara.aidevops.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.domain.bo.RequirementClarifyBo;
import org.dromara.aidevops.domain.vo.RequirementClarifyVo;
import org.dromara.aidevops.service.IRequirementClarifierService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI Agent 能力入口
 *
 * @author ruoyi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-devops/agent")
public class AdoAgentController extends BaseController {

    private final IRequirementClarifierService requirementClarifierService;

    @SaCheckPermission("ai:devops:agent:requirement:clarify")
    @Log(title = "AI需求澄清", businessType = BusinessType.OTHER)
    @RepeatSubmit
    @PostMapping("/requirement/clarify")
    public R<RequirementClarifyVo> clarifyRequirement(@Validated @RequestBody RequirementClarifyBo bo) {
        return R.ok(requirementClarifierService.clarify(bo));
    }

}
