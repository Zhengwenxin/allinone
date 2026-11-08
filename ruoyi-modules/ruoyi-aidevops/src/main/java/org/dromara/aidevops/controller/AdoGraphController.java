package org.dromara.aidevops.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.bo.AdoLinkBo;
import org.dromara.aidevops.domain.vo.TraceabilityVo;
import org.dromara.aidevops.service.IAdoLinkService;
import org.dromara.aidevops.service.IAdoTraceabilityService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 工件图：关系与追溯
 *
 * @author ruoyi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-devops")
public class AdoGraphController extends BaseController {

    private final IAdoLinkService linkService;
    private final IAdoTraceabilityService traceabilityService;

    @SaCheckPermission("ai:devops:link:add")
    @Log(title = "AI-DevOps工件关系", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/link")
    public R<Long> addLink(@Validated @RequestBody AdoLinkBo bo) {
        return R.ok(linkService.createLink(bo));
    }

    @SaCheckPermission("ai:devops:link:remove")
    @Log(title = "AI-DevOps工件关系", businessType = BusinessType.DELETE)
    @DeleteMapping("/link/{linkIds}")
    public R<Void> removeLink(@PathVariable Long[] linkIds) {
        return toAjax(linkService.deleteLinkByIds(Arrays.asList(linkIds)));
    }

    @SaCheckPermission("ai:devops:link:list")
    @GetMapping("/link/list")
    public R<List<AdoLink>> listLink(@RequestParam Long artifactId,
                                     @RequestParam(defaultValue = "both") String direction) {
        return R.ok(linkService.selectLinks(artifactId, direction));
    }

    @SaCheckPermission("ai:devops:link:ack")
    @Log(title = "AI-DevOps关系确认", businessType = BusinessType.UPDATE)
    @PutMapping("/link/ack/{linkId}")
    public R<Void> acknowledge(@PathVariable Long linkId) {
        return toAjax(linkService.acknowledge(linkId));
    }

    @SaCheckPermission("ai:devops:trace:query")
    @GetMapping("/traceability/{artifactId}")
    public R<TraceabilityVo> trace(@PathVariable Long artifactId,
                                   @RequestParam(defaultValue = "both") String direction,
                                   @RequestParam(defaultValue = "3") int depth) {
        return R.ok(traceabilityService.trace(artifactId, direction, depth));
    }

}
