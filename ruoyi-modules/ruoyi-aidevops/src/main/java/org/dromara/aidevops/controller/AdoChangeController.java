package org.dromara.aidevops.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.domain.vo.ChangeImpactVo;
import org.dromara.aidevops.service.IAdoChangePropagationService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 变更传播
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-devops/change-impact")
public class AdoChangeController extends BaseController {

    private final IAdoChangePropagationService changePropagationService;

    @SaCheckPermission("ai:devops:change:impact")
    @GetMapping("/{artifactId}")
    public R<ChangeImpactVo> analyze(@PathVariable Long artifactId,
                                     @RequestParam(required = false) Integer targetVersion) {
        return R.ok(changePropagationService.analyze(artifactId, targetVersion));
    }

}
