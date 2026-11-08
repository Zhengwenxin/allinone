package org.dromara.aidevops.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.domain.AdoContextPackage;
import org.dromara.aidevops.domain.AdoHandoff;
import org.dromara.aidevops.domain.bo.AdoContextPackageBo;
import org.dromara.aidevops.domain.bo.AdoHandoffBo;
import org.dromara.aidevops.service.IAdoContextPackageService;
import org.dromara.aidevops.service.IAdoHandoffService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 协作：交接契约与任务上下文包
 *
 * @author ruoyi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-devops")
public class AdoCollaborationController extends BaseController {

    private final IAdoHandoffService handoffService;
    private final IAdoContextPackageService contextPackageService;

    @SaCheckPermission("ai:devops:handoff:list")
    @GetMapping("/handoff/list")
    public R<List<AdoHandoff>> handoffList(@RequestParam(required = false) Long projectId,
                                           @RequestParam(required = false) String handoffType,
                                           @RequestParam(required = false) String status) {
        return R.ok(handoffService.selectList(projectId, handoffType, status));
    }

    @SaCheckPermission("ai:devops:handoff:query")
    @GetMapping("/handoff/{handoffId}")
    public R<AdoHandoff> getHandoff(@PathVariable Long handoffId) {
        return R.ok(handoffService.selectById(handoffId));
    }

    @SaCheckPermission("ai:devops:handoff:add")
    @Log(title = "AI-DevOps交接", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/handoff")
    public R<Long> addHandoff(@Validated @RequestBody AdoHandoffBo bo) {
        return R.ok(handoffService.createHandoff(bo));
    }

    @SaCheckPermission("ai:devops:handoff:sign")
    @Log(title = "AI-DevOps交接签收", businessType = BusinessType.UPDATE)
    @PutMapping("/handoff/sign/{handoffId}")
    public R<Void> signOff(@PathVariable Long handoffId, @RequestParam String status) {
        return toAjax(handoffService.signOff(handoffId, status));
    }

    @SaCheckPermission("ai:devops:context:build")
    @Log(title = "AI-DevOps上下文包", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/context-package/build")
    public R<Long> buildContextPackage(@Validated @RequestBody AdoContextPackageBo bo) {
        return R.ok(contextPackageService.buildPackage(bo));
    }

    @SaCheckPermission("ai:devops:context:query")
    @GetMapping("/context-package/task/{taskId}")
    public R<AdoContextPackage> getContextPackage(@PathVariable Long taskId) {
        return R.ok(contextPackageService.selectByTaskId(taskId));
    }

}
